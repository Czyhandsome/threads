package ink.czyhandsome.threads.chaptor07;

import java.util.concurrent.*;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/20 0020
 */
public class FutureTimedRun {
    private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(5);

    public static void timedRun(Runnable r,
                                long timeout, TimeUnit unit) throws InterruptedException {
        Future<?> task = exec.submit(r);
        try {
            task.get(timeout, unit);
        } catch (TimeoutException e) {
            System.out.println("Cancelled by timeout!");
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            task.cancel(true);
        }
    }
}
