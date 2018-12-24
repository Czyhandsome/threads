package ink.czyhandsome.threads.chaptor08;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * 将串行循环改为并行
 *
 * @author 曹子钰, 2018/12/21 0021
 */
public class Parallelizing {
    public <E> void processSequentially(List<E> elements, Consumer<E> processor) {
        for (E element : elements) {
            processor.accept(element);
        }
    }

    public <E> void processInParallel(Executor exec, List<E> elements, Consumer<E> processor) {
        for (E element : elements) {
            exec.execute(() -> processor.accept(element));
        }
    }
}
