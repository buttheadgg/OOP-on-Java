import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import static java.lang.String.format;

public class Base {

    private Connection connection = null;
    private static final Logger log = Logger.getLogger(String.valueOf(Base.class));
    private String host;
    private String username;
    private String password;

    public Base() throws SQLException {
        getIniData();
        createConnection();
    }

    private void getIniData(){
        Properties prop = new Properties();
        try {
            File file = new File("src/mydb.cfg");
            prop.load(new java.io.FileInputStream(file));
            host = prop.getProperty("host");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
        } catch (IOException e) {
            log.info("Unable to find mydb.cfg in " + System.getProperty("user.home"));
            host = "Unknown HOST";
            username = "Unknown USER";
            password = "Unknown PASSWORD";
        }
    }

    private void createConnection() throws SQLException {
        connection = DriverManager.getConnection(host, username, password);
        log.info("CONNECTION: " + connection);
    }

    private ResultSet runSqlQuery(String query) {
        ResultSet result = null;
        try {
            Statement statement = connection.createStatement();
            log.info("SQL query: " + query);
            result = statement.executeQuery(query);
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            log.info("Error in SQL query: " + query);
        }

        return result;
    }

    public void addCinema(String cinema ,String capacity,String date,String sales)
    {
        String q = "INSERT INTO cinema(cinema, capacity,date,sales)" +
                "VALUES ('%s', '%s', '%s', '%s')";
        q = format(q, cinema, capacity, date, sales);
        runSqlQuery(q);
        log.info(q);
    }

    public void addFilm(String film ,String director,String year,String genre,String price)
    {
        String q = "INSERT INTO film(film, director,year,genre,price) " +
                "VALUES ('%s', '%s', '%s', '%s', '%s')";
        q = format(q,film , director, year, genre,price);
        runSqlQuery(q);
        log.info(q);
    }

    public void addSeance(int idS, int idF)
    {
        String q = "INSERT INTO seance(film_id, cinema_id)" +
                "VALUES ('%d', '%d')";
        q = format(q,idS,idF);
        runSqlQuery(q);
        log.info(q);
    }

    public void deleteCinema(int id)
    {
        String q = ("DELETE FROM cinema WHERE id=%d");
        q = format(q,id);
        ResultSet result = runSqlQuery(q);
        runSqlQuery(q);
    }

    public void deleteFilm(int id)
    {
        String q = ("DELETE FROM film WHERE id=%d");
        q = format(q,id);
        ResultSet result = runSqlQuery(q);
        runSqlQuery(q);
    }

    public void deleteSeance(int id)
    {
        String q = ("DELETE FROM seance WHERE id=%d");
        q = format(q,id);
        ResultSet result = runSqlQuery(q);
        runSqlQuery(q);
    }

    public String[] getCinema(int id){
        String[] array = new String[5];
        String sql = "SELECT cinema.id, cinema.cinema, cinema.capacity, cinema.date, cinema.sales FROM cinema WHERE id='%d'";
        sql = String.format(sql, id);
        ResultSet result = runSqlQuery(sql);
        try {
            result.next();
            array[0] = result.getString("id");
            array[1] = result.getString("cinema");
            array[2] = result.getString("capacity");
            array[3] = result.getString("date");
            array[4] = result.getString("sales");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }

    public void editCinemaTableRow(int id, String cinema ,String capacity,String date,String sales){
        log.info("Editing row to table");
        if (id!=0) {
            String sql = "UPDATE cinema SET cinema.cinema='%s',cinema.capacity='%s',cinema.date='%s',cinema.sales='%s' WHERE cinema.id='%d'";
            sql = String.format(sql, cinema, capacity, date, sales, id);
            runSqlQuery(sql);
        }
    }

    public String[] getFilm(int id){
        String[] array = new String[6];
        String sql = "SELECT film.id, film.film, film.director, film.year, film.genre, film.price FROM film WHERE id='%d'";
        sql = String.format(sql, id);
        ResultSet result = runSqlQuery(sql);
        try {
            result.next();
            array[0] = result.getString("id");
            array[1] = result.getString("film");
            array[2] = result.getString("director");
            array[3] = result.getString("year");
            array[4] = result.getString("genre");
            array[5] = result.getString("price");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return array;
    }

    public void editFilmTableRow(int id, String film ,String director,String year,String genre,String price){
        log.info("Editing row to table");
        if (id!=0) {
            String sql = "UPDATE film SET film.film='%s',film.director='%s',film.year='%s',film.genre='%s',film.price='%s' WHERE film.id='%d'";
            sql = String.format(sql, film, director, year, genre, price,id);
            runSqlQuery(sql);
        }
    }

    public  ArrayList<Object[]> getCinemaTable(){
        String sql = "SELECT cinema.id, cinema.cinema, cinema.capacity, cinema.date, cinema.sales FROM cinema";
        ResultSet result = runSqlQuery(sql);
        try {
            ArrayList<Object[]> list = new ArrayList<>();
            while (result.next()) {
                String id = String.valueOf(result.getInt("id"));
                String cinema = result.getString("cinema");
                String capacity = String.valueOf(result.getInt("capacity"));
                String date = result.getString("date");
                String sales = String.valueOf(result.getInt("sales"));
                list.add(new Object[]{id,cinema, capacity, date, sales});

            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public  ArrayList<Object[]> getFilmTable(){
        String sql = "SELECT film.id, film.film, film.director, film.year, film.genre, film.price FROM film";
        ResultSet result = runSqlQuery(sql);
        try {
            ArrayList<Object[]> list = new ArrayList<>();
            while (result.next()) {
                String id = String.valueOf(result.getInt("id"));
                String film = result.getString("film");
                String director = result.getString("director");
                String year = String.valueOf(result.getInt("year"));
                String genre = result.getString("genre");
                String price = String.valueOf(result.getInt("price"));
                list.add(new Object[]{id,film, director, year, genre, price});

            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public  ArrayList<Object[]> getSeanceTable(int id){
        String sql = "SELECT seance.id, cinema.cinema, cinema.date, film.film, film.price FROM seance INNER JOIN film ON seance.film_id=film.id INNER JOIN cinema ON seance.cinema_id = cinema.id WHERE seance.cinema_id='%d'";
        sql = String.format(sql, id);
        ResultSet result = runSqlQuery(sql);
        try {
            ArrayList<Object[]> list = new ArrayList<>();
            while (result.next()) {
                String idS = String.valueOf(result.getInt("id"));
                String cinema = result.getString("cinema");
                String date = result.getString("date");
                String film = result.getString("film");
                String price = String.valueOf(result.getInt("price"));
                list.add(new Object[]{idS,cinema, date, film,  price});

            }
            return list;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

}
