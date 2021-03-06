package database;

import config.Config;
import org.apache.log4j.Logger;
import pojo.LogInData;
import pojo.OutputData;
import pojo.RegistrationData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gandy on 24.09.14.
 *
 */
public class Connector {

    // may change to volatile or synchronize if produse error
    private static Connector instance;

    public static final String  DRIVER_NAME = "org.postgresql.Driver";
    public static final String  URL         = "jdbc:postgresql://localhost:5432/BuckerBet";
    public static final String  USER        = "postgres";
    public static final String  PASSWORD    = "postgres";

    private Connection  db;

    private static final Logger LOGGER = Logger.getLogger(Connector.class);

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

        } catch (ClassNotFoundException | SQLException cnfex ){
            LOGGER.error(cnfex);
            cnfex.printStackTrace();
        }
        timeout = System.currentTimeMillis() - timeout;
        LOGGER.info("user:" + USER + " connected to db.. " + "timeout: " + timeout + "ms");
        //System.out.println("user:" + USER + " connected to db.. " + "timeout: " + timeout + "ms");
    }

    //  don`t use this method never!!!! just trust me!!
   /* public void changeAllPassAndMacOnHash(){
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = "SELECT * FROM customers WHERE TRUE";
        String sqlInsert = "UPDATE customers SET pass = '%s', mac = '%s' WHERE (cust_id = '%d')";
        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("cust_id");
                String passHash = Config.getPasswordHash(rs.getString("pass"));
                String macHash = Config.getMacHash(rs.getString("mac"));
                ps = this.db.prepareStatement(String.format(sqlInsert, passHash, macHash, id));
                ps.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
*/
    /*public List<LogInData> getOnlineClients() {
        LogInData data;
        ArrayList<LogInData> arr = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = "SELECT * FROM customers WHERE (is_online = TRUE)";
        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            //System.out.println();

            while (rs.next()){
                data = new LogInData();
                data.setLogin(rs.getString("login"));
                data.setPass(rs.getString("pass"));
                arr.add(data);
            }
        } catch (SQLException e){
            Log.write(e);
            //e.printStackTrace();
        }
        return  arr;
    }
*/
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
            LOGGER.error(e);
            //e.printStackTrace();
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
            LOGGER.error(e);
            //e.printStackTrace();
        }
        return false;
    }


    public Boolean registerCustomer(RegistrationData regData) throws Error{

        Boolean result = false;

        PreparedStatement ps = null;
        String sql = "INSERT  INTO customers VALUES (DEFAULT , " +
                "'" + regData.getSurname()  + "', " +
                "'" + regData.getName()     + "', " +
                "'" + Config.getPasswordHash(regData.getPassword()) + "', " +
                "'" + regData.getEMail()    + "', " +
                "'" + regData.getLogin()    + "', " + " DEFAULT, null,  null, " +
                "'" + regData.getTel()      + "', " +
                "'" + Config.getMacHash(regData.getMac())      + "');";
        try {
            ps = this.db.prepareStatement(sql);
            ps.executeUpdate();
            result = true;
        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
        }
        return result;
    }

    /**
     * checkIsOnline check the customer are online
     * @param logInData login and password about customer
     * @return <code>true</code> if customer is online,
     * <code>false</code> if not are
     * */

    public boolean checkIsOnline(LogInData logInData){

        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = "SELECT * FROM customers WHERE (login = '" +logInData.getLogin() +
                                            "') AND (pass = '"  + Config.getPasswordHash(logInData.getPass()) +
                                            "') AND (is_online = TRUE)";
        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            //System.out.println();

            // if this item contains in DB then return true else false
            return rs.next();

        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
        }

        return false;
    }

    public boolean checkIsRegister(LogInData logInData){
        ResultSet rs = null;
        PreparedStatement ps = null;

        String sql = "SELECT * FROM customers WHERE (login = '" +logInData.getLogin() + "') " +
                                                "AND (pass = '" + Config.getPasswordHash(logInData.getPass()) +"')";
        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            //System.out.println();

            // if this item contains in DB then return true else false
            return rs.next();

        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
        }

        return false;
    }

    public boolean checkIsPC(LogInData user){

        String sql = "SELECT * FROM customers WHERE (login= '" + user.getLogin() +"')" +
                                            " AND  (pass= '" + Config.getPasswordHash(user.getPass()) + "')" +
                                            " AND (mac = '" + Config.getMacHash(user.getMac()) +"')";

            try {
                PreparedStatement ps = this.db.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                //System.out.println();

                // if this item contains in DB then return true else false
                return rs.next();

            } catch (SQLException e){
                LOGGER.error(e);
                //e.printStackTrace();
            }

            return false;
    }

    public boolean login(LogInData user){
        if (!checkIsRegister(user)){
            return false;
        }
        String sqlCheckDate = "SELECT * FROM customers WHERE (login= '" + user.getLogin() +"')" +
                                                    " AND  (pass= '" + Config.getPasswordHash(user.getPass()) + "')" +
                                                    " AND (date_over >= current_date) " +
                                                    " AND (mac = '" + Config.getMacHash(user.getMac()) +"')";

        String sql = "UPDATE customers SET is_online = TRUE WHERE (login= '" + user.getLogin()
                                                        + "') AND  (pass= '" + Config.getPasswordHash(user.getPass()) + "') ";
        try {
            PreparedStatement ps = this.db.prepareStatement(sqlCheckDate);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                ps = this.db.prepareStatement(sql);
                ps.executeUpdate();
                return true;

            } else {
                return false;
            }

        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
        }
        return false;
    }

    public boolean logout(LogInData user){
        if (!checkIsRegister(user)){
            return false;
        }
        String sql = "UPDATE customers SET is_online = FALSE WHERE (login= '" + user.getLogin()
                                                        + "') AND  (pass= '" + Config.getPasswordHash(user.getPass()) + "') ";
        try {
            PreparedStatement ps = this.db.prepareStatement(sql);
            ps.executeUpdate();
            return true;
        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
        }
        return false;
    }


    public List<OutputData> getDataToCLients(){
        OutputData data;
        ArrayList<OutputData> arr = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        // select the data for last week;
        String sql = "SELECT * FROM matches where date > (current_date - interval '7 day') ORDER BY date, time";

        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            //System.out.println();

            while (rs.next()){
                data = new OutputData();
                data.setId(rs.getInt("match_id"));
                data.setEvent(rs.getString("event"));
                data.setDate(rs.getDate("date").toLocalDate());
                data.setTime(rs.getString("time"));
                data.setResult(rs.getString("result"));
                arr.add(data);
            }

        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
        }
        return  arr;
    }

    public List<OutputData> getData(){
        OutputData data;
        ArrayList<OutputData> arr = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;

        // select the data for last week;
        String sql = "SELECT * FROM matches ORDER BY date, time";

        try {
            ps = this.db.prepareStatement(sql);
            rs = ps.executeQuery();
            //System.out.println();

            while (rs.next()){
                data = new OutputData();
                data.setId(rs.getInt("match_id"));
                data.setEvent(rs.getString("event"));
                data.setDate(rs.getDate("date").toLocalDate());
                data.setTime(rs.getString("time"));
                data.setResult(rs.getString("result"));
                arr.add(data);
            }

        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
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
            LOGGER.error(e);
            //e.printStackTrace();
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
            LOGGER.error(e);
            //e.printStackTrace();
        }
    }

    public void removeItem(OutputData data){
        PreparedStatement ps = null;
        String sql = "DELETE FROM matches WHERE match_id = " + data.getId();
        try {
            ps = this.db.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
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
                outputData.setTime  (rs.getString("time"));
                outputData.setResult(rs.getString("result"));
            }

        } catch (SQLException e ){
            LOGGER.error(e);
            //e.printStackTrace();
        }

        return outputData;
    }

    public void close(){
        String sql = "UPDATE customers SET is_online= FALSE WHERE is_online= TRUE";
        try {
            PreparedStatement ps = this.db.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e){
            LOGGER.error(e);
            //e.printStackTrace();
        }
    }


    private static class ConnectionInstanceHolder{
        private ConnectionInstanceHolder(){
            // initialize field owner class
            instance = new Connector();
        }

    }

}
