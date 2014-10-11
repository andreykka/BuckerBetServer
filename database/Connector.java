package database;

 import pojo.OutputData;
 import pojo.RegistrationData;

 import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandy on 24.09.14.
 */
public class Connector {

    // may change to volatile or synchronize if produse error
    private static Connector instance;

    public static final String  DRIVER_NAME = "org.postgresql.Driver";
    public static final String  URL         = "jdbc:postgresql://localhost:5432/BuckerBet";
    public static final String  USER        = "postgres";
    public static final String  PASSWORD    = "postgres";

    private Connection  db;

    public static Connector getInstance(){

        if (instance == null) {
            // initializing field by ConnectionInstanceHolder
            new ConnectionInstanceHolder();
            // now instance is initialized

        }
        return instance;
    }

    private Connector() {

        long timeout = System.currentTimeMillis();
        try {
            Class.forName(DRIVER_NAME);

            this.db = DriverManager.getConnection(URL, USER,PASSWORD);

        } catch (ClassNotFoundException cnfex ){
            cnfex.printStackTrace();
        } catch (SQLException sex){
            sex.printStackTrace();
        }
        timeout = System.currentTimeMillis() - timeout;
        System.out.println("user:" + USER + " connected to db.. " + "timeout: " + timeout + "ms");

    }


    public Boolean checkLoginOnExists(String login){
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = "SELECT * FROM customers WHERE (login = '" + login + "')";
        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            // if this item contains in DB then return true else false
            return rs.next() ;

        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public Boolean checkEMailOnExists(String email){

        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql2 = "SELECT * FROM customers WHERE (e_mail = '" + email + "')";

        try {
            ps = this.db.prepareStatement(sql2);
            rs = ps.executeQuery();
            // if this item contains in DB then return true else false
            return rs.next();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }


    public Boolean registerCustomer(RegistrationData regData) throws Error{

        Boolean result = false;

        PreparedStatement ps = null;
        String sql = "INSERT  INTO customers VALUES (DEFAULT , " +
                "'" + regData.getSurname()  + "', " +
                "'" + regData.getName()     + "', " +
                "'" + regData.getPassword() + "', " +
                "'" + regData.getEMail()    + "', " +
                "'" + regData.getLogin()    + "');";
        try {
            ps = this.db.prepareStatement(sql);
            ps.executeUpdate();
            result = true;
        } catch (SQLException e){
            e.printStackTrace();
        }

        return result;

    }

    public boolean getIsLogin(String login, String pass){

        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = "SELECT * FROM customers WHERE (login = '" +login+ "') AND (pass = '" + pass + "')";

        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            System.out.println();

            // if this item contains in DB then return true else false
            return rs.next();

        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public List<OutputData> getData(){
        OutputData data;
        ArrayList<OutputData> arr = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = "SELECT * FROM matches ORDER BY date, time";

        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            System.out.println();

            while (rs.next()){
                data = new OutputData();
                data.setId(rs.getInt("match_id"));
                data.setEvent(rs.getString("event"));
                data.setDate(rs.getDate("date").toLocalDate());
                data.setTime(rs.getTime("time"));
                data.setResult(rs.getString("result"));
                arr.add(data);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return  arr;
    }

    public void addItem(OutputData data){
        PreparedStatement ps = null;
        String sql = "INSERT  INTO matches VALUES (DEFAULT , " +
                                            "'" + data.getEvent() + "', " +
                                            "'" + data.getDate()  + "', " +
                                            "'" +  data.getTime() + "', " +
                                            "'" + data.getResult()+ "');";
        try {
            ps = this.db.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateItem(OutputData data){
        PreparedStatement ps = null;
        String sql = "UPDATE matches SET event = '" + data.getEvent() + "', " +
                                        "date = '" + data.getDate() +  "', " +
                                        "time = '" + data.getTime() +  "',  " +
                                        "result = '" + data.getResult() + "' " +
                    "WHERE match_id = " + data.getId() ;
        try {
            ps = this.db.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void removeItem(OutputData data){
        PreparedStatement ps = null;
        String sql = "DELETE FROM matches WHERE match_id = " + data.getId();
        try {
            ps = this.db.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public OutputData getItem(int editableId) {

        OutputData outputData = null;

        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT * FROM matches WHERE match_id = " + editableId + " LIMIT 1";
        try {


            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                outputData = new OutputData();
                outputData.setId    (rs.getInt("match_id"));
                outputData.setEvent (rs.getString("event"));
                outputData.setDate  (rs.getDate("date").toLocalDate());
                outputData.setTime  (rs.getTime("time"));
                outputData.setResult(rs.getString("result"));
            }

        } catch (SQLException e ){
            e.printStackTrace();
        }

        return outputData;
    }

    private static class ConnectionInstanceHolder{

        private ConnectionInstanceHolder(){
            // initialize field owner class
            instance = new Connector();
        }

    }

}
