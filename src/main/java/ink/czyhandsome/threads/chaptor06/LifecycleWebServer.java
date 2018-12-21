package ink.czyhandsome.threads.chaptor06;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/17 0017
 */
public class LifecycleWebServer {
    private final ExecutorService exec = Executors.newFixedThreadPool(100);

    public void start() throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (!exec.isShutdown()) {
            try {
                final Socket connection = socket.accept();
                exec.execute(() -> handleRequest(connection));
            } catch (RejectedExecutionException e) {
                if (!exec.isShutdown()) {
                    System.out.println("Task submission rejected: " + e);
                }
            }
        }
    }

    public void stop() {
        exec.shutdown();
    }


    private void handleRequest(Socket connection) {
        Request req = readRequest(connection);
        if (isShutdownRequest(req)) {
            stop();
        } else {
            dispatchRequest(req);
        }
    }

    private boolean isShutdownRequest(Request req) {
        return new Random(req.connection.getInetAddress().hashCode()).nextBoolean();
    }

    private void dispatchRequest(Request request) {
        System.out.println(request + " dispatched!");
    }

    private Request readRequest(Socket connection) {
        return new Request(connection);
    }

    class Request {
        final Socket connection;

        public Request(Socket connection) {
            this.connection = connection;
        }
    }
}
