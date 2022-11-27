package src;

public class Priority {
    private String countryCode;
    private int id;
    private String title;
    private String description;
    // private Effect effect;

    Priority(String countryCode, int id, String title, String description) {
        this.countryCode = countryCode;
        this.id = id;
        this.title = title;
        this.description = description;
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
}
