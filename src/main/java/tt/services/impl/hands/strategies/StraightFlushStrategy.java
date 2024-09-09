package tt.services.impl.hands.strategies;

import tt.models.Card;
import tt.models.Hand;
import tt.models.HandType;
import tt.models.ShowdownResult;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;
import tt.services.impl.hands.HandTypeStrategy;

import java.util.List;

public class StraightFlushStrategy implements HandTypeStrategy {
  private final DefaultHandTypeAnalyzer analyzer;

  public StraightFlushStrategy(DefaultHandTypeAnalyzer analyzer) {
    this.analyzer = analyzer;
  }

  @Override
  public boolean matches(List<Card> cards) {
    // must match both the conditions of the flush and a straight
    return analyzer.isType(HandType.FLUSH, cards) && analyzer.isType(HandType.STRAIGHT, cards);
  }

  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    // the same tiebreaker like the straight applies here
    return analyzer.tiebreak(HandType.STRAIGHT, hand1, hand2);
  }
}
