import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveLoadDemo {

    static class Student implements Serializable {
        private static final long serialVersionUID = 1L;

        int id;
        String name;
        double marks;
        transient String password;

        public Student(int id, String name, double marks, String password) {
            this.id = id;
            this.name = name;
            this.marks = marks;
            this.password = password;
        }

        @Override
        public String toString() {
            return "Student{id=" + id + ", name='" + name + "', marks=" + marks + ", password=" + password + "}";
        }
    }

    public static void main(String[] args) {
        String filename = "students.ser";

        Student[] students = {
            new Student(101, "Alice", 85.5, "secret123"),
            new Student(102, "Bob", 90.0, "pass456"),
            new Student(103, "Charlie", 78.2, "qwerty789")
        };

        System.out.println("Before saving:");
        for (Student s : students) {
            System.out.println(s);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(students);
            System.out.println("\nSaved array to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving: " + e.getMessage());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Student[] loaded = (Student[]) ois.readObject();

            System.out.println("\nAfter loading back:");
            for (Student s : loaded) {
                System.out.println(s);
            }

            System.out.println("\nNotice password is null because it was marked transient.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading: " + e.getMessage());
        }

        try {
            Files.deleteIfExists(Paths.get(filename));
        } catch (IOException ignored) {}
    }
}
