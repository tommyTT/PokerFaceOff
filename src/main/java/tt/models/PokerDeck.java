package tt.models;

import java.util.*;

/**
 * Specifies a deck of 52 cards for playing poker. The order of the cards is fixed!
 */
public class PokerDeck implements Iterable<Card> {
  private final Card[] cards;

  /**
   * Create a new deck.
   *
   * @param cards the cards that should be in the deck.
   */
  public PokerDeck(List<Card> cards) {
    validate(cards);
    this.cards = fill(cards);
  }

  /**
   * Perform validations of the provided cards, so that this represents a valid deck.
   *
   * @param cards the list of cards that should make up the deck
   */
  private void validate(List<Card> cards) {
    // there should be exactly 52 cards!
    if (cards == null || cards.size() != 52) {
      throw new IllegalArgumentException("A valid deck must contain exactly 52 cards!");
    }

    // check if there are any duplicates
    Set<Card> cardsAsSet = new HashSet<>(cards);
    if (cardsAsSet.size() != 52) {
      throw new IllegalArgumentException("There cannot be any duplicate cards in the deck!");
    }

    // there can't be a null value present!
    if (cardsAsSet.contains(null)) {
      throw new IllegalArgumentException("Null is not a valid card!");
    }
  }

  /**
   * Transform the cards into an array, so the order can be fixed.
   *
   * @param cards the cards for the deck
   */
  private Card[] fill(List<Card> cards) {
    return cards.toArray(Card[]::new);
  }

  /**
   * Draws the card with the specified index (0-based!) from the deck.
   *
   * @param index the index of the card between 0 and 51 (incl.)
   * @return the card at that position
   */
  public Card drawCard(int index) {
    if (index < 0 || index >= cards.length) {
      throw new IndexOutOfBoundsException("Can't draw card number " + index + " from a 52 card deck!");
    }
    return cards[index];
  }

  @Override
  public Iterator<Card> iterator() {
    return Arrays.stream(cards).iterator();
  }
}
