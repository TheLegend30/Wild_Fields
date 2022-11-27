package src;

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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ColorUIResource;

import javax.imageio.ImageIO;

public class Main {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy");
    public static Calendar calendar = new GregorianCalendar(2028, Calendar.JUNE, 28);
    public static JLabel calendarLabel = new JLabel(dateFormat.format(calendar.getTime()));

    static String description = "";
    static Country yourCountry = new Country();
    static Country.Military.Division selectedDivision;
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
    public static JButton averagePopulationButton;
    public static JButton averageFactoriesButton;
    public static JButton buildFactoryButton;
    // DIPLOMACY STATS
    public static JComboBox<Country.Diplomacy.Relation> countriesRelationsComboBox;
    static Country.Diplomacy.Relation currentRelation;
    public static JTextArea diplomacyStats = new JTextArea("");
    public static JButton declareWarButton = new JButton("Оголосити війну");
    // MILITARY STATS
    public static JTextArea militaryStats = new JTextArea(yourCountry.getMilitary().toString());
    public static JList<Country.Military.Division> militaryList = new JList<>(
            yourCountry.getMilitary().getDivisionsArray());
    public static JTextArea trainingStats = new JTextArea(yourCountry.getEconomy().getQueue());
    public static JButton trainDivisionButton;
    // PRIORITIES STATs
    public static JPanel prioritiesPanel = new JPanel();
    // SETTINGS BUTTON
    public static JButton exitToMenuButton = new JButton("Вийти");

    public static JButton playSongButton = new JButton("⏵");
    public static JButton pauseSongButton = new JButton("⏸");
    public static JButton nextSongButton = new JButton("Наступна пісня");
    public static JButton previousSongButton = new JButton("Попередня пісня");
    // No country

    // Some variables
    static Regime mapRegime = Regime.STANDARD;

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
    public static HashMap<Integer, Area> allRegions = new HashMap<>();

    Thread checkIfPlayerRunningThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (player.checkIfRunning()) {
                    player.nextSong();
                    player.start();
                }
            }
        }
    });

    {
        Region.setRegions();
        Country.setCountries();
        flagLabel.setIcon(new ImageIcon(yourCountry.getFlagPath()));
        portraitLabel.setIcon(new ImageIcon(yourCountry.getPortraitPath()));
        player = new MidiPlayer();
        yourCountry = Country.countries.get(0);
        countriesRelationsComboBox = new JComboBox<>(yourCountry.getDiplomacy().getRelationsArray());
        currentRelation = (Country.Diplomacy.Relation) countriesRelationsComboBox.getSelectedItem();
        diplomacyStats = new JTextArea(currentRelation.toStringForStats());

        // TODO: MP3
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

    public static enum Regime {
        STANDARD,
        BUILDING,
        TRAINING,
        MOVING,
        AVERAGE_POPULATION,
        AVERAGE_FACTORIES
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

        countriesRelationsComboBox.addActionListener(e -> {
            currentRelation = (Country.Diplomacy.Relation) countriesRelationsComboBox.getSelectedItem();
            diplomacyStats.setText(currentRelation.toStringForStats());
        });

        diplomacyStats.setEditable(false);
        diplomacyStats.setLineWrap(true);
        diplomacyStats.setWrapStyleWord(true);
        diplomacyStats.setForeground(Color.CYAN);
        diplomacyStats.setBackground(Color.BLACK);

        declareWarButton.addActionListener(e -> {
            currentRelation.setInWar(true);
            diplomacyStats.setText(currentRelation.toStringForStats());
        });

        prioritiesPanel.setBackground(Color.BLACK);

        militaryStats.setEditable(false);
        militaryStats.setLineWrap(true);
        militaryStats.setWrapStyleWord(true);
        militaryStats.setForeground(Color.CYAN);
        militaryStats.setBackground(Color.BLACK);

        militaryList.setForeground(Color.CYAN);
        militaryList.setBackground(Color.BLACK);

        trainingStats.setEditable(false);
        trainingStats.setLineWrap(true);
        trainingStats.setWrapStyleWord(true);
        trainingStats.setForeground(Color.CYAN);
        trainingStats.setBackground(Color.BLACK);

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
                if (mapRegime == Regime.BUILDING) {
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
                } else if (mapRegime == Regime.TRAINING) {
                    for (HashMap.Entry<Integer, Area> entrySet : allRegions.entrySet()) {
                        if (entrySet.getValue().contains(pointOnImage)) {
                            key = entrySet.getKey();
                            if (yourCountry.getRegions().contains(Region.getRegionByID(key))) {
                                if (yourCountry.getMilitary().getManpower() >= 1000) {
                                    yourCountry.getMilitary()
                                            .setManpower(yourCountry.getMilitary().getManpower() - 1000);
                                    selectedRegion = Region.getRegionByID(key);
                                    yourCountry.getMilitary().trainDivision(selectedRegion);
                                    refresh();
                                    updateGamePanel();
                                } else {
                                    JOptionPane.showMessageDialog(gameFrame,
                                            "Недостатньо призовників. Необхідно хоча б 1000 чоловік.");
                                }

                            }
                        }
                    }
                } else if (mapRegime == Regime.MOVING) {
                    for (HashMap.Entry<Integer, Area> entrySet : allRegions.entrySet()) {
                        if (entrySet.getValue().contains(pointOnImage)) {
                            key = entrySet.getKey();
                            if (yourCountry.getRegions().contains(Region.getRegionByID(key))) {
                                selectedRegion = Region.getRegionByID(key);
                                selectedDivision.moveDivision(selectedRegion);
                                updateGamePanel();
                                mapRegime = Regime.STANDARD;
                                refresh();
                                militaryList.clearSelection();
                            } else {
                                for (Country.Diplomacy.Relation relation : yourCountry.getDiplomacy().relations) {
                                    if (relation.areInWar()) {
                                        if (relation.getCountry().getRegions().contains(Region.getRegionByID(key))) {
                                            selectedRegion = Region.getRegionByID(key);
                                            selectedDivision.moveDivision(selectedRegion);
                                            updateGamePanel();
                                            mapRegime = Regime.STANDARD;
                                            refresh();
                                            militaryList.clearSelection();
                                        }
                                    }
                                }
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
            EventPane.showNews();
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

        averagePopulationButton = new JButton("Середнє населення по Україні");
        averageFactoriesButton = new JButton("Середнє кількість заводів по Україні");
        buildFactoryButton = new JButton("Побудувати фабрику");
        JButton queueButton = new JButton("Список будівництв");
        JButton queueBackButton = new JButton("Назад");
        trainDivisionButton = new JButton("Підготувати дивізію");
        JButton trainingButton = new JButton("Дивізії, що готуються");
        JButton trainingBackButton = new JButton("Назад");
        JButton divisionsButton = new JButton("Дивізії");
        JButton divisionBackButton = new JButton("Назад");

        averagePopulationButton.addActionListener(e -> {
            averagePopulationMethod();
        });

        averageFactoriesButton.addActionListener(e -> {
            averageFactoriesMethod();
        });

        buildFactoryButton.addActionListener(e -> {
            buildFactoryMethod();
        });

        queueButton.addActionListener(e -> {
            infoPanel.removeAll();
            infoPanel.add(queueBackButton);
            infoPanel.add(queueStats);
            queueStats.setText(yourCountry.getEconomy().getQueue());
            gamePanel.updateUI();
        });

        queueBackButton.addActionListener(e -> {
            economyButton.doClick();
        });

        trainDivisionButton.addActionListener(e -> {
            trainDivisionMethod();
        });

        trainingButton.addActionListener(e -> {
            infoPanel.removeAll();
            JPanel trainingPanel = new JPanel();
            BoxLayout box = new BoxLayout(trainingPanel, BoxLayout.Y_AXIS);
            trainingPanel.setLayout(box);
            trainingPanel.add(trainingStats);
            JScrollPane trainingPane = new JScrollPane(trainingPanel);
            infoPanel.add(trainingBackButton);
            infoPanel.add(trainingPane);
            trainingStats.setText(yourCountry.getMilitary().getTraining());
            trainingStats.setFont(new Font(null, Font.PLAIN, 11));
            gamePanel.updateUI();
        });

        trainingBackButton.addActionListener(e -> {
            militaryButton.doClick();
        });

        divisionsButton.addActionListener(e -> {
            infoPanel.removeAll();
            JPanel militaryPanel = new JPanel(new FlowLayout());
            militaryPanel.add(militaryList);
            JScrollPane militaryPane = new JScrollPane(militaryPanel);
            infoPanel.add(divisionBackButton);
            infoPanel.add(militaryPane);
            militaryList.setFont(new Font(null, Font.PLAIN, 8));
            militaryList.setListData(yourCountry.getMilitary().getDivisionsArray());
            gamePanel.updateUI();
        });

        divisionBackButton.addActionListener(e -> {
            militaryButton.doClick();
        });

        militaryList.addMouseListener(new MouseAdapter() {

            int lastSelectedIndex = -1;

            public void mouseClicked(MouseEvent e) {

                int index = militaryList.getSelectedIndex();

                if (index != -1 && index == lastSelectedIndex) {
                    militaryList.clearSelection();
                    mapRegime = Regime.STANDARD;
                    refresh();
                }

                lastSelectedIndex = militaryList.getSelectedIndex();
            }
        });

        militaryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && militaryList.getSelectedIndex() >= 0) {
                selectedDivision = militaryList.getSelectedValue();
                moveDivisionMethod();
            }
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
            infoPanel.add(buildFactoryButton);
            infoPanel.add(averagePopulationButton);
            infoPanel.add(averageFactoriesButton);
            infoPanel.add(queueButton);
            infoPanel.add(populationtTaxesModifierSlider);
            infoPanel.add(factoriesTaxesModifierSlider);
            economyStats.setText(yourCountry.getEconomy().toString());
            populationtTaxesModifierSlider.setValue(yourCountry.getEconomy().getPopulationTaxesModifier());
            factoriesTaxesModifierSlider.setValue(yourCountry.getEconomy().getFactoriesTaxesModifier());
            gamePanel.updateUI();
        });
        diplomacyButton.addActionListener(e -> {
            infoPanel.removeAll();
            infoPanel.add(countriesRelationsComboBox);
            infoPanel.add(diplomacyStats);
            infoPanel.add(declareWarButton);
            diplomacyStats.setText(currentRelation.toStringForStats());
            gamePanel.updateUI();
        });
        militaryButton.addActionListener(e -> {
            infoPanel.removeAll();
            infoPanel.add(militaryStats);
            infoPanel.add(trainDivisionButton);
            infoPanel.add(trainingButton);
            infoPanel.add(divisionsButton);
            militaryStats.setFont(new Font(null, Font.PLAIN, 11));
            militaryStats.setText(yourCountry.getMilitary().toString());
            gamePanel.updateUI();
        });
        prioritiesButton.addActionListener(e -> {
            infoPanel.removeAll();
            prioritiesPanel.removeAll();
            prioritiesPanel.setLayout(new GridLayout(yourCountry.getPriorities().size(), 1));
            for (int i = 0; i < yourCountry.getPriorities().size(); i++) {
                final int index = i;
                JButton button = new JButton();
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setIcon(new ImageIcon(yourCountry.getPriorities().get(index).getImagePath()));
                button.addActionListener(event -> {
                    JOptionPane.showConfirmDialog(
                            gameFrame,
                            yourCountry.getPriorities().get(index).getDescription(),
                            yourCountry.getPriorities().get(index).getTitle(),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE,
                            new ImageIcon(yourCountry.getPriorities().get(index).getImagePath()));
                });
                prioritiesPanel.add(button);
            }
            infoPanel.add(prioritiesPanel);
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

    private void averagePopulationMethod() {

        if (mapRegime != Regime.AVERAGE_POPULATION) {
            mapRegime = Regime.AVERAGE_POPULATION;
        } else {
            mapRegime = Regime.STANDARD;
        }
        refresh();
    }

    private void averageFactoriesMethod() {
        if (mapRegime != Regime.AVERAGE_FACTORIES) {
            mapRegime = Regime.AVERAGE_FACTORIES;
        } else {
            mapRegime = Regime.STANDARD;
        }
        refresh();
    }

    private void buildFactoryMethod() {
        if (mapRegime != Regime.BUILDING) {
            mapRegime = Regime.BUILDING;
        } else {
            mapRegime = Regime.STANDARD;
        }
        refresh();
    }

    private void trainDivisionMethod() {
        if (mapRegime != Regime.TRAINING) {
            mapRegime = Regime.TRAINING;
        } else {
            mapRegime = Regime.STANDARD;
        }
        refresh();

    }

    private void moveDivisionMethod() {
        if (mapRegime != Regime.MOVING) {
            mapRegime = Regime.MOVING;
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

    public void refresh() {
        output.setIcon(new ImageIcon(getImage()));
    }

    private void updateGamePanel() {
        regionStats.setText(selectedRegion.toString());
        countryStats.setText(yourCountry.toStringForStats());
        economyStats.setText(yourCountry.getEconomy().toString());
        queueStats.setText(yourCountry.getEconomy().getQueue());
        currentRelation = (Country.Diplomacy.Relation) countriesRelationsComboBox.getSelectedItem();
        diplomacyStats.setText(currentRelation.toStringForStats());
        militaryStats.setText(yourCountry.getMilitary().toString());
        trainingStats.setText(yourCountry.getMilitary().getTraining());
        infoPanel.validate();
        gamePanel.updateUI();
    }

    private BufferedImage getImage() {
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = bi.createGraphics();
        g.drawImage(image, 0, 0, output);
        g.setColor(Color.ORANGE.darker());
        g.fill(area);

        if (mapRegime == Regime.AVERAGE_POPULATION) {
            averagePopulationButton.setText("Відмінити");
        } else {
            averagePopulationButton.setText("Середнє населення по Україні");
        }
        if (mapRegime == Regime.AVERAGE_FACTORIES) {
            averageFactoriesButton.setText("Відмінити");
        } else {
            averageFactoriesButton.setText("Середня кількість заводів по Україні");
        }
        if (mapRegime == Regime.BUILDING) {
            buildFactoryButton.setText("Відмінити");
        } else {
            buildFactoryButton.setText("Побудувати фабрику");
        }
        if (mapRegime == Regime.TRAINING) {
            trainDivisionButton.setText("Відмінити");
        } else {
            trainDivisionButton.setText("Підготувати дивізію");
        }

        if (mapRegime == Regime.BUILDING) {
            for (Country country : Country.countries) {
                if (country.getName().equals(yourCountry.getName())) {
                    for (Region region : country.getRegions()) {
                        g.setColor(Color.GREEN);
                        g.fill(allRegions.get(region.getID()));
                    }
                }
            }
        } else if (mapRegime == Regime.TRAINING) {
            for (Country country : Country.countries) {
                if (country.getName().equals(yourCountry.getName())) {
                    for (Region region : country.getRegions()) {
                        g.setColor(Color.GREEN);
                        g.fill(allRegions.get(region.getID()));
                    }
                }
            }
        } else if (mapRegime == Regime.MOVING) {
            for (Country country : Country.countries) {
                if (country.getName().equals(yourCountry.getName())) {
                    for (Region region : country.getRegions()) {
                        g.setColor(Color.GREEN);
                        if (region.getID() == selectedDivision.getLocation().getID()) {
                            g.setColor(Color.ORANGE);
                        }
                        g.fill(allRegions.get(region.getID()));
                    }
                    for (Country.Diplomacy.Relation relation : country.getDiplomacy().relations) {
                        if (relation.areInWar()) {
                            for (Region enemyRegion : relation.getCountry().getRegions()) {
                                g.setColor(Color.RED);
                                if (enemyRegion.getID() == selectedDivision.getLocation().getID()) {
                                    g.setColor(Color.ORANGE);
                                }
                                g.fill(allRegions.get(enemyRegion.getID()));
                            }
                        }
                    }
                }
            }
        } else if (mapRegime == Regime.AVERAGE_POPULATION) {
            for (Region region : Region.regions) {
                int green = 255;
                int red = 255;
                float modifier = (float) region.getPopulation() / (float) Region.getAveragePopulation();
                if (modifier >= 1) {
                    red = (int) (red / modifier);
                } else {
                    green = (int) (green * modifier);
                }
                Color color = new ColorUIResource(red, green, 0);
                g.setColor(color);
                g.fill(allRegions.get(region.getID()));
            }
        } else if (mapRegime == Regime.AVERAGE_FACTORIES) {
            for (Region region : Region.regions) {
                int green = 255;
                int red = 255;
                float modifier = (float) region.getFactories() / (float) Region.getAverageFactories();
                if (modifier >= 1) {
                    red = (int) (red / modifier);
                } else {
                    green = (int) (green * modifier);
                }
                Color color = new ColorUIResource(red, green, 0);
                g.setColor(color);
                g.fill(allRegions.get(region.getID()));
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
            UIManager.put("OptionPane.background", new ColorUIResource(Color.BLACK));
            UIManager.put("OptionPane.messageForeground", new ColorUIResource(Color.CYAN));
            UIManager.put("Panel.background", new ColorUIResource(Color.BLACK));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Main o = new Main();

        try {
            description = Files.readString(Path.of("files/text_files/text_description.txt"));
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
            mainMenuFrame.pack();
        });

        yourCountry = (Country) countrySelector.getSelectedItem();
        flagPic.setIcon(new ImageIcon(yourCountry.getFlagPath()));
        portraitPic.setIcon(new ImageIcon(yourCountry.getPortraitPath()));
        aboutCountryLabel.setText(yourCountry.toStringForStats());

        JButton buttonPlay = new JButton("Почати гру");
        buttonPlay.addActionListener(e -> {
            selectedRegion = yourCountry.getRegions().get(0);
            regionStats.setText(selectedRegion.toString());
            countryStats.setText(yourCountry.toStringForStats());

            flagLabel.setIcon(new ImageIcon((yourCountry.getFlagPath().toString())));
            portraitLabel.setIcon(new ImageIcon((yourCountry.getPortraitPath().toString())));

            // TODO: Change
            countriesRelationsComboBox.setModel(
                    new DefaultComboBoxModel<Country.Diplomacy.Relation>(
                            yourCountry.getDiplomacy().getRelationsArray()));
            currentRelation = (Country.Diplomacy.Relation) countriesRelationsComboBox.getSelectedItem();
            diplomacyStats = new JTextArea(currentRelation.toStringForStats());
            diplomacyStats.setEditable(false);
            diplomacyStats.setLineWrap(true);
            diplomacyStats.setWrapStyleWord(true);
            diplomacyStats.setForeground(Color.CYAN);
            diplomacyStats.setBackground(Color.BLACK);

            gameFrame.setVisible(true);
            mainMenuFrame.dispose();
        });

        JButton buttonCountryDescription = new JButton("Опис країни");
        buttonCountryDescription.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainMenuFrame,
                    yourCountry.getDescription(), "Опис країни", JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(yourCountry.getFlagPath()));
        });

        JButton buttonDescription = new JButton("Опис гри");
        buttonDescription.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainMenuFrame,
                    description, "Опис гри", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("files/icon.png"));
        });

        JButton buttonExit = new JButton("Вийти");
        buttonExit.addActionListener(e -> System.exit(0));

        startMenuPanel.add(gameSignLabel);
        startMenuPanel.add(countrySelector);
        startMenuPanel.add(aboutCountryLabel);
        startMenuPanel.add(aboutCountryPanel);
        startMenuPanel.add(buttonPlay);
        startMenuPanel.add(buttonDescription);
        startMenuPanel.add(buttonCountryDescription);
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

        Image icon = new ImageIcon("files/icon.png")
                .getImage();
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setIconImage(icon);
        gameFrame.pack();

    }

    private static Country[] countriesToArray() {
        Country[] countriesArray = new Country[Country.countries.size()];
        for (int i = 0; i < countriesArray.length; i++) {
            countriesArray[i] = Country.countries.get(i);
        }
        return countriesArray;
    }
}

abstract class Operation {
    protected static final String name = "";

    @Override
    public String toString() {
        String string = "Операція: ";
        return string;
    }

    public String getName() {
        return Operation.name;
    }
}

class OperationStaying extends Operation {
    public static final String name = "Залишатися на місці";
    private Region location;

    OperationStaying(Region location) {
        this.location = location;
    }

    @Override
    public String toString() {
        String string = super.toString() + name + "\n";
        string += "у регіоні: " + location.getName() + "\n";
        return string;
    }

    @Override
    public String getName() {
        return OperationStaying.name;
    }
}

class OperationMovement extends Operation {
    public static final String name = "Переміщення";
    private Region from;
    private Region to;
    private int daysOfMovement;

    OperationMovement(Region from, Region to) {
        this.from = from;
        this.to = to;
        int fromX = 0;
        int fromY = 0;
        int toX = 0;
        int toY = 0;
        for (HashMap.Entry<Integer, Area> entrySet : Main.allRegions.entrySet()) {
            if (from.getID() == entrySet.getKey()) {
                fromX = (int) entrySet.getValue().getBounds().getCenterX();
                fromY = (int) entrySet.getValue().getBounds().getCenterY();
            }
            if (to.getID() == entrySet.getKey()) {
                toX = (int) entrySet.getValue().getBounds().getCenterX();
                toY = (int) entrySet.getValue().getBounds().getCenterY();
            }
            daysOfMovement = (int) ((Math.abs(fromX - toX) + Math.abs(fromY - toY)) / 8);
        }
    }

    public Region getFrom() {
        return from;
    }

    public Region getTo() {
        return to;
    }

    public int getDaysOfMovement() {
        return daysOfMovement;
    }

    public void setDaysOfMovement(int daysOfMovement) {
        this.daysOfMovement = daysOfMovement;
    }

    @Override
    public String toString() {
        String string = super.toString() + name + "\n";
        string += "з регіону " + from.getName() + "\n";
        string += "до регіону " + to.getName() + "\n";
        string += "залишилося " + daysOfMovement + " днів";
        return string;
    }

    @Override
    public String getName() {
        return OperationMovement.name;
    }
}

class OperationBattle extends Operation {
    public static final String name = "Битва";
    ArrayList<Country.Military.Division> divisionsAgainst = new ArrayList<>();
    float battleProgress;

    OperationBattle() {

    }

    @Override
    public String toString() {
        return super.toString() + name;
    }

    @Override
    public String getName() {
        return OperationBattle.name;
    }
}

class OperationOccupation extends Operation {
    public static String name = "Окупація";
    Region occupiedRegion;
    float occupationProgress;

    OperationOccupation(Region occupiedRegion) {
        this.occupiedRegion = occupiedRegion;
        this.occupationProgress = 0f;
    }

    @Override
    public String toString() {
        String string = super.toString() + name + "\n";
        string += "у регіоні " + occupiedRegion.getName() + " з прогресом в "
                + String.format("%.1f", occupationProgress) + "%";
        return string;
    }

    @Override
    public String getName() {
        return OperationOccupation.name;
    }

    public Region getOccupiedRegion() {
        return occupiedRegion;
    }

    public float getOccupationProgress() {
        return occupationProgress;
    }

    public void setOccupationProgress(float occupationProgress) {
        this.occupationProgress = occupationProgress;
    }
}
