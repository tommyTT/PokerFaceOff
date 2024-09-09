package tt.services.impl.hands.strategies;

import tt.models.*;
import tt.services.impl.hands.HandTypeStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static tt.util.CardUtils.groupCardsByValue;

public class FullHouseStrategy implements HandTypeStrategy {

  private record FullHouseHand(CardValue triple, CardValue pair) {
    public static Optional<FullHouseHand> of(Hand hand) {
      if (hand.getType() != HandType.FULL_HOUSE) {
        throw new IllegalArgumentException(
            "this hand is of type " + hand.getType() + " but must be a full house!");
      }

      return of(hand.getCards());
    }

    public static Optional<FullHouseHand> of(List<Card> cards) {
      Map<CardValue, List<Card>> byValue = groupCardsByValue(cards);
      if (byValue.size() != 2) {
        return Optional.empty();
      }

      CardValue triple = null;
      CardValue pair = null;
      for (Map.Entry<CardValue, List<Card>> entry : byValue.entrySet()) {
        CardValue value = entry.getKey();
        int numberOfCards = entry.getValue().size();
        if (numberOfCards == 3 && triple == null) {
          // the triple
          triple = value;
        } else if (numberOfCards == 2 && pair == null) {
          // this is the pair
          pair = value;
        } else {
          // something is seriously wrong
          return Optional.empty();
        }
      }

      if (triple == null || pair == null) {
        return Optional.empty();
      }

      return Optional.of(new FullHouseHand(triple, pair));
    }
  }

  @Override
  public boolean matches(List<Card> cards) {
    return FullHouseHand.of(cards).isPresent();
  }

  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    // the value of the triple is always the tiebreaker
    FullHouseHand fullHouseHand1 = FullHouseHand.of(hand1)
        .orElseThrow(() -> new IllegalArgumentException("hand 1 is not full house!"));
    FullHouseHand fullHouseHand2 = FullHouseHand.of(hand2)
        .orElseThrow(() -> new IllegalArgumentException("hand 2 is not full house!"));
    return fullHouseHand1.triple().compareWith(fullHouseHand2.triple());
  }
}
