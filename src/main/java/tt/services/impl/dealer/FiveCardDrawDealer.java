package tt.services.impl.dealer;

import tt.models.Hand;
import tt.models.HandType;
import tt.models.PokerDeck;
import tt.services.Dealer;
import tt.services.DeckCreator;
import tt.services.HandTypeAnalyzer;
import tt.util.Tuple;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Deal hands for five card draw poker
 */
public class FiveCardDrawDealer implements Dealer {
  private final DeckCreator creator;
  private final HandTypeAnalyzer analyzer;

  public FiveCardDrawDealer(DeckCreator creator, HandTypeAnalyzer analyzer) {
    this.creator = creator;
    this.analyzer = analyzer;
  }

  @Override
  public List<Hand> deal(int numberOfPlayers) {
    if (numberOfPlayers < 2 || numberOfPlayers > 7) {
      throw new IllegalArgumentException(
          ("Five card draw can only be played by 2 to 7 players! You tried to start a game with " + "%d" + " players!").formatted(
              numberOfPlayers));
    }

    PokerDeck deck = creator.create();
    return IntStream.range(0, numberOfPlayers * 5)
        .mapToObj(idx -> new Tuple<>(idx % numberOfPlayers, deck.drawCard(idx)))
        .collect(Collectors.groupingBy(Tuple::first))
        .values()
        .stream()
        .map(tuples -> {
          var cards = tuples.stream().map(Tuple::second).toList();
          return analyzer.createHand(cards);
        })
        .toList();
  }

  @Override
  public List<Hand> showdown(List<Hand> hands) {
    Map<HandType, List<Hand>> handsByType = hands.stream()
        .collect(Collectors.groupingBy(Hand::getType));

    HandType winningHandType = handsByType.keySet()
        .stream()
        .max(Comparator.comparing(HandType::ordinal))
        .orElseThrow();

    List<Hand> potentialWinners = handsByType.get(winningHandType);
    return tiebreaker(potentialWinners);
  }

  private List<Hand> tiebreaker(List<Hand> potentialWinners) {
    return analyzer.tiebreak(potentialWinners);
  }
}
