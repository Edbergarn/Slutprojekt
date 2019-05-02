import java.sql.SQLException;

public class Controller {

    public static void main(String[] args) {

        try {
            Model model = new Model();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
