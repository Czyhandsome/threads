package ink.czyhandsome.threads.chaptor07;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class OneShotExecutionService {

    public boolean checkMail(Set<String> hosts,
                             long timeout, TimeUnit unit) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        final AtomicBoolean hasNewMail = new AtomicBoolean(false);
        try {
            for (String host : hosts) {
                exec.execute(() -> {
                    if (checkMail(host)) {
                        hasNewMail.set(true);
                    }
                });
            }
        } finally {
            exec.shutdown();
            exec.awaitTermination(timeout, unit);
        }
        return hasNewMail.get();
    }

    private boolean checkMail(String host) {
        System.out.println("Checking mail for: " + host);
        return true;
    }
}