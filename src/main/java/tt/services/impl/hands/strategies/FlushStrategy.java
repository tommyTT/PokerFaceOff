package tt.services.impl.hands.strategies;

import tt.models.*;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;
import tt.services.impl.hands.HandTypeStrategy;
import tt.util.CardUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FlushStrategy implements HandTypeStrategy {
  private final DefaultHandTypeAnalyzer analyzer;

  public FlushStrategy(DefaultHandTypeAnalyzer analyzer) {
    this.analyzer = analyzer;
  }

  @Override
  public boolean matches(Collection<Card> cards) {
    // there must be 5 cards in the deck
    if (cards.size() != 5) {
      return false;
    }

    // All the cards must have the same suit
    Map<CardSuit, List<Card>> bySuit = CardUtils.groupCardsBySuit(cards);
    return bySuit.size() == 1;
  }

  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    // since there are two flushes, apply the tiebreak rules for high carding
    return analyzer.tiebreak(HandType.HIGH_CARD, hand1, hand2);
  }
}
