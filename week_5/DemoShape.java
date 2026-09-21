

abstract class Shape {
    abstract double area();
}

class Circle extends Shape {
    private double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    
    double area() {
        return Math.PI * radius * radius;
    }
}

class Rectangle extends Shape {
    private double length, width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    
    double area() {
        return length * width;
    }
}

class Triangle extends Shape {
    private double base, height;

    Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    
    double area() {
        return 0.5 * base * height;
    }
}

public class DemoShape {
    public static void main(String[] args) {

        Shape[] shapes = {
            new Circle(5),
            new Rectangle(4, 6),
            new Triangle(3, 8),
            new Circle(2)
        };

        double total = 0;
        double largest = 0;

        for (Shape shape : shapes) {
            double currentArea = shape.area();

            System.out.println("Area: " + currentArea);

            total += currentArea;

            if (currentArea > largest) {
                largest = currentArea;
            }
        }

        System.out.println("Running total: " + total);
        System.out.println("Largest area: " + largest);
    }
}