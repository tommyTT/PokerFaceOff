package tt.models;

import java.util.Arrays;
import java.util.Optional;

/**
 * Define the available 4 suites of the cards.
 */
public enum CardSuit {
  CLUBS("Clubs", "C", "♣️"),
  DIAMONDS("Diamonds", "D", "♦️"),
  HEARTS("Hearts", "H", "♥️"),
  SPADES("Spades", "S", "♠️"),
  ;

  private final String fullName;
  private final String shortName;
  private final String symbol;

  CardSuit(String fullName, String shortName, String symbol) {
    this.fullName = fullName;
    this.shortName = shortName;
    this.symbol = symbol;
  }

  public String getFullName() {
    return fullName;
  }

  public String getShortName() {
    return shortName;
  }

  public String getSymbol() {
    return symbol;
  }

  /**
   * Return the CardValue that matches the name of the text.
   *
   * @param text the text that should match any card suit representation
   * @return an optional CardSuit that matches the full name, short name or the symbol of the
   * card suit
   */
  public static Optional<CardSuit> of(String text) {
    if (text.isBlank() || text.isEmpty()) {
      return Optional.empty();
    }

    // match any string representation
    return Arrays.stream(values())
        .filter(value -> value.getShortName().equalsIgnoreCase(text) || value.getSymbol()
            .equalsIgnoreCase(text) || value.getFullName().equalsIgnoreCase(text))
        .findFirst();
  }

}
