package server;

import database.Connector;
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

public class CustomerListener {

    private  volatile   ObjectInputStream   in  = null;
    private  volatile   ObjectOutputStream  out = null;
    private  volatile   Socket              socket;
    private             Connector           connect = Connector.getInstance();
    private             Boolean             isListenning;



    public CustomerListener (Socket socket){

        this.socket = socket;
        this.isListenning = true;
        try {
            this.out    = new ObjectOutputStream(this.socket.getOutputStream());
            this.in     = new ObjectInputStream (this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("something wrong in create customer");
            return;
        }

        this.startListening();

    }

    private void startListening(){
        while (isListenning) {

            /*  System.out.println("____________________________________________________");
            System.out.println("Active: " + isActive());

            if (!isActive() || !isListenning) {
                System.out.println("client diskonnect from server... break while");
                break;
            }*/

            System.out.println("timer started");
            Object object;
            FlagsEnum task = null;
            try {
                // read request from client

                /*System.out.println("try to read from client  .... available: " + in.available());

                if (in  != null && in.available() <= 0) {
                    System.out.println("available < 0");
                    continue;
                }
                System.out.println("available: " + in.available());*/


                object = in.readObject();
                System.out.println("read some data from client");

                if (object instanceof FlagsEnum) {
                    task = FlagsEnum.valueOf(((FlagsEnum) object).name());
                } else {
                    System.out.println("object is not a FlagsEnum object");
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
                e.printStackTrace();
                this.logOut();
            }
        }
    }

    private void logOut() {
        System.out.println("LOG OUT...");
        this.isListenning = false;
        try {
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logIn(){

        System.out.println("CustomerListener in login function");
        Object obj;
        LogInData logInData;
        try {
            obj = in.readObject();
            if (obj instanceof LogInData){
                logInData = new LogInData((LogInData)obj);
                System.out.println("read login: " + logInData.getLogin() + " pass: " + logInData.getPass());
            } else {
                out.writeBoolean(false);
                out.flush();
                throw new Error("Object is not a LoginData ERROR");
            }

            Boolean isLogin = connect.getIsLogin(logInData.getLogin(), logInData.getPass());
            System.out.println("client is login? " + isLogin);
            out.writeBoolean(isLogin);
            out.flush();
            System.out.println("write is login to client");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // send data to client
    public void sendData() {
        System.out.println("CustomerListener in sendData function");
        try {
            out.flush();

            ArrayList<OutputData> arr = new ArrayList<>();
            arr.addAll(connect.getData());

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
            System.out.println("try to send to client");
            System.out.println(result.toJSONString());

            out.writeUTF(result.toJSONString());
            out.flush();
            System.out.println("send accepted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void regUser() {

        System.out.println("CustomerListener in registration function");
        Object obj;
        RegistrationData regData = new RegistrationData();
        try {
            obj = in.readObject();

            JSONObject object  = (JSONObject) obj;

            regData.setName(    ((String) object.get("name")).trim());
            regData.setSurname( ((String) object.get("surname")).trim());
            regData.setEMail(   ((String) object.get("email")).trim());
            regData.setLogin(   ((String) object.get("login")).trim());
            regData.setPassword(((String) object.get("password")).trim());

            Boolean isLoginExist = this.connect.checkLoginOnExists(regData.getLogin());

            if (isLoginExist) {
                System.out.println("user with login '" + regData.getLogin() + "' is already register in system");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Пользователь с таким Логином уже зарегистрирован в системе");
                out.flush();
                return;
            }
            Boolean isEmailExist = this.connect.checkEMailOnExists(regData.getEMail());

            if (isEmailExist) {
                System.out.println("user with email '" + regData.getEMail() + "' is already register in system");
                out.writeBoolean(false);
                out.flush();
                out.writeUTF("Пользователь с таким Email уже зарегистрирован в системе");
                out.flush();
                return;
            }

            Boolean result = this.connect.registerCustomer(regData);

            if (!result){
                System.out.println("USER NOT REGISTER!! SOME ERROR!!!!!!");
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
            e.printStackTrace();
        }

    }

    public Boolean getIsListenning(){
        return this.isListenning;
    }

    public void stopListen(){
        this.logOut();
    }

   /* public Boolean isActive(){
        return this.socket.isConnected() && !this.socket.isClosed();
    }*/

}
