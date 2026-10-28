package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Week 11 MiniBank - ReportGenerator
 * Walks the logs folder using Files.newDirectoryStream,
 * reads transaction files line-by-line with BufferedReader,
 * calculates total deposits and withdrawals in long variables,
 * reads file attributes (size, last modified) using Files.readAttributes,
 * and writes an end-of-day summary report to a report file.
 */
public class ReportGenerator {

    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    /**
     * Walks the logs folder, aggregates deposits & withdrawals, reads attributes,
     * and outputs the summary report to the specified report file.
     *
     * @param logsFolder path to the directory containing transaction logs
     * @param reportFile path to the file where summary report will be written
     * @return the generated report text
     * @throws IOException if an I/O error occurs
     */
    public static String generateReport(Path logsFolder, Path reportFile) throws IOException {
        if (!Files.exists(logsFolder)) {
            Files.createDirectories(logsFolder);
        }

        long totalDeposits = 0L;
        long totalWithdrawals = 0L;
        int filesProcessed = 0;
        int totalTransactions = 0;

        List<String> reportLines = new ArrayList<>();
        reportLines.add("================================================================================");
        reportLines.add("                   MINIBANK END-OF-DAY AUDIT & TRANSACTION REPORT               ");
        reportLines.add("================================================================================");
        reportLines.add("Logs Folder: " + logsFolder.toAbsolutePath().normalize());
        reportLines.add("Report Time: " + java.time.LocalDateTime.now());
        reportLines.add("--------------------------------------------------------------------------------");
        reportLines.add(String.format("%-28s | %10s | %-19s | %s", "LOG FILE NAME", "SIZE (B)", "LAST MODIFIED", "STATUS"));
        reportLines.add("--------------------------------------------------------------------------------");

        // Walk the logs folder using Files.newDirectoryStream inside try-with-resources
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder)) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }

                filesProcessed++;
                // Read file attributes using Files.readAttributes
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                long fileSize = attrs.size();
                String modifiedTime = FORMATTER.format(attrs.lastModifiedTime().toInstant());

                reportLines.add(String.format("%-28s | %10d | %-19s | PROCESSED",
                        file.getFileName().toString(), fileSize, modifiedTime));

                // Read line-by-line using BufferedReader
                try (BufferedReader reader = Files.newBufferedReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }

                        totalTransactions++;
                        // Parse log entry format: TYPE ACCOUNT_ID AMOUNT [OPTIONAL_NOTES]
                        // e.g.: "DEPOSIT 101 5000" or "WITHDRAW 102 2000" or comma separated
                        String[] tokens = line.split("[,\\s]+");
                        if (tokens.length >= 3) {
                            String type = tokens[0].toUpperCase();
                            // If timestamp prefixed, shift tokens
                            if (tokens[0].startsWith("[") && tokens.length >= 4) {
                                type = tokens[tokens.length - 3].toUpperCase();
                            }

                            try {
                                long amount = Long.parseLong(tokens[tokens.length - 1]);
                                if (line.toUpperCase().contains("DEPOSIT")) {
                                    totalDeposits += amount;
                                } else if (line.toUpperCase().contains("WITHDRAW")) {
                                    totalWithdrawals += amount;
                                }
                            } catch (NumberFormatException ignored) {
                                // Try second token if last was not numeric
                                for (String token : tokens) {
                                    try {
                                        long val = Long.parseLong(token);
                                        if (val > 0) {
                                            if (line.toUpperCase().contains("DEPOSIT")) {
                                                totalDeposits += val;
                                                break;
                                            } else if (line.toUpperCase().contains("WITHDRAW")) {
                                                totalWithdrawals += val;
                                                break;
                                            }
                                        }
                                    } catch (NumberFormatException ignoredInner) {}
                                }
                            }
                        }
                    }
                }
            }
        }

        long netFlow = totalDeposits - totalWithdrawals;

        reportLines.add("--------------------------------------------------------------------------------");
        reportLines.add(String.format("Total Log Files Processed : %d", filesProcessed));
        reportLines.add(String.format("Total Transactions Read   : %d", totalTransactions));
        reportLines.add(String.format("Total Deposits Amount     : +$%d", totalDeposits));
        reportLines.add(String.format("Total Withdrawals Amount  : -$%d", totalWithdrawals));
        reportLines.add(String.format("Net Cash Flow             : %s$%d", (netFlow >= 0 ? "+" : "-"), Math.abs(netFlow)));
        reportLines.add("================================================================================");

        // Ensure parent directories of report file exist
        if (reportFile.getParent() != null && !Files.exists(reportFile.getParent())) {
            Files.createDirectories(reportFile.getParent());
        }

        // Write summary to report file
        StringBuilder sb = new StringBuilder();
        try (BufferedWriter writer = Files.newBufferedWriter(reportFile)) {
            for (String rLine : reportLines) {
                writer.write(rLine);
                writer.newLine();
                sb.append(rLine).append(System.lineSeparator());
            }
        }

        return sb.toString();
    }
}
