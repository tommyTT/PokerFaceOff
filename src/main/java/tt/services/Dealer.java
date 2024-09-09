package tt.services;

import tt.models.Hand;
import tt.models.Player;

import java.util.List;

/**
 * Deals a number of poker hands
 */
public interface Dealer {
  /**
   * Deal a hand for every player.
   *
   * @param numberOfPlayers the number of players that should receive cards
   * @return a list of players and their hands for the number of players
   */
  List<Player> deal(int numberOfPlayers);

  /**
   * Evaluates the given hands and returns the hands that are the winners.
   *
   * @param players the players and their hands that should be scored
   * @return the player hands that are winning
   */
  List<Player> showdown(List<Player> players);
}
