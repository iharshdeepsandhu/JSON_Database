package server;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;


import javax.swing.tree.VariableHeightLayoutCache;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Main   {
    public static class Response1{
        public String response;
        public Object value;

        public Response1(String response, Object value) {
            this.response = response;
            this.value = value;
        }

        public Response1(String response) {
            this.response = response;
        }
    }
    public static class Response2{
        public String response;
        public String reason;

        public Response2(String response, String reason) {
            this.response = response;
            this.reason = reason;
        }
    }



    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        File file = new File("C:\\Users\\Admin\\IdeaProjects\\JSON Database\\JSON Database\\task\\src\\server\\data\\db.json");



        String address = "127.0.0.1";
        int port = 23456;
        try(ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(address)))
        {
            System.out.println(" Server started!");
            while(true)
            {
                try(Socket socket = server.accept();
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    DataOutputStream output  = new DataOutputStream(socket.getOutputStream()))


                {
                    executor.submit(() -> {});
                    Map<String , Object> dataBase;
                    Map<String, Object> dt = new HashMap<>();
                    ArrayList<String> placePlace;
                    Gson gson = new Gson();


                    String recieved = input.readUTF();


                    Map<String ,Object> cmd = gson.fromJson(recieved, LinkedHashMap.class);
                    String msg;



                    String  type = (String) cmd.get("type");
                    if (type.equals("exit")){
                        Response1 ok = new Response1("OK");
                        msg = gson.toJson(ok);
                        output.writeUTF(msg);
                        break;
                    }

                    Object place = cmd.get("key");
                    String cName = String.valueOf(place.getClass());


                    Object val =  cmd.get("value");


                    ReadWriteLock lock = new ReentrantReadWriteLock();
                    Lock readLock = lock.readLock();
                    Lock writeLock = lock.writeLock();






                    if(type.equals("set")){
                        writeLock.lock();

                        FileReader reader = new FileReader(file);
                        dataBase = gson.fromJson(reader, HashMap.class);


                        if (cName.equals("class java.lang.String")){
                            //placePlace.add((String) place);
                           if (dataBase != null){
                               dt.putAll(dataBase);
                           }
                           dt.put((String) place, val);

                        }
                        else if (cName.equals("class java.util.ArrayList")) {
                            placePlace = (ArrayList<String>) place;
                            Map<String, Object> map = null;
                            //Map<String, Object>map2 = new HashMap<>();


                            if (dataBase != null)
                                dt.putAll(dataBase);
                            System.out.println(dt);
                            System.out.println(val);

                            //boolean isPresent = false;

                            for (int i = placePlace.size() - 1; i >= 0; i--) {
                                map = new HashMap<>();
                                map.put(placePlace.get(i), val);
                                System.out.println(map.keySet());
                                val = gson.fromJson(map.toString(), HashMap.class);
                                System.out.println(val);
                                System.out.println(map);
                            }


//                            map.put(placePlace.get(placePlace.size() -1 ), val);
//                            val = gson.fromJson((map).toString(), HashMap.class);
//                            map.put(placePlace.get(placePlace.size() -2 ), val);
//                            val = gson.fromJson((map).toString(), HashMap.class);
//                            map.put(placePlace.get(placePlace.size() - 3), val);
//                            System.out.println(map);
//                            Object ob = map.get(placePlace.get(1));
//                           map.put(placePlace.get(0), placePlace.get(1));
//                           map.put(placePlace.get(1), placePlace.get(2));
//                            System.out.println(map);


                            //dt.putIfAbsent(placePlace.get(0), map);
//

                            //

                            System.out.println(map.get(placePlace.get(0)));


                            dt.put(placePlace.get(0), map.get(placePlace.get(0)));

                            System.out.println(dt);
                        }

                        FileWriter writer = new FileWriter(file);
                        gson.toJson(dt, writer);
                        writer.close();
                        writeLock.unlock();

                        Response1 ok = new Response1("OK");
                        msg = gson.toJson(ok);
                        output.writeUTF(msg);
                     }






                    else if(type.equals("get")){
                        placePlace = (ArrayList<String>) place;
                        readLock.lock();
                        JsonReader reader =  new JsonReader(new FileReader(file));
                        dataBase = gson.fromJson(reader, HashMap.class);
                        if ( placePlace.size() == 1){
                            if(dataBase.containsKey(placePlace.get(0))) {

                                Object p =  dataBase.get(placePlace.get(0));

                                Response1 getValue = new Response1("OK",p );
                                msg = gson.toJson(getValue);
                                output.writeUTF(msg);
                            }


                        }
                        else if ( placePlace.size() > 1){
                            int a = 0;
                            int w = placePlace.size() - 1;
                            for (int i = 0; i < w; i++){

                                if (dataBase.containsKey(placePlace.get(i))){
                                    dataBase = (Map<String, Object>) dataBase.get(placePlace.get(i));
                                    a++;

                                }

                            }

                            String p = (String) dataBase.get(placePlace.get(a));
                            Response1 getValue = new Response1("OK",p );
                            msg = gson.toJson(getValue);
                            output.writeUTF(msg);
                        }
                        else {
                            Response2 errorMSg = new Response2("ERROR", "No such key");
                            msg = gson.toJson(errorMSg);
                            output.writeUTF(msg);
                        }
                        readLock.unlock();

                    }


                    else if (type.equals("delete")){
                        placePlace = (ArrayList<String>) place;
                        writeLock.lock();
                        JsonReader reader = new JsonReader(new FileReader(file));
                        dataBase = gson.fromJson(reader, HashMap.class);
                        if (placePlace.size() == 1){
                            if (dataBase.containsKey(placePlace.get(0))){
                                dataBase.remove(placePlace.get(0));
                            }
                            FileWriter writer = new FileWriter(file);
                            gson.toJson(dataBase, writer);
                            writer.close();

                            Response1 ok = new Response1("OK");
                            msg = gson.toJson(ok);
                            output.writeUTF(msg);

                        }
                        else if (placePlace.size() > 1) {
                            int a = 0;
                            for (int i = 0; i < placePlace.size() - 1; i++){

                                if (dataBase.containsKey(placePlace.get(i))){
                                    dataBase = (Map<String, Object>) dataBase.get(placePlace.get(i));
                                    a++;

                                }
                            }
                            dataBase.remove(placePlace.get(a));

                            FileWriter writer = new FileWriter(file);
                            gson.toJson(dataBase, writer);
                            writer.close();

                            Response1 ok = new Response1("OK");
                            msg = gson.toJson(ok);
                            output.writeUTF(msg);
                        }
                        else{
                            Response2 errorMSg = new Response2("ERROR", "No such key");
                            msg = gson.toJson(errorMSg);
                            output.writeUTF(msg);
                        }
                        writeLock.unlock();
                    }
                }
            }
        } /*catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }*/ catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}