import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FolderTreeReporter {

    public static void main(String[] args) {
        Path targetDir = args.length > 0 ? Paths.get(args[0]) : Paths.get(".");
        Path reportFile = Paths.get("folder_report.txt");

        List<String> report = new ArrayList<>();
        int[] dirCount = {0};
        int[] fileCount = {0};
        long[] totalSize = {0};

        report.add("Folder Report for: " + targetDir.toAbsolutePath().normalize());
        report.add("--------------------------------------------------");

        try {
            Files.walkFileTree(targetDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    dirCount[0]++;
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    fileCount[0]++;
                    totalSize[0] += attrs.size();
                    String line = file.getFileName() + " | " + attrs.size() + " bytes | " + attrs.lastModifiedTime();
                    report.add(line);
                    return FileVisitResult.CONTINUE;
                }
            });

            report.add("--------------------------------------------------");
            report.add("Total Directories: " + dirCount[0]);
            report.add("Total Files: " + fileCount[0]);
            report.add("Total Size: " + totalSize[0] + " bytes");

            try (BufferedWriter writer = Files.newBufferedWriter(reportFile)) {
                for (String line : report) {
                    writer.write(line);
                    writer.newLine();
                    System.out.println(line);
                }
            }

            System.out.println("\nReport saved to " + reportFile.getFileName());

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
