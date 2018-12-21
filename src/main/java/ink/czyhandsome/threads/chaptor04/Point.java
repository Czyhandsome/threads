package ink.czyhandsome.threads.chaptor04;

import ink.czyhandsome.threads.GuardedBy;
import ink.czyhandsome.threads.NotThreadSafe;
import ink.czyhandsome.threads.ThreadSafe;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
public interface Point {

    int getX();

    int getY();

    @NotThreadSafe
    class MutablePoint implements Point {
        public int x, y;

        public MutablePoint() {
            this.x = 0;
            this.y = 0;
        }

        public MutablePoint(MutablePoint p) {
            this.x = p.x;
            this.y = p.y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }
    }

    @ThreadSafe
    class ImmutablePoint implements Point {
        public final int x, y;

        public ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }
    }

    @ThreadSafe
    class SafePoint implements Point {

        @GuardedBy("this")
        private int x, y;

        private SafePoint(int[] a) {
            this(a[0], a[1]);
        }

        public SafePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public synchronized int[] get() {
            return new int[]{x, y};
        }

        public synchronized void set(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }
    }
}
