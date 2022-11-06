import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Country implements Comparable<Country> {
    private String name = "Пустка";
    private String code = "NON";
    private Color color = Color.YELLOW.darker();
    private Leader leader = new Leader("Ніхто", Ideology.ANARCHY, "Немає", 0);
    private ArrayList<Region> regions = new ArrayList<>();
    private String flagPath = String.format(tempFlagPath, code);
    private String portraitPath = String.format(tempPortraitPath, code + "_" + leader.ideology.valueOf());
    private Economy economy = new Economy();

    public final static String tempFlagPath = "files/flags/flag_%s.png";
    public final static String tempPortraitPath = "files/portraits/portrait_%s.png";

    public static ArrayList<Country> countries = new ArrayList<>();

    Country() {
    }

    Country(String name, String code, Color color, Leader leader, ArrayList<Region> regions) {
        this.name = name;
        this.code = code;
        this.color = color;
        this.leader = leader;
        this.regions = regions;
        this.flagPath = String.format(tempFlagPath, code);
        this.portraitPath = String.format(tempPortraitPath, code + "_" + leader.ideology.valueOf());
        this.economy = new Economy(this);
    }

    public static class Leader {
        private String fullName;
        private Ideology ideology;
        private String partyName;
        private float popularity;

        public Leader(String fullName, Ideology ideology, String partyName, float popularity) {
            this.fullName = fullName;
            this.ideology = ideology;
            this.partyName = partyName;
            if (popularity > 100f) {
                popularity = 100f;
            } else if (popularity < 0f) {
                popularity = 0f;
            }
            this.popularity = popularity;
        }

        public String getFullName() {
            return fullName;
        }

        public Ideology getIdeology() {
            return ideology;
        }

        public String getPartyName() {
            return partyName;
        }

        public float getPopularity() {
            return popularity;
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

        private float populationTaxesModifier = 20.5f;
        private float factoriesTaxesModifier = 13.5f;

        private float populationTaxes = 0f;
        private float factoriesTaxes = 0f;

        // TODO: Technology to improve output
        private float factoriesOutputModifier = 0f;
        private float factoriesOutput = 0f;

        private float profit = 0f;
        private long reserve = 0L;
        private long debt = 0L;

        private Economy() {

        }

        private Economy(Country country) {
            this.population = 0;
            this.factories = 0;
            for (Region region : country.regions) {
                this.population += region.getPopulation();
                this.factories += region.getFactories();
            }
            populationTaxes = population * ((populationTaxesModifier / 100f) * 20f);
            factoriesTaxes = factories * ((factoriesTaxesModifier / 100f) * 3500f);

            factoriesOutputModifier = factories * 5000f / (100f / factoriesTaxesModifier);
            factoriesOutput = factories * factoriesOutputModifier;

            profit += populationTaxes + factoriesTaxes + factoriesOutput;
        }

        @Override
        public String toString() {
            String string = "";
            string += "Населення: " + population + "\n";
            string += "Кількість заводів: " + factories + "\n";
            string += "Подушне: " + String.format("%.2f", populationTaxesModifier) + "%" + "\n";
            string += "Пофабричне: " + String.format("%.2f", factoriesTaxesModifier) + "%" + "\n";
            string += "Розмір подушного: " + String.format("%.2f", populationTaxes) + "₴" + "\n";
            string += "Розмір пофабричного: " + String.format("%.2f", factoriesTaxes) + "₴" + "\n";
            string += "Доход з фабричної продукції: " + String.format("%.2f", factoriesOutput) + "₴" + "\n";
            string += "Загальний прибуток (місячний): " + String.format("%.2f", profit) + "₴" + "\n";
            string += "Резерв: " + reserve + "₴" + "\n";
            string += "Борг: " + debt + "₴" + "\n";
            return string;
        }
    }

    public static enum Ideology {
        ANARCHY,
        TOTALISM,
        BILSHOVISM,
        MENSHEVISM,
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
                    return 1;
                case TOTALISM:
                    return 2;
                case BILSHOVISM:
                    return 3;
                case MENSHEVISM:
                    return 4;
                case SOCIAL_DEMOCRACY:
                    return 5;
                case VOLISM:
                    return 6;
                case ANARCHO_CAPITALISM:
                    return 7;
                case TRADITIONALISM:
                    return 8;
                case RETROGRADISM:
                    return 9;
                case CORPORATISM:
                    return 10;
                case DONTSOVISM:
                    return 11;
                case BANDERISM:
                    return 12;
                case RADICALISM:
                    return 13;
                default:
                    return 0;
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
                case MENSHEVISM:
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

    public Leader getLeader() {
        return leader;
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

    @Override
    public String toString() {
        return getName();
    }

    public String toStringForStats() {
        String string = "";
        string += "Назва держави: " + getName() + "\n";
        string += "Голова держави: " + getLeader().fullName + "\n";
        string += "Правляча партія: " + getLeader().partyName + "\n";
        string += "Ідеологія: " + getLeader().ideology + "\n";
        return string;
    }

    @Override
    public int compareTo(Country o) {
        return this.getName().compareTo(o.getName());
    }

    public static void setCountries() {

        countries.add(new Country("Україна", "UKR", new Color(80, 218, 46),
                new Leader("Володимир Зеленський", Ideology.VOLISM, "Слуга народу", 25),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(47), Region.getRegionByID(49),
                        Region.getRegionByID(52), Region.getRegionByID(34), Region.getRegionByID(46),
                        Region.getRegionByID(68) }))));

        countries.add(new Country("ЗСУ", "ZSU", new Color(236, 31, 120),
                new Leader("Валерій Залужний", Ideology.TRADITIONALISM,
                        "Кліка Залужного", 45),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(64), Region.getRegionByID(77),
                        Region.getRegionByID(87), Region.getRegionByID(50) }))));
        countries.add(new Country("Альянс Помсти", "ALP", new Color(0, 0, 0),
                new Leader("Білий Вождь", Ideology.BANDERISM,
                        "Партія реваншистів", 70),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(58), Region.getRegionByID(40),
                        Region.getRegionByID(45) }))));
        countries.add(new Country("Оновлена Україна", "DEM", new Color(255, 229,
                180),
                new Leader("Ігор Щедрін", Ideology.VOLISM,
                        "Демократична Сокира", 45),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(72) }))));
        countries.add(new Country("Фастівська Народна Республіка", "FAS", new Color(119, 210, 180),
                new Leader("Олесь Янчук", Ideology.SOCIAL_DEMOCRACY,
                        "Директорія - Петлюрівці", 65),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(71), Region.getRegionByID(80) }))));
        countries.add(new Country("Обухівський Мегакомбінат", "OBH", new Color(10,
                117, 223),
                new Leader("Віктор Семенець", Ideology.ANARCHO_CAPITALISM,
                        "Рада директорів", 95),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(70), Region.getRegionByID(89),
                        Region.getRegionByID(119) }))));
        countries.add(new Country("ПЦУ", "PCU", new Color(146, 0, 10),
                new Leader("Митрополит Агапіт", Ideology.RETROGRADISM,
                        "Ортодоксальне крило", 50),
                new ArrayList<>(
                        Arrays.asList(new Region[] { Region.getRegionByID(130), Region.getRegionByID(137),
                                Region.getRegionByID(134), Region.getRegionByID(106), Region.getRegionByID(98)
                        }))));
        // countries.add(new Country("Українська Комуністична Республіка", "UCR", new
        // Color(213, 15, 15),
        // new Leader("Петро Симоненко", Ideology.BILSHOVISM,
        // "КПУ - Стара Гвардія", 85),
        // new ArrayList<>(Arrays.asList(new Integer[] { 13, 17 }))));
        // countries.add(new Country("Соціалістична Республіка Україна", "SRU", new
        // Color(216,
        // 49, 155),
        // new Leader("Ілля Кива", Ideology.MENSHEVISM,
        // "СПУ - Кивовці", 25),
        // new ArrayList<>(Arrays.asList(new Integer[] { 29 }))));
        countries.add(new Country("19 ОБ РХБЗ", "NOB", new Color(10,
                111, 6),
                new Leader("Підполковник Мусій Шовкопляс",
                        Ideology.RADICALISM,
                        "Рада", 100),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(31) }))));

        countries.add(new Country("GSC", "GSC", new Color(51, 153, 153),
                new Leader("Сергій Григорович", Ideology.ANARCHO_CAPITALISM,
                        "Менеджери", 90),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(28) }))));
        countries.add(new Country("Спілка Грибників", "MSH", new Color(152,
                108, 95),
                new Leader("Дядько Свирид", Ideology.RETROGRADISM,
                        "Спілка грибників", 50),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(16) }))));
        countries.add(new Country("Зона", "STA", new Color(255,
                237, 123),
                new Leader("Бармен", Ideology.CORPORATISM,
                        "Вільні сталкери", 35),
                new ArrayList<>(Arrays.asList(new Region[] { Region.getRegionByID(15) }))));

        // countries.add(new Country("СС \"Юнґе Адлер\"", "UNI", new Color(249,
        // 239, 202),
        // new Leader("Мікаель фон Поплавскі", Ideology.RADICALISM,
        // "НСУАП - Флюґель дер Аґрономен", 100),
        // new ArrayList<>(Arrays.asList(new Integer[] { 243, 246 }))));

        // countries.add(new Country("РУНВіра", "RUN", new Color(42,
        // 98, 193),
        // new Leader("Олег Безверхий", Ideology.RETROGRADISM,
        // "Силенкоїсти", 100),
        // new ArrayList<>(Arrays.asList(new Integer[] { 0, 1 }))));

        Collections.sort(countries);
    }

    public String getDescription() {
        String text = "";
        try {
            File file = new File(String.format("files/textFiles/text_%s.txt", this.code));
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
}
