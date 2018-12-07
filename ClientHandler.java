/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private MyServer server;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String name = null;
    private boolean isAuth = false;
    private long start_conn;
    private SendData myMessageArray;
    private MyGdxGame mgame;

    public ClientHandler(MyServer server, Socket socket, MyGdxGame mgame) {
        this.server = server;
        this.mgame=mgame;
        try {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            //ObjectInputStream in1=new ObjectInputStream(socket.getInputStream());
            //in1.readObject()
            start_conn = System.currentTimeMillis();
        } catch (IOException e) {
            System.out.println("Client handler initialization failed: " + e.getLocalizedMessage());
        }
    }

    @Override
    public void run() {
        //long start_conn = System.currentTimeMillis();
        while (!socket.isClosed()) {
            try {
                //String msg = in.readUTF();
                myMessageArray = (SendData) in.readObject();
                //mgame.log("Received SendData class");
                myMessageArray.ReturnData(mgame);

            } catch (java.net.SocketException se) {
                try {
                    mgame.log("Client disconnected "+this.getClass().getName());
                    socket.close();
                    break;
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                    mgame.log("Error closing socket "+this.getClass().getName()+ioex.getMessage());
                }
            }
            catch (Exception e) {
                //e.printStackTrace();
                //System.out.println("Error receiving...");
                mgame.log("Other exception "+this.getClass().getName()+e.getMessage());
            }
        }
//        try {
//            System.out.println("Client disconnected");
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public void sendPersonalMessage(String user, String message) {
        server.sendPrivateMessage(name, user, message);
    }

    private void sendBroadcastMessage(SendData arr) {
        //server.sendBroadcastMessage(arr);
    }

    public void sendMessage(SendData arr) {

        //System.out.println(name + ": " + msg);
            try {
                if(!socket.isClosed()) {
                    out.writeObject(arr);
                    out.flush();
                    out.reset();
                }
            } catch (Exception e) {
                //System.out.println("Error "+ arr.hashCode());
                mgame.log("Error sending message "+this.getClass().getName()+e.getMessage());
                e.printStackTrace();
                try {
                    out.reset();
                } catch (Exception ex) {
                    mgame.log("Error resetting out "+this.getClass().getName()+e.getMessage());
                }
            }
    }

    public boolean isActive() {
        return isAuth;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStart_conn() {
        return start_conn;
    }

    public void close() {
        try {
            socket.close();
            mgame.log("Socket closed (ClientHandler)");
        } catch (IOException ioex) {
            mgame.log("Error closing socket (ClientHandler)"+ioex.getMessage());
        }
    }

}
