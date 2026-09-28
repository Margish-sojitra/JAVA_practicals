import java.lang.annotation.*;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Run {
}

class MyTests {

    @Run
    public void testAddition() {
        System.out.println("Addition test executed");
    }

    @Run
    public void testSubtraction() {
        System.out.println("Subtraction test executed");
    }

    public void normalMethod() {
        System.out.println("Normal method executed");
    }

    @Run
    public void testMultiplication() {
        System.out.println("Multiplication test executed");
    }
}

class TestRunner {

    public static void runTests(Object obj) {

        int count = 0;

        Method[] methods = obj.getClass().getDeclaredMethods();

        for (Method method : methods) {

            if (method.isAnnotationPresent(Run.class)
                    && method.getParameterCount() == 0) {

                try {

                    method.invoke(obj);
                    count++;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Total tests ran: " + count);
    }
}

public class Practical7_2 {

    public static void main(String[] args) {

        MyTests tests = new MyTests();

        TestRunner.runTests(tests);
    }
}
