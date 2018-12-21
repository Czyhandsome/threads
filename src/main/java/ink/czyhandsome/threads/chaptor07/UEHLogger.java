package ink.czyhandsome.threads.chaptor07;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class UEHLogger implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.printf("Thread {%s} terminated with exception: %s%n", t.getName(), e.getMessage());
    }

    public static void main(String[] args) throws Exception {
        ExecutorService exec = Executors.newFixedThreadPool(5);
        Callable<Integer> c1 = new C1();

        try {
            while (true) {
                Future<Integer> result = exec.submit(c1);
                Integer i = result.get();
                System.out.println(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            List<Runnable> runnables = exec.shutdownNow();
            System.out.println(runnables);
        }
    }

    static class C1 implements Callable<Integer> {
        volatile static AtomicInteger num = new AtomicInteger();

        @Override
        public Integer call() throws Exception {
            if (num.get() >= 3) {
                throw new RuntimeException("Num >= 3!");
            }
            return num.getAndIncrement();
        }
    }

}
