import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Week 14 - Practice Program 1
 * Tiny layered app (Model, Service with ArrayList, Menu Main) with @Test methods.
 */

// Custom @Test annotation for practice unit testing
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Test {
}

// --- 1. MODEL LAYER ---
class Student {
    private final int id;
    private final String name;
    private final double marks;

    public Student(int id, String name, double marks) {
        this.id = id;
        this.name = name;
        this.marks = marks;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getMarks() {
        return marks;
    }

    @Override
    public String toString() {
        return "Student[ID=" + id + ", Name=" + name + ", Marks=" + marks + "]";
    }
}

// --- 2. SERVICE LAYER ---
class StudentService {
    private final List<Student> students = new ArrayList<>();

    public void addStudent(Student s) {
        if (s == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (s.getId() <= 0) {
            throw new IllegalArgumentException("Student ID must be positive: " + s.getId());
        }
        if (s.getName() == null || s.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        if (s.getMarks() < 0 || s.getMarks() > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100. Received: " + s.getMarks());
        }
        if (findById(s.getId()) != null) {
            throw new IllegalStateException("Duplicate student ID: " + s.getId());
        }

        students.add(s);
    }

    public Student findById(int id) {
        for (Student s : students) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public List<Student> getAll() {
        return new ArrayList<>(students);
    }

    public int getCount() {
        return students.size();
    }
}

// --- 3. TEST SUITE (@Test) ---
class StudentServiceTest {

    private StudentService service;

    public void setUp() {
        service = new StudentService();
    }

    /**
     * Test 1: Adding a valid student increases the count.
     */
    @Test
    public void testAddIncreasesCount() {
        int initialCount = service.getCount();
        service.addStudent(new Student(1, "Alice", 92.5));
        assertEquals(initialCount + 1, service.getCount(), "Adding student must increase count by 1");
    }

    /**
     * Test 2: An invalid action (negative marks) throws IllegalArgumentException.
     */
    @Test
    public void testInvalidMarksThrowsException() {
        boolean threw = false;
        try {
            service.addStudent(new Student(2, "Bob", -15.0)); // Invalid marks
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        assertTrue(threw, "Negative marks must throw IllegalArgumentException");
    }

    /**
     * Test 3: Duplicate ID throws IllegalStateException.
     */
    @Test
    public void testDuplicateIdThrowsException() {
        service.addStudent(new Student(3, "Charlie", 85.0));
        boolean threw = false;
        try {
            service.addStudent(new Student(3, "DuplicateCharlie", 90.0)); // Duplicate ID
        } catch (IllegalStateException e) {
            threw = true;
        }
        assertTrue(threw, "Duplicate student ID must throw IllegalStateException");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void runTests() {
        System.out.println("\n--- Running Practice Unit Tests (@Test) ---");
        StudentServiceTest suite = new StudentServiceTest();
        Method[] methods = StudentServiceTest.class.getDeclaredMethods();
        int passed = 0;
        int failed = 0;

        for (Method m : methods) {
            if (m.isAnnotationPresent(Test.class)) {
                try {
                    suite.setUp();
                    m.invoke(suite);
                    System.out.println("  [PASS] " + m.getName());
                    passed++;
                } catch (Throwable t) {
                    Throwable cause = t.getCause() != null ? t.getCause() : t;
                    System.err.println("  [FAIL] " + m.getName() + " -> " + cause.getMessage());
                    failed++;
                }
            }
        }
        System.out.println("Test Results: " + passed + " Passed, " + failed + " Failed.");
    }
}

// --- 4. MAIN MENU ---
public class TinyLayeredApp {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   WEEK 14 - PRACTICAL 1: TINY LAYERED APP        ");
        System.out.println("==================================================");

        StudentService service = new StudentService();
        // Seed default students
        service.addStudent(new Student(101, "Alice Smith", 94.0));
        service.addStudent(new Student(102, "Bob Johnson", 81.5));

        // Automatically run unit tests to verify before menu
        StudentServiceTest.runTests();

        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n--- Student Management Menu ---");
            System.out.println("1. Add Student");
            System.out.println("2. List All Students");
            System.out.println("3. Find Student by ID");
            System.out.println("4. Re-run Unit Tests (@Test)");
            System.out.println("5. Exit");
            System.out.print("Enter choice (1-5): ");

            if (!sc.hasNextInt()) {
                System.out.println("\nExiting application.");
                break;
            }

            choice = sc.nextInt();
            sc.nextLine(); // clear newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter ID (integer): ");
                    int id = sc.hasNextInt() ? sc.nextInt() : 0;
                    sc.nextLine();

                    System.out.print("Enter Name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter Marks (0-100): ");
                    double marks = sc.hasNextDouble() ? sc.nextDouble() : -1;
                    sc.nextLine();

                    try {
                        service.addStudent(new Student(id, name, marks));
                        System.out.println("SUCCESS: Added student! Total count is now " + service.getCount());
                    } catch (Exception e) {
                        System.err.println("ERROR: " + e.getMessage());
                    }
                }
                case 2 -> {
                    System.out.println("All Students (" + service.getCount() + " registered):");
                    for (Student s : service.getAll()) {
                        System.out.println("  " + s);
                    }
                }
                case 3 -> {
                    System.out.print("Enter ID to search: ");
                    int id = sc.hasNextInt() ? sc.nextInt() : 0;
                    sc.nextLine();

                    Student found = service.findById(id);
                    if (found != null) {
                        System.out.println("FOUND: " + found);
                    } else {
                        System.out.println("NOT FOUND: No student with ID " + id);
                    }
                }
                case 4 -> StudentServiceTest.runTests();
                case 5 -> System.out.println("Exiting. Thank you!");
                default -> System.out.println("Invalid choice. Please select 1-5.");
            }

        } while (choice != 5);

        sc.close();
    }
}
