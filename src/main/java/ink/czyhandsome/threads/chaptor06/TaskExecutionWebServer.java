package ink.czyhandsome.threads.chaptor06;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/17 0017
 */
public class TaskExecutionWebServer {
    private static final int N_THREADS = 100;
    private static final Executor exec = Executors.newFixedThreadPool(N_THREADS);

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = () -> WebServerUtils.handleRequest(connection);
            exec.execute(task);
        }
    }
}
