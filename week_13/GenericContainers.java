/**
 * Week 13 - Practice Program 1
 * Generic Containers: Box<T>, Pair<A, B>, and generic method getFirstElement()
 */
class Box<T> {
    private T value;

    public Box() {
    }

    public Box(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public String toString() {
        return "Box[" + value + "]";
    }
}

class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair(" + first + ", " + second + ")";
    }
}

public class GenericContainers {

    /**
     * Generic method that returns the first element of any type array.
     */
    public static <T> T getFirst(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[0];
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   WEEK 13 - PRACTICAL 1: GENERIC CONTAINERS      ");
        System.out.println("==================================================");

        // 1. Box<T> with String (Compile-time type safety without casting)
        Box<String> strBox = new Box<>("Hello, Java Generics!");
        String message = strBox.get(); // No cast needed!
        System.out.println("1. Box<String> value : " + message);

        // 2. Box<T> with Integer
        Box<Integer> intBox = new Box<>(100);
        int number = intBox.get(); // Autounboxing, no cast!
        System.out.println("2. Box<Integer> value: " + number);

        // 3. Pair<A, B> with String and Integer (e.g. Student name and roll number)
        Pair<String, Integer> student = new Pair<>("Alice", 101);
        System.out.println("3. Pair<String, Integer>: " + student.getFirst() + " -> Roll No: " + student.getSecond());

        // 4. Pair<A, B> with Integer and Double (e.g. Account number and Balance)
        Pair<Integer, Double> account = new Pair<>(5001, 24500.75);
        System.out.println("4. Pair<Integer, Double>: Account #" + account.getFirst() + " -> Balance: $" + account.getSecond());

        // 5. Generic method getFirst() with different array types
        String[] fruits = { "Apple", "Banana", "Cherry" };
        Integer[] scores = { 95, 88, 72, 99 };
        Double[] temperatures = { 36.6, 37.2, 38.0 };

        String firstFruit = getFirst(fruits);
        Integer firstScore = getFirst(scores);
        Double firstTemp = getFirst(temperatures);

        System.out.println("\n5. Generic Method getFirst() demonstration:");
        System.out.println("   First fruit       : " + firstFruit);
        System.out.println("   First score       : " + firstScore);
        System.out.println("   First temperature : " + firstTemp);
        System.out.println("==================================================");
    }
}
