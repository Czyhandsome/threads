package ink.czyhandsome.threads.chaptor07;

import ink.czyhandsome.threads.GuardedBy;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public abstract class TracingWebCrawler {
    private volatile TrackingExecutor exec = new TrackingExecutor(Executors.newFixedThreadPool(5));

    @GuardedBy("this")
    private final Set<URL> urlsToCrawl;

    protected TracingWebCrawler(Set<URL> urlsToCrawl) {
        this.urlsToCrawl = new HashSet<>(urlsToCrawl);
    }

    public void start() {
        for (URL url : urlsToCrawl) {
            submitCrawlTask(url);
        }
        urlsToCrawl.clear();
    }

    public synchronized void stop() throws InterruptedException {
        try {
            saveUncrawled(exec.shutdownNow());
            if (exec.awaitTermination(10, TimeUnit.SECONDS)) {
                saveUncrawled(exec.getCancelledTasks());
            }
        } finally {
            exec = null;
        }
    }

    protected abstract List<URL> processPage(URL url);

    private void saveUncrawled(List<Runnable> uncrawled) {
        for (Runnable runnable : uncrawled) {
            urlsToCrawl.add(((CrawlTask) runnable).getPage());
        }
    }

    private void submitCrawlTask(URL u) {
        exec.execute(new CrawlTask(u));
    }

    private class CrawlTask implements Runnable {
        private final URL url;

        private CrawlTask(URL url) {
            this.url = url;
        }

        public URL getPage() {
            return url;
        }

        @Override
        public void run() {
            for (URL link : processPage(url)) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                submitCrawlTask(link);
            }
        }
    }
}
