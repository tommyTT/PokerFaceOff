package tt.services.impl.decks;

import tt.models.Card;
import tt.models.CardSuit;
import tt.models.CardValue;
import tt.models.PokerDeck;
import tt.services.DeckCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Create an unshuffled poker deck.
 */
public class UnshuffledPokerDeckCreator implements DeckCreator {
  @Override
  public PokerDeck create() {
    List<Card> cards = generateCards();
    shuffle(cards);
    return new PokerDeck(cards);
  }

  /**
   * Generate the list of all cards in order of suite and value.
   *
   * @return the list of all 52 cards
   */
  private static List<Card> generateCards() {
    return Arrays.stream(CardSuit.values())
        .flatMap(suit -> Arrays.stream(CardValue.values()).map(value -> new Card(value, suit)))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  /**
   * Shuffle the given cards
   *
   * @param cards the cards that should be shuffled
   */
  protected void shuffle(List<Card> cards) {
    // no shuffling here! can be provided by a subclass
  }
}
