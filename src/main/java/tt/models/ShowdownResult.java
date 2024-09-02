package tt.models;

/**
 * Defines the available outcomes for the showdown of two poker hands.
 */
public enum ShowdownResult {
  /**
   * Denotes that one hand is ranked higher than the other one and therefore wins the showdown.
   */
  HIGHER,
  /**
   * Denotes that one hand is ranked lower than the other one and therefore loses the showdown.
   */
  LOWER,
  /**
   * Denotes that both hands are equal and therefore the showdown results in a split pot between
   * the players.
   */
  SPLIT
}
