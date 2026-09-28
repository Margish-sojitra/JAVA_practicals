
import java.lang.annotation.*;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Column {

    String name();
}

class Student {

    @Column(name = "id")
    int id;

    @Column(name = "name")
    String name;

    @Column(name = "email")
    String email;

    public void display() {

        System.out.println("ID : " + id);
        System.out.println("Name : " + name);
        System.out.println("Email : " + email);
    }
}

class RowMapper {

    public static Student map(
            String[] header,
            String[] data) {

        Student student = new Student();

        Field[] fields = Student.class.getDeclaredFields();

        for (Field field : fields) {

            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }

            Column column = field.getAnnotation(Column.class);

            String columnName = column.name();

            int index = -1;

            for (int i = 0; i < header.length; i++) {

                if (header[i].equals(columnName)) {

                    index = i;
                    break;
                }
            }

            // Missing column
            if (index == -1) {

                System.out.println(
                        "Missing column: " + columnName);

                continue;
            }

            try {

                field.setAccessible(true);

                if (field.getType() == int.class) {

                    field.setInt(
                            student,
                            Integer.parseInt(data[index]));

                } else if (field.getType() == String.class) {

                    field.set(
                            student,
                            data[index]);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return student;
    }
}

public class Practical7_3 {

    public static void main(String[] args) {

        String[] header = {
                "id",
                "name",
                "email"
        };

        String[] data = {
                "101",
                "Margish",
                "margish@gmail.com"
        };

        Student student = RowMapper.map(header, data);

        student.display();
    }
}
