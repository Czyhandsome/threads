package ink.czyhandsome.threads.chaptor03;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.math.BigInteger;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
public class ServletUtils {

    public static BigInteger extractFromRequest(ServletRequest req) {
        return BigInteger.valueOf(0L);
    }

    public static BigInteger[] factor(BigInteger i) {
        return new BigInteger[]{i};
    }

    public static void encodeIntoResponse(ServletResponse resp, BigInteger[] factors) {
    }
}
