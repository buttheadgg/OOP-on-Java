import java.sql.SQLException;

public class main {


    public static void main(String[] args)
    {
        try {
            Base dataBase = new Base();
            new UserInterface(dataBase);
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
