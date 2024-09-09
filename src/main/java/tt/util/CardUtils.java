package tt.util;

import tt.models.Card;
import tt.models.CardSuit;
import tt.models.CardValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains helper methods that can be used to handle cards.
 */
public class CardUtils {
  private CardUtils() {
    // no instance
  }

  /**
   * Group the cards by their value.
   *
   * @param cards the cards
   * @return a map of the cards grouped by their value
   */
  public static Map<CardValue, List<Card>> groupCardsByValue(List<Card> cards) {
    return cards.stream().collect(Collectors.groupingBy(Card::value));
  }

  /**
   * Group the cards by their suit.
   *
   * @param cards the cards
   * @return a map of the cards grouped by their suit
   */
  public static Map<CardSuit, List<Card>> groupCardsBySuit(List<Card> cards) {
    return cards.stream().collect(Collectors.groupingBy(Card::suit));
  }

  /**
   * Parse the given text into a list of cards. if there
   *
   * @param text the text
   * @return the list of cards
   * @throws IllegalArgumentException if a part of the text couldn't be parsed
   */
  public static List<Card> parseCardFromString(String text) {
    if (text == null || text.isEmpty()) {
      return Collections.emptyList();
    }

    return Arrays.stream(text.split("[\\s,;]+"))
        .map(token -> Card.of(token)
            .orElseThrow(() -> new IllegalArgumentException("couldn't parse input text " + text)))
        .toList();
  }
}
