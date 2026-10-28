import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Week 12 - Practice Program 3 (For Advanced Learners): Game Leaderboard
 * Demonstrates:
 * - Holding player names and scores in a Map
 * - Sorting entries by score descending using a custom Comparator
 * - Resolving ties by sorting player names alphabetically
 * - Extracting and displaying the Top-N leaderboard entries
 */
public class Leaderboard {

    static class PlayerEntry {
        private final String playerName;
        private final int score;

        public PlayerEntry(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return playerName + " -> " + score + " pts";
        }
    }

    public static List<PlayerEntry> getTopN(Map<String, Integer> scoreMap, int topN) {
        List<PlayerEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> e : scoreMap.entrySet()) {
            entries.add(new PlayerEntry(e.getKey(), e.getValue()));
        }

        // Custom Comparator: Primary sort by score descending, secondary by name ascending
        Comparator<PlayerEntry> leaderboardComparator = (p1, p2) -> {
            int scoreCompare = Integer.compare(p2.getScore(), p1.getScore()); // Higher score wins
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            // Tie-break: alphabetical order of names
            return p1.getPlayerName().compareToIgnoreCase(p2.getPlayerName());
        };

        entries.sort(leaderboardComparator);

        int limit = Math.min(topN, entries.size());
        return new ArrayList<>(entries.subList(0, limit));
    }

    public static void printLeaderboard(List<PlayerEntry> topPlayers, String title) {
        System.out.println("\n================ " + title + " ================");
        System.out.printf("%-6s | %-16s | %s%n", "Rank", "Player Name", "Score (pts)");
        System.out.println("---------------------------------------------");

        int rank = 1;
        for (PlayerEntry player : topPlayers) {
            String badge = switch (rank) {
                case 1 -> "[GOLD]";
                case 2 -> "[SILVER]";
                case 3 -> "[BRONZE]";
                default -> "      ";
            };
            System.out.printf("#%-4d  | %-16s | %-6d %s%n",
                    rank, player.getPlayerName(), player.getScore(), badge);
            rank++;
        }
        System.out.println("=============================================");
    }

    public static void main(String[] args) {
        System.out.println("=== Week 12 Practical 3: Top-N Player Leaderboard with Map & Comparator ===");

        // Player scores in Map with deliberate ties to test tie-breaker
        Map<String, Integer> playerScores = new HashMap<>();
        playerScores.put("Alex", 2450);
        playerScores.put("Chloe", 2800);
        playerScores.put("Brian", 2800);  // Tied with Chloe at 2800 (Brian should appear first alphabetically)
        playerScores.put("Daniel", 1950);
        playerScores.put("Elena", 3200);  // Champion
        playerScores.put("Fiona", 2100);
        playerScores.put("George", 2450); // Tied with Alex at 2450 (Alex should appear first)
        playerScores.put("Hannah", 3050); // Runner-up
        playerScores.put("Ian", 1800);

        System.out.println("\nRegistered Players & Scores in Map: " + playerScores.size() + " players total");

        // Top 5 Leaderboard
        int topN = 5;
        List<PlayerEntry> top5 = getTopN(playerScores, topN);
        printLeaderboard(top5, "TOP-" + topN + " LEADERBOARD");

        // Top 3 Podium
        List<PlayerEntry> top3 = getTopN(playerScores, 3);
        printLeaderboard(top3, "TOP-3 PODIUM WINNERS");

        // Verify tie-breaking logic
        System.out.println("\n--- Tie-Breaking Verification ---");
        System.out.println("Score 2800: Brian vs Chloe -> 'Brian' correctly ranked ahead of 'Chloe': " +
                (top5.get(2).getPlayerName().equals("Brian") && top5.get(3).getPlayerName().equals("Chloe")));
    }
}
