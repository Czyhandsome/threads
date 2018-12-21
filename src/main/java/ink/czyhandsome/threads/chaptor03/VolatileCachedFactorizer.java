package ink.czyhandsome.threads.chaptor03;

import ink.czyhandsome.threads.ThreadSafe;

import javax.servlet.*;
import java.io.IOException;
import java.math.BigInteger;

import static ink.czyhandsome.threads.chaptor03.ServletUtils.*;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
@ThreadSafe
public class VolatileCachedFactorizer implements Servlet {
    private volatile OneValueCache cache = new OneValueCache(null, null);

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = cache.getFactors(i);
        if (factors == null) {
            factors = factor(i);
            cache = new OneValueCache(i, factors);
        }
        encodeIntoResponse(resp, factors);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
