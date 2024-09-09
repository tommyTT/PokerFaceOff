import org.junit.jupiter.api.Test;
import tt.models.*;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;
import tt.util.CardUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultHandTypeAnalyzerTest {
  @Test
  public void testCreatingAHandFromListOfCardsReturnsAHandWithType() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    assertThrows(IllegalArgumentException.class, () -> analyzer.createHand(Collections.emptyList()),
        "a list of cards must be provided, otherwise an exception should be thrown");
    assertThrows(IllegalArgumentException.class, () -> analyzer.createHand(null),
        "a list of cards must be provided, otherwise an exception should be thrown");
    assertThrows(IllegalArgumentException.class,
        () -> analyzer.createHand(CardUtils.parseCardFromString("2S")),
        "only create a hand if there are exactly 5 cards");
    assertThrows(IllegalArgumentException.class,
        () -> analyzer.createHand(CardUtils.parseCardFromString("2S 2S 3S 4S 5S")),
        "all cards must be unique");

    Hand hand = analyzer.createHand(CardUtils.parseCardFromString("2S 2H 3S 4S 5S"));
    assertNotNull(hand, "a valid hand was created from the input");
    assertEquals(HandType.PAIR, hand.getType(), "the hand has been assigned the right type");
    assertEquals(List.of( //
        new Card(CardValue.TWO, CardSuit.SPADES), //
        new Card(CardValue.TWO, CardSuit.HEARTS), //
        new Card(CardValue.THREE, CardSuit.SPADES), //
        new Card(CardValue.FOUR, CardSuit.SPADES), //
        new Card(CardValue.FIVE, CardSuit.SPADES) //
    ), hand.getCards(), "the hand was assigned the right cards");
  }

  @Test
  public void testAnalyzerRecognizesTheCorrectTypeOfAHand() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    Map.of( //
        HandType.HIGH_CARD, "2S 3H 5S 6S 7S", //
        HandType.PAIR, "2S 2H 5S 6S 7S", //
        HandType.TWO_PAIRS, "2S 2H 5S 5S 7S", //
        HandType.THREE_OF_A_KIND, "2S 2H 2C 6S 7S", //
        HandType.STRAIGHT, "2S 3H 4S 5S 6S", //
        HandType.FLUSH, "2S 3S 5S 6S 7S", //
        HandType.FULL_HOUSE, "2S 2H 5S 5H 5D", //
        HandType.FOUR_OF_A_KIND, "2S 2H 2C 2D 7S", //
        HandType.STRAIGHT_FLUSH, "2S 3S 4S 5S 6S" //
    ).forEach((expectedType, cards) -> {
      List<Card> theCards = CardUtils.parseCardFromString(cards);
      // test all the possible hand types
      Arrays.stream(HandType.values()).forEach(type -> {
        if (type == expectedType || expectedType.isSubTypeOf(type)) {
          // only the expected type can match
          assertTrue(analyzer.isType(type, theCards),
              "type %s must match the cards %s".formatted(type, theCards));
        } else {
          // everything else should not match
          assertFalse(analyzer.isType(type, theCards),
              "type %s must not match the cards %s".formatted(type, theCards));
        }
      });
    });

    // an invalid hand should not match against any type
    List.of("", //
        "AS", //
        "AS AS 4S 5S 9H" //
    ).forEach(cards -> {
      List<Card> theCards = CardUtils.parseCardFromString(cards);
      Arrays.stream(HandType.values()).forEach(type -> {
        assertFalse(analyzer.isType(type, theCards),
            "type %s must not match the cards %s".formatted(type, theCards));
      });
    });
  }

  @Test
  public void testHighCardStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // first hand with a high card
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("2S 4D 8H TC KD"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.HIGH_CARD, hand1.getType(), "type of the hand is correct");

    // same values but different suits
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("2D 4S 8C TD KH"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.HIGH_CARD, hand2.getType(), "type of the hand is correct");

    // third hand has a different lower third card
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("2H 4H 5C TC KC"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.HIGH_CARD, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand1, hand2), analyzer.tiebreak(List.of(hand1, hand2)),
        "hand 1 and 2 split the pot");
    assertEquals(List.of(hand1, hand2, hand2), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "hand 1 and 2 split the pot but the duplicate isn't eliminated");
    assertEquals(List.of(hand1, hand2), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 1 and 2 split the pot, hand 3 doesn't");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH JC AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.HIGH_CARD, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }

  @Test
  public void testSinglePairCardStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // first hand with a high card
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("2S 2D 8H TC KD"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.PAIR, hand1.getType(), "type of the hand is correct");

    // same pair but different suits
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("2H 2C 8C TD KH"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.PAIR, hand2.getType(), "type of the hand is correct");

    // third hand has a higher pair
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("4S 4H 5C TC KC"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.PAIR, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand1, hand2), analyzer.tiebreak(List.of(hand1, hand2)),
        "hand 1 and 2 split the pot");
    assertEquals(List.of(hand1, hand2, hand2), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "hand 1 and 2 split the pot but the duplicate isn't eliminated");
    assertEquals(List.of(hand3), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 3 beats both other hands");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH JC AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.PAIR, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }

  @Test
  public void testTwoPairCardStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // first hand with a high card
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("4S 4D 8H 8S KD"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.TWO_PAIRS, hand1.getType(), "type of the hand is correct");

    // same high pair but different low pair
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("3H 3C 8C 8D KH"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.TWO_PAIRS, hand2.getType(), "type of the hand is correct");

    // third hand has a higher high pair but a lower low pair
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("9S 9D 2C 2D KC"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.TWO_PAIRS, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1, hand2)),
        "hand 1 wins because of the higher low pair");
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "hand 1 still wins against hand 2");
    assertEquals(List.of(hand3), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 3 beats both hands because of the high pair");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH JC AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.TWO_PAIRS, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }


  @Test
  public void testThreeOfAKindCardStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // first hand with king high kicker
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("4S 4D 4H 8S KD"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.THREE_OF_A_KIND, hand1.getType(), "type of the hand is correct");

    // lower triple but same kicker
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("3H 3C 3D 8D KH"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.THREE_OF_A_KIND, hand2.getType(), "type of the hand is correct");

    // highest triple
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("9S 9D 9C 2D KC"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.THREE_OF_A_KIND, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1, hand2)),
        "hand 1 wins because of the higher triple");
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "hand 1 still wins against hand 2");
    assertEquals(List.of(hand3), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 3 beats both hands because of the higher triple™");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH JC AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.THREE_OF_A_KIND, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }

  @Test
  public void testStraightCardStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // a 9 high straight
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("5S 6D 7H 8S 9D"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.STRAIGHT, hand1.getType(), "type of the hand is correct");

    // another 9 high straight with different suits
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("5C 6S 7S 8C 9H"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.STRAIGHT, hand2.getType(), "type of the hand is correct");

    // a jack high straight
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("7D 8H 9C TH JC"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.STRAIGHT, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand1, hand2), analyzer.tiebreak(List.of(hand1, hand2)),
        "hand 1 and 2 are both equal and therefore split the pot");
    assertEquals(List.of(hand1, hand2, hand2), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "still both hands are winning");
    assertEquals(List.of(hand3), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 3 beats both hands because of the highest card");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH JC AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.STRAIGHT, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }

  @Test
  public void testFlushCardStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // a 9 high spades flush
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("2S 6S 7S 8S 9S"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.FLUSH, hand1.getType(), "type of the hand is correct");

    // another 9 high flush with different suits and exact same values
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("2C 6C 7C 8C 9C"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.FLUSH, hand2.getType(), "type of the hand is correct");

    // a jack high flush of the same suit as the first one
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("2S 4S 5S TS JS"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.FLUSH, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand1, hand2), analyzer.tiebreak(List.of(hand1, hand2)),
        "hand 1 and 2 are both equal and therefore split the pot");
    assertEquals(List.of(hand1, hand2, hand2), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "still both hands are winning");
    assertEquals(List.of(hand3), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 3 beats both hands because of the highest card");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH JC AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.FLUSH, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }

  @Test
  public void testFullHouseStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // eights full of aces
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("AS AC 8H 8C 8S"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.FULL_HOUSE, hand1.getType(), "type of the hand is correct");

    // nines full of kings
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("9C 9H 9S KC KH"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.FULL_HOUSE, hand2.getType(), "type of the hand is correct");

    // jacks full of queens
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH QS QH"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.FULL_HOUSE, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand2), analyzer.tiebreak(List.of(hand1, hand2)),
        "hand 2 has the higher triple");
    assertEquals(List.of(hand2, hand2), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "still hand 2 wins");
    assertEquals(List.of(hand3), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 3 beats both hands because of the highest card");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH JC AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.FULL_HOUSE, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }

  @Test
  public void testFourOfAKindStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // eights
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("AS 8D 8H 8C 8S"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.FOUR_OF_A_KIND, hand1.getType(), "type of the hand is correct");

    // nines
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("9C 9H 9S 9D KH"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.FOUR_OF_A_KIND, hand2.getType(), "type of the hand is correct");

    // jacks
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH JC QH"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.FOUR_OF_A_KIND, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand2), analyzer.tiebreak(List.of(hand1, hand2)),
        "hand 2 has the higher value");
    assertEquals(List.of(hand2, hand2), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "still hand 2 wins");
    assertEquals(List.of(hand3), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 3 beats both hands because of the higher value");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH AH AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.FOUR_OF_A_KIND, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }

  @Test
  public void testStraightFlushStrategy() {
    DefaultHandTypeAnalyzer analyzer = new DefaultHandTypeAnalyzer();

    // 8 high diamond straight
    Hand hand1 = analyzer.createHand(CardUtils.parseCardFromString("8D 7D 6D 4D 5D"));
    assertNotNull(hand1, "created a hand");
    assertEquals(HandType.STRAIGHT_FLUSH, hand1.getType(), "type of the hand is correct");

    // 8 high clubs straight
    Hand hand2 = analyzer.createHand(CardUtils.parseCardFromString("8C 7C 6C 4C 5C"));
    assertNotNull(hand2, "created a hand");
    assertEquals(HandType.STRAIGHT_FLUSH, hand2.getType(), "type of the hand is correct");

    // royal diamond flush
    Hand hand3 = analyzer.createHand(CardUtils.parseCardFromString("AD KD QD TD JD"));
    assertNotNull(hand3, "created a hand");
    assertEquals(HandType.STRAIGHT_FLUSH, hand3.getType(), "type of the hand is correct");

    // tie-breaking
    assertEquals(List.of(hand1), analyzer.tiebreak(List.of(hand1)), "only one hand always wins");
    assertEquals(List.of(hand1, hand2), analyzer.tiebreak(List.of(hand1, hand2)),
        "both hands have the same highest card and therefore split the pot");
    assertEquals(List.of(hand1, hand2, hand2), analyzer.tiebreak(List.of(hand1, hand2, hand2)),
        "still a split pot");
    assertEquals(List.of(hand3), analyzer.tiebreak(List.of(hand1, hand2, hand3)),
        "hand 3 beats both hands because of the higher value");

    // tie-breaking with another type of hand doesn't work and should throw an exception
    Hand fourOfAKind = analyzer.createHand(CardUtils.parseCardFromString("JS JD JH AH AS"));
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(List.of(hand1, hand2, hand3, fourOfAKind));
    }, "tie-breaking should throw an  exception if one hand has a different type");
    assertThrows(IllegalArgumentException.class, () -> {
      analyzer.tiebreak(HandType.STRAIGHT_FLUSH, hand1, fourOfAKind);
    }, "tie-breaking should throw an  exception if one hand has a different type");
  }

}
