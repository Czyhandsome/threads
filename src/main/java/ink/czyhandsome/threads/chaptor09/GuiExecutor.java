package ink.czyhandsome.threads.chaptor09;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * GUI executor
 *
 * @author 曹子钰, 2018/12/24 0024
 */
public class GuiExecutor extends AbstractExecutorService {
    private static final GuiExecutor instance = new GuiExecutor();

    private GuiExecutor() {
    }

    public static GuiExecutor instance() {
        return instance;
    }

    public void execute(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    @Override
    public void shutdown() {
        instance.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return instance.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return instance.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return instance.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return instance.awaitTermination(timeout, unit);
    }
}
