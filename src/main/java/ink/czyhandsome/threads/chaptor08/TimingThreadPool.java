package ink.czyhandsome.threads.chaptor08;

import ink.czyhandsome.threads.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 计时的ThreadPoolExecutor
 * 通过hook方法, 监听任务创建前、后
 * 与Executor终止的事件
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class TimingThreadPool extends ThreadPoolExecutor {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TimingThreadPool pool = new TimingThreadPool();
        Future<?> task = pool.submit(() -> {
            try {
                Thread.sleep(2000);
                Logger.getLogger("Fack").info("Jobs done!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        task.get();
        pool.shutdown();
    }

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final Logger log = Logger.getLogger(TimingThreadPool.class);
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public TimingThreadPool() {
        super(10, 25, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        try {
            startTime.set(System.nanoTime());
            log.info(String.format("Thread %s: start %s", t, r));
        } finally {
            super.beforeExecute(t, r);
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            log.info(String.format("Thread %s: end %s, time %dns", t, r, taskTime));
        } finally {
            super.afterExecute(r, t);
        }
    }

    @Override
    protected void terminated() {
        try {
            log.info(String.format("Terminated: avg time=%dns", totalTime.get() / numTasks.get()));
        } finally {
            super.terminated();
        }
    }
}
