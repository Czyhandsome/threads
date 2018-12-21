package ink.czyhandsome.threads.chaptor07;

/**
 * 给LogService增加ShutdownHook
 * 当JVM关闭时, 自动关闭LogService
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class LogServiceShutdownHook extends LogWriterBundle.LogService {
    @Override
    public void start() {
        super.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LogServiceShutdownHook.this.stop();
            } catch (InterruptedException ignored) {
            }
        }));
    }
}
