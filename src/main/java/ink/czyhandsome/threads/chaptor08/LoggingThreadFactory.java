package ink.czyhandsome.threads.chaptor08;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 方便日志与调试的ThreadFactory
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class LoggingThreadFactory implements ThreadFactory {
    private final String poolName;

    public LoggingThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new LoggingThread(r, poolName);
    }

    /**
     * Runnable包装类, 用于整理日志信息
     */
    public static class LoggingThread extends Thread {
        private static AtomicInteger created = new AtomicInteger();
        private static AtomicInteger alive = new AtomicInteger();
        private final Runnable runnable;
        private static volatile boolean DEBUG = false;

        public LoggingThread(Runnable runnable, String name) {
            this.runnable = runnable;
            setName(name + "-" + created.incrementAndGet());
            setDefaultUncaughtExceptionHandler((t, e) -> {
                System.out.println("Error from: " + t.getName());
                e.printStackTrace();
            });
        }

        @Override
        public void run() {
            boolean debug = DEBUG;
            if (debug) {
                System.out.println("Created: " + getName());
            }
            try {
                alive.incrementAndGet();
                this.runnable.run();
            } finally {
                alive.decrementAndGet();
                if (debug) {
                    System.out.println("Exiting: " + getName());
                }
            }
        }

        public static int getThreadsCreated() {
            return created.get();
        }

        public static int getThreadsAlive() {
            return alive.get();
        }

        public static boolean getDebug() {
            return DEBUG;
        }

        public void enableDebug() {
            DEBUG = true;
        }
    }
}
