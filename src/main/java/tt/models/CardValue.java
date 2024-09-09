package tt.models;

import java.util.Arrays;
import java.util.Optional;

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

  /**
   * Return the CardValue that matches the name of the text.
   *
   * @param text must be exactly one token, case-insensitive
   * @return an optional CardValue that matches the name of a card value
   */
  public static Optional<CardValue> of(String text) {
    if (text.isBlank() || text.length() != 1) {
      return Optional.empty();
    }

    return Arrays.stream(values())
        .filter(value -> value.getName().equalsIgnoreCase(text))
        .findFirst();
  }

  public String getName() {
    return name;
  }

  /**
   * Determines the showdown result of this value compared to the given card value
   *
   * @param other the other value
   * @return the result of the comparison
   */
  public ShowdownResult compareWith(CardValue other) {
    int compare = this.compareTo(other);
    if (compare == 0) {
      return ShowdownResult.SPLIT;
    } else if (compare < 0) {
      return ShowdownResult.LOWER;
    } else {
      return ShowdownResult.HIGHER;
    }
  }

  /**
   * Determines if this value is the direct successor of the other value.
   *
   * @param other the other value
   * @return true if this value is directly succeeding the given value
   */
  public boolean isDirectSuccessorOf(CardValue other) {
    // determine by the ordinal
    return other.ordinal() == ordinal() - 1;
  }

  /**
   * Determines if this value is the direct predecessor of the other value.
   *
   * @param other the other value
   * @return true if this value is directly preceding the given value
   */
  public boolean isDirectPredecessorOf(CardValue other) {
    // determine by the ordinal
    return other.ordinal() == ordinal() + 1;
  }
}
