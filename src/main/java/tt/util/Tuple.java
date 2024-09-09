package tt.util;

/**
 * Just a basic 2-tuple helper class.
 *
 * @param first the first value
 * @param second the second value
 * @param <T> type of the first value
 * @param <S> type of the second value
 */
public record Tuple<T, S>(T first, S second) {
}
