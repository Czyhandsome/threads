package ink.czyhandsome.threads.chaptor03;

import ink.czyhandsome.threads.Immutable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
public class ThreadConfinement {

    public static class StackConfinement {
        public int loadTheArk(Collection<Animal> candidates) {
            SortedSet<Animal> animals;
            int numPairs = 0;
            Animal candidate = null;

            animals = new TreeSet<>(new SpeciesGenderComparator());
            animals.addAll(candidates);
            for (Animal a : animals) {
                if (candidate == null || !candidate.isPotentialMate(a)) {
                    candidate = a;
                } else {
                    load(new AnimalPair(candidate, a));
                    ++numPairs;
                    candidate = null;
                }
            }
            return numPairs;
        }

        private void load(AnimalPair animalPair) {
        }

        public interface Animal {
            boolean isPotentialMate(Animal animal);
        }

        public class SpeciesGenderComparator implements Comparator<Animal> {

            @Override
            public int compare(Animal o1, Animal o2) {
                return 0;
            }
        }

        public class AnimalPair {

            public AnimalPair(Animal candidate, Animal a) {
            }
        }
    }

    public static class ThreadLocalConfinement {
        private static final String DB_URL = "FOO:BAR";
        private static ThreadLocal<Connection> connectionHolder = ThreadLocal.withInitial(() -> {
            try {
                return DriverManager.getConnection(DB_URL);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        public static Connection getConnection() {
            return connectionHolder.get();
        }
    }

    @Immutable
    public static final class ThreeStooges {
        private final Set<String> stooges = new HashSet<>();

        public ThreeStooges() {
            stooges.add("Moe");
            stooges.add("Larry");
            stooges.add("Curly");
        }

        public boolean isStooge(String name) {
            return stooges.contains(name);
        }
    }
}
