package tt.services.impl.hands.strategies;

import tt.models.Card;
import tt.models.CardValue;
import tt.models.Hand;
import tt.models.ShowdownResult;
import tt.services.impl.hands.HandTypeStrategy;

import java.util.List;

public class StraightStrategy implements HandTypeStrategy {
  @Override
  public boolean matches(List<Card> cards) {
    List<CardValue> sortedValues = cards.stream().map(Card::value).sorted().toList();

    CardValue lastValue = null;
    for (CardValue nextValue : sortedValues) {
      // the next value must be the successor of the
      if (lastValue != null && nextValue.isDirectSuccessorOf(lastValue)) {
        return false;
      }
      lastValue = nextValue;
    }

    return true;
  }

  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    // both straights are compared by their highest card value
    CardValue highestCard1 = hand1.getHighestCardValue();
    CardValue highestCard2 = hand2.getHighestCardValue();
    return highestCard1.compareWith(highestCard2);
  }
}
