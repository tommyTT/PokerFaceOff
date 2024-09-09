import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tt.models.Player;
import tt.services.impl.dealer.FiveCardDrawDealer;
import tt.services.impl.decks.SeededShuffledPokerDeckCreator;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;
import tt.util.CardUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DealerTest {

  private int playerCounter = 0;

  @Test
  public void testDealAHandOfFiveCardDrawWith2Players() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    List<Player> players = dealer.deal(2);
    assertEquals(2, players.size(), "must return 2 hands of poker");

    Player playerOne = players.get(0);
    Assertions.assertNotNull(playerOne, "first hand can't be null");
    assertEquals("Player 1", playerOne.name(), "Player got a name");

    Player playerTwo = players.get(1);
    Assertions.assertNotNull(playerTwo, "second hand can't be null");
    assertEquals("Player 2", playerTwo.name(), "Player got a name");

    Assertions.assertNotEquals(playerTwo, playerOne, "the two hands are not allowed to be equal");
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
      Player kingHigh = createPlayer(analyzer, "KS 2C 3H 5D 6C");
      Player aceHigh = createPlayer(analyzer, "AS 2H 3C 5H 6D");
      assertEquals(List.of(aceHigh), dealer.showdown(List.of(kingHigh, aceHigh)), "ace high wins");

      // ace high versus ace high with different suits is a tie
      Player aceHigh2 = createPlayer(analyzer, "AC 2C 3D 5D 6C");
      assertEquals(List.of(aceHigh, aceHigh2), dealer.showdown(List.of(aceHigh, aceHigh2)), "tie");
    }

    // kings win against ace high
    {
      Player kings = createPlayer(analyzer, "KS KC 3H 5D 6C");
      Player aceHigh = createPlayer(analyzer, "AS 2H 3C 5H 6D");
      assertEquals(List.of(kings), dealer.showdown(List.of(kings, aceHigh)),
          "kings win against ace high");

      // kings versus other kings use the high carding
      Player kings2 = createPlayer(analyzer, "KD KH 3C 5H 7C");
      assertEquals(List.of(kings2), dealer.showdown(List.of(kings, kings2)),
          "high carding after the pair");
    }

    // aces and eights win against kings
    {
      Player kings = createPlayer(analyzer, "KS KC 3H 5D 6C");
      Player acesAndEights = createPlayer(analyzer, "AS AH 8C 8H 6D");
      assertEquals(List.of(acesAndEights), dealer.showdown(List.of(kings, acesAndEights)),
          "aces&eights win against kings");
    }

    // three kings win against aces and eights
    {
      Player threeKings = createPlayer(analyzer, "KS KC KH 5D 6C");
      Player acesAndEights = createPlayer(analyzer, "AS AH 8C 8H 6D");
      assertEquals(List.of(threeKings), dealer.showdown(List.of(threeKings, acesAndEights)),
          "three kings beat aces&eights");
    }

    // straight win against three kings
    {
      Player threeKings = createPlayer(analyzer, "KS KC KH 5D 6C");
      Player straight = createPlayer(analyzer, "TH 9C 8H 7D 6H");
      assertEquals(List.of(straight), dealer.showdown(List.of(threeKings, straight)),
          "straight beats the kings");
    }

    // flush wins against straight
    {
      Player flush = createPlayer(analyzer, "KS QS JS 5S 3S");
      Player straight = createPlayer(analyzer, "TH 9C 8H 7D 6H");
      assertEquals(List.of(flush), dealer.showdown(List.of(flush, straight)),
          "flush beats straight");
    }

    // full house wins against flush
    {
      Player flush = createPlayer(analyzer, "KS QS JS 5S 3S");
      Player fullHouse = createPlayer(analyzer, "KS KD KH 5S 5C");
      assertEquals(List.of(fullHouse), dealer.showdown(List.of(flush, fullHouse)),
          "full house beats flush");
    }

    // four of a kind beats full house
    {
      Player fourOfAKind = createPlayer(analyzer, "QS QH QC QD 3S");
      Player fullHouse = createPlayer(analyzer, "KS KD KH 5S 5C");
      assertEquals(List.of(fourOfAKind), dealer.showdown(List.of(fourOfAKind, fullHouse)),
          "four of a kind beats full house");
    }

    // straight flush beats four of a kind
    {
      Player fourOfAKind = createPlayer(analyzer, "QS QH QC QD 3S");
      Player straightFlush = createPlayer(analyzer, "2H 3H 4H 5H 6H");
      assertEquals(List.of(straightFlush), dealer.showdown(List.of(fourOfAKind, straightFlush)),
          "straight flush beats four of a kind");
    }
  }

  private Player createPlayer(DefaultHandTypeAnalyzer analyzer, String text) {
    return new Player("Player " + (++playerCounter), analyzer.createHand(CardUtils.parseCardFromString(text)));
  }

  @Test
  public void testDealerDealsAHandFor2PlayersAndScoresTheShowdown() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    List<Player> players = dealer.deal(2);
    assertEquals(2, players.size(), "must return 2 hands of poker");

    Player player1 = players.get(0);
    Assertions.assertNotNull(player1);

    Player player2 = players.get(1);
    Assertions.assertNotNull(player2);

    assertEquals(List.of(player1), dealer.showdown(players), "hand 1 is better than hand 2");
  }

  @Test
  public void testDealerDealsAHandFor7PlayersAndScoresTheShowdown() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    List<Player> players = dealer.deal(7);
    assertEquals(7, players.size(), "must return 7 hands of poker");

    Player player1 = players.get(0);
    Assertions.assertNotNull(player1);

    Player player2 = players.get(1);
    Assertions.assertNotNull(player2);

    Player player3 = players.get(2);
    Assertions.assertNotNull(player3);

    Player player4 = players.get(3);
    Assertions.assertNotNull(player4);

    Player player5 = players.get(4);
    Assertions.assertNotNull(player5);

    Player player6 = players.get(5);
    Assertions.assertNotNull(player6);

    Player player7 = players.get(6);
    Assertions.assertNotNull(player7);

    assertEquals(List.of(player4), dealer.showdown(players), "hand 1 is the winner");
  }
}
