import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UniqueVisitors {

    public static void main(String[] args) {
        List<String> logins = Arrays.asList(
            "alice", "bob", "alice", "charlie", "bob", "david", "alice", "emma"
        );

        System.out.println("Logins with repeats: " + logins);

        Set<String> uniqueUsers = new HashSet<>(logins);
        System.out.println("Unique count (using HashSet): " + uniqueUsers.size());

        Set<String> firstSeen = new LinkedHashSet<>(logins);
        System.out.println("First-seen order (using LinkedHashSet): " + firstSeen);

        System.out.println("\nFirst three distinct visitors:");
        Iterator<String> it = firstSeen.iterator();
        int count = 0;
        while (it.hasNext() && count < 3) {
            System.out.println((count + 1) + ". " + it.next());
            count++;
        }
    }
}
