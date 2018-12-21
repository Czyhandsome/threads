package ink.czyhandsome.threads.chaptor06;

import java.util.concurrent.Executor;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/17 0017
 */
public class ExecutorBundle {

    public static class ThreadPerTaskExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            new Thread(command).start();
        }
    }

    public static class SingleThreadExecutor implements Executor {

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}
