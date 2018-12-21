package ink.czyhandsome.threads.chaptor07;

import ink.czyhandsome.threads.NotThreadSafe;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/18 0018
 */
@NotThreadSafe
public class BrokenPrimeGenerator extends Thread {
    private final BlockingQueue<BigInteger> queue;
    private volatile boolean cancelled = false;

    public BrokenPrimeGenerator(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        BigInteger p = BigInteger.ONE;
        while (!cancelled) {
            try {
                queue.put(p = p.nextProbablePrime());
            } catch (InterruptedException ignore) {
            }
        }
    }

    public void cancel() {
        this.cancelled = true;
    }

    public static void main(String[] args) throws InterruptedException {

        BlockingQueue<BigInteger> primes = new LinkedBlockingDeque<>();
        BrokenPrimeGenerator producer = new BrokenPrimeGenerator(primes);
        producer.start();
        try {
            while (new Random().nextBoolean()) {
                System.out.println(primes.take());
            }
        } finally {
            producer.cancel();
        }
    }
}
