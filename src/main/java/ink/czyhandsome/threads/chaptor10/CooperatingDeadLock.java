package ink.czyhandsome.threads.chaptor10;

import ink.czyhandsome.threads.DeadLock;
import ink.czyhandsome.threads.GuardedBy;

import java.util.HashSet;
import java.util.Set;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/24 0024
 */
@DeadLock
public class CooperatingDeadLock {
    public static class Taxi {
        @GuardedBy("this")
        private Point location, destination;

        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        @DeadLock
        public synchronized void setLocation(Point location) {
            this.location = location;
            if (location.equals(destination)) {
                dispatcher.notifyAvailable(this);
            }
        }

        @DeadLock
        public synchronized Point getLocation() {
            return location;
        }
    }

    public static class Dispatcher {
        @GuardedBy("this")
        private final Set<Taxi> taxis = new HashSet<>();

        @GuardedBy("this")
        private final Set<Taxi> availableTaxis = new HashSet<>();

        @DeadLock
        public synchronized void notifyAvailable(Taxi taxi) {
            availableTaxis.add(taxi);
        }

        @DeadLock
        public synchronized Image getImage() {
            Image image = new Image();
            for (Taxi taxi : taxis) {
                image.drawMarker(taxi.getLocation());
            }
            return image;
        }
    }

    public interface Point {
    }

    public static class Image {
        public void drawMarker(Point point) {
            System.out.println("Draw marker for: " + point);
        }
    }
}
