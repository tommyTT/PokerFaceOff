package tt;

import tt.models.Player;
import tt.services.DeckCreator;
import tt.services.impl.dealer.FiveCardDrawDealer;
import tt.services.impl.decks.DefaultShuffledPokerDeckCreator;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
  public static void main(String[] args) {
    FiveCardDrawDealer dealer = new FiveCardDrawDealer(getDeckCreator(args),
        new DefaultHandTypeAnalyzer());

    var numberOfPlayers = 7;

    List<Player> players = dealer.deal(numberOfPlayers);

    System.out.println("""
        🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱
        🂡🂡  Poker Face Off 🂡🂡
        🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱
        """);
    for (int i = 1; i <= numberOfPlayers; i++) {
      Player player = players.get(i - 1);
      System.out.printf("%s \t → \t %s \t → \t%s%n", player.name(), player.hand().toPrettyString(),
          player.hand().getType().getFullName());
    }

    List<Player> winners = dealer.showdown(players);
    System.out.printf("""
        
        ********************
        🏆 Winner%s:
        %s
        """,
        winners.size() > 1 ? "s are" : " is",
        winners.stream().map(Player::name).collect(Collectors.joining("\n")));
  }

  private static DeckCreator getDeckCreator(String[] args) {
    // fixme tt accept arguments
    return new DefaultShuffledPokerDeckCreator();
  }
}
