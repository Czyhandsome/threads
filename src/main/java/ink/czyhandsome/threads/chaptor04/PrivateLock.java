package ink.czyhandsome.threads.chaptor04;

import ink.czyhandsome.threads.GuardedBy;
import ink.czyhandsome.threads.ThreadSafe;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
@ThreadSafe
public class PrivateLock {

    private final Object myLock = new Object();

    @GuardedBy("lock")
    private Widget widget;

    void someMethod() {
        synchronized (myLock) {
            // Access or modify the state of widget
        }
    }

    interface Widget {
    }
}
