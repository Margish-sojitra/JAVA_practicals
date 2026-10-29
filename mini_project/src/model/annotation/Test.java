package model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom @Test annotation for MiniBank automated testing (Practical 14 Requirement 4).
 * Allows unit tests to run cleanly in any IDE without requiring external JUnit jar downloads.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}

