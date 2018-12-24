package ink.czyhandsome.threads.chaptor09;

import java.util.concurrent.*;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/24 0024
 */
public abstract class BackgroundTask<V> implements Runnable, Future<V> {
    private final FutureTask<V> computation = new Computation();

    protected abstract V compute();

    protected void setProgress(final int current, final int max) {
        GuiExecutor.instance().execute(() -> onProgress(current, max));
    }

    protected void onCompletion(V result, Throwable thrown, boolean cancelled) {
    }

    protected void onProgress(int current, int max) {
    }

    private class Computation extends FutureTask<V> {
        Computation() {
            super(BackgroundTask.this::compute);
        }

        @Override
        protected void done() {
            GuiExecutor.instance().execute(() -> {
                V value = null;
                Throwable thrown = null;
                boolean cancelled = false;
                try {
                    value = get();
                } catch (ExecutionException e) {
                    thrown = e.getCause();
                } catch (CancellationException e) {
                    cancelled = true;
                } catch (InterruptedException ignored) {
                } finally {
                    onCompletion(value, thrown, cancelled);
                }
            });
        }
    }

    @Override
    public boolean isCancelled() {
        return computation.isCancelled();
    }

    @Override
    public boolean isDone() {
        return computation.isDone();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return computation.cancel(mayInterruptIfRunning);
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return computation.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return computation.get(timeout, unit);
    }

    @Override
    public void run() {
        computation.run();
    }
}
