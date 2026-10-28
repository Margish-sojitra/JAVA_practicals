import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Week 12 - Practice Program 2: Unique Visitors
 * Demonstrates:
 * - Processing duplicate login attempts
 * - Using HashSet to count unique users efficiently
 * - Using LinkedHashSet to retain exact first-seen (chronological insertion) order
 * - Printing the unique count and the first three distinct visitors
 */
public class UniqueVisitors {

    public static void main(String[] args) {
        System.out.println("=== Week 12 Practical 2: Unique Visitors with HashSet & LinkedHashSet ===");

        // List of login events containing duplicate user logins in order of arrival
        List<String> rawLoginEvents = Arrays.asList(
            "alice",
            "bob",
            "alice",     // duplicate
            "charlie",
            "bob",       // duplicate
            "david",
            "alice",     // duplicate
            "emma",
            "frank",
            "charlie",   // duplicate
            "grace",
            "bob"        // duplicate
        );

        System.out.println("\n--- Raw Login Log Stream (" + rawLoginEvents.size() + " total entries) ---");
        for (int i = 0; i < rawLoginEvents.size(); i++) {
            System.out.printf("  Event #%02d: %s%n", (i + 1), rawLoginEvents.get(i));
        }

        // 1. Using HashSet to count unique visitors (unordered, fast O(1))
        Set<String> hashSetVisitors = new HashSet<>(rawLoginEvents);
        System.out.println("\n--- 1. HashSet (Unordered Unique Set) ---");
        System.out.println("HashSet Content: " + hashSetVisitors);
        System.out.println("Unique Visitors Count: " + hashSetVisitors.size());

        // 2. Using LinkedHashSet to retain first-seen order (predictable iteration order)
        Set<String> linkedHashSetVisitors = new LinkedHashSet<>(rawLoginEvents);
        System.out.println("\n--- 2. LinkedHashSet (First-Seen Ordered Unique Set) ---");
        System.out.println("LinkedHashSet Content (Chronological): " + linkedHashSetVisitors);
        System.out.println("Unique Visitors Count: " + linkedHashSetVisitors.size());

        // 3. Print the first three distinct names based on first-seen order
        System.out.println("\n--- 3. First Three Distinct Visitors in Arrival Order ---");
        Iterator<String> iterator = linkedHashSetVisitors.iterator();
        int count = 0;
        while (iterator.hasNext() && count < 3) {
            count++;
            System.out.printf("  Visitor #%d: %s%n", count, iterator.next());
        }

        System.out.println("\n--- Verification Summary ---");
        System.out.println("Total raw login events  : " + rawLoginEvents.size());
        System.out.println("Unique visitor count    : " + linkedHashSetVisitors.size());
        System.out.println("Duplicate visits filtered: " + (rawLoginEvents.size() - linkedHashSetVisitors.size()));
    }
}
