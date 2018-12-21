package ink.czyhandsome.threads.chaptor08;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * 使用Semaphore阻塞的Executor
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class BoundedExecutor {
    private final Semaphore semaphore;
    private final Executor exec;

    public BoundedExecutor(int size, Executor exec) {
        this.semaphore = new Semaphore(size);
        this.exec = exec;
    }

    /**
     * 提交一个任务
     * 若当前正在运行的任务数等于最大数量，则阻塞
     *
     * @param command 任务
     * @throws InterruptedException 阻塞
     */
    public void submitTask(final Runnable command) throws InterruptedException {
        this.semaphore.acquire();
        try {
            exec.execute(() -> {
                try {
                    command.run();
                } finally {
                    this.semaphore.release();
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
        }
    }
}
