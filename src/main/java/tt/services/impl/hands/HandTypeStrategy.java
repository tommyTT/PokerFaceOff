package tt.services.impl.hands;

import tt.models.Card;
import tt.models.Hand;
import tt.models.ShowdownResult;

import java.util.Collection;
import java.util.List;

public interface HandTypeStrategy {
  boolean matches(Collection<Card> cards);

  ShowdownResult determineTiebreakResult(Hand hand1, Hand hand2);
}
