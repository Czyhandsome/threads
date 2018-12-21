package ink.czyhandsome.threads.chaptor07;

import ink.czyhandsome.threads.GuardedBy;
import ink.czyhandsome.threads.ThreadSafe;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/20 0020
 */
public class TaskCancelBundle {

    public interface CancellableTask<V> extends Callable<V> {
        void cancel();

        RunnableFuture<V> newTask();
    }

    @ThreadSafe
    public static class CancellingExecutor extends ThreadPoolExecutor {

        public CancellingExecutor(int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
            if (callable instanceof CancellableTask) {
                return ((CancellableTask<T>) callable).newTask();
            } else {
                return super.newTaskFor(callable);
            }
        }
    }

    public abstract class SocketUsingTask<T> implements CancellableTask<T> {
        @GuardedBy("this")
        private Socket socket;

        protected synchronized void setSocket(Socket socket) {
            this.socket = socket;
        }

        @Override
        public synchronized void cancel() {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
        }

        @Override
        public RunnableFuture<T> newTask() {
            return new FutureTask<T>(this) {
                @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    boolean cancel;
                    try {
                        SocketUsingTask.this.cancel();
                    } finally {
                        cancel = super.cancel(mayInterruptIfRunning);
                    }
                    return cancel;
                }
            };
        }
    }
}
