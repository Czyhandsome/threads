package ink.czyhandsome.threads.chaptor06;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/18 0018
 */
public class HtmlRenderers {
    public static void main(String[] args) {
        new CompletionRenderer().renderPage("");
    }

    public static class SingleThreadRenderer extends Renderer {
        @Override
        public void renderPage(CharSequence source) {
            renderText(source);
            List<ImageData> imageData = new ArrayList<>();
            for (ImageInfo imageInfo : scanForImageInfo(source)) {
                imageData.add(imageInfo.downloadImage());
            }
            for (ImageData imageDatum : imageData) {
                renderImage(imageDatum);
            }
        }
    }

    public static class FutureRenderer extends Renderer {
        private static final ExecutorService exec = Executors.newFixedThreadPool(10);

        @Override
        public void renderPage(CharSequence source) {
            renderText(source);
            Future<List<ImageData>> renderImage = exec.submit(() -> {
                List<ImageData> imageData = new ArrayList<>();
                for (ImageInfo imageInfo : scanForImageInfo(source)) {
                    imageData.add(imageInfo.downloadImage());
                }
                return imageData;
            });
            try {
                List<ImageData> imageData = renderImage.get();
                for (ImageData imageDatum : imageData) {
                    renderImage(imageDatum);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                renderImage.cancel(true);
            } catch (ExecutionException e) {
                throw new LaunderThrowable(e);
            }
        }
    }

    public static class CompletionRenderer extends Renderer {
        private static final ExecutorService exec = Executors.newFixedThreadPool(5);

        @Override
        public void renderPage(CharSequence source) {
            final List<ImageInfo> info = scanForImageInfo(source);
            CompletionService<ImageData> completionService = new ExecutorCompletionService<>(exec);
            for (ImageInfo imageInfo : info) {
                completionService.submit(imageInfo::downloadImage);
            }

            renderText(source);

            try {
                for (int i = 0; i < info.size(); i++) {
                    Future<ImageData> f = completionService.take();
                    ImageData image = f.get();
                    renderImage(image);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                throw new LaunderThrowable(e);
            }
        }

        Page renderPageWithAd() throws InterruptedException {
            final long TIME_BUDGET = 1000;
            long endNanos = System.nanoTime() + TIME_BUDGET;
            Future<Ad> f = exec.submit(new FetchAdTask());
            Page page = renderPageBody();
            Ad ad;
            try {
                long timeLeft = endNanos - System.nanoTime();
                ad = f.get(timeLeft, TimeUnit.NANOSECONDS);
            } catch (ExecutionException e) {
                ad = new Ad();
            } catch (TimeoutException e) {
                ad = new Ad();
                f.cancel(true);
            }
            page.setAd(ad);
            return page;
        }

        private Page renderPageBody() {
            return new Page();
        }

        private static class FetchAdTask implements Callable<Ad> {
            @Override
            public Ad call() throws Exception {
                return new Ad();
            }
        }
    }

    public static abstract class Renderer {
        void renderImage(ImageData imageData) {
            try {
                System.out.println("Image Rendering!");
                Thread.sleep(300);
                System.out.println("Image Rendered");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        void renderText(CharSequence source) {
            try {
                System.out.println("Rendering Text!");
                Thread.sleep(200);
                System.out.println("Text Rendered!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<ImageInfo> scanForImageInfo(CharSequence source) {
            return Arrays.asList(new ImageInfo(), new ImageInfo(), new ImageInfo());
        }

        public abstract void renderPage(CharSequence source);
    }

    static class ImageInfo {

        ImageData downloadImage() {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new ImageData();
        }
    }

    static class ImageData {
    }

    static class LaunderThrowable extends RuntimeException {
        public LaunderThrowable(Throwable cause) {
            super(cause);
        }
    }

    static class Page {
        void setAd(Ad ad) {
        }
    }

    static class Ad {
    }
}
