package tt.models;

/**
 * Defines a hand of 5 card for poker. The order of the cards is irrelevant!
 *
 * @param card1 the first card
 * @param card2 the second card
 * @param card3 the third card
 * @param card4 the fourth card
 * @param card5 the fifth card
 */
public record Hand(Card card1, Card card2, Card card3, Card card4, Card card5) {
}
