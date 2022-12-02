package src;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Priority {
    private String countryCode;
    private int id;
    private String title;
    private String description;
    private PriorityEffect priorityEffect;

    Priority(String countryCode, int id, String title, String description, PriorityEffect priorityEffect) {
        this.countryCode = countryCode;
        this.id = id;
        this.title = title;
        this.description = description;
        this.priorityEffect = priorityEffect;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return "files/countries/" + countryCode + "/priorities/" + id + ".png";
    }

    public void doMethod() {
        Method method = null;
        try {
            method = priorityEffect.getClass().getMethod(priorityEffect.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        try {
            method.invoke(priorityEffect, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
