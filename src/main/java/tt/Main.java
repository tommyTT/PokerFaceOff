package tt;

import tt.models.Card;
import tt.models.Player;
import tt.models.PokerDeck;
import tt.services.DeckCreator;
import tt.services.impl.dealer.FiveCardDrawDealer;
import tt.services.impl.decks.DefaultShuffledPokerDeckCreator;
import tt.services.impl.decks.UnshuffledPokerDeckCreator;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;
import tt.util.CardUtils;
import tt.util.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
  public static void main(String[] args) {
    System.out.println("""
        🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱
        🂡🂡  Poker Face Off 🂡🂡
        🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱🂱
        """);

    var tuple = parseArguments(args);
    var numberOfPlayers = tuple.first();
    FiveCardDrawDealer dealer = new FiveCardDrawDealer(tuple.second(),
        new DefaultHandTypeAnalyzer());


    // Deal the hands
    List<Player> players = dealer.deal(numberOfPlayers);
    System.out.println("The dealer dealt the following hands:");
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
            """, winners.size() > 1 ? "s are" : " is",
        winners.stream().map(Player::name).collect(Collectors.joining("\n")));
  }

  private static Tuple<Integer, DeckCreator> parseArguments(String[] args) {
    if (args.length == 0) {
      // if no arguments are given, then we play heads up with a shuffled deck
      System.out.println("*the dealer shuffles the deck*");
      return new Tuple<>(2, new DefaultShuffledPokerDeckCreator());
    }

    // first argument must be the number of players
    int numberOfPlayers = Integer.parseInt(args[0]);
    if (args.length == 1) {
      // no more arguments, so we use a shuffled deck
      System.out.println("*the dealer shuffles the deck*");
      return new Tuple<>(numberOfPlayers, new DefaultShuffledPokerDeckCreator());
    }

    // otherwise we create a deck that holds the given cards for each player
    if (numberOfPlayers != args.length - 1) {
      throw new IllegalArgumentException(
          "Number of players %s must match the number of hands given %s".formatted(numberOfPlayers,
              args.length - 1));
    }

    System.out.println("*the dealer uses sleight of hand to use a preconfigured deck from the command line*");
    return new Tuple<>(numberOfPlayers, getDeckCreator(numberOfPlayers, Arrays.copyOfRange(args, 1, args.length)));
  }

  private static DeckCreator getDeckCreator(int numberOfPlayers, String[] args) {
    // determine the specified hands from the inputs
    List<List<Card>> cardsFromArgs = Arrays.stream(args)
        .map(CardUtils::parseCardFromString)
        .toList();

    // collect all the specified cards in an order that allows them to be drawn in the right order
    Set<Card> allCards = new HashSet<>();
    List<Card> cardsInOrder = new ArrayList<>(numberOfPlayers * 5);
    for (int round = 0; round < 5; round++) {
      for (int player = 0; player < numberOfPlayers; player++) {
        List<Card> currentPlayer = cardsFromArgs.get(player);
        if (currentPlayer.size() < round) {
          throw new IllegalArgumentException("invalid number of cards for player " + player);
        }

        Card nextCard = currentPlayer.get(round);
        if (!allCards.add(nextCard)) {
          throw new IllegalArgumentException(
              "duplicate card specified! " + nextCard.toSymbolString());
        }

        cardsInOrder.add(nextCard);
      }
    }

    // insert all missing cards in order at the end, so the list of cards for the deck is complete
    new UnshuffledPokerDeckCreator().create().forEach(card -> {
      if (!allCards.contains(card)) {
        cardsInOrder.add(card);
      }
    });

    // create the deck
    return () -> new PokerDeck(cardsInOrder);
  }
}
