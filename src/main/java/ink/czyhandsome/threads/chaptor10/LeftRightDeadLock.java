package ink.czyhandsome.threads.chaptor10;

import ink.czyhandsome.threads.DeadLock;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/24 0024
 */
@DeadLock
public class LeftRightDeadLock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight() {
        synchronized (left) {
            synchronized (right) {
                System.out.println("Foo!");
            }
        }
    }

    public void rightLeft() {
        synchronized (right) {
            synchronized (left) {
                System.out.println("Bar!");
            }
        }
    }
}
