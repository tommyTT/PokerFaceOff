package tt.services.impl.hands.strategies;

import tt.models.*;
import tt.services.impl.hands.HandTypeStrategy;

import java.util.List;
import java.util.Map;

import static tt.util.CardUtils.groupCardsByValue;

public class TwoPairStrategy implements HandTypeStrategy {
  private record TwoPairHand(CardValue highPair, CardValue lowPair, CardValue extraCard) {
    public static TwoPairHand of(Hand hand) {
      if (hand.getType() != HandType.TWO_PAIRS) {
        throw new IllegalArgumentException(
            "this hand is of type " + hand.getType() + " but must be two pairs!");
      }
      Map<CardValue, List<Card>> byValue = groupCardsByValue(hand.getCards());

      CardValue highPair = null;
      CardValue lowPair = null;
      CardValue extraCard = null;
      for (Map.Entry<CardValue, List<Card>> entry : byValue.entrySet()) {
        int numberOfCards = entry.getValue().size();
        if (numberOfCards == 2) {
          // exactly two cards of this value means this is a pair
          var pairValue = entry.getKey();
          if (highPair == null) {
            // no high pair yet
            highPair = pairValue;
          } else if (highPair.compareWith(pairValue) == ShowdownResult.LOWER) {
            // high pair is already set and is lower than the current pair, so invert the order
            lowPair = highPair;
            highPair = pairValue;
          } else {
            // otherwise we found the lower pair
            lowPair = pairValue;
          }
        } else if (numberOfCards == 1 && extraCard == null) {
          // this must be the single card
          extraCard = entry.getKey();
        } else {
          // something is majorly wrong
          throw new IllegalArgumentException("illegal cards in two pair hand!");
        }
      }

      if (highPair == null) {
        throw new IllegalArgumentException("no high pair found in hand!");
      } else if (lowPair == null) {
        throw new IllegalArgumentException("no low pair found in hand!");
      } else if (extraCard == null) {
        throw new IllegalArgumentException("no high card found in hand!");
      }

      // return the new hand
      return new TwoPairHand(highPair, lowPair, extraCard);
    }
  }

  @Override
  public boolean matches(List<Card> cards) {
    // group all cards by their value and count the number of cards
    List<Integer> valueCounts = groupCardsByValue(cards).values().stream().map(List::size).toList();

    // there must now be exactly 3 different distinct card values to be a hand with two pairs
    return valueCounts.size() != 3;
  }

  @Override
  public ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2) {
    TwoPairHand twoPairHand1 = TwoPairHand.of(hand1);
    TwoPairHand twoPairHand2 = TwoPairHand.of(hand2);

    // compare the first pairs
    ShowdownResult resultHigherPair = twoPairHand1.highPair().compareWith(twoPairHand2.highPair());
    if (resultHigherPair != ShowdownResult.SPLIT) {
      return resultHigherPair;
    }

    // then compare the second pairs
    ShowdownResult resultLowerPair = twoPairHand1.lowPair().compareWith(twoPairHand2.lowPair());
    if (resultLowerPair != ShowdownResult.SPLIT) {
      return resultLowerPair;
    }

    // otherwise there is high carding involved
    return twoPairHand1.extraCard().compareWith(twoPairHand2.extraCard());
  }
}
