/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;


public class SendData implements Serializable, Cloneable {
    private int[][] tiles = null;
    private ArrayList<String[]> bullets;
    private HashMap<String, Integer[]> tanks;
    private TreeMap<Integer, Integer[]> enemies;
    private HashMap<Integer, Integer[]> bonuses;
    private int status;
    private int level;
    private HashMap<Integer, String> enemy_hit;
    private HashMap<String, String> tank_hit;
    private HashMap<String, String> names;
    private HashMap<String, Integer> teams;
    private ArrayList<Float[]> boss;
    private transient ReentrantLock lock;
    private transient MyGdxGame mg;

    public SendData(int width, int height, ReentrantLock lock, MyGdxGame mg) {
        //tiles=new int[width][height];
        bullets = new ArrayList<String[]>();
        tanks = new HashMap<String, Integer[]>();
        enemies = new TreeMap<Integer, Integer[]>();
        bonuses = new HashMap<Integer, Integer[]>();
        enemy_hit = new HashMap<Integer, String>();
        tank_hit = new HashMap<String, String>();
        names = new HashMap<String, String>();
        teams = new HashMap<String, Integer>();
        boss = new ArrayList<>();
        status = 1;
        this.lock = lock;
        this.mg = mg;
    }

    public void addBoss(Float x, Float y) {
        addBoss(x, y, null, null, null, null);
    }

    public void addBoss_phase(Float phase) {
        addBoss(null, null, null, null, phase, null);
    }

    public void addBoss_need_orient(Float need_orient) {
        addBoss(null, null, need_orient, null, null, null);
    }

    public void addBoss_need_turret_orient(Float need_turret_orient) {
        addBoss(null, null, null, null, null, need_turret_orient);
    }

    public void addBoss(Float x, Float y, Float need_orient, Float turret_orient, Float phase) {
        addBoss(x, y, need_orient, turret_orient, phase, null);
    }

    public void addBoss(Float x, Float y, Float need_orient, Float turret_orient, Float phase, Float need_turret_orient) {
        try {
            lock.lock();
            Float a[];
            if (boss.size() == 0) {
                a = new Float[6];
                a[0] = x;
                a[1] = y;
                a[2] = need_orient;
                a[3] = turret_orient;
                a[4]=phase;
                a[5] = need_turret_orient;
                boss.add(a);
            } else {
                a = boss.get(0);
                if (x != null) a[0] = x;
                if (y != null) a[1] = y;
                if (need_orient != null) a[2] = need_orient;
                if (turret_orient != null) a[3] = turret_orient;
                if (phase != null) a[4] = phase;
                if (need_turret_orient != null) a[5] = need_turret_orient;
            }
        } finally {
            lock.unlock();
        }
    }

    public void add_enemy_hit(int i, String id) {
        try {
            lock.lock();
            enemy_hit.put(i, id);
        } finally {
            lock.unlock();
        }
    }

    public void add_tank_hit(String i, String id) {
        try {
            lock.lock();
            tank_hit.put(i, id);
        } finally {
            lock.unlock();
        }
    }

    public void addName(String id, String name) {
        try {
            lock.lock();
            names.put(id, name);
        } finally {
            lock.unlock();
        }
    }

    public void addTeam(String id, Integer teamid) {
        try {
            lock.lock();
            teams.put(id, teamid);
        } finally {
            lock.unlock();
        }
    }

    public void addBullet(float x, float y, int dx, int dy, String h, int fp) {
        try {
            String[] bullet = new String[6];
            bullet[0] = String.valueOf((int) x);
            bullet[1] = String.valueOf((int) y);
            bullet[2] = String.valueOf(dx);
            bullet[3] = String.valueOf(dy);
            bullet[4] = h;
            bullet[5] = String.valueOf(fp);
            lock.lock();
            bullets.add(bullet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void addBullet(float x, float y, double dx, double dy, String h, int fp) {
        try {
            String[] bullet = new String[6];
            bullet[0] = String.valueOf((int) x);
            bullet[1] = String.valueOf((int) y);
            bullet[2] = String.valueOf(dx);
            bullet[3] = String.valueOf(dy);
            bullet[4] = h;
            bullet[5] = String.valueOf(fp);
            lock.lock();
            bullets.add(bullet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void addTiles(tile[][] tiles) {
        try {
            lock.lock();

            if (this.tiles == null) this.tiles = new int[Constants.WINDOW_WIDTH / Constants.TILE_SIZE][Constants.WINDOW_HEIGHT / Constants.TILE_SIZE];
            for (int i = 0; i < Constants.WINDOW_WIDTH; i += Constants.TILE_SIZE) {
                for (int j = 0; j < Constants.WINDOW_HEIGHT; j += Constants.TILE_SIZE) {
                    this.tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE] = tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE].getType();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void addTank(float x, float y, int orient, String id) {
        try {
            lock.lock();
            Integer[] tank = new Integer[3];
            tank[0] = ((int) x);
            tank[1] = ((int) y);
            tank[2] = orient;
            tanks.put(id, tank);

            mg.log("addtank(SendData) " + id + " pos " + tank[0]);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void addEnemy(float x, float y, int orient, int dx, int dy, int type, int id) {
        try {
            lock.lock();
            Integer[] en = new Integer[6];
            en[0] = ((int) x);
            en[1] = ((int) y);
            en[2] = orient;
            en[3] = dx;
            en[4] = dy;
            en[5] = type;

            enemies.put(id, en);
            //int f=1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void addBonus(float x, float y, int count, int id) {
        try {
            lock.lock();
            Integer[] tank = new Integer[3];
            tank[0] = ((int) x);
            tank[1] = ((int) y);
            tank[2] = count;

            bonuses.put(id, tank);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void ClearData() {
        try {
            lock.lock();
            tiles = null;
            bullets.clear();
            tanks.clear();
            enemies.clear();
            bonuses.clear();
            enemy_hit.clear();
            tank_hit.clear();
            status = -1;
            level = -1;
            names.clear();
            teams.clear();
            boss.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void ReturnData(MyGdxGame mg) {
        try {
            if (tiles != null) {
                for (int i = 0; i < Constants.WINDOW_WIDTH; i += Constants.TILE_SIZE) {
                    for (int j = 0; j < Constants.WINDOW_HEIGHT; j += Constants.TILE_SIZE) {
                        if (mg.getGame_type() == Constants.GAME_TYPE_PvP && mg.getUsers().get(0).getTeamid() == 2)
                            mg.getTiles()[i / Constants.TILE_SIZE][(Constants.WINDOW_HEIGHT - j - 1) / Constants.TILE_SIZE].rewrite(tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE]);
                        else mg.getTiles()[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE].rewrite(tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE]);
                    }
                }
            }

            for (String[] b : bullets) {
                if (!mg.getTank().getId().equals(b[4])) {
                    boolean other = false;
                    for (int j = 0; j < mg.getUsers().size(); j++) {
                        if (mg.getUsers().get(j).getId().equals(b[4])) {
                            if (mg.getUsers().get(j).getTeamid() != mg.getTank().getTeamid()) other = true;
                            break;
                        }
                    }

                    if (mg.getGame_type() == Constants.GAME_TYPE_PvP) {
                        if ((other && mg.getGame_mode() == Constants.GAME_MODE_SERVER) || mg.getTank().getTeamid() == 2) {
                            mg.AddShell(Integer.valueOf(b[0]), Constants.WINDOW_HEIGHT - Integer.valueOf(b[1]) - (Integer.valueOf(b[2]) != 0 ? 8 : 0),
                                    Integer.valueOf(b[2]), -Integer.valueOf(b[3]), b[4], Integer.valueOf(b[5]));
                        } else
                            mg.AddShell(Integer.valueOf(b[0]), Integer.valueOf(b[1]), Integer.valueOf(b[2]), Integer.valueOf(b[3]), b[4], Integer.valueOf(b[5]));
//                                else if (mg.getTank().getTeamid() == 2) {
//                                    mg.getShls()[i].enable(Integer.valueOf(b[0]), Constants.WINDOW_HEIGHT - Integer.valueOf(b[1]) - (Integer.valueOf(b[2]) != 0 ? 8 : 0),
//                                            Integer.valueOf(b[2]), -Integer.valueOf(b[3]), b[4], Integer.valueOf(b[5]), mg);
//                                }
                    } else
                        try {
                            Integer.valueOf(b[2]);
                            Integer.valueOf(b[3]);
                            mg.AddShell(Integer.valueOf(b[0]), Integer.valueOf(b[1]), Integer.valueOf(b[2]), Integer.valueOf(b[3]), b[4], Integer.valueOf(b[5]));
                        } catch (NumberFormatException e) {
                            mg.AddShell(Integer.valueOf(b[0]), Integer.valueOf(b[1]), Double.valueOf(b[2]), Double.valueOf(b[3]), b[4], Integer.valueOf(b[5]));
                        }

                }
            }

            for (Integer bon : bonuses.keySet()) {
                Integer[] bs = bonuses.get(bon);
                if (mg.getGame_type() == Constants.GAME_TYPE_PvP && mg.getUsers().get(0).getTeamid() == 2)
                    mg.getBs()[bon].Enable(bs[0], Constants.WINDOW_HEIGHT - bs[1] - (int) mg.getBs()[0].getSpriteHeight(), bs[2]);
                else mg.getBs()[bon].Enable(bs[0], bs[1], bs[2]);
            }

            if (!enemies.isEmpty()) {
                if (mg.getEnemies().size() == 0) {
                    for (Integer i : enemies.keySet()) {
                        Integer[] tank = enemies.get(i);
                        Enemy en = new Enemy(tank[0], tank[1], tank[5]);
                        mg.getEnemies().add(en);
                        mg.log("Add enemy (ReturnData) " + String.valueOf(tank[0]) + " ; " + String.valueOf(tank[1]) + " ; " + String.valueOf(tank[5]));
                    }
                } else {
                    for (int i = 0; i < mg.getEnemies().size(); i++) {
                        if (enemies.containsKey(i)) {
                            Integer[] pos = enemies.get(i);
                            mg.getEnemies().get(i).SetPos(pos[0], pos[1], pos[2], pos[3], pos[4], pos[5], mg.getMs());
                        }
                    }
                }
            }

            if (!tanks.isEmpty()) {
                for (int i = 1; i < mg.getUsers().size(); i++) {
                    if (tanks.containsKey(mg.getUsers().get(i).getId())) {
                        Integer[] pos = tanks.get(mg.getUsers().get(i).getId());
                        if (mg.getGame_type() == Constants.GAME_TYPE_PvP) {
                            if ((mg.getTank().getTeamid() != mg.getUsers().get(i).getTeamid() && mg.getGame_mode() == Constants.GAME_MODE_SERVER) || mg.getTank().getTeamid() == 2)
                                mg.getUsers().get(i).SetPos(pos[0], Constants.WINDOW_HEIGHT - pos[1], (pos[2] == 0 || pos[2] == 180) ? (pos[2] + 180) % 360 : pos[2], false, mg);
                            else mg.getUsers().get(i).SetPos(pos[0], pos[1], pos[2], false, mg);

                        } else mg.getUsers().get(i).SetPos(pos[0], pos[1], pos[2], false, mg);
                        tanks.remove(mg.getUsers().get(i).getId());
                        mg.log("Set tank (ReturnData)" + mg.getUsers().get(i).getId() + " pos " + pos[0]);
                    }
                }
                for (String i : tanks.keySet()) {
                    if (!i.equals(mg.getTank().getId())) {
                        user usr = new user(mg, i);
                        Integer[] pos = tanks.get(i);
                        usr.SetPos(pos[0], pos[1], pos[2], true, mg);
                        mg.log("Add new tank (ReturnData)" + i + " pos " + pos[0]);
                        mg.getUsers().add(usr);
                    }
                }
            }

            for (int k : enemy_hit.keySet()) {
                if (mg.getEnemies().get(k).isAlive()) {
                    mg.getEnemies().get(k).setDurability(0, mg, enemy_hit.get(k));
                }
            }

            for (String k : tank_hit.keySet()) {
                for (int i = 0; i < mg.getUsers().size(); i++) {
                    if (mg.getUsers().get(i).isAlive() && mg.getUsers().get(i).getId().equals(k)) {
                        mg.getUsers().get(i).setDurability(0, mg, tank_hit.get(k));
                    }
                }
            }

            if (names.size() > 0) {
                for (int i = 1; i < mg.getUsers().size(); i++) {
                    if (names.containsKey(mg.getUsers().get(i).getId())) {
                        mg.getUsers().get(i).setName(names.get(mg.getUsers().get(i).getId()));
                        //if(mg.getGame_mode()==Constants.GAME_MODE_SERVER || mg.getGame_mode()==Constants.GAME_MODE_CLIENT) {
                        //mg.getOpf().AddJTAText("Connected client: "+mg.getUsers().get(i).getName());
                        mg.getOpf().AddTableString(mg.getUsers().get(i).getName(), mg.getUsers().get(i).getId());
                        if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) {
                            for (int j = 0; j < mg.getUsers().size(); j++) {
                                mg.getSd().addTank(mg.getUsers().get(j).getPosition().x, mg.getUsers().get(j).getPosition().y, mg.getUsers().get(j).getOrient(), mg.getUsers().get(j).getId());
                                mg.log("Add tank (ReturnData+addnames) " + mg.getUsers().get(j).getPosition().x + " " + getClass().getName());
                                mg.getSd().addName(mg.getUsers().get(j).getId(), mg.getUsers().get(j).getName());
                            }
                            mg.sendBroadcastMessage(mg.getSd());
                        }
                        //}
                    }
                }
            }

            if (teams.size() > 0) {
                for (int i = 0; i < mg.getUsers().size(); i++) {
                    if (teams.containsKey(mg.getUsers().get(i).getId())) {
                        mg.getUsers().get(i).setTeamid(teams.get(mg.getUsers().get(i).getId()));
                        mg.getOpf().ChangeTeam(mg.getUsers().get(i).getId(), teams.get(mg.getUsers().get(i).getId()));
                    }
                }
            }

            if (boss.size() > 0) {
                Float[] a = boss.get(0);
                if(a[0]!=null) {
                    if(a[0]==-5000 && mg.getBb().isAlive()) {
                        mg.getBb().setDurability(0,mg);
                    }
                    if(a[1]==1000) {
                        mg.getBb().renew(a[0],a[1]);
                        mg.getBb().setPos(a[0],a[1]);
                    } else {
                        mg.getBb().setPosition(new Vector2(a[0],a[1]));
                    }
                }
                if(a[2]!=null) mg.getBb().setNeed_orient(a[2]);
                if(a[3]!=null) mg.getBb().setTurret_orient(a[3]);
                if(a[4]!=null) mg.getBb().setPhase(a[4].intValue());
                if(a[5]!=null) mg.getBb().setNeed_turret_orient(a[5].intValue());
            }

            switch (level) {
                case 0:
                    mg.getMmenu().setLevel("easy");
                    break;
                case 1:
                    mg.getMmenu().setLevel("medium");
                    break;
                case 2:
                    mg.getMmenu().setLevel("hard");
                    break;
            }

            switch (status) {
                case Constants.GAME_BEGIN:
//                mg.getTank().rewrite(mg);
//                for (int i = 0; i < mg.getUsers().size(); i++) {
//                    mg.getUsers().get(i).rewrite(mg);
//                }
                    if (tanks.containsKey(mg.getTank().getId())) {
                        Integer[] pos = tanks.get(mg.getTank().getId());
                        if (mg.getGame_type() == Constants.GAME_TYPE_PvP && mg.getTank().getTeamid() == 2) {
                            mg.InitEnemiesClient(pos[0], Constants.WINDOW_HEIGHT - pos[1], (pos[2] == 0 || pos[2] == 180) ? (pos[2] + 180) % 360 : pos[2]);
                        } else mg.InitEnemiesClient(pos[0], pos[1], pos[2]);
                        mg.log("Write tank pos. (ReturnData GAME_BEGIN) " + pos[0]);
                    }
                    break;
                case Constants.GAME_START:
                    mg.setBegin(false);
                    mg.getOpf().setVisible(false);
                    break;
                case Constants.GAME_STOP:
                    mg.setBegin(true);
                    mg.StageClientEnd();
                    //mg.getOpf().setVisible(false);
                    break;
                case Constants.GAME_PAUSE:
                    if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) mg.paus(1, true);
                    else mg.paus(1, false);
                    break;
                case Constants.GAME_UNPAUSE:
                    if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) mg.paus(2, true);
                    else mg.paus(2, false);
                    break;
                case Constants.GAME_ENDSTAGE_WIN:
                    mg.setEndcount(Constants.END_DELAY_TIME);
                    if (mg.getGame_type() == Constants.GAME_TYPE_PvP && mg.getTank().getTeamid() == 2)
                        mg.getRe().setWin(2);
                    else mg.getRe().setWin(1);
                    break;
                case Constants.GAME_ENDSTAGE_FAIL:
                    mg.setEndcount(Constants.END_DELAY_TIME);
                    if (mg.getGame_type() == Constants.GAME_TYPE_PvP && mg.getTank().getTeamid() == 2)
                        mg.getRe().setWin(1);
                    else mg.getRe().setWin(2);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void SetStatus(int s) {
        try {
            lock.lock();
            status = s;
        } finally {
            lock.unlock();
        }
    }

    protected Object clone() throws CloneNotSupportedException {
        SendData newsd;
        try {
            lock.lock();
            newsd = new SendData(Constants.WINDOW_WIDTH / Constants.TILE_SIZE, Constants.WINDOW_HEIGHT / Constants.TILE_SIZE, lock, mg);
            newsd.setBonuses((HashMap<Integer, Integer[]>) bonuses.clone());
            newsd.setBullets((ArrayList<String[]>) bullets.clone());
            newsd.setTanks((HashMap<String, Integer[]>) tanks.clone());
            newsd.setEnemies((TreeMap<Integer, Integer[]>) enemies.clone());
            if (tiles != null) newsd.setTiles(tiles.clone());
            newsd.SetStatus(status);
            newsd.setLevel(level);
            newsd.setNames((HashMap<String, String>) names.clone());
            newsd.setTeams((HashMap<String, Integer>) teams.clone());
            newsd.setEnemy_hit((HashMap<Integer, String>) enemy_hit.clone());
            newsd.setTank_hit((HashMap<String, String>) tank_hit.clone());
            newsd.setBoss((ArrayList<Float[]>) boss.clone());
            newsd.setLock(lock);
        } finally {
            lock.unlock();
        }
        return newsd;
    }

    public void setEnemies(TreeMap<Integer, Integer[]> enemies) {
        this.enemies = enemies;
    }

    public void setBonuses(HashMap<Integer, Integer[]> bonuses) {
        this.bonuses = bonuses;
    }

    public void setBullets(ArrayList<String[]> bullets) {
        this.bullets = bullets;
    }

    public void setTanks(HashMap<String, Integer[]> tanks) {
        this.tanks = tanks;
    }

    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
    }

    public void setNames(HashMap<String, String> names) {
        this.names = names;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public void setTeams(HashMap<String, Integer> teams) {
        this.teams = teams;
    }

    public void setEnemy_hit(HashMap<Integer, String> enemy_hit) {
        this.enemy_hit = enemy_hit;
    }

    public void setTank_hit(HashMap<String, String> tank_hit) {
        this.tank_hit = tank_hit;
    }

    public void setBoss(ArrayList<Float[]> boss) {
        this.boss = boss;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
