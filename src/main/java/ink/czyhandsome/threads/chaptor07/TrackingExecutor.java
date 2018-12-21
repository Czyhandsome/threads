package ink.czyhandsome.threads.chaptor07;

import java.util.*;
import java.util.concurrent.*;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class TrackingExecutor extends AbstractExecutorService {
    private final ExecutorService exec;
    private final Set<Runnable> taskCancelledAtShutdown = Collections.synchronizedSet(new HashSet<>());

    public TrackingExecutor(ExecutorService exec) {
        this.exec = exec;
    }

    // List all tasks get cancelled when terminated
    public List<Runnable> getCancelledTasks() {
        if (!exec.isTerminated()) {
            throw new IllegalStateException();
        }
        return new ArrayList<>(taskCancelledAtShutdown);
    }

    @Override
    public void execute(Runnable command) {
        exec.execute(wrappedCommand(command));
    }

    private Runnable wrappedCommand(Runnable command) {
        return () -> {
            try {
                command.run();
            } finally {
                if (isShutdown() && Thread.currentThread().isInterrupted()) {
                    taskCancelledAtShutdown.add(command);
                }
            }
        };
    }

    @Override
    public Future<?> submit(Runnable task) {
        return exec.submit(wrappedCommand(task));
    }

    @Override
    public void shutdown() {
        exec.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return exec.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return exec.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return exec.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return exec.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return exec.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return exec.submit(task, result);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return exec.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                         long timeout,
                                         TimeUnit unit) throws InterruptedException {
        return exec.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return exec.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                           long timeout,
                           TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return exec.invokeAny(tasks, timeout, unit);
    }
}
