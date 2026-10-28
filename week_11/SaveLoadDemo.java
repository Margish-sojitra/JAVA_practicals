import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Week 11 - Practice Program 1: Save/load with Serialization
 * Demonstrates:
 * - Serializable interface implementation with serialVersionUID
 * - Saving an array of objects with ObjectOutputStream (try-with-resources)
 * - Restoring objects with ObjectInputStream
 * - Proving transient fields are omitted during serialization
 */
public class SaveLoadDemo {

    static class StudentRecord implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int id;
        private final String name;
        private final double gpa;

        // transient field: will NOT be persisted during serialization
        private final transient String securityPin;

        public StudentRecord(int id, String name, double gpa, String securityPin) {
            this.id = id;
            this.name = name;
            this.gpa = gpa;
            this.securityPin = securityPin;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getGpa() {
            return gpa;
        }

        public String getSecurityPin() {
            return securityPin;
        }

        @Override
        public String toString() {
            return String.format("StudentRecord[id=%d, name='%s', gpa=%.2f, securityPin=%s]",
                    id, name, gpa, (securityPin != null ? "'" + securityPin + "'" : "null (NOT SAVED)"));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Week 11 Practical 1: Object Serialization & Transient Field Demo ===");

        String filename = "students_data.ser";

        // 1. Create an array of StudentRecord objects
        StudentRecord[] originalStudents = new StudentRecord[] {
            new StudentRecord(101, "Alice Smith", 3.92, "PIN-8821"),
            new StudentRecord(102, "Bob Jones", 3.65, "PIN-3310"),
            new StudentRecord(103, "Charlie Patel", 3.88, "PIN-9904")
        };

        System.out.println("\n--- 1. Original Objects Before Serialization ---");
        for (StudentRecord student : originalStudents) {
            System.out.println(student);
        }

        // 2. Save array to file using ObjectOutputStream inside try-with-resources
        System.out.println("\n--- 2. Saving Array to File (" + filename + ") ---");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(originalStudents);
            System.out.println("Objects serialized successfully to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving objects: " + e.getMessage());
            return;
        }

        // 3. Read back into fresh objects using ObjectInputStream inside try-with-resources
        System.out.println("\n--- 3. Reading Array Back Using ObjectInputStream ---");
        StudentRecord[] loadedStudents = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            loadedStudents = (StudentRecord[]) ois.readObject();
            System.out.println("Objects deserialized successfully from " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading objects: " + e.getMessage());
            return;
        }

        // 4. Verify values and show that the transient field was not saved
        System.out.println("\n--- 4. Deserialized Objects Verification ---");
        boolean valuesSurvived = true;
        boolean transientFieldOmitted = true;

        for (int i = 0; i < loadedStudents.length; i++) {
            StudentRecord loaded = loadedStudents[i];
            StudentRecord orig = originalStudents[i];

            System.out.println(loaded);

            if (loaded.getId() != orig.getId() ||
                !loaded.getName().equals(orig.getName()) ||
                Double.compare(loaded.getGpa(), orig.getGpa()) != 0) {
                valuesSurvived = false;
            }

            if (loaded.getSecurityPin() != null) {
                transientFieldOmitted = false;
            }
        }

        System.out.println("\n--- Verification Summary ---");
        System.out.println("Regular fields (id, name, gpa) survived: " + (valuesSurvived ? "SUCCESS [YES]" : "FAILED [NO]"));
        System.out.println("Transient field (securityPin) was not saved: " + (transientFieldOmitted ? "SUCCESS [YES - Became null]" : "FAILED [NO]"));
    }
}
