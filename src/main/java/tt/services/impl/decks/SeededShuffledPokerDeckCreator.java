package tt.services.impl.decks;

import tt.models.Card;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Uses an internal instance of {@link Random} with the given seed in order to shuffle the deck of cardsxyl
 */
public class SeededShuffledPokerDeckCreator extends UnshuffledPokerDeckCreator {
  private final Random random;

  public SeededShuffledPokerDeckCreator(int seed) {
    this.random = new Random(seed);
  }

  @Override
  protected void shuffle(List<Card> cards) {
    // use the provided random number generator to shuffle the un-shuffled deck
    Collections.shuffle(cards, random);
  }
}
