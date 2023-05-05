package client;



import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import java.util.Map;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import com.google.gson.internal.LinkedTreeMap;


public class Main {
    @Parameter(names = {"-in"},
            description = "file location")
    private String n;
    @Parameter(names = {"-t"},

            description = "type of the request")
    private String t ;
    @Parameter(names = "-k",

            description = "database location")
    private String i;
    @Parameter(names = {"-v"},

            description = "data to be saved")
    private String m ;


    public static class Msg{
        public String type;
        public String key;
        public String value;

        public Msg(String type, String key) {
            this.type = type;
            this.key = key;
        }

        public Msg(String type) {
            this.type = type;
        }

        public Msg(String type, String key, String value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

    }


    public static void main(String[] args) throws FileNotFoundException {
        Main jArgs = new Main();
        JCommander cmdArg = JCommander.newBuilder()
                .addObject(jArgs)
                .build();
        cmdArg.parse(args);

        File file = new File("C:\\Users\\Admin\\IdeaProjects\\JSON Database\\JSON Database\\task\\src\\client\\data\\");

        String address = "127.0.1";
        int port = 23456;

        try(Socket socket = new Socket(InetAddress.getByName(address), port);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream())){
            System.out.println("Client started!");

            Msg mssge;
            Gson gson = new Gson();
            String mssgeJson = "";
            if(args[0].equals("-in")) {
                FileReader reader = new FileReader(file + "\\" + args[1]);
                Map<String ,Object> map = gson.fromJson(reader, LinkedTreeMap.class);

                mssgeJson = gson.toJson(map);

            }
            else if (args.length == 2) {
                mssge = new Msg(args[1]);
                mssgeJson = gson.toJson(mssge);


            }
            else if (args.length == 4) {
                mssge = new Msg(args[1], args[3]);
                mssgeJson = gson.toJson(mssge);
            }

        else if (args.length == 6) {
                mssge = new Msg(args[1], args[3], args[5]);
                mssgeJson = gson.toJson(mssge);

            }


            System.out.println("Sent: "+ mssgeJson);
            output.writeUTF(String.valueOf(mssgeJson));
            String in = input.readUTF();

            System.out.println("Received: " + in);


        }catch(IOException e){
            e.printStackTrace();
        }
    }

}