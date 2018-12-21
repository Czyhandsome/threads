package ink.czyhandsome.threads.chaptor07;

import ink.czyhandsome.threads.GuardedBy;
import ink.czyhandsome.threads.NotPerfect;

import java.io.PrintWriter;
import java.util.concurrent.*;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/20 0020
 */
public class LogWriterBundle {
    public interface Logger {
        void log(String message) throws Exception;
    }

    public static abstract class LogWriter implements Logger {
        protected final BlockingQueue<String> queue = new LinkedBlockingDeque<>(20);
        protected final LoggingThread logger = new LoggingThread();

        public void start() {
            logger.start();
        }

        @Override
        public abstract void log(String message) throws InterruptedException;

        private class LoggingThread extends Thread {
            private final PrintWriter writer = new PrintWriter(System.out);

            @Override
            public void run() {
                try {
                    while (true) {
                        writer.println(queue.take());
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    writer.close();
                }
            }
        }
    }

    @NotPerfect
    public static class FlagLogWriter extends LogWriter {
        private volatile boolean shutdownRequested = false;

        @Override
        public void log(String message) throws InterruptedException {
            if (!shutdownRequested) {
                queue.put(message);
            } else {
                throw new IllegalStateException("Logging is shut down!");
            }
        }
    }

    public static class SaferLogService implements Logger {
        private final BlockingQueue<String> queue = new LinkedBlockingDeque<>(20);
        private final LoggingThread loggerThread = new LoggingThread();
        private final PrintWriter writer = new PrintWriter(System.out);
        @GuardedBy("this")
        private boolean isShutdown = false;
        @GuardedBy("this")
        private int reservations = 0;

        public void start() {
            loggerThread.start();
        }

        public void stop() {
            synchronized (this) {
                isShutdown = true;
            }
            loggerThread.interrupt();
        }

        @Override
        public void log(String message) throws InterruptedException {
            synchronized (this) {
                if (isShutdown) {
                    throw new IllegalStateException();
                }
                ++reservations;
            }
            queue.put(message);
        }

        private class LoggingThread extends Thread {
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            synchronized (this) {
                                if (isShutdown && reservations == 0) {
                                    break;
                                }
                            }
                            String msg = queue.take();
                            synchronized (this) {
                                --reservations;
                            }
                            writer.println(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    writer.close();
                }
            }
        }
    }

    public static class LogService implements Logger {
        private final ExecutorService exec = Executors.newSingleThreadExecutor();
        private final PrintWriter writer = new PrintWriter(System.out);

        public void start() {
        }

        public void stop() throws InterruptedException {
            try {
                exec.shutdown();
                exec.awaitTermination(10, TimeUnit.SECONDS);
            } finally {
                writer.close();
            }
        }

        @Override
        public void log(String msg) {
            try {
                exec.execute(new WriterTask(msg));
            } catch (RejectedExecutionException ignored) {
            }
        }

        private class WriterTask implements Runnable {
            private final String msg;

            private WriterTask(String msg) {
                this.msg = msg;
            }

            @Override
            public void run() {
                writer.println(msg);
            }
        }
    }
}
