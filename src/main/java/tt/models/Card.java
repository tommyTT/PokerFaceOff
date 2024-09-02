package tt.models;

/**
 * Defines a card by specifying its value and suit.
 *
 * @param value the face value of the card
 * @param suit the suit of the card
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
}
