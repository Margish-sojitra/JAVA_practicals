import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;

/**
 * Week 11 - Practice Program 2: Log Analyzer
 * Demonstrates:
 * - Creating text log files using NIO Files and Path
 * - Reading files line by line using BufferedReader / Files
 * - Counting total lines and keyword matches across multiple files
 * - Reading file attributes (size, last modified time) using Files.readAttributes
 */
public class LogAnalyzer {

    public static void main(String[] args) {
        System.out.println("=== Week 11 Practical 2: Log Analyzer with NIO Path & Files ===");

        Path logsDir = Paths.get("practice_logs");
        String keyword = "ERROR";

        try {
            // 1. Create logs directory if not present
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
            }

            // 2. Create sample log files
            Path log1 = logsDir.resolve("auth_service.log");
            Path log2 = logsDir.resolve("payment_gateway.log");
            Path log3 = logsDir.resolve("database.log");

            List<String> log1Lines = Arrays.asList(
                "[INFO] 10:00:01 User alice logged in",
                "[WARN] 10:02:15 Password attempt failed for bob",
                "[ERROR] 10:02:18 Account locked for bob",
                "[INFO] 10:05:00 Session timeout for user charlie"
            );

            List<String> log2Lines = Arrays.asList(
                "[INFO] 10:10:00 Processing transaction TXN-1001",
                "[ERROR] 10:10:05 Payment gateway timeout on TXN-1001",
                "[ERROR] 10:10:06 Retry failed for TXN-1001",
                "[INFO] 10:11:00 Transaction TXN-1002 completed successfully",
                "[INFO] 10:12:00 Payment settlement dispatched"
            );

            List<String> log3Lines = Arrays.asList(
                "[INFO] 09:59:00 Database connection pool initialized",
                "[INFO] 10:15:30 Checkpoint saved to disk",
                "[ERROR] 10:20:00 Deadlock detected on table ACCOUNTS - transaction aborted",
                "[INFO] 10:20:01 Transaction rollback executed"
            );

            Files.write(log1, log1Lines);
            Files.write(log2, log2Lines);
            Files.write(log3, log3Lines);

            List<Path> logFiles = Arrays.asList(log1, log2, log3);

            long totalLinesAllFiles = 0;
            long keywordMatchesAllFiles = 0;

            System.out.println("\n--- Analyzing Log Files for Keyword: '" + keyword + "' ---");

            for (Path file : logFiles) {
                // Read file attributes (size, modification time)
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                long fileSize = attrs.size();
                String modifiedTime = attrs.lastModifiedTime().toString();

                System.out.println("\nFile: " + file.getFileName());
                System.out.println("  Path: " + file.toAbsolutePath());
                System.out.println("  File Size: " + fileSize + " bytes");
                System.out.println("  Last Modified: " + modifiedTime);

                long fileLines = 0;
                long fileKeywordCount = 0;

                // Read line-by-line using BufferedReader
                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines++;
                        if (line.contains(keyword)) {
                            fileKeywordCount++;
                            System.out.println("    [Match L" + fileLines + "]: " + line);
                        }
                    }
                }

                System.out.printf("  Summary: %d lines total, %d line(s) contain '%s'%n",
                        fileLines, fileKeywordCount, keyword);

                totalLinesAllFiles += fileLines;
                keywordMatchesAllFiles += fileKeywordCount;
            }

            System.out.println("\n================ Overall Summary ================");
            System.out.println("Total Files Analyzed : " + logFiles.size());
            System.out.println("Total Lines Counted  : " + totalLinesAllFiles);
            System.out.println("Total '" + keyword + "' Matches : " + keywordMatchesAllFiles);
            System.out.println("==================================================");

        } catch (IOException e) {
            System.err.println("Error analyzing log files: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
