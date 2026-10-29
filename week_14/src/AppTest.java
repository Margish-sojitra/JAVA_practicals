import model.Student;
import service.StudentService;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Test {
}

/**
 * Week 14 - Automated Unit Tests
 * Includes test for saving and reloading data unchanged (Requirement 2).
 */
public class AppTest {

    private StudentService service;

    public void setUp() {
        service = new StudentService();
    }

    @Test
    public void testAddIncreasesCount() {
        int initial = service.getCount();
        service.addStudent(new Student(1, "Alice", 95.0));
        assertEquals(initial + 1, service.getCount(), "Count must increase by 1");
    }

    @Test
    public void testInvalidActionThrows() {
        boolean threw = false;
        try {
            service.addStudent(new Student(2, "Bob", -10.0));
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        assertTrue(threw, "Negative marks must throw IllegalArgumentException");
    }

    /**
     * Requirement 14.2: Test that saves and reloads its data unchanged.
     */
    @Test
    public void testSaveAndReloadDataUnchanged() throws IOException, ClassNotFoundException {
        Path tempFile = Files.createTempFile("students_test", ".ser");
        tempFile.toFile().deleteOnExit();

        Student s1 = new Student(101, "Emma Watson", 88.5);
        Student s2 = new Student(102, "Daniel Radcliffe", 91.0);

        service.addStudent(s1);
        service.addStudent(s2);

        // 1. Save to file
        service.saveToFile(tempFile);

        // 2. Reload into a fresh service instance
        StudentService newService = new StudentService();
        newService.loadFromFile(tempFile);

        // 3. Assert count and elements are identical and unchanged
        assertEquals(2, newService.getCount(), "Reloaded count must match saved count");
        List<Student> reloadedList = newService.getAll();
        assertEquals(s1.getId(), reloadedList.get(0).getId(), "First student ID must match");
        assertEquals(s1.getName(), reloadedList.get(0).getName(), "First student Name must match");
        assertEquals(s2.getId(), reloadedList.get(1).getId(), "Second student ID must match");
        assertEquals(s2.getName(), reloadedList.get(1).getName(), "Second student Name must match");

        Files.deleteIfExists(tempFile);
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void runAll() {
        System.out.println("==================================================");
        System.out.println("       WEEK 14 - AUTOMATED TEST SUITE (@Test)     ");
        System.out.println("==================================================");
        AppTest suite = new AppTest();
        Method[] methods = AppTest.class.getDeclaredMethods();
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
        System.out.println("--------------------------------------------------");
        System.out.println("Total Tests: " + (passed + failed) + " | Passed: " + passed + " | Failed: " + failed);
        System.out.println("==================================================");
    }

    public static void main(String[] args) {
        runAll();
    }
}
