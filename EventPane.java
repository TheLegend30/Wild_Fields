import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class EventPane extends JOptionPane {
    private EventPane(String title, String text, String path) {

        ImageIcon icon = new ImageIcon(path);
        JOptionPane.showMessageDialog(
                null,
                text,
                title, JOptionPane.INFORMATION_MESSAGE,
                icon);
    }

    public static void showNews() {
        String title, text, path;
        if (Main.calendar.equals(new GregorianCalendar(2028, Calendar.AUGUST, 24))) {
            title = "Путін помер!";
            text = "Президент Російської Федерації Володимир Путін \nнарешті вмер не виходячи з коми вже декілька місяців. Уся Україна радіє!";
            path = "files/newsPics/0.png";
            new EventPane(title, text, path);
        } else if (Main.calendar.equals(new GregorianCalendar(2029, Calendar.JANUARY, 20))) {
            title = "Президентом США став Кокеш!";
            text = "На виборах 2028 у Америці вперше за декілька столітть переміг кандидат \nне від республіканської чи демократичної партії Адам Кокеш. Сьогодні відбулалася інавгурація президента-лібертаріанця";
            path = "files/newsPics/1.png";
            new EventPane(title, text, path);
        }
    }
}