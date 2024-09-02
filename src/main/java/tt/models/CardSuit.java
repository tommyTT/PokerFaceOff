package tt.models;

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
}
