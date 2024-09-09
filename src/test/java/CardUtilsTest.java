import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tt.models.Card;
import tt.models.CardSuit;
import tt.models.CardValue;
import tt.util.CardUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardUtilsTest {

  @Test
  public void testParseCardsFromValidTextReturnsAListOfCards() {
    assertEquals(CardUtils.parseCardFromString(""), Collections.emptyList(),
        "empty string returns an empty list of cards");
    assertEquals(CardUtils.parseCardFromString(null), Collections.emptyList(),
        "null string returns an empty list of cards");

    assertEquals(CardUtils.parseCardFromString("AS"),
        List.of(new Card(CardValue.ACE, CardSuit.SPADES)), "single card returns the ace of spades");

    assertEquals(CardUtils.parseCardFromString("AS AS AS"),
        List.of(new Card(CardValue.ACE, CardSuit.SPADES), new Card(CardValue.ACE, CardSuit.SPADES),
            new Card(CardValue.ACE, CardSuit.SPADES)), "multiple ace of spades returned");

    assertEquals(CardUtils.parseCardFromString("AS AD AC AH"),
        List.of(new Card(CardValue.ACE, CardSuit.SPADES),
            new Card(CardValue.ACE, CardSuit.DIAMONDS), new Card(CardValue.ACE, CardSuit.CLUBS),
            new Card(CardValue.ACE, CardSuit.HEARTS)), "all aces in the same order");

    assertEquals(CardUtils.parseCardFromString("AS AD 4H AC AH"),
        List.of(new Card(CardValue.ACE, CardSuit.SPADES),
            new Card(CardValue.ACE, CardSuit.DIAMONDS), new Card(CardValue.FOUR, CardSuit.HEARTS),
            new Card(CardValue.ACE, CardSuit.CLUBS), new Card(CardValue.ACE, CardSuit.HEARTS)),
        "aces and a four in the right order");

    assertEquals(CardUtils.parseCardFromString("2S 3S 4S 5S 6S 7S 8S 9S TS JS QS KS AS"),
        List.of(new Card(CardValue.TWO, CardSuit.SPADES),
            new Card(CardValue.THREE, CardSuit.SPADES), new Card(CardValue.FOUR, CardSuit.SPADES),
            new Card(CardValue.FIVE, CardSuit.SPADES), new Card(CardValue.SIX, CardSuit.SPADES),
            new Card(CardValue.SEVEN, CardSuit.SPADES), new Card(CardValue.EIGHT, CardSuit.SPADES),
            new Card(CardValue.NINE, CardSuit.SPADES), new Card(CardValue.TEN, CardSuit.SPADES),
            new Card(CardValue.JACK, CardSuit.SPADES), new Card(CardValue.QUEEN, CardSuit.SPADES),
            new Card(CardValue.KING, CardSuit.SPADES), new Card(CardValue.ACE, CardSuit.SPADES)),
        "all spades in order");

    assertEquals(CardUtils.parseCardFromString("2S,3S,4S;5S;6S,7S,8S,9S,TS;JS, QS, KS, AS"),
        List.of(new Card(CardValue.TWO, CardSuit.SPADES),
            new Card(CardValue.THREE, CardSuit.SPADES), new Card(CardValue.FOUR, CardSuit.SPADES),
            new Card(CardValue.FIVE, CardSuit.SPADES), new Card(CardValue.SIX, CardSuit.SPADES),
            new Card(CardValue.SEVEN, CardSuit.SPADES), new Card(CardValue.EIGHT, CardSuit.SPADES),
            new Card(CardValue.NINE, CardSuit.SPADES), new Card(CardValue.TEN, CardSuit.SPADES),
            new Card(CardValue.JACK, CardSuit.SPADES), new Card(CardValue.QUEEN, CardSuit.SPADES),
            new Card(CardValue.KING, CardSuit.SPADES), new Card(CardValue.ACE, CardSuit.SPADES)),
        "all spades in order and with different delimiters");
  }

  @Test
  public void testParseCardsFromInvalidTextThrowsAnException() {
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> CardUtils.parseCardFromString("BS"), "Invalid card value should throw an exception");

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> CardUtils.parseCardFromString("A4"), "Invalid card suit should throw an exception");

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> CardUtils.parseCardFromString("B4"),
        "invalid suit and value should throw an exception");

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> CardUtils.parseCardFromString("AS B4 AD"),
        "doesn't matter where the invalid value is");
  }

  @Test
  public void testGroupingOfCardsByValueShouldReturnAMapOfAllCardValuesFromTheHand() {
    assertEquals(Map.of(
        CardValue.ACE, List.of( //
            new Card(CardValue.ACE, CardSuit.SPADES), //
            new Card(CardValue.ACE, CardSuit.DIAMONDS), //
            new Card(CardValue.ACE, CardSuit.SPADES), //
            new Card(CardValue.ACE, CardSuit.HEARTS) //
        ), //
        CardValue.EIGHT, List.of( //
            new Card(CardValue.EIGHT, CardSuit.SPADES), //
            new Card(CardValue.EIGHT, CardSuit.HEARTS) //
        ), //
        CardValue.TWO, List.of( //
            new Card(CardValue.TWO, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES) //
        )
    ), CardUtils.groupCardsByValue(
        List.of( //
            new Card(CardValue.ACE, CardSuit.SPADES), //
            new Card(CardValue.ACE, CardSuit.DIAMONDS), //
            new Card(CardValue.EIGHT, CardSuit.SPADES), //
            new Card(CardValue.EIGHT, CardSuit.HEARTS), //
            new Card(CardValue.ACE, CardSuit.SPADES), //
            new Card(CardValue.ACE, CardSuit.HEARTS), //
            new Card(CardValue.TWO, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES)
        )), "grouping should return a map that keeps all duplicates");
  }

  @Test
  public void testGroupingOfCardsBySuitShouldReturnAMapOfAllCardSuitsFromTheHand() {
    assertEquals(Map.of(
        CardSuit.SPADES, List.of( //
            new Card(CardValue.ACE, CardSuit.SPADES), //
            new Card(CardValue.EIGHT, CardSuit.SPADES), //
            new Card(CardValue.ACE, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES) //
        ), //
        CardSuit.DIAMONDS, List.of( //
            new Card(CardValue.ACE, CardSuit.DIAMONDS) //
        ), //
        CardSuit.HEARTS, List.of( //
            new Card(CardValue.EIGHT, CardSuit.HEARTS), //
            new Card(CardValue.ACE, CardSuit.HEARTS) //
        ) //
    ), CardUtils.groupCardsBySuit(
        List.of( //
            new Card(CardValue.ACE, CardSuit.SPADES), //
            new Card(CardValue.ACE, CardSuit.DIAMONDS), //
            new Card(CardValue.EIGHT, CardSuit.SPADES), //
            new Card(CardValue.EIGHT, CardSuit.HEARTS), //
            new Card(CardValue.ACE, CardSuit.SPADES), //
            new Card(CardValue.ACE, CardSuit.HEARTS), //
            new Card(CardValue.TWO, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES), //
            new Card(CardValue.TWO, CardSuit.SPADES)
        )), "grouping should return a map that keeps all duplicates");
  }

}
