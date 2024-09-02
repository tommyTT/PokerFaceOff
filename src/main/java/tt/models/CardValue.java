package tt.models;

/**
 * Defines the available face values of cards and also defines their order via the enum order.
 */
public enum CardValue {
  TWO("2"),
  THREE("3"),
  FOUR("4"),
  FIVE("5"),
  SIX("6"),
  SEVEN("7"),
  EIGHT("8"),
  NINE("9"),
  TEN("T"),
  JACK("J"),
  QUEEN("Q"),
  KING("K"),
  ACE("A"),
  ;
  private final String name;

  CardValue(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
