package tt.services.impl.hands;

import tt.models.Card;
import tt.models.Hand;
import tt.models.HandType;
import tt.models.ShowdownResult;
import tt.services.HandTypeAnalyzer;
import tt.services.impl.hands.strategies.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * Abstract implementation for all strategies that analyze hands.
 */
public class DefaultHandTypeAnalyzer implements HandTypeAnalyzer {

  private final Map<HandType, HandTypeStrategy> strategies;

  public DefaultHandTypeAnalyzer() {
    this.strategies = initStrategies();
  }

  private Map<HandType, HandTypeStrategy> initStrategies() {
    Map<HandType, HandTypeStrategy> strategies = new HashMap<>();

    // Add all the strategies to the map
    strategies.put(HandType.HIGH_CARD, new HighCardStrategy(this));
    strategies.put(HandType.PAIR, new SinglePairStrategy());
    strategies.put(HandType.TWO_PAIRS, new TwoPairStrategy());
    strategies.put(HandType.THREE_OF_A_KIND, new ThreeOfAKindStrategy());
    strategies.put(HandType.STRAIGHT, new StraightStrategy());
    strategies.put(HandType.FLUSH, new FlushStrategy(this));
    strategies.put(HandType.FULL_HOUSE, new FullHouseStrategy());
    strategies.put(HandType.FOUR_OF_A_KIND, new FourOfAKindStrategy());
    strategies.put(HandType.STRAIGHT_FLUSH, new StraightFlushStrategy(this));

    return strategies;
  }

  @Override
  public Hand createHand(List<Card> cards) {
    // determine the type from highest to lowest and match it by the strategy
    return Hand.of(getHandType(cards), cards);
  }

  private HandType getHandType(List<Card> cards) {
    return Arrays.asList(HandType.values()).reversed().stream().filter(type -> {
      HandTypeStrategy strategy = strategies.get(type);
      return strategy.matches(cards);
    }).findFirst().orElseThrow();
  }

  @Override
  public List<Hand> tiebreak(List<Hand> hands) {
    if (hands.size() <= 1) {
      // none or one hand, so all hands win
      return hands;
    } else if (hands.stream().map(Hand::getType).distinct().count() != 1) {
      throw new IllegalArgumentException("only hands of a single type can be tiebroken");
    }

    // do the tiebreaking procedure
    return hands.stream()
        .map(List::of)
        .reduce(Collections.emptyList(), (winningHands, nextHands) -> {
          // currently no hand is winning
          if (winningHands.isEmpty()) {
            return nextHands;
          } else {
            // compare the currently winning hand with the next hand
            return switch (tiebreak(nextHands.getFirst(), winningHands.getLast())) {
              case LOWER ->
                // nothing to do since the current hand still has the tiebreaker
                  winningHands;
              case HIGHER ->
                // the current hand wins over the previous hands and therefore is the new winner
                  nextHands;
              case SPLIT ->
                // both hands are of equal strength and are the current winners
                  Stream.concat(winningHands.stream(), nextHands.stream()).toList();
            };
          }
        });
  }

  /**
   * Determine if the type matches the cards.
   *
   * @param handType the specific type that should be checked
   * @param cards    the cards that form the hand
   * @return true if the cards match the type of the hand
   */
  public boolean isType(HandType handType, List<Card> cards) {
    return strategies.get(handType).matches(cards);
  }

  /**
   * Determine the result of the tiebreaker of the two hands.
   *
   * @param hand1 the first hand
   * @param hand2 the second hand
   * @return the result of the showdown
   */
  private ShowdownResult tiebreak(Hand hand1, Hand hand2) {
    HandType type = hand1.getType();
    return tiebreak(type, hand1, hand2);
  }

  public ShowdownResult tiebreak(HandType type, Hand hand1, Hand hand2) {
    HandTypeStrategy strategy = strategies.get(type);
    return strategy.determineTiebreakResult(hand1, hand2);
  }

}
