package model;

import java.io.Serializable;

/**
 * Model Entity (Serializable for file persistence).
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student s)) return false;
        return id == s.id && Double.compare(s.marks, marks) == 0 && name.equals(s.name);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Student[ID=" + id + ", Name=" + name + ", Marks=" + marks + "]";
    }
}
