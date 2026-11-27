import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

// A mini IoC container: it creates objects (beans) and injects config values.
public class Container {
    // bean name -> bean object. LinkedHashMap keeps the order beans were added.
    private final Map<String, Object> beans = new LinkedHashMap<>();

    // Creates a bean for every class that has @Component.
    public void scan(Class<?>... classes) {
        for (Class<?> cls : classes) {
            // Reflection: read the @Component annotation from the class.
            Component comp = cls.getAnnotation(Component.class);
            if (comp == null) {
                // No @Component, so we do not manage this class.
                System.out.println("[Container] Skipped: " + cls.getSimpleName() + " (no @Component)");
                continue;
            }
            try {
                // Create the object using the no-arg constructor.
                Object instance = cls.getDeclaredConstructor().newInstance();

                // Bean name = value(), or the class name with a lowercase first letter.
                String name = comp.value();
                if (name.isEmpty()) {
                    String s = cls.getSimpleName();
                    name = Character.toLowerCase(s.charAt(0)) + s.substring(1);
                }
                beans.put(name, instance);
                System.out.println("[Container] Registered: " + name + " (" + cls.getSimpleName() + ")");
            } catch (ReflectiveOperationException e) {
                System.out.println("[Container] Failed to create " + cls.getSimpleName() + ": " + e);
            }
        }
    }

    // Sets every @Value field of every bean from the config map.
    public void injectValues(Map<String, String> config) {
        for (Object bean : beans.values()) {
            // getDeclaredFields() also returns private fields, in declaration order.
            for (Field f : bean.getClass().getDeclaredFields()) {
                Value v = f.getAnnotation(Value.class);
                if (v == null) continue;                 // field has no @Value, skip it
                String key = v.value();
                if (!config.containsKey(key)) continue;  // key not in config, skip it
                String raw = config.get(key);            // config values are always Strings
                try {
                    f.setAccessible(true);               // allow writing to a private field
                    Class<?> t = f.getType();
                    if (t == int.class || t == Integer.class) {
                        f.set(bean, Integer.parseInt(raw.trim())); // convert text to int
                    } else {
                        f.set(bean, raw);                // String field, set directly
                    }
                    System.out.println("[Container] Injected: " + key + " -> "
                            + bean.getClass().getSimpleName() + "." + f.getName());
                } catch (IllegalAccessException | NumberFormatException e) {
                    System.out.println("[Container] Could not inject " + key + ": " + e);
                }
            }
        }
    }

    // Returns a bean by its name, or null if there is none.
    public Object getBean(String name) {
        return beans.get(name);
    }

    // Used by the Router to search all beans.
    Map<String, Object> getBeans() {
        return beans;
    }
}
