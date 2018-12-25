package ink.czyhandsome.threads.chaptor10;

import ink.czyhandsome.threads.DeadLock;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/24 0024
 */
@DeadLock
public class DynamicOrderDeadLock {
    @DeadLock
    public void transferMoney(final Account from,
                              final Account to,
                              DollarAmount amount) throws InsufficientFundsException {
        synchronized (from) {
            synchronized (to) {
                if (from.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException();
                } else {
                    from.debit(amount);
                    from.credit(amount);
                }
            }
        }
    }

    interface Account {
        DollarAmount getBalance();

        void debit(DollarAmount amount);

        void credit(DollarAmount amount);
    }

    interface DollarAmount extends Comparable<DollarAmount> {
    }

    public static class InsufficientFundsException extends Exception {
    }
}
