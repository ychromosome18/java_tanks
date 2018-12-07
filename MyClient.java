/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import java.net.Socket;
import java.io.*;

public class MyClient {
    //static private final String SERVER_ADDR = "localhost";
    //static private final int SERVER_PORT = 8189;

    private Socket sock;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    //private SendData myMessageArray;
    private MyGdxGame mgame;
    //private boolean authorized = false;

      public MyClient(String SERVER_ADDR, int SERVER_PORT, MyGdxGame mg) {
        mgame=mg;
        try {
            sock = new Socket(SERVER_ADDR, SERVER_PORT);
            System.out.println("Client connected!");
            mgame.log("Client connected!");
            in = new ObjectInputStream(sock.getInputStream());
            out = new ObjectOutputStream(sock.getOutputStream());

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!sock.isClosed()) {
                    try {

                        //myMessageArray=new SendData();
                        SendData myMessageArray = (SendData) in.readObject();
                        myMessageArray.ReturnData(mgame);
                        //System.out.println("Received SendData class");
                        //mgame.log("Received SendData class");

                    } catch (Exception e) {
                        //System.out.println("Error receiving...");
                        e.printStackTrace();
                        mgame.log("Error receiving message "+this.getClass().getName()+e.getMessage());
                        if(e.getClass().getName().equals("java.io.EOFException") || e.getClass().getName().equals("java.net.SocketException")) Close();
                    }
                }
            }
        }).start();

          new Thread(new Runnable() {
              @Override
              public void run() {

                  while (!sock.isClosed()) {
                      try {
                          if (mgame.getSd() != null && !mgame.isBegin()) {
                              synchronized (mgame.getSd()) {
                                  sendMessage(mgame.getSd());
                                  mgame.getSd().ClearData();
                                  //mgame.log("Sended SendData class");
                              }
                          }
                          Thread.sleep(Constants.GAME_NET_SEND_PERIOD);

                  } catch(Exception e){
                      e.printStackTrace();
                      mgame.log("Error sending message "+this.getClass().getName()+e.getMessage());

                  }
              }
              }
          }).start();

        } catch (IOException e) {
            e.printStackTrace();
            mgame.log("Error other (MyClient) "+this.getClass().getName()+e.getMessage());
        }

      }

    public synchronized void sendMessage(SendData arr) {
        //System.out.println(name + ": " + msg);
        try {
            out.writeObject(arr);
            out.flush();
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
            mgame.log("Error sending message (sendMessage) "+this.getClass().getName()+e.getMessage());
        }
    }

    public Socket getSock() {
        return sock;
    }

    public void Close() {
        try {
          if(sock!=null ) {
              //out.close();
              sock.close();
              mgame.log(this.getClass().getName()+"Socket closed.");
              mgame.getOpf().DefaultView("Server closed connection.");
              mgame.DeleteUsers();
          }
        } catch (IOException e) {
            e.printStackTrace();
            mgame.log("Error closing socket "+this.getClass().getName()+e.getMessage());
        }
    }
}
