package ink.czyhandsome.threads.chaptor08;

import ink.czyhandsome.threads.NotThreadSafe;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 由于线程池数量不足,
 * 若一个任务依赖令一个任务完成,
 * 会导致死锁
 *
 * @author 曹子钰, 2018/12/21 0021
 */
@NotThreadSafe
public class ThreadDeadLock {
    private static ExecutorService exec = Executors.newSingleThreadExecutor();

    @NotThreadSafe
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try {
            Future<String> header;
            AtomicReference<Future<String>> footer = new AtomicReference<>();
            header = exec.submit(() -> {
                footer.set(exec.submit(new LoadFileTask("Bar!")));
                return "Foo!";
            });
            System.out.println(header.get() + footer.get().get());
            new SynchronousQueue<Integer>();
        } finally {
            exec.shutdown();
        }
    }

    public static class LoadFileTask implements Callable<String> {
        private final String info;

        public LoadFileTask(String info) {
            this.info = info;
        }

        @Override
        public String call() throws Exception {
            TimeUnit.SECONDS.sleep(3);
            return info;
        }
    }
}
