import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Week 12 - Practice Program 1: Word Frequency Analyzer
 * Demonstrates:
 * - Splitting a paragraph into words
 * - Counting occurrences using HashMap
 * - Sorting entries by frequency (highest first) using a custom Comparator
 */
public class WordFrequency {

    public static void main(String[] args) {
        System.out.println("=== Week 12 Practical 1: Word Frequency Analyzer with HashMap & Comparator ===");

        String paragraph = 
            "Object-oriented programming in Java is powerful. Java collections provide efficient data " +
            "structures like HashMap, HashSet, and ArrayList. In Java, a HashMap allows fast key-value " +
            "lookups. A HashMap stores unique keys, while an ArrayList maintains insertion order. Java " +
            "developers use collections and comparators to sort and organize data effectively.";

        System.out.println("\n--- Input Paragraph ---");
        System.out.println(paragraph);

        // 1. Split paragraph into clean lowercase words
        // Split on non-word characters (spaces, punctuation, tabs, newlines)
        String[] rawWords = paragraph.toLowerCase().split("[^a-zA-Z0-9]+");

        // 2. Count frequency in HashMap
        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String word : rawWords) {
            if (word.isEmpty()) continue;
            wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
        }

        System.out.println("\nTotal Words Processed : " + rawWords.length);
        System.out.println("Distinct Words Found  : " + wordFrequency.size());

        // 3. Convert Map entries to a List for sorting
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(wordFrequency.entrySet());

        // 4. Sort with Comparator: Highest frequency first, ties broken alphabetically
        Comparator<Map.Entry<String, Integer>> frequencyComparator = 
            (e1, e2) -> {
                int freqCompare = Integer.compare(e2.getValue(), e1.getValue()); // Descending frequency
                if (freqCompare != 0) {
                    return freqCompare;
                }
                return e1.getKey().compareTo(e2.getKey()); // Ascending alphabetical tie-breaker
            };

        entryList.sort(frequencyComparator);

        // 5. Print sorted words
        System.out.println("\n--- Words Sorted by Frequency (Highest First) ---");
        System.out.printf("%-4s | %-15s | %s%n", "Rank", "Word", "Frequency");
        System.out.println("-------------------------------------");

        int rank = 1;
        for (Map.Entry<String, Integer> entry : entryList) {
            System.out.printf("%-4d | %-15s | %d%n", rank++, entry.getKey(), entry.getValue());
        }
    }
}
