import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Week 13 - Practice Program 3 (For Advanced Learners)
 * Generic Stack container backed by a resizable Object array.
 * Reused for two different types and handles empty conditions sensibly.
 */
class SimpleStack<T> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 4;

    public SimpleStack() {
        this(DEFAULT_CAPACITY);
    }

    public SimpleStack(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    /**
     * Pushes an item onto the top of this stack, automatically growing the array if full.
     */
    public void push(T item) {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
        elements[size++] = item;
    }

    /**
     * Removes and returns the object at the top of this stack.
     * Throws EmptyStackException if this stack is empty.
     */
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T item = (T) elements[--size];
        elements[size] = null; // Prevent memory leak
        return item;
    }

    /**
     * Looks at the object at the top of this stack without removing it.
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return (T) elements[size - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("] (Top: ").append(isEmpty() ? "none" : elements[size - 1]).append(")");
        return sb.toString();
    }
}

public class GenericStack {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   WEEK 13 - PRACTICAL 3: GENERIC STACK CONTAINER ");
        System.out.println("==================================================");

        // --- Use Case 1: SimpleStack with String (Browser Navigation History) ---
        System.out.println("1. Demonstrating SimpleStack<String> (Browser History):");
        SimpleStack<String> history = new SimpleStack<>();

        history.push("https://google.com");
        history.push("https://github.com");
        history.push("https://stackoverflow.com");

        System.out.println("   Current Stack : " + history);
        System.out.println("   Top element   : " + history.peek());
        System.out.println("   Popped (Back) : " + history.pop());
        System.out.println("   Popped (Back) : " + history.pop());
        System.out.println("   Remaining     : " + history);

        // --- Use Case 2: SimpleStack with Integer (Undo Operation Action IDs) ---
        System.out.println("\n2. Demonstrating SimpleStack<Integer> (Undo Action IDs):");
        SimpleStack<Integer> undoStack = new SimpleStack<>();

        undoStack.push(101);
        undoStack.push(102);
        undoStack.push(103);
        undoStack.push(104);
        undoStack.push(105); // Triggers dynamic array resizing!

        System.out.println("   Current Stack : " + undoStack);
        System.out.println("   Undo Action   : " + undoStack.pop());
        System.out.println("   Undo Action   : " + undoStack.pop());
        System.out.println("   Remaining     : " + undoStack);

        // --- 3. Sensible Empty Case Handling ---
        System.out.println("\n3. Sensible Empty Stack Handling:");
        SimpleStack<Double> emptyStack = new SimpleStack<>();

        System.out.println("   Is stack empty? " + emptyStack.isEmpty());
        try {
            System.out.println("   Attempting pop() on empty stack...");
            emptyStack.pop();
        } catch (EmptyStackException e) {
            System.out.println("   CAUGHT EXPECTED EmptyStackException: Stack is empty, cannot pop element.");
        }

        try {
            System.out.println("   Attempting peek() on empty stack...");
            emptyStack.peek();
        } catch (EmptyStackException e) {
            System.out.println("   CAUGHT EXPECTED EmptyStackException: Stack is empty, cannot peek element.");
        }
        System.out.println("==================================================");
    }
}
