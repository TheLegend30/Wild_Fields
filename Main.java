import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

// TODO: Borders for pics
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import javax.imageio.ImageIO;

public class Main {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
    public static Calendar calendar = new GregorianCalendar(2028, Calendar.AUGUST, 24);

    static String description = "";
    static Country yourCountry = new Country();
    static JFrame jFrame;
    JPanel ui = null;
    JLabel output = new JLabel();

    JPanel gamePanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(gamePanel, BoxLayout.Y_AXIS);

    JPanel infoPanel = new JPanel();

    // INFO STATS
    public static JLabel calendarLabel = new JLabel(dateFormat.format(calendar.getTime()));
    public static JTextArea regionStats = new JTextArea(Region.noRegion.toString());
    public static JTextArea countryStats = new JTextArea(yourCountry.toStringForStats());
    // No country

    public static JLabel flagLabel = new JLabel();
    public static JLabel portraitLabel = new JLabel();

    {
        flagLabel.setIcon(new ImageIcon(yourCountry.getFlagPath()));
        portraitLabel.setIcon(new ImageIcon(yourCountry.getPortraitPath()));
    }

    // ALL BUTTONS
    private JButton buttonResume = new JButton("⏵");
    private JButton buttonPause = new JButton("⏸");
    private JButton buttonInfo = new JButton("ІНФО");
    private JButton buttonDiplomacy = new JButton("ДИПЛОМАТІЯ");
    private JButton buttonEconomy = new JButton("ЕКОНОМІКА");
    private JButton buttonMilitary = new JButton("ВІЙСЬКО");
    private JButton buttonPriorities = new JButton("ПРІОРИТЕТИ");
    private JButton buttonSettings = new JButton("НАЛАШТУВАННЯ");

    BufferedImage image;
    Area area;
    ArrayList<Shape> shapeList;
    static HashMap<Integer, Area> allRegions = new HashMap<>();

    public Main() {
        try {
            initUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public final void initUI() throws Exception {
        if (ui != null) {
            return;
        }

        ui = new JPanel(new BorderLayout(4, 4));
        ui.setBorder(new EmptyBorder(4, 4, 4, 4));

        gamePanel.setLayout(boxLayout);
        gamePanel.setOpaque(true);
        gamePanel.setBackground(Color.BLACK);

        regionStats.setEditable(false);
        regionStats.setLineWrap(true);
        regionStats.setWrapStyleWord(true);
        regionStats.setForeground(Color.CYAN);
        regionStats.setBackground(Color.BLACK);

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
                        regionStats.setText(
                                key + "\n"
                                        + (!Region.getRegionByID(key).equals(Region.noRegion)
                                                ? Region.getRegionByID(key).toString()
                                                : Region.noRegion.toString()));
                        break;
                    }
                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }

        });

        buttonInfo.addActionListener(e -> {
            countryStats.setText(yourCountry.toStringForStats());
        });
        buttonEconomy.addActionListener(e -> {
            countryStats.setText(yourCountry.getEconomy().toString());
        });

        JPanel picsPanel = new JPanel(new FlowLayout());
        picsPanel.setOpaque(true);
        picsPanel.setBackground(Color.BLACK);

        // init panals
        picsPanel.add(flagLabel);
        picsPanel.add(portraitLabel);

        JPanel timePanel = new JPanel(new FlowLayout());
        timePanel.setOpaque(true);
        timePanel.setBackground(Color.BLACK);
        timePanel.add(calendarLabel);
        timePanel.add(buttonResume);
        timePanel.add(buttonPause);

        Timer timer = new Timer(1000, e -> {
            calendar.add(Calendar.DATE, 1);
            calendarLabel.setText(dateFormat.format(calendar.getTime()));
            // Move for countries
        });

        buttonResume.addActionListener(e -> {
            timer.start();
        });

        buttonPause.addActionListener(e -> {
            timer.stop();
        });

        BoxLayout infoBox = new BoxLayout(infoPanel, BoxLayout.Y_AXIS);
        infoPanel.setLayout(infoBox);
        infoPanel.add(regionStats);
        infoPanel.add(countryStats);
        infoPanel.setOpaque(true);
        infoPanel.setBackground(Color.BLACK);

        calendarLabel.setForeground(Color.CYAN);

        gamePanel.add(timePanel);
        gamePanel.add(infoPanel);
        gamePanel.add(picsPanel);

        addButtonsToGamePanel();

        ui.add(output);
        ui.add(gamePanel);

        refresh();
    }

    private void addButtonsToGamePanel() {
        gamePanel.add(buttonInfo);
        gamePanel.add(buttonDiplomacy);
        gamePanel.add(buttonEconomy);
        gamePanel.add(buttonMilitary);
        gamePanel.add(buttonPriorities);
        gamePanel.add(buttonSettings);
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

        Region.setRegions();
        Country.setCountries();

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

        for (Country country : Country.countries) {
            // TODO: Name of countries
            for (Region region : country.getRegions()) {
                g.setColor(country.getColor());
                g.fill(allRegions.get(region.getID()));
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

        for (Country country : Country.countries) {
            if (country.getName().equals(yourCountry.getName())) {
                for (Region region : country.getRegions()) {
                    g.setColor(country.getColor().brighter().brighter().brighter());
                    g.draw(allRegions.get(region.getID()));
                }
                break;
            }
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
            try {
                description = Files.readString(Path.of("files/textFiles/text_description.txt"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // Menu before game

            JFrame mainMenu = new JFrame("Wild Fields");
            mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainMenu.setLocationByPlatform(true);
            mainMenu.setLayout(new FlowLayout());
            mainMenu.getContentPane().setBackground(Color.BLACK);
            mainMenu.setResizable(false);

            JPanel startMenuPanel = new JPanel();
            BoxLayout startMenuPanelBoxLayout = new BoxLayout(startMenuPanel, BoxLayout.Y_AXIS);
            startMenuPanel.setLayout(startMenuPanelBoxLayout);
            startMenuPanel.setBackground(Color.BLACK);

            JPanel aboutCountryPanel = new JPanel(new FlowLayout());
            aboutCountryPanel.setBackground(Color.BLACK);

            JLabel flagPic = new JLabel();
            JLabel portraitPic = new JLabel();
            JTextArea aboutCountryLabel = new JTextArea();
            aboutCountryLabel.setBackground(Color.BLACK);
            aboutCountryLabel.setForeground(Color.CYAN);
            aboutCountryLabel.setOpaque(true);

            JTextArea descriptionCountryLabel = new JTextArea();
            descriptionCountryLabel.setBackground(Color.BLACK);
            descriptionCountryLabel.setForeground(Color.CYAN);
            descriptionCountryLabel.setOpaque(true);
            descriptionCountryLabel.setSize(new Dimension(500, 500));

            aboutCountryPanel.add(flagPic);
            aboutCountryPanel.add(portraitPic);

            Country[] countriesArray = countriesToArray();
            JComboBox<Country> countrySelector = new JComboBox<>(countriesArray);
            countrySelector.addActionListener(e -> {
                yourCountry = (Country) countrySelector.getSelectedItem();
                flagPic.setIcon(new ImageIcon(yourCountry.getFlagPath()));
                portraitPic.setIcon(new ImageIcon(yourCountry.getPortraitPath()));
                aboutCountryLabel.setText(yourCountry.toStringForStats());
                descriptionCountryLabel.setText(yourCountry.getDescription());
                mainMenu.pack();
            });

            yourCountry = (Country) countrySelector.getSelectedItem();
            flagPic.setIcon(new ImageIcon(yourCountry.getFlagPath()));
            portraitPic.setIcon(new ImageIcon(yourCountry.getPortraitPath()));
            aboutCountryLabel.setText(yourCountry.toStringForStats());
            descriptionCountryLabel.setText(yourCountry.getDescription());

            JButton buttonPlay = new JButton("Почати гру");
            buttonPlay.addActionListener(e -> {
                regionStats.setText(yourCountry.getRegions().get(0).toString());
                countryStats.setText(yourCountry.toStringForStats());

                flagLabel.setIcon(new ImageIcon((yourCountry.getFlagPath().toString())));
                portraitLabel.setIcon(new ImageIcon((yourCountry.getPortraitPath().toString())));

                jFrame.setVisible(true);
                mainMenu.dispose();
            });

            JButton buttonDescription = new JButton("Опис гри");
            buttonDescription.addActionListener(e -> {
                JOptionPane.showMessageDialog(mainMenu,
                        description);
            });

            JButton buttonExit = new JButton("Вийти");
            buttonExit.addActionListener(e -> System.exit(0));

            startMenuPanel.add(countrySelector);
            startMenuPanel.add(aboutCountryLabel);
            startMenuPanel.add(descriptionCountryLabel);
            startMenuPanel.add(aboutCountryPanel);
            startMenuPanel.add(buttonPlay);
            startMenuPanel.add(buttonDescription);
            startMenuPanel.add(buttonExit);

            mainMenu.add(startMenuPanel);
            mainMenu.pack();

            mainMenu.setVisible(true);

            jFrame = new JFrame("Wild Fields");
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setLocationByPlatform(true);
            jFrame.setContentPane(o.getUI());
            jFrame.setLayout(new FlowLayout());
            // jFrame.setResizable(false);
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
        Country[] countriesArray = new Country[Country.countries.size()];
        for (int i = 0; i < countriesArray.length; i++) {
            countriesArray[i] = Country.countries.get(i);
        }
        return countriesArray;
    }
}
