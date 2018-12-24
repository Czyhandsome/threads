package ink.czyhandsome.threads;

import java.time.LocalDateTime;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class Logger {
    private final String name;

    private Logger(String name) {
        this.name = name;
    }

    public static Logger getLogger(Class<?> clz) {
        return getLogger(clz.getSimpleName());
    }

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    public void info(String msg) {
        System.out.printf("[%s]: %s - %s%n", LocalDateTime.now().toString(), name, msg);
    }
}
