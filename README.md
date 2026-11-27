# CampusBoot: Custom Annotations with Java Reflection

**Name:** Shayla Akter | **ID:** IT24022
**Lab work:** Custom Annotation Test (Java Reflection & Custom Annotations)

CampusBoot is a tiny framework that shows how Spring Boot works under the hood: annotations plus reflection.

## Submission file

- [`IT24022_CampusBoot.zip`](IT24022_CampusBoot.zip) contains all `.java` files and `answers.txt`.
- The same source files are also in the [`src`](src) folder so the code can be read directly on GitHub.

## Project structure

| File | Purpose |
|---|---|
| `Component.java` | `@Component` (class): marks a class to be created by the container, `String value() default ""` |
| `Value.java` | `@Value` (field): injects a config value, `String value()` |
| `Range.java` | `@Range` (field): `int min()`, `int max()`, `String message() default "Value out of range"` |
| `Route.java` | `@Route` (method): maps a URL path to a method |
| `Container.java` | Mini IoC container: `scan`, `injectValues`, `getBean` |
| `Validator.java` | Reads `@Range` and returns violation messages |
| `Router.java` | Finds the `@Route` method for a path and invokes it, or returns `404 Not Found: <path>` |
| `GradeController.java`, `ReportPrinter.java`, `Student.java`, `Main.java` | Starter code (not modified, only imports added to `Main`) |
| `answers.txt` | Reflection questions (Part E) |

All four annotations use `@Retention(RetentionPolicy.RUNTIME)` so reflection can read them. `@Component` targets `TYPE`, `@Value` and `@Range` target `FIELD`, and `@Route` targets `METHOD`.

> Note: the starter code uses `@Route("/hello")`, which Java only allows when the element is named `value()`. So `@Route` has both `value()` and `path()`, and the Router accepts either.

## How it works

1. `Container.scan(...)` creates an object for each class annotated with `@Component`, names it with `value()` (or the lowercase-first-letter class name), and skips other classes.
2. `Container.injectValues(config)` finds `@Value` fields, calls `setAccessible(true)`, converts to `int` when needed, and sets the value.
3. `Validator.validate(obj)` reads each `@Range` int field and reports `fieldName - message (actual: value)` when it is out of range.
4. `Router.dispatch(container, path)` finds the bean method with a matching `@Route` and invokes it.

## How to run

**IntelliJ IDEA:** create a plain Java project (Build system: IntelliJ), copy all `.java` files into `src`, and run `Main`.

**Command line:**
```
javac *.java
java Main
```

## Output

The program output matches the Sample Output given in the assignment:

<img width="1356" height="516" alt="output" src="https://github.com/user-attachments/assets/d0279cc4-a799-48da-b9f3-6420c0047d15" />


