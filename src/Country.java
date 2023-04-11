package src;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.plaf.ColorUIResource;

import src.Country.Diplomacy.Relation;

public class Country implements Comparable<Country> {
    private String name = "Пустка";
    private String code = "NON";
    private Color color = Color.YELLOW.darker();
    private ArrayList<Region> regions = new ArrayList<>();
    private Party[] parties = new Party[13];
    private Party currentParty = new Party("Ніхто", Ideology.ANARCHY, 0, "Немає");
    private String flagPath = String.format(tempFlagPath, code, 0);
    private String portraitPath = String.format(tempPortraitPath, code, currentParty.ideology.valueOf());
    private Economy economy = new Economy();
    private Diplomacy diplomacy = new Diplomacy();
    private ArrayList<Priority> priorities = new ArrayList<>();
    private Military military = new Military();

    public final static String tempFlagPath = "files/countries/%s/flags/flag_%d.png";
    public final static String tempPortraitPath = "files/countries/%s/portraits/portrait_%d.png";

    public static ArrayList<Country> countries = new ArrayList<>();

    Country() {
    }

    Country(String name, String code, Color color, Party[] parties, int numberOfParty,
            ArrayList<Region> regions) {
        this.name = name;
        this.code = code;
        this.color = color;
        this.parties = parties;
        this.currentParty = parties[numberOfParty];
        this.regions = regions;
        this.flagPath = String.format(tempFlagPath, code, 0);
        this.portraitPath = String.format(tempPortraitPath, code, currentParty.ideology.valueOf());
        this.economy = new Economy(this);
        this.military = new Military(this);
    }

    public static class Party {
        private String partyName = "Ніхто";
        private Ideology ideology = Ideology.ANARCHY;
        private float popularity = 0;
        private String leaderName = "Немає";

        public Party(String partyName, Ideology ideology, float popularity, String leaderName) {
            this.partyName = partyName;
            this.ideology = ideology;
            if (popularity > 100f) {
                popularity = 100f;
            } else if (popularity < 0f) {
                popularity = 0f;
            }
            this.popularity = popularity;
            this.leaderName = leaderName;
        }

        public String getPartyName() {
            return partyName;
        }

        public Ideology getIdeology() {
            return ideology;
        }

        public float getPopularity() {
            return popularity;
        }

        public String getLeaderName() {
            return leaderName;
        }

        public void setPopularity(float popularity) {
            if (popularity > 100f) {
                popularity = 100f;
            } else if (popularity < 0f) {
                popularity = 0f;
            }
            this.popularity = popularity;
        }
    }

    public static class Economy {
        private int population = 0;
        private int factories = 0;

        private int populationTaxesModifier = 20;
        private int factoriesTaxesModifier = 13;

        private float populationTaxes = 0f;
        private float factoriesTaxes = 0f;

        // TODO: Technology to improve output
        private float factoriesOutputModifier = 0f;
        private float factoriesOutput = 0f;
        private float consumption = 0f;
        private float tradeOutput = 0f;

        private float profit = 0f;
        private long reserve = 0L;
        private long debt = 0L;

        private ArrayList<Queue> factoriesBuildQueue = new ArrayList<>();

        private static class Queue {
            private int daysLeft;
            private Region region;

            private Queue(int daysLeft, Region region) {
                this.daysLeft = daysLeft;
                this.region = region;
            }

            public int getDaysLeft() {
                return daysLeft;
            }

            public void setDaysLeft(int daysLeft) {
                this.daysLeft = daysLeft;
            }

        }

        private Economy() {

        }

        private Economy(Country country) {
            update(country);
        }

        public void update(Country country) {
            this.population = 0;
            this.factories = 0;
            for (Region region : country.regions) {
                this.population += region.getPopulation();
                this.factories += region.getFactories();
            }

            this.populationTaxes = this.population * ((this.populationTaxesModifier * 0.01f) * 20f);
            this.factoriesTaxes = this.factories * ((this.factoriesTaxesModifier * 0.01f) * 35000f);

            this.factoriesOutputModifier = this.factories * 50f * (1f - (this.factoriesTaxesModifier * 0.01f));
            this.factoriesOutput = this.factories * this.factoriesOutputModifier;
            this.consumption = this.population * 0.15f + this.getQueue().length() * 300f;
            this.tradeOutput = (this.factoriesOutput - this.consumption) * 55f;

            this.profit = this.populationTaxes + this.factoriesTaxes + this.tradeOutput - (this.debt * 0.09f);
        }

        private void addProfit() {
            reserve += profit;
            if (reserve < 0) {
                debt -= reserve;
                reserve = 0;
            }
        }

        public int getPopulation() {
            return population;
        }

        public int getPopulationTaxesModifier() {
            return populationTaxesModifier;
        }

        public int getFactoriesTaxesModifier() {
            return factoriesTaxesModifier;
        }

        public long getReserve() {
            return reserve;
        }

        public void setPopulationTaxesModifier(int populationTaxesModifier) {
            this.populationTaxesModifier = populationTaxesModifier;
        }

        public void setFactoriesTaxesModifier(int factoriesTaxesModifier) {
            this.factoriesTaxesModifier = factoriesTaxesModifier;
        }

        public void setReserve(long reserve) {
            this.reserve = reserve;
        }

        public void buildFactory(Region region) {
            factoriesBuildQueue.add(new Queue(80, region));
        }

        public void checkIfBuilt() {
            Iterator<Queue> qIterator = factoriesBuildQueue.iterator();
            while (qIterator.hasNext()) {
                Queue queue = qIterator.next();
                queue.setDaysLeft(queue.getDaysLeft() - 1);
                if (queue.getDaysLeft() == 0) {
                    queue.region.setFactories(queue.region.getFactories() + 1);
                    qIterator.remove();
                }
            }
        }

        @Override
        public String toString() {
            String string = "";
            string += "Населення: " + population + "\n";
            string += "Кількість фабрик: " + factories + "\n";
            string += "Подушне: " + populationTaxesModifier + "%" + "\n";
            string += "Пофабричне: " + factoriesTaxesModifier + "%" + "\n";
            string += "Розмір подушного: " + String.format("%.2f", populationTaxes) + "₴" + "\n";
            string += "Розмір пофабричного: " + String.format("%.2f", factoriesTaxes) + "₴" + "\n";
            string += "Вироблено фабричної продукції: " + String.format("%.2f", factoriesOutput) + " одиниць" + "\n";
            string += "Спожито населенням: " + String.format("%.2f", consumption) + " одиниць" + "\n";
            string += "Залишки продано (придбано): " + String.format("%.2f", tradeOutput) + "₴" + "\n";
            string += "Загальний прибуток (місячний): " + String.format("%.2f", profit) + "₴" + "\n";
            string += "Резерв: " + reserve + "₴" + "\n";
            string += "Борг: " + debt + "₴" + "\n";
            return string;
        }

        public String getQueue() {
            String string = "";
            for (Queue queue : factoriesBuildQueue) {
                string += "Фабрика будується: " + queue.daysLeft + " днів в регіоні " + queue.region.getName() + "\n";
            }
            return string;
        }
    }

    public static class Diplomacy {
        public ArrayList<Relation> relations = new ArrayList<>();

        private Diplomacy() {

        }

        public static class Relation implements Comparable<Relation> {
            private Country country;
            private int relationLevel = 0;
            private boolean trading = false;
            private boolean inWar = false;

            public Relation(Country country) {
                this.country = country;
            }

            public Country getCountry() {
                return country;
            }

            public int getRelationLevel() {
                return relationLevel;
            }

            public boolean areTrading() {
                return trading;
            }

            public boolean areInWar() {
                return inWar;
            }

            public void setCountry(Country country) {
                this.country = country;
            }

            public void setRelationLevel(int relationLevel) {
                if (relationLevel > 100) {
                    relationLevel = 100;
                } else if (relationLevel < -100) {
                    relationLevel = -100;
                }
                this.relationLevel = relationLevel;
            }

            public void setTrading(boolean trading) {
                this.trading = trading;
            }

            public void setInWar(boolean inWar) {
                if (inWar) {
                    this.setRelationLevel(-100);
                }
                this.inWar = inWar;
            }

            @Override
            public String toString() {
                String string = "";
                string = country.getName();
                return string;
            }

            public String toStringForStats() {
                String string = "";
                string = "Держава: " + country.getName() + "\n";
                string += "Голова: " + country.getCurrentParty().getLeaderName() + "\n";
                string += "Ідеологія: " + country.getCurrentParty().getIdeology().toString() + "\n";
                string += "Відносини: " + relationLevel + "\n";
                string += "Торговий договір: " + (areTrading() ? "так" : "ні") + "\n";
                string += "Війна: " + (areInWar() ? "так" : "ні") + "\n";
                return string;
            }

            @Override
            public int compareTo(Country.Diplomacy.Relation o) {
                return this.country.compareTo(o.country);
            }
        }

        public Relation[] getRelationsArray() {
            Relation[] r = new Relation[relations.size()];
            for (int i = 0; i < r.length; i++) {
                r[i] = relations.get(i);
            }
            return r;

        }
    }

    public static class Military {
        private int manpower = 0;
        private ArrayList<Division> divisions = new ArrayList<>();

        private ArrayList<Training> divisionsAreTraining = new ArrayList<>();

        private Military() {
            this.manpower = 0;
        }

        private Military(Country country) {
            this.manpower = (int) (country.getEconomy().getPopulation() * 0.05);
        }

        private static class Training {
            private int daysLeft;
            private Region region;

            private Training(int daysLeft, Region region) {
                this.daysLeft = daysLeft;
                this.region = region;
            }

            public int getDaysLeft() {
                return daysLeft;
            }

            public void setDaysLeft(int daysLeft) {
                this.daysLeft = daysLeft;
            }

        }

        public static class Division {
            private static int current_number = 1;
            private int number = 0;
            private int soldiers = 0;
            private float quality = 0f;
            // fDO: equipement need
            // private float equipmentNeed = 0f;
            private Region location = Region.noRegion;

            private Operation currentOperation = new OperationStaying(location);

            private Division(int soldiers, float quality, Region location) {
                this.number = current_number++;
                this.soldiers = soldiers;
                this.quality = quality;
                // this.equipmentNeed = soldiers * (3 + quality * 2);
                this.location = location;
                this.currentOperation = new OperationStaying(location);
            }

            public Region getLocation() {
                return location;
            }

            public void moveDivision(Region location) {
                currentOperation = new OperationMovement(this.location, location);
            }

            @Override
            public String toString() {
                return "\nДивизія №" + number + " в " + soldiers + " чоловік з якістю " + String.format("%.2f", quality)
                        + "\n" + currentOperation.toString();
            }
        }

        public void trainDivision(Region location) {
            int daysLeft = 10;
            divisionsAreTraining.add(new Training(daysLeft, location));
        }

        public int getManpower() {
            return manpower;
        }

        public Division[] getDivisionsArray() {

            Division[] d = new Division[divisions.size()];
            for (int i = 0; i < d.length; i++) {
                d[i] = divisions.get(i);
            }
            return d;

        }

        public void setManpower(int manpower) {
            this.manpower = manpower;
        }

        public void checkIfTrained() {
            Iterator<Training> tIterator = divisionsAreTraining.iterator();
            while (tIterator.hasNext()) {
                Training training = tIterator.next();
                training.setDaysLeft(training.getDaysLeft() - 1);
                if (training.getDaysLeft() == 0) {
                    divisions.add(new Division(1000, 1, training.region));
                    tIterator.remove();
                }
            }
        }

        @Override
        public String toString() {
            String string = "";
            string += "Призовники: " + manpower + "\n";
            return string;
        }

        public String getTraining() {
            String string = "";
            for (Training training : divisionsAreTraining) {
                string += "\nДивизія готується: " + training.daysLeft
                        + " днів в регіоні " + training.region.getName();
            }
            return string;
        }

        public String divisionsToString() {
            String string = "";
            for (Division division : divisions) {
                string += division.toString();
            }
            return string;
        }
    }

    public enum Ideology {
        ANARCHY,
        TOTALISM,
        BILSHOVISM,
        MENSHOVISM,
        SOCIAL_DEMOCRACY,
        VOLISM,
        ANARCHO_CAPITALISM,
        TRADITIONALISM,
        RETROGRADISM,
        CORPORATISM,
        DONTSOVISM,
        BANDERISM,
        RADICALISM;

        public int valueOf() {
            switch (this) {
                case ANARCHY:
                    return 0;
                case TOTALISM:
                    return 1;
                case BILSHOVISM:
                    return 2;
                case MENSHOVISM:
                    return 3;
                case SOCIAL_DEMOCRACY:
                    return 4;
                case VOLISM:
                    return 5;
                case ANARCHO_CAPITALISM:
                    return 6;
                case TRADITIONALISM:
                    return 7;
                case RETROGRADISM:
                    return 8;
                case CORPORATISM:
                    return 9;
                case DONTSOVISM:
                    return 10;
                case BANDERISM:
                    return 11;
                case RADICALISM:
                    return 12;
                default:
                    return 0;
            }
        }

        public static Color getIdeologyColor(int n) {
            switch (n) {
                case 0:
                    return new ColorUIResource(78, 63, 63);
                case 1:
                    return new ColorUIResource(84, 8, 8);
                case 2:
                    return new ColorUIResource(141, 31, 31);
                case 3:
                    return new ColorUIResource(212, 5, 5);
                case 4:
                    return new ColorUIResource(253, 113, 113);
                case 5:
                    return new ColorUIResource(249, 177, 54);
                case 6:
                    return new ColorUIResource(242, 249, 54);
                case 7:
                    return new ColorUIResource(51, 103, 247);
                case 8:
                    return new ColorUIResource(2, 2, 149);
                case 9:
                    return new ColorUIResource(102, 204, 255);
                case 10:
                    return new ColorUIResource(102, 102, 51);
                case 11:
                    return new ColorUIResource(70, 44, 10);
                case 12:
                    return new ColorUIResource(51, 212, 84);
                default:
                    return new ColorUIResource(255, 255, 255);
            }
        }

        public static Ideology getIdeology(int n) {
            switch (n) {
                case 0:
                    return ANARCHY;
                case 1:
                    return TOTALISM;
                case 2:
                    return BILSHOVISM;
                case 3:
                    return MENSHOVISM;
                case 4:
                    return SOCIAL_DEMOCRACY;
                case 5:
                    return VOLISM;
                case 6:
                    return ANARCHO_CAPITALISM;
                case 7:
                    return TRADITIONALISM;
                case 8:
                    return RETROGRADISM;
                case 9:
                    return CORPORATISM;
                case 10:
                    return DONTSOVISM;
                case 11:
                    return BANDERISM;
                case 12:
                    return RADICALISM;
                default:
                    return ANARCHY;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case ANARCHY:
                    return "Анархія";
                case TOTALISM:
                    return "Тоталізм";
                case BILSHOVISM:
                    return "Більшовизм";
                case MENSHOVISM:
                    return "Меньшовизм";
                case SOCIAL_DEMOCRACY:
                    return "Соціал-Демократія";
                case VOLISM:
                    return "Волізм";
                case ANARCHO_CAPITALISM:
                    return "Анархо-Капіталізм";
                case TRADITIONALISM:
                    return "Традиціоналізм";
                case RETROGRADISM:
                    return "Ретроградизм";
                case CORPORATISM:
                    return "Корпоратизм";
                case DONTSOVISM:
                    return "Донцовізм";
                case BANDERISM:
                    return "Бандеризм";
                case RADICALISM:
                    return "Радикалізм";
                default:
                    return "Помилка";
            }
        }
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Party getCurrentParty() {
        return currentParty;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public String getPortraitPath() {
        return portraitPath;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Diplomacy getDiplomacy() {
        return diplomacy;
    }

    public ArrayList<Priority> getPriorities() {
        return priorities;
    }

    public Military getMilitary() {
        return military;
    }

    public Party[] getParties() {
        return parties;
    }

    public void setFlagPath(int i) {
        this.flagPath = String.format(tempFlagPath, code, i);
    }

    @Override
    public String toString() {
        return getName();
    }

    public String toStringForStats() {
        String string = "";
        string += "Назва держави: " + getName() + "\n";
        string += "Голова держави: " + getCurrentParty().leaderName + "\n";
        string += "Правляча партія: " + getCurrentParty().partyName + "\n";
        string += "Ідеологія: " + getCurrentParty().ideology + "\n";
        return string;
    }

    @Override
    public int compareTo(Country o) {
        return this.getName().compareTo(o.getName());
    }

    public static void setCountries() {

        countries = new ArrayList<>();

        // countries.add(new Country("Україна", "UKR", new Color(80, 218, 46),
        // new Leader("Володимир Зеленський", Ideology.VOLISM, "Слуга народу", 15),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(47),
        // Region.getRegionByID(49),
        // Region.getRegionByID(52), Region.getRegionByID(34), Region.getRegionByID(46),
        // Region.getRegionByID(68) }))));

        // countries.add(new Country("ЗСУ", "ZSU", new Color(236, 31, 120),
        // new Leader("Валерій Залужний", Ideology.TRADITIONALISM,
        // "Кліка Залужного", 45),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(64),
        // Region.getRegionByID(77),
        // Region.getRegionByID(87), Region.getRegionByID(50) }))));

        // countries.add(new Country("Альянс Помсти", "ALP", new Color(0, 0, 0),
        // new Leader("Білий Вождь", Ideology.BANDERISM,
        // "Партія реваншистів", 70),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(58),
        // Region.getRegionByID(40),
        // Region.getRegionByID(45) }))));

        // countries.add(new Country("Оновлена Україна", "DEM", new Color(255, 229,
        // 180),
        // new Leader("Ігор Щедрін", Ideology.VOLISM,
        // "Демократична Сокира", 45),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(72) }))));

        // countries.add(new Country("Фастівська Народна Республіка", "FAS", new
        // Color(119, 210, 180),
        // new Leader("Олесь Янчук", Ideology.SOCIAL_DEMOCRACY,
        // "Директорія - Петлюрівці", 65),
        // new ArrayList<>(
        // Arrays.asList(new Region[] { Region.getRegionByID(71),
        // Region.getRegionByID(80) }))));

        // countries.add(new Country("Обухівський Мегакомбінат", "OBH", new Color(10,
        // 117, 223),
        // new Leader("Віктор Семенець", Ideology.ANARCHO_CAPITALISM,
        // "Рада директорів", 95),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(70),
        // Region.getRegionByID(89),
        // Region.getRegionByID(119) }))));

        // countries.add(new Country("ПЦУ", "PCU", new Color(146, 0, 10),
        // new Leader("Митрополит Агапіт", Ideology.RETROGRADISM,
        // "Ортодоксальне крило", 50),
        // new ArrayList<>(
        // Arrays.asList(new Region[] { Region.getRegionByID(130),
        // Region.getRegionByID(137),
        // Region.getRegionByID(134), Region.getRegionByID(106),
        // Region.getRegionByID(98)
        // }))));

        // countries.add(new Country("Українська Радянська Соціалістична Республіка",
        // "UCR", new Color(213, 15, 15),
        // new Leader("Петро Симоненко", Ideology.BILSHOVISM,
        // "КПУ - Стара Гвардія", 85),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(13),
        // Region.getRegionByID(17) }))));

        // countries.add(new Country("Соціалістична Республіка Україна", "SRU", new
        // Color(216,
        // 49, 155),
        // new Leader("Ілля Кива", Ideology.MENSHEVISM,
        // "СПУ - Кивовці", 25),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(29) }))));

        // countries.add(new Country("19 ОБ РХБЗ", "NOB", new Color(10,
        // 111, 6),
        // new Leader("Полковник Юрій Оніщук",
        // Ideology.RADICALISM,
        // "Рада офіцерів", 100),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(31) }))));

        // countries.add(new Country("GSC", "GSC", new Color(51, 153, 153),
        // new Leader("Сергій Григорович", Ideology.ANARCHO_CAPITALISM,
        // "Менеджери", 90),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(28) }))));

        // countries.add(new Country("Спілка Грибників", "MSH", new Color(152,
        // 108, 95),
        // new Leader("Дядько Свирид", Ideology.RETROGRADISM,
        // "Спілка грибників", 50),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(16) }))));

        // countries.add(new Country("Зона", "STA", new Color(255,
        // 237, 123),
        // new Leader("Бармен", Ideology.CORPORATISM,
        // "Вільні сталкери", 35),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(15) }))));

        try (Scanner scanner = new Scanner(new File("files/text_files/text_countries.txt"))) {
            while (scanner.hasNextLine()) {
                String name = "";
                String code = "";
                Color color = null;
                Party[] parties = new Party[13];
                int numberOfParty = 0;
                ArrayList<Region> regions = new ArrayList<>();

                if (scanner.nextLine().equals("<name>")) {
                    name = scanner.nextLine();
                }
                if (scanner.nextLine().equals("<code>")) {
                    code = scanner.nextLine();
                }
                if (scanner.nextLine().equals("<color>")) {
                    color = new ColorUIResource(Integer.parseInt(scanner.nextLine()),
                            Integer.parseInt(scanner.nextLine()),
                            Integer.parseInt(scanner.nextLine()));
                }
                if (scanner.nextLine().equals("<parties>")) {
                    for (int i = 0; i < parties.length; i++) {
                        parties[i] = new Party(scanner.nextLine(), Ideology.getIdeology(i),
                                Integer.parseInt(scanner.nextLine()), scanner.nextLine());
                    }
                }
                if (scanner.nextLine().equals("<numberOfParty>")) {
                    numberOfParty = Integer.parseInt(scanner.nextLine());
                }
                if (scanner.nextLine().equals("<regions>")) {
                    while (true) {
                        String buf = scanner.nextLine();
                        if (buf.equals("<regions>")) {
                            break;
                        }
                        regions.add(Region.getRegionByID(Integer.parseInt(buf)));
                    }
                }

                Country country = new Country(name, code, color, parties, numberOfParty, regions);
                countries.add(country);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // countries.add(new Country("Чигиринський полк", "CHH", new Color(216,
        // 29, 79),
        // new Leader("Полковник Тиміш Хмельницький", Ideology.DONTSOVISM,
        // "Лоялісти Хмельницького", 48),
        // new ArrayList<>(
        // Arrays.asList(new Region[] { Region.getRegionByID(160),
        // Region.getRegionByID(164) }))));

        // countries.add(new Country("Канівський полк", "KAN", new Color(24, 85, 144),
        // new Leader("Полковник Добровіст Захаренко", Ideology.ANARCHY,
        // "Канівська голота", 60),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(146) }))));

        // countries.add(new Country("Гетьманат", "HET", new Color(243, 204, 15),
        // new Leader("Гетьман Гнат Многогрішний", Ideology.TRADITIONALISM,
        // "Черкаська старшина", 55),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(131) }))));

        // countries.add(new Country("Кропивнянський полк", "KRO", new Color(220, 220,
        // 220),
        // new Leader("Полковник-Кобзар Тарас Компаніченко", Ideology.RETROGRADISM,
        // "Бандуристи", 65),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(129),
        // Region.getRegionByID(108),
        // Region.getRegionByID(116) }))));

        // countries.add(new Country("Лубенський полк", "LUB", new Color(240, 226, 193),
        // new Leader("Полковник Євген Петренко", Ideology.CORPORATISM,
        // "Звичаївці", 65),
        // new ArrayList<>(Arrays.asList(new Region[] {
        // Region.getRegionByID(100),
        // Region.getRegionByID(66) }))));

        // countries.add(new Country("Миргородський полк", "MYR", new Color(180, 180,
        // 180),
        // new Leader("Полковник Анатолій Шевченко", Ideology.BANDERISM,
        // "Реєстровці", 65),
        // new ArrayList<>(Arrays.asList(new Region[] {
        // Region.getRegionByID(99),
        // Region.getRegionByID(113) }))));

        // countries.add(new Country("Гадяцький полк", "HAD", new Color(40, 40,
        // 40),
        // new Leader("Полковник Валентин Соколовський", Ideology.RADICALISM,
        // "Обʼєднанці", 30),
        // new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(67) }))));

        setCountriesRelations();
        setCountriesPriorities();

        Collections.sort(countries);
    }

    private static void setCountriesRelations() {
        for (Country country : countries) {
            country.diplomacy.relations.clear();
            for (Country otherCountry : countries) {
                if (country.equals(otherCountry)) {
                    continue;
                }
                country.diplomacy.relations.add(new Country.Diplomacy.Relation(otherCountry));
            }
            Collections.sort(country.diplomacy.relations);
        }
    }

    private static void setCountriesPriorities() {
        for (Country country : countries) {
            String code = country.code;
            String filePath = "files/countries/" + code + "/priorities/desc.txt";
            try (Scanner scanner = new Scanner(new File(filePath))) {
                while (scanner.hasNextLine()) {
                    int id = 0;
                    String title = "";
                    String description = "";
                    PriorityEffect priorityEffect = null;
                    if (scanner.nextLine().equals("<id>")) {
                        id = Integer.parseInt(scanner.nextLine());
                    }
                    if (scanner.nextLine().equals("<title>")) {
                        title = scanner.nextLine();
                    }
                    if (scanner.nextLine().equals("<description>")) {
                        String buf = "";
                        while (!(buf = scanner.nextLine()).equals("<description>")) {
                            description += buf + "\n";
                        }
                    }

                    if (scanner.nextLine().equals("<effect>")) {
                        priorityEffect = new PriorityEffect(country.code, scanner.nextLine());
                        scanner.nextLine();
                    }

                    Priority priority = new Priority(code, id, title, description, priorityEffect);
                    country.priorities.add(priority);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public String getDescription() {
        String text = "";
        try {
            File file = new File(String.format("files/countries/%s/desc.txt", this.code));
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                if (scanner.nextLine().equals("ОПИС")) {
                    String buf;
                    while (!(buf = scanner.nextLine()).equals("ОПИС")) {
                        text += buf + "\n";
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Помилка при отриманні опису.");
            e.printStackTrace();
        }
        return text;
    }

    private void checkIfExisting() {
        Iterator<Country> iteratorCountry = countries.iterator();
        while (iteratorCountry.hasNext()) {
            Country country = iteratorCountry.next();
            if (country.regions.size() == 0) {
                iteratorCountry.remove();
                for (Country otherCountry : countries) {
                    for (Relation relation : otherCountry.getDiplomacy().relations) {
                        if (relation.country.equals(country)) {
                            otherCountry.getDiplomacy().relations.remove(relation);
                            break;
                        }
                    }
                }
            }
        }
        if (countries.size() != 1) {
            Main.countriesRelationsComboBox
                    .setModel(new DefaultComboBoxModel<Relation>(getDiplomacy().getRelationsArray()));
        } else {
            JOptionPane.showConfirmDialog(null, "Ви обʼєднали Україну!", "Перемога", JOptionPane.OK_OPTION);
            System.exit(0);
        }

    }

    private void checkIfMoved() {
        for (Country.Military.Division division : this.military.divisions) {
            if (division.currentOperation.getName().equals(OperationMovement.name)) {
                OperationMovement operationMovement = (OperationMovement) division.currentOperation;
                if (operationMovement.getDaysOfMovement() == 0) {
                    division.location = operationMovement.getTo();
                    if (this.regions.contains(operationMovement.getTo())) {

                        division.currentOperation = new OperationStaying(division.location);
                    } else {
                        division.currentOperation = new OperationOccupation(division.location);
                    }
                }
                operationMovement.setDaysOfMovement(operationMovement.getDaysOfMovement() - 1);
            }
        }
    }

    private void checkIfOccupied() {
        for (Country.Military.Division division : this.military.divisions) {
            if (division.currentOperation.getName().equals(OperationOccupation.name)) {
                OperationOccupation operationOccupation = (OperationOccupation) division.currentOperation;
                if (operationOccupation.getOccupationProgress() >= 100f) {
                    Iterator<Country.Diplomacy.Relation> iteratorRelation = this.diplomacy.relations.iterator();
                    while (iteratorRelation.hasNext()) {
                        Country.Diplomacy.Relation relation = iteratorRelation.next();
                        if (relation.country.regions.contains(division.getLocation())) {
                            relation.country.regions.remove(division.getLocation());
                            this.regions.add(division.getLocation());
                            if (relation.country.regions.size() == 0) {
                                countries.remove(relation.getCountry());
                                this.diplomacy.relations.remove(relation);
                            }
                            break;
                        }
                    }
                    division.currentOperation = new OperationStaying(division.getLocation());
                    Main.main.refresh();
                }
                operationOccupation.setOccupationProgress(operationOccupation.getOccupationProgress()
                        + ((float) (division.soldiers * 200f) / (float) division.getLocation().getPopulation()));
            }
        }
    }

    public void changePartyPopularity(Party party, int number) {
        for (Party p : this.parties) {
            if (!p.equals(party)) {
                p.setPopularity(p.getPopularity() - ((float) number / (this.parties.length - 1)));
            }
        }
        party.setPopularity(party.getPopularity() + number);
    }

    public void moveOneDay() {
        economy.checkIfBuilt();
        checkIfExisting();
        checkIfMoved();
        checkIfOccupied();
        military.checkIfTrained();
    }

    public void moveOneMonth() {
        for (Region region : regions) {
            region.setPopulation((int) (region.getPopulation() * 1.0005));
            military.setManpower((int) (military.getManpower() + (economy.getPopulation() * 0.01 * 0.05)));
        }
        economy.update(this);
        economy.addProfit();
    }

    public void setPartyPieChart() {
        Main.partyPieChart.clearData();
        for (int i = 0; i < parties.length; i++) {
            Main.partyPieChart.addData(new ModelPieChart(parties[i].getPartyName(), parties[i].getPopularity(),
                    Ideology.getIdeologyColor(i)));
        }
    }
}
