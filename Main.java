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
    public static Calendar calendar = new GregorianCalendar(2028, Calendar.NOVEMBER, 8);
    public static JLabel calendarLabel = new JLabel(dateFormat.format(calendar.getTime()));

    static String description = "";
    static Country yourCountry = new Country();
    static Region selectedRegion = Region.noRegion;
    static JFrame mainMenuFrame;
    static JFrame gameFrame;
    JPanel ui = null;
    JLabel output = new JLabel();

    JPanel gamePanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(gamePanel, BoxLayout.Y_AXIS);

    JPanel infoPanel = new JPanel();
    JScrollPane infoPane = new JScrollPane(infoPanel);

    // INFO STATS
    public static JTextArea regionStats = new JTextArea(selectedRegion.toString());
    public static JTextArea countryStats = new JTextArea(yourCountry.toStringForStats());
    public static JSlider populationtTaxesModifierSlider = new JSlider(0, 100);
    public static JSlider factoriesTaxesModifierSlider = new JSlider(0, 100);
    // ECONOMY STATS
    public static JTextArea economyStats = new JTextArea(yourCountry.getEconomy().toString());
    public static JTextArea queueStats = new JTextArea(yourCountry.getEconomy().getQueue());
    // SETTINGS BUTTON
    public static JButton exitToMenuButton = new JButton("Вийти");

    public static JButton playSongButton = new JButton("⏵");
    public static JButton pauseSongButton = new JButton("⏸");
    public static JButton nextSongButton = new JButton("Наступна пісня");
    public static JButton previousSongButton = new JButton("Попередня пісня");
    // No country

    // Some variables
    public static JButton buildFactoryButton;
    static Boolean isBuilding = false;

    public static JLabel flagLabel = new JLabel();
    public static JLabel portraitLabel = new JLabel();

    // ALL BUTTONS
    private JButton resumeButton = new JButton("⏵");
    private JButton fasterButton = new JButton("⏵⏵");
    private JButton pauseButton = new JButton("⏸");
    private JPanel buttonPanel = new JPanel(new GridLayout(3, 2));
    private JButton infoButton = new JButton("ІНФО");
    private JButton diplomacyButton = new JButton("ДИПЛОМАТІЯ");
    private JButton economyButton = new JButton("ЕКОНОМІКА");
    private JButton militaryButton = new JButton("ВІЙСЬКО");
    private JButton prioritiesButton = new JButton("ПРІОРИТЕТИ");
    private JButton settingsButton = new JButton("НАЛАШТУВАННЯ");

    public static MidiPlayer player;
    BufferedImage image;
    Area area;
    ArrayList<Shape> shapeList;
    static HashMap<Integer, Area> allRegions = new HashMap<>();

    Thread checkIfPlayerRunningThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                player.checkIfRunning();
            }
        }
    });

    {
        flagLabel.setIcon(new ImageIcon(yourCountry.getFlagPath()));
        portraitLabel.setIcon(new ImageIcon(yourCountry.getPortraitPath()));
        player = new MidiPlayer();
        // player.start();
        // checkIfPlayerRunningThread.start();
    }

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

        infoPane.setPreferredSize(new Dimension(300, 350));

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

        economyStats.setEditable(false);
        economyStats.setLineWrap(true);
        economyStats.setWrapStyleWord(true);
        economyStats.setForeground(Color.CYAN);
        economyStats.setBackground(Color.BLACK);

        queueStats.setEditable(false);
        queueStats.setLineWrap(true);
        queueStats.setWrapStyleWord(true);
        queueStats.setForeground(Color.CYAN);
        queueStats.setBackground(Color.BLACK);

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
                if (isBuilding) {
                    for (HashMap.Entry<Integer, Area> entrySet : allRegions.entrySet()) {
                        if (entrySet.getValue().contains(pointOnImage)) {
                            key = entrySet.getKey();
                            if (yourCountry.getRegions().contains(Region.getRegionByID(key))) {
                                selectedRegion = Region.getRegionByID(key);
                                yourCountry.getEconomy().buildFactory(selectedRegion);
                                yourCountry.getEconomy().update(yourCountry);
                                refresh();
                            }
                        }
                    }
                } else {
                    for (HashMap.Entry<Integer, Area> entrySet : allRegions.entrySet()) {
                        if (entrySet.getValue().contains(pointOnImage)) {
                            key = entrySet.getKey();
                            selectedRegion = Region.getRegionByID(key);
                            regionStats.setText(
                                    key + "\n"
                                            + (!selectedRegion.equals(Region.noRegion)
                                                    ? selectedRegion.toString()
                                                    : Region.noRegion.toString()));

                            break;
                        }
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

        JPanel picsPanel = new JPanel(new FlowLayout());
        picsPanel.setOpaque(true);
        picsPanel.setBackground(Color.BLACK);

        // init panals
        picsPanel.add(flagLabel);
        picsPanel.add(portraitLabel);

        JPanel timePanel = new JPanel(
                new FlowLayout());
        timePanel.setOpaque(true);
        timePanel.setBackground(Color.BLACK);
        timePanel.add(calendarLabel);
        timePanel.add(resumeButton);
        timePanel.add(fasterButton);
        timePanel.add(pauseButton);

        Timer timer = new Timer(1000, e -> {
            calendar.add(Calendar.DATE, 1);
            calendarLabel.setText(dateFormat.format(calendar.getTime()));
            yourCountry.moveOneDay();
            if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
                yourCountry.moveOneMonth();
            }
            updateGamePanel();
            // TODO: Move for other countries
        });

        resumeButton.addActionListener(e -> {
            resumeButton.setForeground(Color.CYAN);
            fasterButton.setForeground(Color.BLACK);
            pauseButton.setForeground(Color.BLACK);
            timer.setDelay(1000);
            timer.start();
        });

        fasterButton.addActionListener(e -> {
            resumeButton.setForeground(Color.BLACK);
            fasterButton.setForeground(Color.CYAN);
            pauseButton.setForeground(Color.BLACK);
            timer.setDelay(100);
            timer.start();
        });

        pauseButton.addActionListener(e -> {
            resumeButton.setForeground(Color.BLACK);
            fasterButton.setForeground(Color.BLACK);
            pauseButton.setForeground(Color.CYAN);
            timer.stop();
        });

        pauseButton.doClick();

        exitToMenuButton.addActionListener(e -> {
            gameFrame.dispose();
            mainMenuFrame.setVisible(true);
        });

        playSongButton.addActionListener(e -> {
            player.start();
        });

        pauseSongButton.addActionListener(e -> {
            player.stop();
        });

        nextSongButton.addActionListener(e -> {
            player.nextSong();
        });

        previousSongButton.addActionListener(e -> {
            player.previousSong();
        });

        BoxLayout infoBox = new BoxLayout(infoPanel,
                BoxLayout.Y_AXIS);
        infoPanel.setLayout(infoBox);
        infoPanel.add(regionStats);
        infoPanel.add(countryStats);
        infoPanel.setOpaque(true);
        infoPanel.setBackground(Color.BLACK);

        calendarLabel.setFont(new Font(null, Font.PLAIN, 10));
        calendarLabel.setForeground(Color.CYAN);

        populationtTaxesModifierSlider.setOpaque(true);
        populationtTaxesModifierSlider.setForeground(Color.BLACK);
        populationtTaxesModifierSlider.setBackground(Color.CYAN);
        populationtTaxesModifierSlider.setPaintTrack(true);
        populationtTaxesModifierSlider.setMinorTickSpacing(5);
        populationtTaxesModifierSlider.setMajorTickSpacing(20);
        populationtTaxesModifierSlider.setPaintLabels(true);
        populationtTaxesModifierSlider.addChangeListener(e -> {
            yourCountry.getEconomy().setPopulationTaxesModifier(populationtTaxesModifierSlider.getValue());
            yourCountry.getEconomy().update(yourCountry);
            economyStats.setText(yourCountry.getEconomy().toString());
        });

        factoriesTaxesModifierSlider.setOpaque(true);
        factoriesTaxesModifierSlider.setForeground(Color.BLACK);
        factoriesTaxesModifierSlider.setBackground(Color.CYAN);
        factoriesTaxesModifierSlider.setPaintTrack(true);
        factoriesTaxesModifierSlider.setMinorTickSpacing(5);
        factoriesTaxesModifierSlider.setMajorTickSpacing(20);
        factoriesTaxesModifierSlider.setPaintLabels(true);
        factoriesTaxesModifierSlider.addChangeListener(e -> {
            yourCountry.getEconomy().setFactoriesTaxesModifier(factoriesTaxesModifierSlider.getValue());
            yourCountry.getEconomy().update(yourCountry);
            economyStats.setText(yourCountry.getEconomy().toString());
        });

        buildFactoryButton = new JButton("Побудувати фабрику");
        JButton queueButton = new JButton("Список будівництва");
        JButton queueBackButton = new JButton("Назад");

        buildFactoryButton.addActionListener(e -> {
            buildFactoryMethod();
        });

        queueButton.addActionListener(e -> {
            infoPanel.removeAll();
            infoPanel.add(queueStats);
            infoPanel.add(queueBackButton);
            queueStats.setText(yourCountry.getEconomy().getQueue());
            gamePanel.updateUI();
        });

        queueBackButton.addActionListener(e -> {
            economyButton.doClick();
        });

        infoButton.addActionListener(e -> {
            infoPanel.removeAll();
            infoPanel.add(regionStats);
            infoPanel.add(countryStats);
            regionStats.setText(selectedRegion.toString());
            countryStats.setText(yourCountry.toStringForStats());
            gamePanel.updateUI();
        });
        economyButton.addActionListener(e -> {
            infoPanel.removeAll();
            infoPanel.add(economyStats);
            infoPanel.add(populationtTaxesModifierSlider);
            infoPanel.add(factoriesTaxesModifierSlider);
            infoPanel.add(buildFactoryButton);
            infoPanel.add(queueButton);
            economyStats.setText(yourCountry.getEconomy().toString());
            populationtTaxesModifierSlider.setValue(yourCountry.getEconomy().getPopulationTaxesModifier());
            factoriesTaxesModifierSlider.setValue(yourCountry.getEconomy().getFactoriesTaxesModifier());
            gamePanel.updateUI();
        });
        settingsButton.addActionListener(e -> {
            infoPanel.removeAll();
            JPanel songPanel = new JPanel(new GridLayout(2, 3));
            songPanel.setBackground(Color.BLACK);
            infoPanel.add(exitToMenuButton);

            songPanel.add(playSongButton);
            songPanel.add(pauseSongButton);
            songPanel.add(nextSongButton);
            songPanel.add(previousSongButton);

            infoPanel.add(songPanel);
            gamePanel.updateUI();
        });

        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Color.BLACK);

        buttonPanel.add(infoButton);
        buttonPanel.add(diplomacyButton);
        buttonPanel.add(economyButton);
        buttonPanel.add(militaryButton);
        buttonPanel.add(prioritiesButton);
        buttonPanel.add(settingsButton);

        gamePanel.add(timePanel);
        gamePanel.add(infoPane);
        gamePanel.add(picsPanel);
        gamePanel.add(buttonPanel);

        ui.add(output);
        ui.add(gamePanel);

        refresh();

    }

    private void buildFactoryMethod() {
        isBuilding = !isBuilding;
        if (isBuilding) {
            buildFactoryButton.setText("Відмінити");
        } else {
            buildFactoryButton.setText("Побудувати фабрику");
        }
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
        updateGamePanel();
    }

    private void updateGamePanel() {
        regionStats.setText(selectedRegion.toString());
        countryStats.setText(yourCountry.toStringForStats());
        economyStats.setText(yourCountry.getEconomy().toString());
        queueStats.setText(yourCountry.getEconomy().getQueue());
        gamePanel.updateUI();
    }

    private BufferedImage getImage() {
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bi.createGraphics();
        g.drawImage(image, 0, 0, output);
        g.setColor(Color.ORANGE.darker());
        g.fill(area);

        if (isBuilding) {
            for (Country country : Country.countries) {
                if (country.getName().equals(yourCountry.getName())) {
                    for (Region region : country.getRegions()) {
                        g.setColor(Color.GREEN);
                        g.fill(allRegions.get(region.getID()));
                    }
                }
            }
        } else {
            for (Country country : Country.countries) {
                // TODO: Name of countries
                for (Region region : country.getRegions()) {
                    g.setColor(country.getColor());
                    g.fill(allRegions.get(region.getID()));
                }
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

        mainMenuFrame = new JFrame("Wild Fields");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setLocationByPlatform(true);
        mainMenuFrame.setLayout(new FlowLayout());
        mainMenuFrame.getContentPane().setBackground(Color.BLACK);
        mainMenuFrame.setResizable(false);

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

        JLabel gameSignLabel = new JLabel();
        gameSignLabel.setIcon(new ImageIcon("files/logo.png"));
        Country[] countriesArray = countriesToArray();
        JComboBox<Country> countrySelector = new JComboBox<>(countriesArray);
        countrySelector.addActionListener(e -> {
            yourCountry = (Country) countrySelector.getSelectedItem();
            flagPic.setIcon(new ImageIcon(yourCountry.getFlagPath()));
            portraitPic.setIcon(new ImageIcon(yourCountry.getPortraitPath()));
            aboutCountryLabel.setText(yourCountry.toStringForStats());
            descriptionCountryLabel.setText(yourCountry.getDescription());
            mainMenuFrame.pack();
        });

        yourCountry = (Country) countrySelector.getSelectedItem();
        flagPic.setIcon(new ImageIcon(yourCountry.getFlagPath()));
        portraitPic.setIcon(new ImageIcon(yourCountry.getPortraitPath()));
        aboutCountryLabel.setText(yourCountry.toStringForStats());
        descriptionCountryLabel.setText(yourCountry.getDescription());

        JButton buttonPlay = new JButton("Почати гру");
        buttonPlay.addActionListener(e -> {
            selectedRegion = yourCountry.getRegions().get(0);
            regionStats.setText(selectedRegion.toString());
            countryStats.setText(yourCountry.toStringForStats());

            flagLabel.setIcon(new ImageIcon((yourCountry.getFlagPath().toString())));
            portraitLabel.setIcon(new ImageIcon((yourCountry.getPortraitPath().toString())));

            gameFrame.setVisible(true);
            mainMenuFrame.dispose();
        });

        JButton buttonDescription = new JButton("Опис гри");
        buttonDescription.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainMenuFrame,
                    description);
        });

        JButton buttonExit = new JButton("Вийти");
        buttonExit.addActionListener(e -> System.exit(0));

        startMenuPanel.add(gameSignLabel);
        startMenuPanel.add(countrySelector);
        startMenuPanel.add(aboutCountryLabel);
        startMenuPanel.add(descriptionCountryLabel);
        startMenuPanel.add(aboutCountryPanel);
        startMenuPanel.add(buttonPlay);
        startMenuPanel.add(buttonDescription);
        startMenuPanel.add(buttonExit);

        mainMenuFrame.add(startMenuPanel);
        mainMenuFrame.pack();

        mainMenuFrame.setVisible(true);

        gameFrame = new JFrame("Wild Fields");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationByPlatform(true);
        gameFrame.setContentPane(o.getUI());
        gameFrame.setLayout(new FlowLayout());
        gameFrame.getContentPane().setBackground(Color.BLACK);

        // TODO: Icon
        Image icon = new ImageIcon("files/icon.png")
                .getImage();
        gameFrame.setIconImage(icon);
        gameFrame.pack();

        // TODO: Full Screen
    }

    private static Country[] countriesToArray() {
        Country[] countriesArray = new Country[Country.countries.size()];
        for (int i = 0; i < countriesArray.length; i++) {
            countriesArray[i] = Country.countries.get(i);
        }
        return countriesArray;
    }
}
