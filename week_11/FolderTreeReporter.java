import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Week 11 - Practice Program 3 (For Advanced Learners): Folder Tree Reporter
 * Demonstrates:
 * - Walking a directory tree using NIO Files.walkFileTree and SimpleFileVisitor
 * - Extracting metadata: filename, relative path, size, last modified timestamp
 * - Producing a structured audit report with counts and totals
 * - Writing the report summary to a file
 */
public class FolderTreeReporter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    static class ReportStats {
        int directoryCount = 0;
        int fileCount = 0;
        long totalBytes = 0;
        final List<String> lines = new ArrayList<>();
    }

    public static ReportStats generateFolderReport(Path rootDir) throws IOException {
        ReportStats stats = new ReportStats();

        stats.lines.add("================================================================================");
        stats.lines.add("                     NIO FOLDER TREE AUDIT REPORT                              ");
        stats.lines.add("================================================================================");
        stats.lines.add("Target Directory: " + rootDir.toAbsolutePath().normalize());
        stats.lines.add("Generated At    : " + java.time.LocalDateTime.now());
        stats.lines.add("--------------------------------------------------------------------------------");
        stats.lines.add(String.format("%-40s | %12s | %-19s", "FILE / DIRECTORY NAME", "SIZE (BYTES)", "LAST MODIFIED"));
        stats.lines.add("--------------------------------------------------------------------------------");

        Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                stats.directoryCount++;
                Path relative = rootDir.relativize(dir);
                String display = relative.toString().isEmpty() ? "." : "[DIR] " + relative;
                stats.lines.add(String.format("%-40s | %12s | %-19s",
                        truncate(display, 40), "-", DATE_TIME_FORMATTER.format(attrs.lastModifiedTime().toInstant())));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                stats.fileCount++;
                stats.totalBytes += attrs.size();
                Path relative = rootDir.relativize(file);
                String display = "  " + relative;
                stats.lines.add(String.format("%-40s | %12d | %-19s",
                        truncate(display, 40), attrs.size(), DATE_TIME_FORMATTER.format(attrs.lastModifiedTime().toInstant())));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                stats.lines.add("[ACCESS DENIED / ERROR] " + file.getFileName() + " (" + exc.getMessage() + ")");
                return FileVisitResult.CONTINUE;
            }
        });

        stats.lines.add("--------------------------------------------------------------------------------");
        stats.lines.add(String.format("Total Directories Visited : %d", stats.directoryCount));
        stats.lines.add(String.format("Total Files Found        : %d", stats.fileCount));
        stats.lines.add(String.format("Total Combined Size      : %d bytes (%.2f KB / %.2f MB)",
                stats.totalBytes, stats.totalBytes / 1024.0, stats.totalBytes / (1024.0 * 1024.0)));
        stats.lines.add("================================================================================");

        return stats;
    }

    private static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return "..." + text.substring(text.length() - (maxLength - 3));
    }

    public static void main(String[] args) {
        System.out.println("=== Week 11 Practical 3: NIO Folder Tree Walker & Reporter ===");

        // Target directory to scan: either args[0] or current directory
        Path targetDir = args.length > 0 ? Paths.get(args[0]) : Paths.get(".");
        Path reportFile = Paths.get("folder_tree_report.txt");

        try {
            System.out.println("Scanning directory: " + targetDir.toAbsolutePath().normalize());
            ReportStats stats = generateFolderReport(targetDir);

            // Write report to file
            try (BufferedWriter writer = Files.newBufferedWriter(reportFile)) {
                for (String line : stats.lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            // Print report to standard output
            for (String line : stats.lines) {
                System.out.println(line);
            }

            System.out.println("\nReport has been successfully written to: " + reportFile.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Failed to walk folder tree: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
