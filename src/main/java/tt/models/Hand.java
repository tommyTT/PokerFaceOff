package tt.models;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Defines a hand of card for poker. The order of the cards is irrelevant!
 */
public class Hand {
  private final Card[] cards;
  private final HandType type;

  private Hand(HandType type, Card[] cards) {
    this.cards = cards;
    this.type = type;
  }

  /**
   * Create a new Hand of poker from the given cards. There must be exactly 5 cards
   *
   * @param type  the type
   * @param cards the cards
   * @return the hand
   */
  public static Hand of(HandType type, List<Card> cards) {
    var cardsAsSet = new HashSet<>(cards);
    if (cardsAsSet.size() != 5) {
      throw new IllegalArgumentException("must have exactly 5 distinct cards");
    } else if (cardsAsSet.contains(null)) {
      throw new IllegalArgumentException("all cards must not be null");
    }

    return new Hand(type, cards.toArray(Card[]::new));
  }

  /**
   * Returns the type of hand that is represented by the cards.
   *
   * @return the type
   */
  public HandType getType() {
    return type;
  }

  /**
   * Returns the cards in this hand. There is no order to the cards!
   *
   * @return the cards
   */
  public List<Card> getCards() {
    return Arrays.asList(cards);
  }

  /**
   * Returns the highest card value
   *
   * @return the highest card value in this hand
   */
  public CardValue getHighestCardValue() {
    return getCards().stream().map(Card::value).max(Comparator.naturalOrder()).orElseThrow();
  }
}
