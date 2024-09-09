package tt.models;

import java.util.Arrays;
import java.util.Optional;

/**
 * Defines a card by specifying its value and suit.
 *
 * @param value the face value of the card
 * @param suit  the suit of the card
 */
public record Card(CardValue value, CardSuit suit) {
  @Override
  public String toString() {
    return "%s of %s".formatted(value.getName(), suit.getFullName());
  }

  /**
   * Return a fancier string version for this card.
   *
   * @return the symbolized version of this card
   */
  public String toSymbolString() {
    return "%s%s".formatted(value.getName(), suit.getSymbol());
  }

  /**
   * Return the Card that matches the text. The text has to be in the form of
   * <p>
   * {CardValue}{CardSuit}, e.g.
   * TH is the ten of hearts
   * 4D is the four of diamonds
   *
   * @param text a textual representation of the card value and suit
   * @return an optional Card instance if the text represents exactly on part
   */
  public static Optional<Card> of(String text) {
    if (text.isBlank() || text.length() != 2) {
      return Optional.empty();
    }

    // determine the value
    Optional<CardValue> value = CardValue.of(text.substring(0, 1));
    if (value.isEmpty()) {
      return Optional.empty();
    }

    // determine the suit
    Optional<CardSuit> suit = CardSuit.of(text.substring(1));
    return suit.map(cardSuit -> new Card(value.get(), cardSuit));
  }

}
