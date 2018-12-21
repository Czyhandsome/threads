package ink.czyhandsome.threads.chaptor07;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/20 0020
 */
public class ReaderThread extends Thread {

    private final Socket socket;
    private final InputStream in;

    public ReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignored) {
        } finally {
            super.interrupt();
        }
    }

    public void run() {
        try {
            final int BUF_SIZE = 1024;
            byte[] buf = new byte[BUF_SIZE];
            while (true) {
                int count = in.read(buf);
                if (count < 0) {
                    break;
                } else if (count > 0) {
                    System.out.println("Processing buffer: " + buf);
                }
            }
        } catch (IOException e) {
            System.out.println("Exit by IOException!");
        }
    }
}
