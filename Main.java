import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
// TODO: Borders for pics
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import javax.imageio.ImageIO;

public class Main {

    static Country yourCountry = new Country();
    public final static String tempFlagPath = "files/flags/flag_%s.png";
    public final static String tempPortraitPath = "files/portraits/portrait_%s.png";

    static JFrame jFrame;
    JPanel ui = null;
    JLabel output = new JLabel();

    JPanel statsPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(statsPanel, BoxLayout.Y_AXIS);

    // INFO STATS
    JTextArea countryStats = new JTextArea(yourCountry.toStringForStats());
    // No country
    ImageIcon flag = new ImageIcon(yourCountry.getFlagPath());
    ImageIcon portrait = new ImageIcon(yourCountry.getPortraitPath());
    JLabel flagLabel = new JLabel();
    JLabel portraitLabel = new JLabel();

    // ALL BUTTONS
    JButton buttonStats = new JButton("ІНФО");
    JButton buttonDiplomacy = new JButton("ДИПЛОМАТІЯ");
    JButton buttonMilitary = new JButton("ЕКОНОМІКА");
    JButton buttonEconomy = new JButton("ВІЙСЬКО");
    JButton buttonPriorities = new JButton("ПРІОРИТЕТИ");
    JButton buttonSettings = new JButton("НАЛАШТУВАННЯ");

    BufferedImage image;
    Area area;
    ArrayList<Shape> shapeList;
    static HashMap<Integer, Area> allRegions = new HashMap<>();
    static ArrayList<Country> countries = new ArrayList<>();

    public Main() {
        try {
            initUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static class Country implements Comparable<Country> {
        private String name = "Пустка";
        private String code = "NON";
        private Color color = Color.YELLOW.darker();
        private Leader leader = new Leader("Ніхто", Ideology.ANARCHY, "Немає", 0);
        private ArrayList<Integer> regions = new ArrayList<>();
        private String flagPath = String.format(tempFlagPath, code);
        private String portraitPath = String.format(tempPortraitPath, code + "_" + leader.ideology.valueOf());

        Country() {
        }

        Country(String name, String code, Color color, Leader leader, ArrayList<Integer> regions) {
            this.name = name;
            this.code = code;
            this.color = color;
            this.leader = leader;
            this.regions = regions;
            this.flagPath = String.format(tempFlagPath, code);
            this.portraitPath = String.format(tempPortraitPath, code + "_" + leader.ideology.valueOf());
        }

        public static class Leader {
            String fullName;
            Ideology ideology;
            String partyName;
            float popularity;

            Leader(String fullName, Ideology ideology, String partyName, float popularity) {
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

        public ArrayList<Integer> getRegions() {
            return regions;
        }

        public String getFlagPath() {
            return flagPath;
        }

        public String getPortraitPath() {
            return portraitPath;
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
        public int compareTo(Main.Country o) {
            return this.getName().compareTo(o.getName());
        }

    }

    public static void setCountries() {
        // Change

        countries.add(new Country("Україна", "UKR", new Color(80, 218, 46),
                new Country.Leader("Володимир Зеленський", Country.Ideology.VOLISM, "Слуга народу", 25),
                new ArrayList<>(Arrays.asList(new Integer[] { 47, 49, 52, 34, 46, 68, 70 }))));
        countries.add(new Country("ЗСУ", "ZSU", new Color(236, 31, 120),
                new Country.Leader("Валерій Залужний", Country.Ideology.TRADITIONALISM,
                        "Кліка Залужного", 45),
                new ArrayList<>(Arrays.asList(new Integer[] { 64, 77, 87, 50 }))));
        countries.add(new Country("Альянс Помсти", "ALP", new Color(0, 0, 0),
                new Country.Leader("Білий Вождь", Country.Ideology.BANDERISM,
                        "Партія реваншистів", 70),
                new ArrayList<>(Arrays.asList(new Integer[] { 58, 40, 45 }))));
        countries.add(new Country("Оновлена Україна", "DEM", new Color(255, 229, 180),
                new Country.Leader("Ігор Щедрін", Country.Ideology.VOLISM,
                        "Демократична Сокира", 45),
                new ArrayList<>(Arrays.asList(new Integer[] { 72 }))));
        countries.add(new Country("Фастівська Народна Республіка", "FAS", new Color(119, 210, 180),
                new Country.Leader("Олесь Янчук", Country.Ideology.SOCIAL_DEMOCRACY,
                        "Директорія - Петлюрівці", 65),
                new ArrayList<>(Arrays.asList(new Integer[] { 71, 80 }))));
        countries.add(new Country("ПЦУ", "PCU", new Color(146, 0, 10),
                new Country.Leader("Митрополит Агапіт", Country.Ideology.RETROGRADISM,
                        "Ортодоксальне крило", 50),
                new ArrayList<>(Arrays.asList(new Integer[] { 130, 137, 134, 119, 106, 98, 89
                }))));
        countries.add(new Country("Українська Комуністична Республіка", "UCR", new Color(213, 15, 15),
                new Country.Leader("Петро Симоненко", Country.Ideology.BILSHOVISM,
                        "КПУ - Стара Гвардія", 85),
                new ArrayList<>(Arrays.asList(new Integer[] { 13, 17 }))));

        countries.add(new Country("Соціалістична Республіка Україна", "SRU", new Color(216,
                49, 155),
                new Country.Leader("Ілля Кива", Country.Ideology.MENSHEVISM,
                        "СПУ - Кивовці", 25),
                new ArrayList<>(Arrays.asList(new Integer[] { 29 }))));
        countries.add(new Country("19 ОБ РХБЗ", "NOB", new Color(10,
                111, 6),
                new Country.Leader("Підполковник Мусій Шовкопляс", Country.Ideology.RADICALISM,
                        "Рада", 100),
                new ArrayList<>(Arrays.asList(new Integer[] { 31 }))));

        countries.add(new Country("GSC", "GSC", new Color(51, 153, 153),
                new Country.Leader("Сергій Григорович", Country.Ideology.ANARCHO_CAPITALISM,
                        "Менеджери", 90),
                new ArrayList<>(Arrays.asList(new Integer[] { 28 }))));
        countries.add(new Country("Спілка Грибників", "MSH", new Color(152,
                108, 95),
                new Country.Leader("Дядько Свирид", Country.Ideology.RETROGRADISM,
                        "Спілка грибників", 50),
                new ArrayList<>(Arrays.asList(new Integer[] { 15 }))));
        countries.add(new Country("Зона", "STA", new Color(255,
                237, 123),
                new Country.Leader("Бармен", Country.Ideology.CORPORATISM,
                        "Вільні сталкери", 35),
                new ArrayList<>(Arrays.asList(new Integer[] { 16 }))));
        countries.add(new Country("СС \"Юнґе Адлер\"", "UNI", new Color(249,
                239, 202),
                new Country.Leader("Мікаель фон Поплавскі", Country.Ideology.RADICALISM,
                        "НСУАП - Флюґель дер Аґрономен", 100),
                new ArrayList<>(Arrays.asList(new Integer[] { 243, 246 }))));

        Collections.sort(countries);

    }

    public final void initUI() throws Exception {
        if (ui != null) {
            return;
        }

        ui = new JPanel(new BorderLayout(4, 4));
        ui.setBorder(new EmptyBorder(4, 4, 4, 4));

        statsPanel.setLayout(boxLayout);
        statsPanel.setOpaque(true);
        statsPanel.setBackground(Color.BLACK);

        countryStats.setEditable(false);
        countryStats.setLineWrap(true);
        countryStats.setWrapStyleWord(true);
        countryStats.setForeground(Color.CYAN);
        countryStats.setBackground(Color.BLACK);

        File file = new File("files/map.png");
        image = ImageIO.read(file);
        area = getOutline(Color.WHITE, image, 12);
        shapeList = separateShapeIntoRegions(area);

        output.addMouseMotionListener(new MousePositionListener());
        output.addMouseListener(new MouseInputListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                Point p1 = output.getLocationOnScreen();
                int x = p.x - p1.x;
                int y = p.y - p1.y;
                Point pointOnImage = new Point(x, y);

                int key = 0;
                for (HashMap.Entry<Integer, Area> entrySet : allRegions.entrySet()) {
                    if (entrySet.getValue().contains(pointOnImage)) {
                        key = entrySet.getKey();
                        for (Country country : countries) {
                            if (country.getRegions().contains(key)) {
                                countryStats.setText(country.toStringForStats());
                                flag = new ImageIcon(country.getFlagPath());
                                portrait = new ImageIcon(country.getPortraitPath());

                                flagLabel.setIcon(flag);
                                portraitLabel.setIcon(portrait);

                                break;
                            }
                        }
                        System.out.println(key);
                        break;
                    }
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub

            }

        });

        JPanel picsPanel = new JPanel(new FlowLayout());
        picsPanel.setOpaque(true);
        picsPanel.setBackground(Color.BLACK);

        flagLabel.setIcon(flag);
        portraitLabel.setIcon(portrait);

        picsPanel.add(flagLabel);
        picsPanel.add(portraitLabel);

        statsPanel.add(countryStats);
        statsPanel.add(picsPanel);
        statsPanel.add(buttonStats);
        statsPanel.add(buttonDiplomacy);
        statsPanel.add(buttonMilitary);
        statsPanel.add(buttonEconomy);
        statsPanel.add(buttonPriorities);
        statsPanel.add(buttonSettings);
        ui.add(output);
        ui.add(statsPanel);

        refresh();
    }

    public Area getOutline(Color target, BufferedImage bi, int tolerance) {
        GeneralPath gp = new GeneralPath();

        boolean cont = false;
        for (int xx = 0; xx < bi.getWidth(); xx++) {
            for (int yy = 0; yy < bi.getHeight(); yy++) {
                if (isIncluded(new Color(bi.getRGB(xx, yy)), target, tolerance)) {
                    if (cont) {
                        gp.lineTo(xx, yy);
                        gp.lineTo(xx, yy + 1);
                        gp.lineTo(xx + 1, yy + 1);
                        gp.lineTo(xx + 1, yy);
                        gp.lineTo(xx, yy);
                    } else {
                        gp.moveTo(xx, yy);
                    }
                    cont = true;
                } else {
                    cont = false;
                }
            }
            cont = false;
        }
        gp.closePath();

        return new Area(gp);
    }

    public static ArrayList<Shape> separateShapeIntoRegions(Shape shape) {
        ArrayList<Shape> regions = new ArrayList<>();

        PathIterator pi = shape.getPathIterator(null);
        GeneralPath gp = new GeneralPath();

        int id = 0;
        while (!pi.isDone()) {
            double[] coords = new double[6];
            int pathSegmentType = pi.currentSegment(coords);
            int windingRule = pi.getWindingRule();
            gp.setWindingRule(windingRule);
            if (pathSegmentType == PathIterator.SEG_MOVETO) {
                gp = new GeneralPath();
                gp.setWindingRule(windingRule);
                gp.moveTo(coords[0], coords[1]);
            } else if (pathSegmentType == PathIterator.SEG_LINETO) {
                gp.lineTo(coords[0], coords[1]);
            } else if (pathSegmentType == PathIterator.SEG_QUADTO) {
                gp.quadTo(coords[0], coords[1], coords[2], coords[3]);
            } else if (pathSegmentType == PathIterator.SEG_CUBICTO) {
                gp.curveTo(
                        coords[0], coords[1],
                        coords[2], coords[3],
                        coords[4], coords[5]);
            } else if (pathSegmentType == PathIterator.SEG_CLOSE) {
                gp.closePath();
                Area area = new Area(gp);
                regions.add(area);
                if (area.getBounds().getHeight() > 5.0 && area.getBounds().getWidth() > 5.0) {
                    allRegions.put(id++, area);
                }
            } else {
                System.err.println("Unexpected value! " + pathSegmentType);
            }

            pi.next();
        }

        setCountries();

        return regions;
    }

    class MousePositionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            refresh();
        }
    }

    public static boolean isIncluded(Color target, Color pixel, int tolerance) {
        int rT = target.getRed();
        int gT = target.getGreen();
        int bT = target.getBlue();
        int rP = pixel.getRed();
        int gP = pixel.getGreen();
        int bP = pixel.getBlue();
        return ((rP - tolerance <= rT) && (rT <= rP + tolerance)
                && (gP - tolerance <= gT) && (gT <= gP + tolerance)
                && (bP - tolerance <= bT) && (bT <= bP + tolerance));
    }

    private void refresh() {
        output.setIcon(new ImageIcon(getImage()));
    }

    private BufferedImage getImage() {
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bi.createGraphics();
        g.drawImage(image, 0, 0, output);
        g.setColor(Color.ORANGE.darker());
        g.fill(area);

        for (Country country : countries) {
            // TODO: Name of countries
            for (Integer integer : country.getRegions()) {

                g.setColor(country.getColor());
                g.fill(allRegions.get(integer));
            }
        }

        g.setColor(Color.CYAN.darker().darker());
        g.draw(area);
        try {
            Point p = MouseInfo.getPointerInfo().getLocation();
            Point p1 = output.getLocationOnScreen();
            int x = p.x - p1.x;
            int y = p.y - p1.y;
            Point pointOnImage = new Point(x, y);
            for (Shape shape : shapeList) {
                if (shape.contains(pointOnImage) && allRegions.containsValue(shape)) {
                    g.setColor(Color.BLUE.brighter().brighter());
                    g.fill(shape);
                    break;
                }
            }
        } catch (Exception doNothing) {
        }

        g.dispose();

        return bi;
    }

    public JComponent getUI() {
        return ui;
    }

    public static void main(String[] args) {
        Runnable r = () -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Main o = new Main();

            // Menu before game

            JFrame mainMenu = new JFrame("Wild Fields");
            mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainMenu.setLocationByPlatform(true);
            mainMenu.setLayout(new FlowLayout());

            mainMenu.getContentPane().setBackground(Color.BLACK);
            mainMenu.setResizable(false);

            Country[] countriesArray = countriesToArray();
            JComboBox<Country> countrySelector = new JComboBox<>(countriesArray);

            JButton buttonPlay = new JButton("Play");
            buttonPlay.addActionListener(e -> {
                yourCountry = (Country) countrySelector.getSelectedItem();
                jFrame.setVisible(true);
                mainMenu.dispose();
            });

            mainMenu.add(countrySelector);
            mainMenu.add(buttonPlay);
            mainMenu.pack();

            mainMenu.setVisible(true);

            jFrame = new JFrame("Wild Fields");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setLocationByPlatform(true);
            jFrame.setContentPane(o.getUI());
            jFrame.setLayout(new FlowLayout());
            jFrame.setResizable(false);
            jFrame.getContentPane().setBackground(Color.BLACK);

            // TODO: Icon
            Image icon = new ImageIcon("files/icon.png")
                    .getImage();
            jFrame.setIconImage(icon);
            jFrame.pack();

            // TODO: Full Screen

        };
        SwingUtilities.invokeLater(r);
    }

    private static Country[] countriesToArray() {
        Country[] countriesArray = new Country[countries.size()];
        for (int i = 0; i < countriesArray.length; i++) {
            countriesArray[i] = countries.get(i);
        }
        return countriesArray;
    }
}
