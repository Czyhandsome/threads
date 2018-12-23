package ink.czyhandsome.threads.chaptor08;

import ink.czyhandsome.threads.GuardedBy;
import ink.czyhandsome.threads.ThreadSafe;

import java.util.concurrent.CountDownLatch;

/**
 * $DESC
 *
 * @author 曹子钰, 2018-12-22
 */
@ThreadSafe
public class ValueLatch<T> {
    @GuardedBy("this")
    private T value;

    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return (done.getCount() == 0);
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this) {
            return value;
        }
    }
}
