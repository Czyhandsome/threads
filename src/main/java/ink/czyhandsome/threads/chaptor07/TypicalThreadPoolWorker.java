package ink.czyhandsome.threads.chaptor07;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public abstract class TypicalThreadPoolWorker extends Thread {
    protected abstract Runnable getTaskFromWorkQueue() throws Exception;

    public void run() {
        Throwable throwable = null;
        try {
            while (!isInterrupted()) {
                getTaskFromWorkQueue().run();
            }
        } catch (Throwable t) {
            throwable = t;
        } finally {
            threadExit(this, throwable);
        }
    }

    protected abstract void threadExit(Runnable command, Throwable t);
}
