import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Leaderboard {

    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 250);
        scores.put("Bob", 400);
        scores.put("Charlie", 400);
        scores.put("Dave", 180);
        scores.put("Emma", 320);

        List<Map.Entry<String, Integer>> list = new ArrayList<>(scores.entrySet());

        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                int scoreCompare = e2.getValue().compareTo(e1.getValue());
                if (scoreCompare != 0) {
                    return scoreCompare;
                }
                return e1.getKey().compareTo(e2.getKey());
            }
        });

        int topN = 3;
        System.out.println("Top " + topN + " Players:");
        for (int i = 0; i < Math.min(topN, list.size()); i++) {
            Map.Entry<String, Integer> entry = list.get(i);
            System.out.println((i + 1) + ". " + entry.getKey() + " - " + entry.getValue() + " pts");
        }
    }
}
