package chess.internal;

/**
 * A {@code class} denoting an action to execute either synchronously or
 * asynchronously.
 *
 * @see #run(Object)
 * @param <V> the type of {@code ref}.
 */
@FunctionalInterface
public interface Task<V> {

    /**
     * Take any action whatsoever in response to {@code ref}.
     * 
     * @param ref a reference to an object of {@code R}.
     */
    void run(final V ref);
}
