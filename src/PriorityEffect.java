package src;

public class PriorityEffect {
    private String code = "NON";
    private String name = "";

    PriorityEffect(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void improveVolist() {
        Main.yourCountry.changePartyPopularity(Main.yourCountry.getParties()[Country.Ideology.VOLISM.valueOf()], 5);
    }

    public void buildFactory() {
        Main.yourCountry.getEconomy().buildFactory(Main.yourCountry.getRegions().get(0));
    }

    public void makeToOblast() {
        Main.yourCountry.setFlagPath(1);
        Main.main.updateLabels();
    }

    public void showEvent() {
        EventPane.showEvent(this.code, 1);
    }
}
