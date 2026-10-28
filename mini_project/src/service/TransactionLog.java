package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

/**
 * Week 11 MiniBank - TransactionLog
 * Provides an append-only audit log for bank transactions.
 * Uses Files.write(...) with CREATE and APPEND options.
 */
public class TransactionLog {

    /**
     * Appends a log line to the specified file.
     * Uses Files.write(...) with StandardOpenOption.CREATE and StandardOpenOption.APPEND.
     *
     * @param file target log file path
     * @param line transaction entry to append
     * @throws IOException if an I/O error occurs
     */
    public static void append(Path file, String line) throws IOException {
        if (file.getParent() != null && !Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        Files.write(
            file,
            Collections.singletonList(line),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
    }
}
