package ink.czyhandsome.threads.chaptor07;

import ink.czyhandsome.threads.NotPerfect;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/19 0019
 */
@NotPerfect
public class RethrowableTimedRun {
    private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(5);

    @NotPerfect
    public static void timedRun(Runnable runnable,
                                long timeout, TimeUnit unit) throws InterruptedException {
        class Rethrowable implements Runnable {
            private volatile Throwable t;

            public void run() {
                try {
                    runnable.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            private void rethrow() {
                if (t != null) {
                    throw new RuntimeException(t);
                }
            }
        }
        Rethrowable task = new Rethrowable();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        exec.schedule(taskThread::interrupt, timeout, unit);
        taskThread.join(unit.toMillis(timeout));
        task.rethrow();
    }
}
