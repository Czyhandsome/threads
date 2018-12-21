package ink.czyhandsome.threads.chaptor07;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/18 0018
 */
public class InterrptedPrimeGenerator extends Thread {
    private final BlockingQueue<BigInteger> queue;

    public InterrptedPrimeGenerator(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            while (!Thread.currentThread().isInterrupted()) {
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException e) {
            System.out.println("Cancelled!");
        }
    }

    public void cancel() {
        interrupt();
    }
}
