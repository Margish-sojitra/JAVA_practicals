import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

public class LogAnalyzer {

    public static void main(String[] args) {
        Path log1 = Paths.get("app1.log");
        Path log2 = Paths.get("app2.log");
        Path log3 = Paths.get("app3.log");

        String keyword = "ERROR";

        try {
            Files.write(log1, Arrays.asList(
                "INFO User logged in",
                "WARN Slow response time",
                "ERROR Failed to connect to database"
            ));

            Files.write(log2, Arrays.asList(
                "INFO Payment started",
                "ERROR Transaction timeout",
                "ERROR Card declined",
                "INFO Retrying transaction"
            ));

            Files.write(log3, Arrays.asList(
                "INFO Server started on port 8080",
                "INFO Health check OK",
                "ERROR Out of memory"
            ));

            List<Path> files = Arrays.asList(log1, log2, log3);

            long totalLines = 0;
            long keywordCount = 0;

            for (Path file : files) {
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                System.out.println("File: " + file.getFileName() + " | Size: " + attrs.size() + " bytes");

                long fileLines = 0;
                long fileMatches = 0;

                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines++;
                        if (line.contains(keyword)) {
                            fileMatches++;
                        }
                    }
                }

                System.out.println("  Lines: " + fileLines + ", Matches for '" + keyword + "': " + fileMatches);
                totalLines += fileLines;
                keywordCount += fileMatches;
            }

            System.out.println("\nTotal lines across all files: " + totalLines);
            System.out.println("Total lines with '" + keyword + "': " + keywordCount);

            Files.deleteIfExists(log1);
            Files.deleteIfExists(log2);
            Files.deleteIfExists(log3);

        } catch (IOException e) {
            System.out.println("Error reading files: " + e.getMessage());
        }
    }
}
