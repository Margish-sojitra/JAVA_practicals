package service;

import model.annotation.Audit;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Simple Reflective Audit Logger (Practical 14 Optional Requirement).
 * Uses reflection to check if a method has @Audit and logs its call.
 */
public class AuditLogger {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path LOG_FILE = Paths.get("mini_project", "logs", "audit.log");

    /**
     * Inspects target class for @Audit on the called method using Reflection.
     */
    public static void log(Class<?> clazz, String methodName, Object... args) {
        try {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(Audit.class)) {
                    Audit audit = method.getAnnotation(Audit.class);
                    String action = audit.action().isEmpty() ? methodName.toUpperCase() : audit.action();
                    String time = LocalDateTime.now().format(FORMAT);
                    String argsStr = args == null || args.length == 0 ? "none" : Arrays.toString(args);

                    String message = String.format("[AUDIT %s] Action: %s | Method: %s | Args: %s",
                            time, action, methodName, argsStr);

                    System.out.println(message);

                    // Save to logs/audit.log
                    try {
                        TransactionLog.append(LOG_FILE, message);
                    } catch (IOException ignored) {}

                    break;
                }
            }
        } catch (Exception ignored) {}
    }
}
