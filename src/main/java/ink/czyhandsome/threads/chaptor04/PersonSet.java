package ink.czyhandsome.threads.chaptor04;

import ink.czyhandsome.threads.GuardedBy;
import ink.czyhandsome.threads.ThreadSafe;

import java.util.HashSet;
import java.util.Set;

/**
 * $DESC
 *
 * @author 曹子钰, 2018/12/10 0010
 */
@ThreadSafe
public class PersonSet {
    @GuardedBy("this")
    private final Set<Person> mySet = new HashSet<>();

    public synchronized void addPerson(Person p) {
        mySet.add(p);
    }

    public synchronized boolean containsPerson(Person p) {
        return mySet.contains(p);
    }

    public interface Person {
    }
}
