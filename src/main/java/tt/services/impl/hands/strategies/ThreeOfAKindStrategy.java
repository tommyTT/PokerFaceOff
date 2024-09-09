package tt.services.impl.hands.strategies;

import tt.models.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static tt.util.CardUtils.groupCardsByValue;

public class ThreeOfAKindStrategy implements tt.services.impl.hands.HandTypeStrategy {
  private record ThreeOfAKindHand(CardValue triple, CardValue extraCard1, CardValue extraCard2) {
    public static Optional<ThreeOfAKindHand> of(Hand hand) {
      if (hand.getType() != HandType.THREE_OF_A_KIND) {
        throw new IllegalArgumentException(
            "this hand is of type " + hand.getType() + " but must be three of a kind!");
      }
      return of(hand.getCards());
    }

    public static Optional<ThreeOfAKindHand> of(List<Card> cards) {
      Map<CardValue, List<Card>> byValue = groupCardsByValue(cards.stream().distinct().toList());

      CardValue triple = null;
      CardValue extraCard1 = null;
      CardValue extraCard2 = null;
      for (Map.Entry<CardValue, List<Card>> entry : byValue.entrySet()) {
        CardValue nextValue = entry.getKey();
        int numberOfCards = entry.getValue().size();
        if (numberOfCards == 3 && triple == null) {
          // the single triple is found
          triple = nextValue;
        } else if (numberOfCards == 1 && (extraCard1 == null || extraCard2 == null)) {
          // an extra card is found
          if (extraCard1 == null) {
            // this is the high card for the moment
            extraCard1 = nextValue;
          } else if (nextValue.compareWith(extraCard1) == ShowdownResult.HIGHER) {
            // a new high card is found
            extraCard2 = extraCard1;
            extraCard1 = nextValue;
          } else {
            // the low card is found
            extraCard2 = nextValue;
          }
        } else {
          // something is wrong
          return Optional.empty();
        }
      }

      if (triple == null || extraCard1 == null || extraCard2 == null) {
        return Optional.empty();
      }

      return Optional.of(new ThreeOfAKindHand(triple, extraCard1, extraCard2));
    }
  }

  @Override
  public boolean matches(List<Card> cards) {
    return ThreeOfAKindHand.of(cards).isPresent();
  }

  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    ThreeOfAKindHand threeOfAKindHand1 = ThreeOfAKindHand.of(hand1)
        .orElseThrow(() -> new IllegalArgumentException("hand 1 is not three of a kind!"));
    ThreeOfAKindHand threeOfAKindHand2 = ThreeOfAKindHand.of(hand2)
        .orElseThrow(() -> new IllegalArgumentException("hand 1 is not three of a kind!"));

    // compare the triple, this must always be the tiebreaker since only 4 cards of that value
    // can be present
    return threeOfAKindHand1.triple().compareWith(threeOfAKindHand2.triple());
  }
}
