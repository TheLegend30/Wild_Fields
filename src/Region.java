package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Region {
    private int id = -1;
    private String name = "Пустка";
    private int population = 0;
    private int factories = 0;

    // Temp
    public static Region noRegion = new Region();

    public static ArrayList<Region> regions = new ArrayList<>();

    private Region() {
    }

    private Region(int id, String name, int population, int factories) {
        this.id = id;
        this.name = name;
        this.population = population;
        this.factories = factories;
    }

    public static void setRegions() {
        try {
            File file = new File("files/text_files/text_regions.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                Region region = new Region(Integer.parseInt(scanner.nextLine()), scanner.nextLine(),
                        Integer.parseInt(scanner.nextLine()),
                        Integer.parseInt(scanner.nextLine()));
                regions.add(region);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Помилка при встановленні значень для регіонів.");
            e.printStackTrace();
        }
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getFactories() {
        return factories;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setFactories(int factories) {
        this.factories = factories;
    }

    @Override
    public String toString() {
        String string;
        String countryOwner = new Country().getName();
        string = "Обраний регіон" + "\n";
        string += "Назва регіону: " + getName() + "\n";
        string += "Населення: " + getPopulation() + "\n";
        string += "Фабрики: " + getFactories() + "\n";
        for (Country country : Country.countries) {
            if (country.getRegions().contains(this)) {
                countryOwner = country.getName();
            }
        }
        string += "Країна: " + countryOwner + "\n";

        return string;
    }

    public static Region getRegionByID(int id) {
        for (Region region : regions) {
            if (region.getID() == id) {
                return region;
            }
        }
        return noRegion;
    }

    public static int getAveragePopulation() {
        int allPopulation = 0;
        for (Region r : regions) {
            allPopulation += r.getPopulation();
        }
        return allPopulation / regions.size();
    }

    public static int getAverageFactories() {
        int allFactories = 0;
        for (Region r : regions) {
            allFactories += r.getFactories();
        }
        return allFactories / regions.size();
    }
}
