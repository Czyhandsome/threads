package ink.czyhandsome.threads.chaptor04;

import ink.czyhandsome.threads.GuardedBy;
import ink.czyhandsome.threads.ThreadSafe;
import ink.czyhandsome.threads.chaptor04.Point.ImmutablePoint;
import ink.czyhandsome.threads.chaptor04.Point.MutablePoint;
import ink.czyhandsome.threads.chaptor04.Point.SafePoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
public interface VehicleTracker {
    Map<String, ? extends Point> getLocations();

    Point getLocation(String id);

    void setLocation(String id, int x, int y);

    class MonitorVehicleTracker implements VehicleTracker {
        @GuardedBy("this")
        private final Map<String, MutablePoint> locations;

        public MonitorVehicleTracker(Map<String, MutablePoint> locations) {
            this.locations = deepCopy(locations);
        }

        @Override
        public synchronized Map<String, MutablePoint> getLocations() {
            return deepCopy(locations);
        }

        @Override
        public synchronized MutablePoint getLocation(String id) {
            MutablePoint loc = locations.get(id);
            return loc == null ? null : new MutablePoint(loc);
        }

        @Override
        public synchronized void setLocation(String id, int x, int y) {
            MutablePoint loc = locations.get(id);
            if (loc == null)
                throw new IllegalArgumentException("No such ID: " + id);
            loc.x = x;
            loc.y = y;
        }

        private static Map<String, MutablePoint> deepCopy(
                Map<String, MutablePoint> m) {
            Map<String, MutablePoint> result =
                    new HashMap<>();
            for (String id : m.keySet())
                result.put(id, new MutablePoint(m.get(id)));
            return Collections.unmodifiableMap(result);
        }
    }

    @ThreadSafe
    class DelegatingVehicleTracker implements VehicleTracker {
        private final ConcurrentMap<String, ImmutablePoint> locations;
        private final Map<String, ImmutablePoint> unmodifiableMap;

        public DelegatingVehicleTracker(Map<String, ImmutablePoint> points) {
            locations = new ConcurrentHashMap<>(points);
            unmodifiableMap = Collections.unmodifiableMap(locations);
        }

        @Override
        public Map<String, ImmutablePoint> getLocations() {
            return unmodifiableMap;
        }

        @Override
        public ImmutablePoint getLocation(String id) {
            return locations.get(id);
        }

        @Override
        public void setLocation(String id, int x, int y) {
            if (locations.replace(id, new ImmutablePoint(x, y)) == null) {
                throw new IllegalArgumentException("invalid vehicle name: " + id);
            }
        }
    }

    @ThreadSafe
    class PublishingVehicleTracker implements VehicleTracker {
        private final Map<String, SafePoint> locations;
        private final Map<String, SafePoint> unmodifiableMap;

        public PublishingVehicleTracker(Map<String, SafePoint> locations) {
            this.locations = new ConcurrentHashMap<>(locations);
            this.unmodifiableMap = Collections.unmodifiableMap(this.locations);
        }

        @Override
        public Map<String, ? extends Point> getLocations() {
            return unmodifiableMap;
        }

        @Override
        public Point getLocation(String id) {
            return locations.get(id);
        }

        @Override
        public void setLocation(String id, int x, int y) {
            if (!locations.containsKey(id)) {
                throw new IllegalArgumentException("invalid vehicle name: " + id);
            }
            locations.get(id).set(x, y);
        }
    }
}
