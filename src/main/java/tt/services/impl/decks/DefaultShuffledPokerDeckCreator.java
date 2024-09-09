package tt.services.impl.decks;

import tt.models.Card;

import java.util.Collections;
import java.util.List;

/**
 * Create a deck by using the random shuffle algorithm provided by {@link Collections#shuffle(List)}
 */
public class DefaultShuffledPokerDeckCreator extends UnshuffledPokerDeckCreator {
  @Override
  protected void shuffle(List<Card> cards) {
    // use the default random shuffle from the jdk
    Collections.shuffle(cards);
  }
}
