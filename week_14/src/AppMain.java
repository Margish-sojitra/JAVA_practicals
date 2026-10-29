import model.Student;
import service.StudentService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Week 14 - Packaged Application Main Entry Point.
 */
public class AppMain {

    private static final Path DATA_FILE = Paths.get("week_14", "students.ser");

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   WEEK 14 - PACKAGED STUDENT APP (RUNNABLE)      ");
        System.out.println("==================================================");

        StudentService service = new StudentService();

        // 1. Automatically attempt loading saved data on startup
        try {
            service.loadFromFile(DATA_FILE);
            if (service.getCount() > 0) {
                System.out.println("Loaded " + service.getCount() + " saved students from " + DATA_FILE);
            }
        } catch (Exception ignored) {}

        // Seed initial data if empty
        if (service.getCount() == 0) {
            service.addStudent(new Student(101, "Alice Smith", 94.0));
            service.addStudent(new Student(102, "Bob Johnson", 88.5));
            service.addStudent(new Student(103, "Charlie Brown", 79.0));
        }

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Student Management Menu ---");
            System.out.println("1. Add Student");
            System.out.println("2. List All Students");
            System.out.println("3. Find Student by ID");
            System.out.println("4. Run Automated Tests (@Test)");
            System.out.println("5. Save Data to File");
            System.out.println("6. Exit");
            System.out.print("Enter choice (1-6): ");

            if (!sc.hasNextInt()) {
                System.out.println("\nExiting session.");
                break;
            }

            choice = sc.nextInt();
            sc.nextLine(); // clear newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Student ID (int): ");
                    int id = sc.hasNextInt() ? sc.nextInt() : 0;
                    sc.nextLine();

                    System.out.print("Enter Student Name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter Marks (0-100): ");
                    double marks = sc.hasNextDouble() ? sc.nextDouble() : -1;
                    sc.nextLine();

                    try {
                        service.addStudent(new Student(id, name, marks));
                        System.out.println("SUCCESS: Added student! Total count: " + service.getCount());
                    } catch (Exception e) {
                        System.err.println("ERROR: " + e.getMessage());
                    }
                }
                case 2 -> {
                    System.out.println("Registered Students (" + service.getCount() + "):");
                    for (Student s : service.getAll()) {
                        System.out.println("  " + s);
                    }
                }
                case 3 -> {
                    System.out.print("Enter ID: ");
                    int id = sc.hasNextInt() ? sc.nextInt() : 0;
                    sc.nextLine();
                    Student s = service.findById(id);
                    System.out.println(s != null ? "Found: " + s : "No student found with ID " + id);
                }
                case 4 -> AppTest.runAll();
                case 5 -> {
                    try {
                        service.saveToFile(DATA_FILE);
                        System.out.println("SUCCESS: Saved " + service.getCount() + " students to " + DATA_FILE);
                    } catch (Exception e) {
                        System.err.println("Save failed: " + e.getMessage());
                    }
                }
                case 6 -> {
                    try {
                        service.saveToFile(DATA_FILE);
                        System.out.println("Saved students. Goodbye!");
                    } catch (Exception ignored) {}
                }
                default -> System.out.println("Invalid selection. Choose 1-6.");
            }

        } while (choice != 6);

        sc.close();
    }
}
