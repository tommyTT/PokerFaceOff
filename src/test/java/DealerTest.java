import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tt.models.Hand;
import tt.services.impl.dealer.FiveCardDrawDealer;
import tt.services.impl.decks.SeededShuffledPokerDeckCreator;
import tt.services.impl.hands.DefaultHandTypeAnalyzer;

import java.util.List;

public class DealerTest {
  @Test
  public void testDealAHandOfFiveCardDrawWith2Players() {
    var deckCreator = new SeededShuffledPokerDeckCreator(42);
    var analyzer = new DefaultHandTypeAnalyzer();
    var dealer = new FiveCardDrawDealer(deckCreator, analyzer);

    List<Hand> hands = dealer.deal(2);
    Assertions.assertEquals(2, hands.size(), "must return 2 hands of poker");

    Hand handOne = hands.get(0);
    Assertions.assertNotNull(handOne, "first hand can't be null");

    Hand handTwo = hands.get(1);
    Assertions.assertNotNull(handTwo, "second hand can't be null");

    Assertions.assertNotEquals(handTwo, handOne, "the two hands are not allowed to be equal");
  }
}
