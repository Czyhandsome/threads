package ink.czyhandsome.threads.chaptor04;

import ink.czyhandsome.threads.GuardedBy;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
public class Counter {
    @GuardedBy("this")
    private long value = 0;

    public synchronized long getValue() {
        return value;
    }

    public synchronized long increment() {
        if (value == Long.MAX_VALUE) {
            throw new IllegalStateException("Counter Overflow!");
        }
        return ++value;
    }
}
