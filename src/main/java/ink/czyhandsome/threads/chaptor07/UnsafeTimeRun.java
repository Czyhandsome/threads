package ink.czyhandsome.threads.chaptor07;

import ink.czyhandsome.threads.Unsafe;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/19 0019
 */
@Unsafe
public class UnsafeTimeRun {
    private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(5);

    @Unsafe
    public static void timedRun(Runnable runnable,
                                long timeout, TimeUnit unit) {
        final Thread taskThread = Thread.currentThread();
        exec.schedule(taskThread::interrupt, timeout, unit);
        runnable.run();
    }
}
