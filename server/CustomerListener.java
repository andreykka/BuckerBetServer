package server;

import database.Connector;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import pojo.FlagsEnum;
import pojo.LogInData;
import pojo.OutputData;
import pojo.RegistrationData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
* Created by gandy on 27.09.14.
* */

public class CustomerListener extends Thread {

    private  volatile   ObjectInputStream   in  = null;
    private  volatile   ObjectOutputStream  out = null;
    private  volatile   Socket              socket;
    private             Connector           connect = Connector.getInstance();
    private             Boolean             isListenning;

    private static final Logger LOGGER = Logger.getLogger(CustomerListener.class);
    
    public CustomerListener (Socket socket){
        this.socket = socket;
        this.isListenning = true;
        try {
            this.out    = new ObjectOutputStream(this.socket.getOutputStream());
            this.in     = new ObjectInputStream (this.socket.getInputStream());

        } catch (IOException e) {
            //e.printStackTrace();
            //System.out.println("something wrong in create customer");
            LOGGER.error("something wrong in create customer");
            return;
        }

        start();
    }

    @Override
    public void run() {
        super.run();
        startListening();
    }

    public void startListening(){
        while (isListenning) {

            Object object;
            FlagsEnum task = null;
            try {

                if (in == null ){
                    this.logOut();
                }
                object = in.readObject();

                if (object instanceof FlagsEnum) {
                    task = FlagsEnum.valueOf(((FlagsEnum) object).name());
                    LOGGER.info("read " + task.name() + "  from client");
                    //System.out.println("read '" + task.name() + "'  from client");
                } else {
                    LOGGER.info("object is not a FlagsEnum object");
                    //System.out.println("object is not a FlagsEnum object");
                }
                assert task != null; // 100 task != null else ERROR
                // check request
                switch (task) {
                    case GET_DATA:  { sendData();   break;  }
                    case LOG_IN:    { logIn();      break;  }
                    case REG_USER:  { regUser();    break;  }
                    case LOG_OUT:   { logOut();     break;  }
                }

            } catch (IOException | ClassNotFoundException e) {
                LOGGER.error(e);
                ////e.printStackTrace();
                this.logOut();
            }
        }
    }

    public void logOut() {
        //System.out.println("TRY LOG OUT...");
        LOGGER.info("TRY LOG OUT ");
        this.isListenning = false;
        if (Customer.user != null){
            boolean f = connect.logout(Customer.user);
            System.out.println("login: " + Customer.user.getLogin());
            System.out.println("pass: " + Customer.user.getPass());
            if (!f){
                LOGGER.info("not change status online!!!!");
                //System.out.println("not change status online!!!!");
            } else {
                LOGGER.info("user has status offline!!!!");
                //System.out.println("user has status offline!!!!");
            }
        }

        //System.out.println("user is not login in system before");
        LOGGER.info("user is not login in system before");

        try {
           // Customer.user = null;
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (IOException e) {
            LOGGER.error(e);
            //e.printStackTrace();
        }
        //System.out.println("LOG OUT FINISH. good bay customer");
        LOGGER.info("LOG OUT FINISH. good bay customer");
    }

    private void logIn(){
        //System.out.println("CustomerListener in login function");
        LOGGER.info("CustomerListener in login function");
        Object obj;
        LogInData logInData;
        try {
            obj = in.readObject();
            if (obj instanceof LogInData){
                logInData = new LogInData((LogInData)obj);
                LOGGER.info("read login: "  + logInData.getLogin()
                                + " pass: " + logInData.getPass()
                                + " mac "   + logInData.getMac());
                //System.out.println("read login: " + logInData.getLogin() + " pass: " + logInData.getPass());
            } else {
                out.writeBoolean(false);
                out.flush();
                throw new Error("Object is not a LoginData ERROR");
            }

            boolean isRegiter = connect.checkIsRegister(logInData);
            if (!isRegiter) {
                //System.out.println("user '" + logInData.getLogin() + "' is not register in system");
                LOGGER.info("user '" + logInData.getLogin() + "' is not register in system");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Введен не правильный логин или пароль");
                out.flush();
                return;
            }

            boolean isOnline = connect.checkIsOnline(logInData);
            if (isOnline){
                //System.out.println("user '" + logInData.getLogin() + "' is already ONLINE");
                LOGGER.info("user '" + logInData.getLogin() + "' is already ONLINE");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Пользователь " + logInData.getLogin() + " уже вошел в систему");
                out.flush();
                return;
            }

            boolean isPC = connect.checkIsPC(logInData);
            System.out.println(isPC);
            if (!isPC) {
                LOGGER.info("mac '" + logInData.getMac() + "' is not " + logInData.getMac() + " PC");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Логин " + logInData.getLogin() + " привязан к другому комп’ютеру");
                out.flush();
                return;
            }

            boolean isLogin = connect.login(logInData);
            if (!isLogin){
                LOGGER.info("user '" + logInData.getLogin() + "' is not login");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Невозможно войти в систему.\r\nВы не оплатили лицензию, или срок действия истек!!");
                out.flush();
                return;
            }

            out.writeBoolean(true);
            out.flush();

            LOGGER.info("user " + logInData.getLogin() + " has status online");
            //throw new Error("somthimg wrong is login Function becouse login = false");
            Customer.user = new LogInData(logInData);

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(e);
            //e.printStackTrace();
        }
    }

    // send data to client
    public void sendData() {
        //System.out.println("CustomerListener in sendData function");
        try {
            if (out == null){
                return;
            }
            out.flush();

            ArrayList<OutputData> arr = new ArrayList<>();
            arr.addAll(connect.getDataToCLients());

            JSONObject result = new JSONObject();
            int i=0;
            for(OutputData data: arr){
                JSONObject object = new JSONObject();

                object.put("event",     data.getEvent());
                object.put("date",      data.getDate().toString());
                object.put("time",      data.getTime().toString());
                object.put("result",    data.getResult());

                result.put(i,object);
                ++i;
            }
            //System.out.println("try to send to client");
            //System.out.println(result.toJSONString());

            out.writeUTF(result.toJSONString());
            out.flush();
            //System.out.println("send accepted");
        } catch (IOException e) {
            LOGGER.error(e);
            //e.printStackTrace();
        }
    }

    private void regUser() {

        //System.out.println("CustomerListener in registration function");
        Object obj;
        RegistrationData regData = new RegistrationData();
        try {
            obj = in.readObject();
            JSONObject object  = (JSONObject) obj;

            regData.setName(    ((String) object.get("name")).trim());
            regData.setSurname(((String) object.get("surname")).trim());
            regData.setEMail(((String) object.get("email")).trim());
            regData.setTel(((String) object.get("tel")).trim());
            regData.setLogin(((String) object.get("login")).trim());
            regData.setPassword(((String) object.get("password")).trim());
            regData.setMac(((String) object.get("mac")).trim());

            Boolean isLoginExist = this.connect.checkLoginOnExists(regData.getLogin());
            if (isLoginExist) {
                //System.out.println("user with login '" + regData.getLogin() + "' is already register in system");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Пользователь с таким Логином уже зарегистрирован в системе");
                out.flush();
                return;
            }

            Boolean isEmailExist = this.connect.checkEMailOnExists(regData.getEMail());
            if (isEmailExist) {
                //System.out.println("user with email '" + regData.getEMail() + "' is already register in system");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Пользователь с таким Email уже зарегистрирован в системе");
                out.flush();
                return;
            }

            Boolean result = this.connect.registerCustomer(regData);
            if (!result){
                //System.out.println("USER NOT REGISTER!! SOME ERROR!!!!!!");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Попытка зарегистрироваться не удалась (((");
                out.flush();
                return;
            }

            out.writeBoolean(true);
            out.flush();
            out.writeUTF("Поздравляем! Вы успешно загеристрированы");
            out.flush();

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(e);
            //e.printStackTrace();
        }
    }

    public Boolean getIsListenning(){

        return  this.isListenning &&
                this.in != null &&
                !this.socket.isClosed()  &&
                this.out != null &&
                !socket.isInputShutdown() &&
                !socket.isOutputShutdown() &&
                socket.isConnected();


    }

}
