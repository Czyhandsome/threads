package ink.czyhandsome.threads.chaptor10;

import ink.czyhandsome.threads.DeadLock;
import ink.czyhandsome.threads.GuardedBy;
import ink.czyhandsome.threads.ThreadSafe;

import java.util.HashSet;
import java.util.Set;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/25 0025
 */
@ThreadSafe
public class ThreadSafeTaxi {

    public static class Taxi {
        @GuardedBy("this")
        private CooperatingDeadLock.Point location, destination;

        private final Dispatcher dispatcher;

        public Taxi(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public synchronized CooperatingDeadLock.Point getLocation() {
            return location;
        }

        public void setLocation(CooperatingDeadLock.Point location) {
            boolean reachedDestination;
            synchronized (this) {
                this.location = location;
                reachedDestination = location.equals(destination);
            }
            if (reachedDestination) {
                dispatcher.notifyAvailable(this);
            }
        }
    }

    @ThreadSafe
    public static class Dispatcher {
        @GuardedBy("this")
        private final Set<Taxi> taxis = new HashSet<>();

        @GuardedBy("this")
        private final Set<Taxi> availableTaxis = new HashSet<>();

        @DeadLock
        public void notifyAvailable(Taxi taxi) {
            availableTaxis.add(taxi);
        }

        @DeadLock
        public CooperatingDeadLock.Image getImage() {
            Set<Taxi> copy;
            synchronized (this) {
                copy = new HashSet<>(taxis);
            }
            CooperatingDeadLock.Image image = new CooperatingDeadLock.Image();
            for (Taxi taxi : copy) {
                image.drawMarker(taxi.getLocation());
            }
            return image;
        }
    }
}
