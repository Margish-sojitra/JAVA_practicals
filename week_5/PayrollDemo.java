abstract class Employee {

    protected String name;
    protected String id;

    Employee(String name, String id) {
        this.name = name;
        this.id = id;
    }

    abstract double monthlySalary();
}

class FullTime extends Employee {

    private double fixedSalary;

    FullTime(String name, String id, double fixedSalary) {
        super(name, id);
        this.fixedSalary = fixedSalary;
    }

    @Override
    double monthlySalary() {
        return fixedSalary;
    }
}

class PartTime extends Employee {

    private int hours;
    private double rate;

    PartTime(String name, String id, int hours, double rate) {
        super(name, id);
        this.hours = hours;
        this.rate = rate;
    }

    @Override
    double monthlySalary() {
        return hours * rate;
    }
}

class Intern extends Employee {

    private double stipend;

    Intern(String name, String id, double stipend) {
        super(name, id);
        this.stipend = stipend;
    }

    @Override
    double monthlySalary() {
        return stipend;
    }
}

public class PayrollDemo {

    public static void main(String[] args) {

        Employee[] employees = {
            new FullTime("Rahul", "E101", 50000),
            new PartTime("Amit", "E102", 80, 300),
            new Intern("Neha", "E103", 15000),
            new FullTime("Priya", "E104", 60000)
        };

        double totalSalary = 0;

        for (Employee employee : employees) {

            double salary = employee.monthlySalary();

            System.out.printf(
                "Name: %s | ID: %s | Salary: %.2f",
                employee.name,
                employee.id,
                salary
            );

            if (employee instanceof Intern) {
                System.out.print(" | Note: This is an Intern");
            }

            System.out.println();

            totalSalary += salary;
        }

        System.out.printf("%nTotal Payroll = %.2f%n", totalSalary);
    }
}