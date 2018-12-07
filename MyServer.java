/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * MyServer
 *
 * @author anton
 * @since 06/11/17
 */
public class MyServer implements Runnable {

    private List<ClientHandler> clients = new ArrayList<ClientHandler>();
    private Socket s;
    private ServerSocket server;
    private MyGdxGame mgame;
    private Object lock=new Object();

    public MyServer(int port, MyGdxGame mg) {
        mgame = mg;
        s = null;
        server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("Server created. Waiting for client...");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (!server.isClosed()) {
                            KillTimedOUT();

                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(this).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
//        } finally {
//            try {
//                if (server != null) server.close();
//                System.out.println("Server closed");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!server.isClosed()) {
                        if (mgame.getSd() != null && !mgame.isBegin()) sendBroadcastMessage(mgame.getSd());

                        Thread.sleep(Constants.GAME_NET_SEND_PERIOD);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mgame.log("Error sending message "+this.getClass().getName()+e.getMessage());
                }
            }
        }).start();

    }

    @Override
    public void run() {
        try {

            while (!server.isClosed()) {
                s = server.accept();
                System.out.println("Server accepts client");
                ClientHandler client = new ClientHandler(this, s, mgame);
                new Thread(client).start();
                clients.add(client);
                mgame.getOpf().setNumClients(clients.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
            mgame.log("Error accepting client "+this.getClass().getName()+e.getMessage());
        } finally {
            try {
                if (s != null) s.close();
            } catch (IOException e) {
                e.printStackTrace();
                mgame.log("Error closing socket "+this.getClass().getName()+e.getMessage());
            }
        }
    }

    public void sendBroadcastMessage(SendData arr) {
        SendData sd;
        synchronized (lock) {
            try {
                sd = (SendData) arr.clone();
                arr.ClearData();
                for (ClientHandler c : clients) {
                    c.sendMessage(sd);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mgame.log("Error sending broadcast message " + this.getClass().getName() + e.getMessage());
            }
        }
    }

    public void close(Socket socket) {
        //clients.removeIf(clientHandler -> clientHandler.getSocket().equals(socket));
        //FIXME
    }

    void sendPrivateMessage(String from, String userName, String message) {
        for (ClientHandler c : clients) {
            String name = c.getName();
            //if (name.equals(userName) && c.isActive())
            //c.sendMessage(from + " написал лично " + userName + ": " + message);
        }
    }

    private void KillTimedOUT() {
        List<ClientHandler> to_remove = new ArrayList<ClientHandler>();
        for (ClientHandler c : clients) {
            if (c.getSocket().isClosed()) {
                to_remove.add(c);
                System.out.println("Remove client "+c.hashCode());
                //if (!c.isActive() && (System.currentTimeMillis()-c.getStart_conn()>120000)) {
            }
        }

        for (ClientHandler c : to_remove) clients.remove(c);
        //Thread.sleep(100);
    }

    public ServerSocket getServer() {
        return server;
    }

    public void Close() {
        try {
            if (server != null) {
                for (ClientHandler c : clients) c.close();
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getNumClients() {
        return (clients.size());
    }
}
