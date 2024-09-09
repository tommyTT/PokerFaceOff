package tt.services.impl.hands.strategies;

import tt.models.*;
import tt.services.impl.hands.HandTypeStrategy;

import java.util.*;

import static tt.util.CardUtils.groupCardsByValue;

public class SinglePairStrategy implements HandTypeStrategy {
  private record SinglePairHand(CardValue pair, CardValue extraCard1, CardValue extraCard2,
                                CardValue extraCard3) {
    public static Optional<SinglePairHand> of(Hand hand) {
      if (hand.getType() != HandType.PAIR) {
        throw new IllegalArgumentException(
            "this hand is of type " + hand.getType() + " but must be a pair!");
      }
      return of(hand.getCards());
    }

    public static Optional<SinglePairHand> of(Collection<Card> cards) {
      Map<CardValue, List<Card>> byValue = groupCardsByValue(cards.stream().distinct().toList());

      CardValue thePair = null;
      List<CardValue> extraCards = new ArrayList<>(3);
      for (Map.Entry<CardValue, List<Card>> entry : byValue.entrySet()) {
        int numberOfCards = entry.getValue().size();
        if (numberOfCards == 2 && thePair == null) {
          // this is the pair
          thePair = entry.getKey();
        } else if (numberOfCards == 1 && extraCards.size() < 3) {
          // another single card
          extraCards.add(entry.getKey());
        } else {
          // something is majorly wrong
          return Optional.empty();
        }
      }

      if (thePair == null || extraCards.size() != 3) {
        return Optional.empty();
      }

      // sort the extra cards
      Collections.sort(extraCards);

      // return the new hand and order the extra cards by their value
      return Optional.of(
          new SinglePairHand(thePair, extraCards.get(0), extraCards.get(1), extraCards.get(2)));
    }
  }

  @Override
  public boolean matches(Collection<Card> cards) {
    return SinglePairHand.of(cards).isPresent();
  }


  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    SinglePairHand pairHand1 = SinglePairHand.of(hand1)
        .orElseThrow(() -> new IllegalArgumentException("hand 1 is not a single pair!"));
    SinglePairHand pairHand2 = SinglePairHand.of(hand2)
        .orElseThrow(() -> new IllegalArgumentException("hand 2 is not a single pair!"));

    // first the pair
    ShowdownResult pairResult = pairHand1.pair().compareWith(pairHand2.pair());
    if (pairResult != ShowdownResult.SPLIT) {
      return pairResult;
    }

    // the highest card of the extras
    ShowdownResult extraCard1Result = pairHand1.extraCard1().compareWith(pairHand2.extraCard1());
    if (extraCard1Result != ShowdownResult.SPLIT) {
      return extraCard1Result;
    }

    // the second-highest card of the extras
    ShowdownResult extraCard2Result = pairHand1.extraCard2().compareWith(pairHand2.extraCard2());
    if (extraCard2Result != ShowdownResult.SPLIT) {
      return extraCard2Result;
    }

    // the last card of the extras
    return pairHand1.extraCard3().compareWith(pairHand2.extraCard3());
  }
}
