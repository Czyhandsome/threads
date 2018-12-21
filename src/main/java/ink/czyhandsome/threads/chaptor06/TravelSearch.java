package ink.czyhandsome.threads.chaptor06;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/18 0018
 */
public class TravelSearch {
    private static final ExecutorService exec = Executors.newFixedThreadPool(10);

    public static class QuoteTask implements Callable<TravelQuote> {
        private final TravelCompany travelCompany;
        private final TravelInfo travelInfo;

        public QuoteTask(TravelCompany travelCompany,
                         TravelInfo travelInfo) {
            this.travelCompany = travelCompany;
            this.travelInfo = travelInfo;
        }

        @Override
        public TravelQuote call() throws Exception {
            return travelCompany.solicitQuote(travelInfo);
        }

        public TravelQuote getFailureQuote(Throwable cause) {
            return new TravelQuote();
        }

        public TravelQuote getTimeoutQuote(CancellationException e) {
            return new TravelQuote();
        }
    }

    public List<TravelQuote> getRankedTravelQuotes(TravelInfo travelInfo,
                                                   Set<TravelCompany> companies,
                                                   Comparator<TravelQuote> ranking,
                                                   long time,
                                                   TimeUnit unit) throws InterruptedException {
        List<QuoteTask> tasks = companies.stream()
                .map(c -> new QuoteTask(c, travelInfo))
                .collect(Collectors.toList());
        List<Future<TravelQuote>> futures = exec.invokeAll(tasks);

        List<TravelQuote> quotes = new ArrayList<>(tasks.size());
        Iterator<QuoteTask> taskIterator = tasks.iterator();
        for (Future<TravelQuote> future : futures) {
            QuoteTask task = taskIterator.next();
            try {
                quotes.add(future.get());
            } catch (ExecutionException e) {
                quotes.add(task.getFailureQuote(e.getCause()));
            } catch (CancellationException e) {
                quotes.add(task.getTimeoutQuote(e));
            }
        }
        quotes.sort(ranking);
        return quotes;
    }

    public static class TravelQuote {
    }

    public static class TravelInfo {
    }

    public static class TravelCompany {
        public TravelQuote solicitQuote(TravelInfo travelInfo) {
            return new TravelQuote();
        }
    }
}
