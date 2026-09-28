
class FileResource implements AutoCloseable {

    public FileResource() {
        System.out.println("Resource opened.");
    }

    public void use() {
        System.out.println("Using the resource...");
    }

    @Override
    public void close() {
        System.out.println("Resource closed.");
    }
}

public class AutoCloseDemo {

    public static void main(String[] args) {

        try {
            try (FileResource resource = new FileResource()) {

                resource.use();

                System.out.println("Throwing an exception...");
                throw new RuntimeException("Original error occurred.");

            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        System.out.println("Program continues normally.");
    }
}

