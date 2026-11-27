import java.lang.annotation.*;

// @Component marks a class that the Container should create and manage.
@Target(ElementType.TYPE)             // can be placed only on a class
@Retention(RetentionPolicy.RUNTIME)   // kept at runtime so reflection can read it
public @interface Component {
    // Optional bean name. Empty means "use the class name with a lowercase first letter".
    String value() default "";
}
