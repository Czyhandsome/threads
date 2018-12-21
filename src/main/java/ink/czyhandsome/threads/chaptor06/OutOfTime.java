package ink.czyhandsome.threads.chaptor06;

import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/17 0017
 */
public class OutOfTime {

    public static void main(String[] args) throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(throwTask(), 1);
        SECONDS.sleep(1);
        timer.schedule(throwTask(), 1);
        SECONDS.sleep(5);
    }

    private static TimerTask throwTask() {
        return new TimerTask() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        };
    }
}
