import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
}

class SignupForm {

    @NotBlank
    @MaxLength(20)
    String username;

    @NotBlank
    @MaxLength(30)
    String email;

    SignupForm(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

class Validator {

    public static List<String> validate(Object obj) {

        List<String> errors = new ArrayList<>();

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {

            field.setAccessible(true);

            try {
                Object value = field.get(obj);

                if (field.isAnnotationPresent(NotBlank.class)) {

                    if (value == null ||
                            value.toString().trim().isEmpty()) {

                        errors.add(field.getName()
                                + " must not be blank");
                    }
                }

                if (field.isAnnotationPresent(MaxLength.class)
                        && value != null) {

                    MaxLength annotation = field.getAnnotation(MaxLength.class);

                    int maxLength = annotation.value();

                    if (value.toString().length() > maxLength) {

                        errors.add(field.getName()
                                + " must be at most "
                                + maxLength
                                + " characters");
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return errors;
    }
}

public class Practical7_1 {

    public static void main(String[] args) {

        SignupForm form = new SignupForm(
                "",
                "veryveryveryveryveryveryveryverylong@gmail.com");

        List<String> errors = Validator.validate(form);

        if (errors.isEmpty()) {

            System.out.println("Form is valid");

        } else {

            System.out.println("Validation Errors:");

            for (String error : errors) {
                System.out.println(error);
            }
        }
    }
}
