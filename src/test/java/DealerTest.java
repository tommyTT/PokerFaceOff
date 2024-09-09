import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tt.models.Hand;
import tt.services.impl.dealer.FiveCardDrawDealer;
import tt.services.impl.decks.SeededShuffledPokerDeckCreator;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;
import tt.util.CardUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DealerTest {
  @Test
  public void testDealAHandOfFiveCardDrawWith2Players() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    List<Hand> hands = dealer.deal(2);
    assertEquals(2, hands.size(), "must return 2 hands of poker");

    Hand handOne = hands.get(0);
    Assertions.assertNotNull(handOne, "first hand can't be null");

    Hand handTwo = hands.get(1);
    Assertions.assertNotNull(handTwo, "second hand can't be null");

    Assertions.assertNotEquals(handTwo, handOne, "the two hands are not allowed to be equal");
  }

  @Test
  public void testDealerDoesntAllowMoreThan7OrLessThan2Players() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    Assertions.assertThrows(IllegalArgumentException.class, () -> dealer.deal(-1),
        "no negative number of players allowed");
    Assertions.assertThrows(IllegalArgumentException.class, () -> dealer.deal(0),
        "must be at least 2 players");
    Assertions.assertThrows(IllegalArgumentException.class, () -> dealer.deal(1),
        "must be at least 2 players");
    Assertions.assertThrows(IllegalArgumentException.class, () -> dealer.deal(8),
        "must be at most 7 players");
  }

  @Test
  public void testDealerScoresTheShowdownCorrectly() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    // Ace high versus king high
    {
      Hand kingHigh = analyzer.createHand(CardUtils.parseCardFromString("KS 2C 3H 5D 6C"));
      Hand aceHigh = analyzer.createHand(CardUtils.parseCardFromString("AS 2H 3C 5H 6D"));
      assertEquals(List.of(aceHigh), dealer.showdown(List.of(kingHigh, aceHigh)), "ace high wins");

      // ace high versus ace high with different suits is a tie
      Hand aceHigh2 = analyzer.createHand(CardUtils.parseCardFromString("AC 2C 3D 5D 6C"));
      assertEquals(List.of(aceHigh, aceHigh2), dealer.showdown(List.of(aceHigh, aceHigh2)), "tie");
    }

    // kings win against ace high
    {
      Hand kings = analyzer.createHand(CardUtils.parseCardFromString("KS KC 3H 5D 6C"));
      Hand aceHigh = analyzer.createHand(CardUtils.parseCardFromString("AS 2H 3C 5H 6D"));
      assertEquals(List.of(kings), dealer.showdown(List.of(kings, aceHigh)),
          "kings win against ace high");

      // kings versus other kings use the high carding
      Hand kings2 = analyzer.createHand(CardUtils.parseCardFromString("KD KH 3C 5H 7C"));
      assertEquals(List.of(kings2), dealer.showdown(List.of(kings, kings2)),
          "high carding after the pair");
    }

    // aces and eights win against kings
    {
      Hand kings = analyzer.createHand(CardUtils.parseCardFromString("KS KC 3H 5D 6C"));
      Hand acesAndEights = analyzer.createHand(CardUtils.parseCardFromString("AS AH 8C 8H 6D"));
      assertEquals(List.of(acesAndEights), dealer.showdown(List.of(kings, acesAndEights)),
          "aces&eights win against kings");
    }

    // three kings win against aces and eights
    {
      Hand threeKings = analyzer.createHand(CardUtils.parseCardFromString("KS KC KH 5D 6C"));
      Hand acesAndEights = analyzer.createHand(CardUtils.parseCardFromString("AS AH 8C 8H 6D"));
      assertEquals(List.of(threeKings), dealer.showdown(List.of(threeKings, acesAndEights)),
          "three kings beat aces&eights");
    }

    // straight win against three kings
    {
      Hand threeKings = analyzer.createHand(CardUtils.parseCardFromString("KS KC KH 5D 6C"));
      Hand straight = analyzer.createHand(CardUtils.parseCardFromString("TH 9C 8H 7D 6H"));
      assertEquals(List.of(straight), dealer.showdown(List.of(threeKings, straight)),
          "straight beats the kings");
    }

    // flush wins against straight
    {
      Hand flush = analyzer.createHand(CardUtils.parseCardFromString("KS QS JS 5S 3S"));
      Hand straight = analyzer.createHand(CardUtils.parseCardFromString("TH 9C 8H 7D 6H"));
      assertEquals(List.of(flush), dealer.showdown(List.of(flush, straight)),
          "flush beats straight");
    }

    // full house wins against flush
    {
      Hand flush = analyzer.createHand(CardUtils.parseCardFromString("KS QS JS 5S 3S"));
      Hand fullHouse = analyzer.createHand(CardUtils.parseCardFromString("KS KD KH 5S 5C"));
      assertEquals(List.of(fullHouse), dealer.showdown(List.of(flush, fullHouse)),
          "full house beats flush");
    }

    // four of a kind beats full house
    {
      Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("QS QH QC QD 3S"));
      Hand fullHouse = analyzer.createHand(CardUtils.parseCardFromString("KS KD KH 5S 5C"));
      assertEquals(List.of(fourOfAKind), dealer.showdown(List.of(fourOfAKind, fullHouse)),
          "four of a kind beats full house");
    }

    // straight flush beats four of a kind
    {
      Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("QS QH QC QD 3S"));
      Hand straightFlush = analyzer.createHand(CardUtils.parseCardFromString("2H 3H 4H 5H 6H"));
      assertEquals(List.of(straightFlush), dealer.showdown(List.of(fourOfAKind, straightFlush)),
          "straight flush beats four of a kind");
    }
  }

  @Test
  public void testDealerDealsAHandFor2PlayersAndScoresTheShowdown() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    List<Hand> hands = dealer.deal(2);
    assertEquals(2, hands.size(), "must return 2 hands of poker");

    Hand hand1 = hands.get(0);
    Assertions.assertNotNull(hand1);

    Hand hand2 = hands.get(1);
    Assertions.assertNotNull(hand2);

    assertEquals(List.of(hand1), dealer.showdown(hands), "hand 1 is better than hand 2");
  }

  @Test
  public void testDealerDealsAHandFor7PlayersAndScoresTheShowdown() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    List<Hand> hands = dealer.deal(2);
    assertEquals(2, hands.size(), "must return 2 hands of poker");

    Hand hand1 = hands.get(0);
    Assertions.assertNotNull(hand1);

    Hand hand2 = hands.get(1);
    Assertions.assertNotNull(hand2);

    assertEquals(List.of(hand1), dealer.showdown(hands), "hand 1 is better than hand 2");
  }
}
