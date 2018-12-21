package ink.czyhandsome.threads.chaptor03;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
public class UnSafePublication {

    public Holder holder;

    public void initialize() {
        holder = new Holder(42);
    }

    public static class Holder {
        private int n;

        public Holder(int n) {
            this.n = n;
        }

        public void assertSanity() {
            if (n != n) {
                throw new AssertionError("This statement is false");
            }
        }
    }
}
