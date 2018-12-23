package ink.czyhandsome.threads.chaptor08;

import ink.czyhandsome.threads.Immutable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * $DESC
 *
 * @author 曹子钰, 2018-12-22
 */
public class PuzzleFramework {
    public interface Puzzle<P, M> {
        P initialPosition();

        boolean isGoal(P pos);

        Set<M> legalMoves(P pos);

        P move(P position, M move);
    }

    @Immutable
    public static class Node<P, M> {
        final P pos;
        final M move;
        final Node<P, M> prev;

        public Node(P pos, M move, Node<P, M> prev) {
            this.pos = pos;
            this.move = move;
            this.prev = prev;
        }

        List<M> asMoves() {
            List<M> solutions = new LinkedList<>();
            for (Node<P, M> n = this; n.move != null; n = n.prev) {
                solutions.add(0, n.move);
            }
            return solutions;
        }
    }

    public static class SequentialPuzzleSolver<P, M> {
        private final Puzzle<P, M> puzzle;
        private final Set<P> seen = new HashSet<>();

        public SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
            this.puzzle = puzzle;
        }

        public List<M> solve() {
            P pos = puzzle.initialPosition();
            return search(new Node<>(pos, null, null));
        }

        private List<M> search(Node<P, M> node) {
            P currentPos = node.pos;
            if (!seen.contains(currentPos)) {
                seen.add(currentPos);
                if (puzzle.isGoal(currentPos)) {
                    return node.asMoves();
                }
                for (M move : puzzle.legalMoves(currentPos)) {
                    P nextPos = puzzle.move(currentPos, move);
                    Node<P, M> child = new Node<>(nextPos, move, node);
                    List<M> result = search(child);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return null;
        }
    }

    public static class ConcurrentPuzzleSolver<P, M> {
        private final Puzzle<P, M> puzzle;
        private final ExecutorService exec = Executors.newCachedThreadPool();
        private final ConcurrentHashMap<P, Boolean> seen = new ConcurrentHashMap<>();
        private final ValueLatch<Node<P, M>> solution = new ValueLatch<>();

        public ConcurrentPuzzleSolver(Puzzle<P, M> puzzle) {
            this.puzzle = puzzle;
        }

        public List<M> solve() throws InterruptedException {
            try {
                P p = puzzle.initialPosition();
                exec.execute(newTask(p, null, null));
                // Block until solution found
                Node<P, M> resultNode = solution.getValue();
                return (resultNode == null) ? null : resultNode.asMoves();
            } finally {
                exec.shutdown();
            }
        }

        private Runnable newTask(P p, M m, Node<P, M> n) {
            return new SolverTask(p, m, n);
        }

        class SolverTask implements Runnable {
            private P p;
            private M m;
            private Node<P, M> n;

            SolverTask(P p, M m, Node<P, M> n) {
                this.p = p;
                this.m = m;
                this.n = n;
            }

            @Override
            public void run() {
                if (solution.isSet() || seen.putIfAbsent(p, true) != null) {
                    return;
                }
                if (puzzle.isGoal(p)) {
                    solution.setValue(n);
                } else {
                    for (M legalMove : puzzle.legalMoves(p)) {
                        exec.execute(newTask(puzzle.move(p, m), m, n));
                    }
                }
            }
        }

        class CountingSolverTask extends SolverTask {
            private final AtomicInteger taskCount = new AtomicInteger();

            CountingSolverTask(P p, M m, Node<P, M> n) {
                super(p, m, n);
                taskCount.incrementAndGet();
            }

            @Override
            public void run() {
                try {
                    super.run();
                } finally {
                    if (taskCount.decrementAndGet() == 0) {
                        solution.setValue(null);
                    }
                }
            }
        }
    }
}
