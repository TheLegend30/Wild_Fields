package src;

public class PriorityEffect {
    private String name = "";

    PriorityEffect(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void improveVolist() {
        Main.yourCountry.changePartyPopularity(Main.yourCountry.getParties()[Country.Ideology.VOLISM.valueOf()], 5);
    }

    public static void buildFactory() {
        Main.yourCountry.getEconomy().buildFactory(Main.yourCountry.getRegions().get(0));
    }

    public static void makeToOblast() {
        Main.yourCountry.setFlagPath(1);
        Main.main.updateLabels();
    }
}
