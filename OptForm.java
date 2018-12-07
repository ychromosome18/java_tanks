/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class OptForm extends JFrame {
    private JTextField server = new JTextField("localhost");
    private JTextField port = new JTextField("8189");
    private JTextField clserver = new JTextField("localhost");
    private JTextField clport = new JTextField("8189");
    private JTextField jtfname = new JTextField(System.getProperty("user.name"), 10);
    private JTextArea jta;
    private JLabel jlclients;
    private JButton jbclCreate, jbCreate, jbstart, jbClose;
    private JComboBox<String> levelcb;
    JTable table;

    private MyGdxGame mgame;

    public OptForm(MyGdxGame mg) {
        mgame = mg;
        setBounds(600, 300, 750, 300);
        setTitle("Настройки сетевой игры");
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        JPanel allpanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(allpanel, BoxLayout.Y_AXIS);
        allpanel.setLayout(boxLayout);
        //setLayout(boxLayout);

        JPanel topPanel = new JPanel();
        jbCreate = new JButton("Создать сервер");
        jbClose = new JButton("Прикрыть сервер");
        jbClose.setEnabled(false);
        JLabel jlserver = new JLabel("Сервер");
        JLabel jlport = new JLabel("Порт");
        jlclients = new JLabel("Клиенты: ");
        jbstart = new JButton("Начать мочилово!");
        jbstart.setEnabled(false);

        server.setSize(150, 20);
        topPanel.add(jlserver, FlowLayout.LEFT);
        topPanel.add(server);
        topPanel.add(jlport);
        topPanel.add(port);
        topPanel.add(jbCreate);
        topPanel.add(jbClose);
        topPanel.add(jlclients);
        topPanel.add(jbstart);
        // создать сервер
        jbCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mgame.setGame_mode(Constants.GAME_MODE_SERVER);
                if (mgame.InitServer(Integer.valueOf(port.getText()), jtfname.getText())) {
                    jta.append("Server created!" + "\r\n");
                    jbclCreate.setEnabled(false);
                    jbCreate.setEnabled(false);
                    jbClose.setEnabled(true);
                    AddTableString(jtfname.getText(), mgame.getUsers().get(0).getId());
                } else jta.append("Error creating server!" + "\r\n");
            }
        });
        // Прикрыть сервер
        jbClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mgame.getMserv().Close();

                mgame.DeleteUsers();

                jta.append("Server closed!" + "\r\n");
                jbCreate.setEnabled(true);
                jbClose.setEnabled(false);
                ClearTable();
            }
        });
        // начать игру
        jbstart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch(levelcb.getSelectedIndex()) {
                    case 0:
                        mgame.getMmenu().setLevel("easy");
                        break;
                    case 1:
                        mgame.getMmenu().setLevel("medium");
                        break;
                    case 2:
                        mgame.getMmenu().setLevel("hard");
                        break;
                }
                if(mgame.getGame_mode()==Constants.GAME_MODE_SERVER) mgame.getSd().setLevel(levelcb.getSelectedIndex());
//                Constants.USER_DURABILITY = 1;
//                Constants.ENEMY_MAX_VISIBLE = 10;
//                mgame.getTank().setDurability(1, mgame);
                mgame.InitEnemies(mgame.getMserv().getNumClients() + 1);
                //mgame.setBegin(false);
                setVisible(false);
            }
        });

        JPanel clPanel = new JPanel();
        jbclCreate = new JButton("Подключиться к серверу");
        JLabel jlname = new JLabel("Имя");
        JLabel jlclserver = new JLabel("Сервер");
        JLabel jlclport = new JLabel("Порт");
        clPanel.add(jlname);
        clPanel.add(jtfname);
        //jtfname.setSize(100,jtfname.getHeight());
        clPanel.add(jlclserver);
        clPanel.add(clserver);
        clPanel.add(jlclport);
        clPanel.add(clport);
        clPanel.add(jbclCreate);
        String[] choices = { "ЛЕГКО","СРЕДНЕ", "СЛОЖНО"};
        levelcb = new JComboBox<String>(choices);
        clPanel.add(levelcb);

        allpanel.add(topPanel);
        allpanel.add(clPanel);
        add(allpanel, BorderLayout.NORTH);
        //add(topPanel, BorderLayout.NORTH);
        //add(clPanel, BorderLayout.NORTH);
        // подключиться к серверу
        jbclCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mgame.setGame_mode(Constants.GAME_MODE_CLIENT);
                if (mgame.InitClient(clserver.getText(), Integer.valueOf(clport.getText()), jtfname.getText())) {
                    jta.append("Connected to server!" + "\r\n");
                    jbCreate.setEnabled(false);
                    AddTableString(jtfname.getText(), mgame.getUsers().get(0).getId());
                } else jta.append("Error connecting to server!" + "\r\n");
            }
        });

        jta = new JTextArea();
        //jta.setMinimumSize(new Dimension(200,50));
        jta.setEditable(false);
        jta.setLineWrap(true);
        JScrollPane jsp = new JScrollPane(jta);
        add(jsp, BorderLayout.SOUTH);

        CreateTable(this, BorderLayout.CENTER);

    }

    public void setNumClients(int n) {
        jlclients.setText("Клиенты: " + n + " шт.");
        jbstart.setEnabled(true);
    }

    public void AddJTAText(String txt) {
        jta.append(txt + "\r\n");
    }

    public void CreateTable(JFrame form, Object constraints) {
        String[] colName = new String[]{
                "Команда1", "Переместить", "Команда2", "id"
        };
        Object[][] products = new Object[][]{};
        DefaultTableModel model = new DefaultTableModel(products, colName);

        table = new JTable(model);
        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.valueOf(e.getActionCommand());
                if (mgame.getGame_mode() == Constants.GAME_MODE_SERVER && modelRow != 0) {
                    JTable table = (JTable) e.getSource();

                    DefaultTableModel tbm = (DefaultTableModel) table.getModel();
                    if (tbm.getValueAt(modelRow, 0) == null) {
                        ChangeTeamAt(tbm, modelRow, 1);
                        mgame.getSd().addTeam(tbm.getValueAt(modelRow, 3).toString(), 1);
                        mgame.sendBroadcastMessage(mgame.getSd());
                        for (int i = 0; i < mgame.getUsers().size(); i++) {
                            if (mgame.getUsers().get(i).getId() == tbm.getValueAt(modelRow, 3).toString())
                                mgame.getUsers().get(i).setTeamid(1);
                        }
                    } else {
                        ChangeTeamAt(tbm, modelRow, 2);
                        mgame.getSd().addTeam(tbm.getValueAt(modelRow, 3).toString(), 2);
                        mgame.sendBroadcastMessage(mgame.getSd());
                        for (int i = 0; i < mgame.getUsers().size(); i++) {
                            if (mgame.getUsers().get(i).getId() == tbm.getValueAt(modelRow, 3).toString())
                                mgame.getUsers().get(i).setTeamid(2);
                        }
                    }

                    boolean ispvp = false;
                    for (int i = 0; i < tbm.getRowCount(); i++) {
                        if (tbm.getValueAt(i, 2) != null) ispvp = true;
                    }
                    if (!ispvp) mgame.setGame_type(Constants.GAME_TYPE_PvB);
                }
            }
        };

        ButtonColumn buttonColumn = new ButtonColumn(table, delete, 1);
        buttonColumn.setMnemonic(KeyEvent.VK_D);

        JScrollPane jsptable = new JScrollPane(table);
        form.add(jsptable, constraints);
    }

    public void AddTableString(String pl_name, String id) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
//        boolean exist=false;
//        for(int i=0; i<model.getRowCount(); i++) {
//            if(model.getValueAt(i,0).equals(pl_name)) {
//                exist=true;
//                break;
//            }
//        }
//        if(!exist) model.addRow(new Object[]{pl_name, ">>>>>", null, id});
        model.addRow(new Object[]{pl_name, ">>>>>", null, id});
    }

    public void ClearTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }

    public void ChangeTeam(String id, int teamid) {
        DefaultTableModel tbm = (DefaultTableModel) table.getModel();
        boolean ispvp = false;
        for (int i = 0; i < tbm.getRowCount(); i++) {
            if (id == tbm.getValueAt(i, 3).toString()) {
                if (teamid == 1 && tbm.getValueAt(i, 0) == null) {
                    ChangeTeamAt(tbm, i, 1);
                }
                if (teamid == 2 && tbm.getValueAt(i, 2) == null) {
                    ChangeTeamAt(tbm, i, 2);
                }
            }
            if (tbm.getValueAt(i, 2) != null) ispvp = true;
        }
        if (!ispvp) mgame.setGame_type(Constants.GAME_TYPE_PvB);
    }

    public void ChangeTeamAt(DefaultTableModel tbm, int rowid, int team) {
        if (team == 1) {
            tbm.setValueAt(tbm.getValueAt(rowid, 2), rowid, 0);
            tbm.setValueAt(">>>>>", rowid, 1);
            tbm.setValueAt(null, rowid, 2);
        } else {
            tbm.setValueAt(tbm.getValueAt(rowid, 0), rowid, 2);
            tbm.setValueAt("<<<<<", rowid, 1);
            tbm.setValueAt(null, rowid, 0);
            mgame.setGame_type(Constants.GAME_TYPE_PvP);
        }
    }

    public void DefaultView(String message) {
        ClearTable();
        jbCreate.setEnabled(true);
        jbClose.setEnabled(false);
        jbclCreate.setEnabled(true);
        if (message != null) jta.append(message + "\r\n");
    }
}
