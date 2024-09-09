package tt.services;

import tt.models.Card;
import tt.models.Hand;
import tt.models.HandType;

import java.util.List;

public interface HandTypeAnalyzer {
  /**
   * Create a new hand from the given cards.
   *
   * @param cards the cards for the hand
   * @return the new hand of poker
   */
  Hand createHand(List<Card> cards);

  /**
   * Determine the tiebreaker between the given hands of the type.
   *
   * @param hands the hands to tiebreak
   * @return the list of hands that are winning the tiebreaker
   */
  List<Hand> tiebreak(List<Hand> hands);
}
