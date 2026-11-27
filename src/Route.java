import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    // The starter code writes @Route("/hello"), which Java only allows
    // when the element is named value(). path() is kept to match the spec table.
    String value() default "";
    String path() default "";
}