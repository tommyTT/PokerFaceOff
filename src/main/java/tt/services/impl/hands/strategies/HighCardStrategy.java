package tt.services.impl.hands.strategies;

import tt.models.*;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;
import tt.services.impl.hands.HandTypeStrategy;
import tt.util.CardUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * High Card strategy.
 */
public class HighCardStrategy implements HandTypeStrategy {

  private final DefaultHandTypeAnalyzer analyzer;

  public HighCardStrategy(DefaultHandTypeAnalyzer analyzer) {
    this.analyzer = analyzer;
  }

  @Override
  public boolean matches(Collection<Card> cards) {
    // there must be 5 cards in the deck
    if (cards.size() != 5) {
      return false;
    }

    // there can be no duplicates, otherwise it is at least a pair
    Map<CardValue, List<Card>> byValue = CardUtils.groupCardsByValue(cards);
    if (byValue.size() != cards.size()) {
      return false;
    }

    // can't be a flush
    if (analyzer.isType(HandType.FLUSH, cards)) {
      return false;
    }

    // and it can't be a straight
    return !analyzer.isType(HandType.STRAIGHT, cards);
  }

  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    if (hand1.getType() != hand2.getType()) {
      throw new IllegalArgumentException("both hands must be of equal types!");
    }

    List<CardValue> valuesOfHand1 = hand1.getCards()
        .stream()
        .map(Card::value)
        .sorted(Comparator.reverseOrder())
        .toList();
    List<CardValue> valuesOfHand2 = hand2.getCards()
        .stream()
        .map(Card::value)
        .sorted(Comparator.reverseOrder())
        .toList();
    if (valuesOfHand1.size() != valuesOfHand2.size()) {
      throw new IllegalArgumentException("Hands have different sizes of card values!");
    }

    // determine the tiebreaker by comparing each value until there is not a split
    return IntStream.range(0, valuesOfHand1.size())
        .mapToObj(i -> valuesOfHand1.get(i).compareWith(valuesOfHand2.get(i)))
        .filter(resultOfCurrentCardValues -> resultOfCurrentCardValues != ShowdownResult.SPLIT)
        .findFirst()
        .orElse(ShowdownResult.SPLIT);
  }
}
