package tt.models;

/**
 * Defines the valid types of poker hands that can be ranked. The ranking of the types is
 * represented by the order of the enum values.
 */
public enum HandType {
  HIGH_CARD("High Card"),
  PAIR("Pair"),
  TWO_PAIRS("Two Pairs"),
  THREE_OF_A_KIND("Three of a Kind"),
  STRAIGHT("Straight"),
  FLUSH("Flush"),
  FULL_HOUSE("Full House"),
  FOUR_OF_A_KIND("Four Of A Kind"),
  STRAIGHT_FLUSH("Straight Flush") {
    @Override
    public boolean isSubTypeOf(HandType other) {
      if (other == STRAIGHT || other == FLUSH) {
        return true;
      }
      return super.isSubTypeOf(other);
    }
  },
  ;

  private final String fullName;

  HandType(String fullName) {
    this.fullName = fullName;
  }

  /**
   * Gives the full name for the hand.
   *
   * @return the full name of the hand
   */
  public String getFullName() {
    return fullName;
  }

  public boolean isSubTypeOf(HandType other) {
    return this == other;
  }
}
