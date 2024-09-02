package tt.services;

import tt.models.PokerDeck;

/**
 * Allows the creation of a poker deck.
 */
public interface DeckCreator {
  /**
   * Create a valid deck of poker cards.
   *
   * @return a valid poker deck of 52 cards
   */
  PokerDeck create();
}
