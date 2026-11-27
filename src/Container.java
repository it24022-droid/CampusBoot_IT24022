import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class Container {
    private final Map<String, Object> beans = new LinkedHashMap<>();

    public void scan(Class<?>... classes) {
        for (Class<?> cls : classes) {
            Component comp = cls.getAnnotation(Component.class);
            if (comp == null) {
                System.out.println("[Container] Skipped: " + cls.getSimpleName() + " (no @Component)");
                continue;
            }
            try {
                Object instance = cls.getDeclaredConstructor().newInstance();
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

    public void injectValues(Map<String, String> config) {
        for (Object bean : beans.values()) {
            for (Field f : bean.getClass().getDeclaredFields()) {
                Value v = f.getAnnotation(Value.class);
                if (v == null) continue;
                String key = v.value();
                if (!config.containsKey(key)) continue;
                String raw = config.get(key);
                try {
                    f.setAccessible(true);
                    Class<?> t = f.getType();
                    if (t == int.class || t == Integer.class) {
                        f.set(bean, Integer.parseInt(raw.trim()));
                    } else {
                        f.set(bean, raw);
                    }
                    System.out.println("[Container] Injected: " + key + " -> "
                            + bean.getClass().getSimpleName() + "." + f.getName());
                } catch (IllegalAccessException | NumberFormatException e) {
                    System.out.println("[Container] Could not inject " + key + ": " + e);
                }
            }
        }
    }

    public Object getBean(String name) {
        return beans.get(name);
    }

    Map<String, Object> getBeans() {
        return beans;
    }
}