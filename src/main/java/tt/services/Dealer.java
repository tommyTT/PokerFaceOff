package tt.services;

import tt.models.Hand;

import java.util.List;

/**
 * Deals a number of poker hands
 */
public interface Dealer {
  /**
   * Deal a hand for every player.
   *
   * @param numberOfPlayers the number of players that should receive cards
   * @return a list of hands for the number of players
   */
  List<Hand> deal(int numberOfPlayers);

  /**
   * Evaluates the given hands and returns the hands that are the winners.
   *
   * @param hands the hands that should be scored
   * @return the hands that are winning
   */
  List<Hand> showdown(List<Hand> hands);
}
