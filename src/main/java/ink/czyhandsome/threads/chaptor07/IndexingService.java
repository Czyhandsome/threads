package ink.czyhandsome.threads.chaptor07;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * A crawler and indexer service
 * Terminated by poison pills
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class IndexingService {

    private static final File POISON = new File("");
    private final CrawlerThread producer = new CrawlerThread();
    private final IndexerThread consumer = new IndexerThread();
    private final BlockingQueue<File> queue = new LinkedBlockingDeque<>();
    private final FileFilter fileFilter;
    private final File root;

    public IndexingService(FileFilter fileFilter, File root) {
        this.fileFilter = fileFilter;
        this.root = root;
    }

    public void start() {
        producer.start();
        consumer.start();
    }

    public void stop() {
        producer.interrupt();
    }

    public void awaitTermination() throws InterruptedException {
        consumer.join();
    }

    private class CrawlerThread extends Thread {
        @Override
        public void run() {
            try {
                crawl(root);
            } catch (InterruptedException e) {
                while (true) {
                    try {
                        queue.put(POISON);
                        break;
                    } catch (InterruptedException ignord) {
                    }
                }
            }
        }

        private void crawl(File root) throws InterruptedException {
            System.out.println("Crawling: " + root);
        }
    }

    private class IndexerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    File file = queue.take();
                    if (file == POISON) {
                        break;
                    } else {
                        indexFile(file);
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }

        private void indexFile(File file) {
            System.out.println("Indexing: " + file);
        }
    }
}
