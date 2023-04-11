package src;

import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class EventPane extends JOptionPane {

    private String newsPath = "files/news/pics/%d.png";
    private String eventPath = "files/countries/%s/events/pics/%d.png";

    private EventPane(String title, String text, String path) {

        ImageIcon icon = new ImageIcon(path);
        JOptionPane.showMessageDialog(
                null,
                text,
                title, JOptionPane.INFORMATION_MESSAGE,
                icon);
    }

    // public static void showNews() {
    // String title, text, path;
    // try (Scanner scanner = new Scanner(new File("files/news/text_news.txt"))) {
    // if (Main.calendar.equals(new GregorianCalendar(2028, Calendar.JULY, 7))) {
    // if (scanner.nextLine().equals(path))
    // new EventPane(title, text, path);
    // } else if (Main.calendar.equals(new GregorianCalendar(2028, Calendar.AUGUST,
    // 24))) {
    // new EventPane(title, text, path);
    // } else if (Main.calendar.equals(new GregorianCalendar(2028,
    // Calendar.NOVEMBER, 8))) {
    // new EventPane(title, text, path);
    // } else if (Main.calendar.equals(new GregorianCalendar(2029, Calendar.JANUARY,
    // 20))) {
    // new EventPane(title, text, path);
    // }
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // }

    // }

    public static void showEvent(String code, int i) {
        String path = "files/countries/" + code + "/events";
        ImageIcon icon = new ImageIcon(path + "/pics/" + i + ".png");

        Scanner scanner = new Scanner(path + "desc.txt");
        String title = "";
        String description = "";
        int id = 0;

        while (true) {
            if (scanner.nextLine().equals("<id>")) {
                id = Integer.parseInt(scanner.nextLine());
                if (id == i) {
                    break;
                }
            }
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

        JOptionPane.showMessageDialog(
                null,
                description,
                title, JOptionPane.INFORMATION_MESSAGE,
                icon);

        scanner.close();
    }
}
