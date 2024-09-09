package tt.services.impl.hands.strategies;

import tt.models.*;
import tt.services.impl.hands.HandTypeStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static tt.util.CardUtils.groupCardsByValue;

public class FourOfAKindStrategy implements HandTypeStrategy {
  private record FourOfAKindHand(CardValue fourValue, CardValue extraCard) {
    public static Optional<FourOfAKindHand> of(Hand hand) {
      if (hand.getType() != HandType.FOUR_OF_A_KIND) {
        throw new IllegalArgumentException(
            "this hand is of type " + hand.getType() + " but must be four of a kind!");
      }

      return of(hand.getCards());
    }

    private static Optional<FourOfAKindHand> of(List<Card> cards) {
      Map<CardValue, List<Card>> byValue = groupCardsByValue(cards);
      if (byValue.size() != 2) {
        // must have exactly two distinct values, otherwise this can't be four of a kind
        return Optional.empty();
      }

      CardValue four = null;
      CardValue extraCard = null;
      for (Map.Entry<CardValue, List<Card>> entry : byValue.entrySet()) {
        CardValue value = entry.getKey();
        int numberOfCards = entry.getValue().size();
        if (numberOfCards == 4 && four == null) {
          // the four cards
          four = value;
        } else if (numberOfCards == 1 && extraCard == null) {
          // this is the extra card
          extraCard = value;
        } else {
          // something is seriously wrong
          return Optional.empty();
        }
      }

      if (four == null || extraCard == null) {
        return Optional.empty();
      }

      return Optional.of(new FourOfAKindHand(four, extraCard));
    }
  }

  @Override
  public boolean matches(List<Card> cards) {
    return FourOfAKindHand.of(cards).isPresent();
  }

  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    // the value of the 4 cards is the tiebreaker
    FourOfAKindHand fours1 = FourOfAKindHand.of(hand1)
        .orElseThrow(() -> new IllegalArgumentException("hand 1 is not four of a kind!"));
    FourOfAKindHand fours2 = FourOfAKindHand.of(hand2)
        .orElseThrow(() -> new IllegalArgumentException("hand 2 is not four of a kind!"));
    return fours1.fourValue().compareWith(fours2.fourValue());
  }
}
