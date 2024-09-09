import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tt.models.Card;
import tt.models.CardSuit;
import tt.models.CardValue;
import tt.models.PokerDeck;
import tt.services.impl.decks.SeededShuffledPokerDeckCreator;
import tt.services.impl.decks.UnshuffledPokerDeckCreator;

import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class DeckCreatorTest {
  @Test
  public void testCreatingAnUnshuffledDeck() {
    var creator = new UnshuffledPokerDeckCreator();

    PokerDeck deck = creator.create();
    Assertions.assertNotNull(deck, "a deck was created");

    Assertions.assertEquals(52, sizeOf(deck), "deck must contain exactly 52 cards");

    Assertions.assertEquals(new Card(CardValue.TWO, CardSuit.CLUBS), deck.drawCard(0),
        "first card is the two of clubs");
    Assertions.assertEquals(new Card(CardValue.ACE, CardSuit.SPADES), deck.drawCard(51),
        "last card is the ace of spades");

    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> deck.drawCard(52),
        "can't draw more than 52 cards");
    Assertions.assertThrows(IndexOutOfBoundsException.class, () -> deck.drawCard(-1),
        "can't draw a card with a negative index");
  }

  @Test
  public void testCreatingAShuffledDeck() {
    // use a defined seed to create decks
    int seed = 42;

    PokerDeck deck = new SeededShuffledPokerDeckCreator(seed).create();
    Assertions.assertNotNull(deck, "a deck was created");
    Assertions.assertEquals(52, sizeOf(deck), "deck must contain exactly 52 cards");

    Assertions.assertEquals(new Card(CardValue.FOUR, CardSuit.DIAMONDS), deck.drawCard(0),
        "first card is the four of diamonds");
    Assertions.assertEquals(new Card(CardValue.TWO, CardSuit.HEARTS), deck.drawCard(51),
        "last card is the two of hearts");

    // creating another deck should return the same order of cards
    PokerDeck otherDeck = new SeededShuffledPokerDeckCreator(seed).create();
    Assertions.assertNotNull(deck, "a deck was created");
    Assertions.assertEquals(52, sizeOf(deck), "deck must contain exactly 52 cards");

    IntStream.range(0, 52).forEach(index -> {
      Assertions.assertEquals(deck.drawCard(index), otherDeck.drawCard(index),
          "should return the same cards in order");
    });
  }

  private static int sizeOf(PokerDeck deck) {
    return StreamSupport.stream(deck.spliterator(), false).toList().size();
  }
}
