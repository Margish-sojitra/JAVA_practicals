package service;

import model.Student;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer with in-memory ArrayList and Object Serialization.
 */
public class StudentService {

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
            throw new IllegalArgumentException("Marks must be between 0 and 100");
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

    public void clear() {
        students.clear();
    }

    /**
     * Requirement 14.2: Save data using ObjectOutputStream.
     */
    public void saveToFile(Path file) throws IOException {
        if (file.getParent() != null && !Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(new ArrayList<>(students));
        }
    }

    /**
     * Requirement 14.2: Reload data using ObjectInputStream.
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile(Path file) throws IOException, ClassNotFoundException {
        if (!Files.exists(file)) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            List<Student> loaded = (List<Student>) ois.readObject();
            students.clear();
            if (loaded != null) {
                students.addAll(loaded);
            }
        }
    }
}
