package server;

import config.TaskTimerScheduler;
import database.Connector;
import exceptions.TaskNotExecuteCorrectException;
import listenner.ICustomerListener;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import pojo.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
* Created by gandy on 27.09.14.
* */

public class CustomerService implements Runnable {

    private static List<ICustomerListener> listeners = new ArrayList<>();

    private static final Logger LOGGER = Logger.getLogger(CustomerService.class);

    private  volatile   Socket              socket;
    private  volatile   ObjectInputStream   in  = null;
    private  volatile   ObjectOutputStream  out = null;

    private  volatile   Socket              connectionSocket;
    private  volatile   ObjectInputStream   lin  = null;
    private  volatile   ObjectOutputStream  lout = null;


    private             Connector           connect = Connector.getInstance();
    private             Boolean             isListening;
    private             Customer            customer;
//    private             Thread              thread;
    
    public CustomerService(Socket socket, Socket connectionSocket){
        this.socket = socket;
        this.connectionSocket = connectionSocket;
        this.isListening = true;
        try {
            this.out    = new ObjectOutputStream(this.socket.getOutputStream());
            this.in     = new ObjectInputStream (this.socket.getInputStream());
            this.lout    = new ObjectOutputStream(this.connectionSocket.getOutputStream());
            this.lin     = new ObjectInputStream (this.connectionSocket.getInputStream());
        } catch (IOException e) {
            LOGGER.error("something wrong in create customer");
            return;
        }
    }

    @Override
    public void run() {
        startListening();
    }

    public void startListening(){

        Object object;
        FlagsEnum task = null;
        while (isListening) try {
            if (in == null ){
                this.logOut();
            }
            object = in.readObject();

            if (object instanceof FlagsEnum) {
                task = FlagsEnum.valueOf(((FlagsEnum) object).name());
                LOGGER.info("read " + task.name() + "  from client");
            } else {
                LOGGER.info("object is not a FlagsEnum object");
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
            this.logOut();
        }
    }

    public void logOut() {
        LOGGER.info("TRY LOG OUT ");
        this.isListening = false;

        if (customer != null){
            boolean f = connect.logout(customer);
            for (ICustomerListener l: listeners) {
                l.customerLogout(customer);
            }
            if (!f){
                LOGGER.info("not change status online!!!!");
            } else {
                LOGGER.info("user has status offline!!!!");
            }
        }

        try {
            if(in != null)
                this.in.close();
            if(out != null)
                this.out.close();
            if(socket != null)
                this.socket.close();

            if(lin != null)
                this.lin.close();
            if(lout != null)
                this.lout.close();
            if (connectionSocket != null)
                this.connectionSocket.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        LOGGER.info("LOG OUT FINISH. good bay customer");
    }

    private void logIn(){
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
            } else {
                out.writeBoolean(false);
                out.flush();
                throw new Error("Object is not a LoginData ERROR");
            }

            boolean isRegiter = connect.checkIsRegister(logInData);
            if (!isRegiter) {
                LOGGER.info("user '" + logInData.getLogin() + "' is not register in system");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Введен не правильный логин или пароль");
                out.flush();
                return;
            }

            boolean isOnline = connect.checkIsOnline(logInData);
            if (isOnline){
                LOGGER.info("user '" + logInData.getLogin() + "' is already ONLINE");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Пользователь " + logInData.getLogin() + " уже вошел в систему");
                out.flush();
                return;
            }

            boolean isPC = connect.checkIsPC(logInData);
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
                out.writeUTF("Невозможно войти в систему." +
                        "\r\nВы не оплатили лицензию, или срок действия истек!!");
                out.flush();
                return;
            }

            out.writeBoolean(true);
            out.flush();

            LOGGER.info("user " + logInData.getLogin() + " has status online");
            this.customer = connect.getCustomerByLoginData(logInData);

            for (ICustomerListener l: listeners) {
                l.customerLogin(customer);
            }

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(e);
        }
    }

    // send data to client
    public void sendData() {
        try {
            if (out == null){
                return;
            }
            out.flush();

            ArrayList<OutputData> arr = new ArrayList<>();
            arr.addAll(connect.getDataToClients());

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
            out.writeUTF(result.toJSONString());
            out.flush();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public void sendBroadcastData(){
        try /*(ObjectOutputStream lout = new ObjectOutputStream(connectionSocket.getOutputStream()))*/{
            if (!this.checkConnection())
                return;
            lout.writeObject(FlagsEnum.GET_DATA);
            lout.flush();

            ArrayList<OutputData> arr = new ArrayList<>();
            arr.addAll(connect.getDataToClients());

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
            lout.writeUTF(result.toJSONString());
            lout.flush();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void regUser() {
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
                //System.out.println("user with login '" + regData.getLogin() +
                // "' is already register in system");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Пользователь с таким Логином уже зарегистрирован в системе");
                out.flush();
                return;
            }

            Boolean isEmailExist = this.connect.checkEMailOnExists(regData.getEMail());
            if (isEmailExist) {
                //System.out.println("user with email '" + regData.getEMail() + "'
                // is already register in system");
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
        }
    }

    private Boolean getIsListening(){
        return  this.isListening &&
                this.in != null &&
                this.out != null &&
                !this.socket.isClosed();
    }

    // return true if connection are success
    public boolean checkConnection(){
        LOGGER.info("checking connection");

        try {
            if (! getIsListening())
                return false;
            LOGGER.info("write CHECK_CONNECTION");
            lout.writeObject(FlagsEnum.CHECK_CONNECTION);
            lout.flush();

            // якщо не можливо відправити чи отримати дані від клієнта
            // генеруємо помилку
            boolean executionResult = new TaskTimerScheduler(() -> {
                try {
                    Object obj = lin.readObject();
                    if (obj == null || !(obj instanceof Boolean))
                        throw new TaskNotExecuteCorrectException();
                } catch (IOException | ClassNotFoundException e) {
                    LOGGER.error(e);
                }
                LOGGER.info("read info success");
            }, 300).executeTask();

            return executionResult;

        } catch (IOException e) {
            LOGGER.error(e);
            return false;
        }
    }


    public static void addListener(ICustomerListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(ICustomerListener listener) {
        listeners.remove(listener);
    }

}
