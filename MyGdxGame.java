/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class MyGdxGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private ArrayList<user> users;
    private ArrayList<shell> shls;
    private Texture textureshell;
    private tile[][] tiles;
    private ArrayList<Enemy> enemies;
    private Explosion[] explosions;
    private MainMenu mmenu;
    private Bonus[] bs;
    private Score sc;
    private Text tx;
    private Mysounds ms;
    private MyServer mserv = null;
    private MyClient mcl = null;
    private SendData sd = null;
    private OptForm opf;
    private RoundEnd re;
    private BigBoss bb;
    private int init_time;
    private int game_mode = Constants.GAME_MODE_SINGLE;
    private int game_type = Constants.GAME_TYPE_PvB;
    private int enem_pos;
    private int net_send_count;
    private int win_rounds=0;
    private boolean pause;
    private boolean begin;
    private int endcount, enemy_count;
    private ReentrantLock lock;

    private static final Logger logger = Logger.getLogger(MyGdxGame.class.getName());

    @Override
    public void create() {
        try {
            FileHandler h = new FileHandler("gdxgame.log");
            h.setFormatter(new SimpleFormatter());
            h.setLevel(Level.INFO);
            logger.addHandler(h);
        } catch (IOException e) {
            e.printStackTrace();
        }


        batch = new SpriteBatch();
        batch.enableBlending();
        //  текстура для снаряда
        textureshell = new Texture("shell.png");
        tx = new Text();

        init_time = 0;
        enem_pos = 0;
        pause = false;
        begin = true;
        endcount = 0;
        net_send_count = Constants.GAME_NET_SEND_PERIOD;

        users = new ArrayList<user>();
        users.add(new user(this, null));
        shls = new ArrayList<shell>();

//        for (int i = 0; i < shls.length; i++) {
//            shls[i] = new shell();
//        }

        //  создание всех тайлов
        tiles = new tile[Constants.WINDOW_WIDTH / Constants.TILE_SIZE][Constants.WINDOW_HEIGHT / Constants.TILE_SIZE];
        for (int i = 0; i < Constants.WINDOW_WIDTH; i += Constants.TILE_SIZE) {
            for (int j = 0; j < Constants.WINDOW_HEIGHT; j += Constants.TILE_SIZE) {
                tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE] = new tile(i, j, 0);
            }
        }

        //   взрывы
        explosions = new Explosion[15];
        for (int i = 0; i < explosions.length; i++) explosions[i] = new Explosion();

        //  бонусы
        bs = new Bonus[8];
        for (int i = 0; i < bs.length; i++) {
            bs[i] = new Bonus((byte) i);
        }

        //  враги
        enemies = new ArrayList<Enemy>();
        ms = new Mysounds(this);

        Enemy.InitTextures();
        //  счёт игры
        sc = new Score(Enemy.getTex_average(), Enemy.getTex_fast(), Enemy.getTex_big(), user.getTex_average(), user.getTex_fast(), user.getTex_big());
        tx = new Text();

        //if(game_mode==Constants.GAME_MODE_SERVER) mserv=new MyServer();
        //if(mserv!=null) mserv.sendBroadcastMessage(GetTilesType());
        mmenu = new MainMenu();
        opf = new OptForm(this);

        //  вселенский зам'ок
        lock = new ReentrantLock();

        //  картнка в конце раунда
        re=new RoundEnd(this);

        //  босс
        bb=new BigBoss(300,300, this);
        //bb.Activate();
    }

    @Override
    public void render() {
        if (!pause && !begin && endcount!=1) {

            update();

            //  показывать по 3 врага в начале раунда
            if (game_mode != Constants.GAME_MODE_CLIENT) {
                if (init_time == Constants.INIT_DELAY_GROUP && enemies.size() > 0) {
                    int count = 0, visible = 0;
                    for (int i = 0; i < enemies.size(); i++) {
                        if (enemies.get(i).isAlive() && !enemies.get(i).isVisible()) {
                            enemies.get(i).Activate(getMs());
                            count++;
                            if (count == 3) break;
                        }
                        if (enemies.get(i).isVisible()) visible++;
                        if (visible >= Constants.ENEMY_MAX_VISIBLE) break;
                    }
                    init_time = 0;
                }
                init_time++;
            }
        } else if(endcount==1 && game_mode!=Constants.GAME_MODE_CLIENT) {
            re.update(this);
        } else {
            mmenu.Update(this);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (!begin) {
            //	рисование танков игроков
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).isAlive()) users.get(i).render(batch);
            }

            //	рисование врагов
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).render(batch);
            }

            //	рисование патронов
            for (int i = 0; i < shls.size(); i++) {
                if (shls.get(i).isActive()) {
                    batch.draw(textureshell, shls.get(i).getPosition().x, shls.get(i).getPosition().y);
                }
            }
            //	рисование квадратов
            for (int i = 0; i < Constants.WINDOW_WIDTH; i += Constants.TILE_SIZE) {
                for (int j = 0; j < Constants.WINDOW_HEIGHT; j += Constants.TILE_SIZE) {
                    tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE].render(batch);
                }
            }

            //	рисование босса
            if(bb.isAlive()) bb.render(batch);

            //	рисование взрывов
            for (int i = 0; i < explosions.length; i++) {
                explosions[i].render(batch);
            }
            //	рисование бонусов
            for (Bonus b : bs) {
                b.render(batch);
            }
            //	рисование бонусов  -  иконок
            //if (tank.visible) tank.renderBonus(batch);
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).isAlive()) users.get(i).renderBonus(batch);
            }
            for (int i = 0; i < enemies.size(); i++) {
                enemies.get(i).renderBonus(batch);
            }
            //	рисование бонусов босса
            if(bb.isAlive()) bb.renderBonus(batch);
            //	рисование счёта
            sc.render(batch, users, game_type);

            //tx.render(batch, "DJOPPA", 300,300);
            if (endcount == 1) re.render(batch);     //  показать финальную картинку

        }
        if (pause || begin) mmenu.Render(batch, this);
        batch.end();
    }

    //  рассчёт движений объектов
    public void update() {
        if (endcount > 1) endcount--;

        //if (tank.isAlive()) tank.update(this);

        //  определение живых команд
        boolean player_alive = false, team2_alive=false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).isAlive()) {
                if (i == 0) users.get(i).update(this);
                else users.get(i).update_net_users(this);
                if(users.get(i).getTeamid()==1) player_alive = true;
                else team2_alive=true;
            }
        }
        //if(tank.isAlive()) player_alive=true;

        //  бонусы
        for (Bonus b : bs) {
            b.update(this);
            //b.CheckPick(tank, this);
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).isVisible()) b.CheckPick(users.get(i), this);
            }
        }

        //  определение живых врагов
        boolean any_alive = false;
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).active) {
                enemies.get(i).update(this);
                if (game_mode == Constants.GAME_MODE_SERVER)
                    if (enemies.get(i).getAction_delay() > 0)
                        sd.addEnemy(enemies.get(i).getPosition().x, enemies.get(i).getPosition().y, enemies.get(i).getOrient(), 0, 0, -1, i);
                    else
                        sd.addEnemy(enemies.get(i).getPosition().x, enemies.get(i).getPosition().y, enemies.get(i).getOrient(), (int) enemies.get(i).getDirection().x, (int) enemies.get(i).getDirection().y, -1, i);
                for (Bonus b : bs) b.CheckPick(enemies.get(i), this, i);
            }
            if (enemies.get(i).isAlive()) any_alive = true;
        }

        //  активировать босса
        if(bb.isAlive() && !bb.isActive()) bb.Activate();
        if(bb.isAlive()) {
            bb.update(this);
            for (Bonus b : bs) b.CheckPick(this);
        }

        //  включение экрана конца раунда, если надо
        if (game_mode != Constants.GAME_MODE_CLIENT) {
            if (game_type == Constants.GAME_TYPE_PvB) {
                if (((!any_alive && !bb.isAlive()) || !player_alive) && endcount == 0) {
                    endcount = Constants.END_DELAY_TIME;
                    if (!any_alive && !bb.isAlive()) {
                        re.setWin(1);
                        if(bb.getPosition().y==-1000) win_rounds++;
                    } else re.setWin(2);
                    if (game_mode == Constants.GAME_MODE_SERVER) {
                        if (!any_alive && !bb.isAlive()) getSd().SetStatus(Constants.GAME_ENDSTAGE_WIN);
                        else getSd().SetStatus(Constants.GAME_ENDSTAGE_FAIL);
                        log("Set end stage");
                        sendBroadcastMessage(sd);
                    }
                }
            } else {
                if((!player_alive || !team2_alive) && endcount == 0) {
                    endcount = Constants.END_DELAY_TIME;
                    if (!team2_alive) re.setWin(1); else re.setWin(2);
                    if (game_mode == Constants.GAME_MODE_SERVER) {
                        if (!team2_alive) getSd().SetStatus(Constants.GAME_ENDSTAGE_WIN);
                        else getSd().SetStatus(Constants.GAME_ENDSTAGE_FAIL);
                        log("Set end stage");
                        sendBroadcastMessage(sd);
                    }
                }

            }
        }

        //  взрывы
        for (int i = 0; i < explosions.length; i++) {
            if (explosions[i].isActive()) {
                explosions[i].update(this);
            }
        }

        //  снаряды
        ArrayList<shell> to_del=new ArrayList<shell>();
        for (int i = 0; i < shls.size(); i++) {
            if(!shls.get(i).update(this)) to_del.add(shls.get(i));
        }
        for (shell s: to_del) {
            shls.remove(s);
        }

//        if(game_mode==Constants.GAME_MODE_SERVER || game_mode==Constants.GAME_MODE_CLIENT) {
//            net_send_count++;
//            if(net_send_count>=Constants.GAME_NET_SEND_PERIOD) {
//                if(game_mode==Constants.GAME_MODE_SERVER) mserv.sendBroadcastMessage(sd);
//                else  mcl.sendMessage(sd);
//                net_send_count=0;
//            }
//        }
    }

    @Override
    public void dispose() {
        if(mserv!=null) mserv.Close();
        batch.dispose();
        textureshell.dispose();
        users.clear();
        mmenu.Dispose();
        tiles[0][0].dispose();
        Explosion.Dispose();
        Enemy.dispose();
        Bonus.dispose();
        sc.dispose();
        tx.dispose();
        ms.dispose();
        re.dispose();
        bb.dispose();
    }

    //  пауза
    public void paus(int p, boolean local) {
        if(p==1) pause=true;
        else if(p==2) pause=false;
        else if(p==0) pause = !pause;
        if (pause) {
            ms.pause_melody();
            getBb().pauseSound();
        }
        else if (!begin) {
            ms.play_melody();
            getBb().resumeSound();
        }
        if ((game_mode == Constants.GAME_MODE_SERVER || game_mode == Constants.GAME_MODE_CLIENT) && local) {
            if(pause) sd.SetStatus(Constants.GAME_PAUSE);
            else sd.SetStatus(Constants.GAME_UNPAUSE);
        }
        log("paus "+pause);
    }

    void InitEnemies(int count) {
        if (game_mode == Constants.GAME_MODE_SERVER) {
            setBegin(true);
            sendBroadcastMessage(sd);
            log("Broadcast manual begin=true");

            //  на всякий случай
            paus(2, true);
            sendBroadcastMessage(sd);
            log("Broadcast manual unpause");
        }

        ms.stop_melody();
        enemy_count = count;
        endcount=0;

        //  генерация расположения игроков
        int shift=Constants.WINDOW_WIDTH/(users.size()+1);
        for (int i = 0; i < users.size(); i++) {
            //users.get(i).SetPos(Constants.USER_INIT_X + 100 * (i + 1), Constants.USER_INIT_Y, Constants.USER_ORIENT, true, this);
            if(users.get(i).getTeamid()==2)
                users.get(i).SetPos(shift * (i + 1), Constants.WINDOW_HEIGHT-Constants.USER_INIT_Y, (Constants.USER_ORIENT == 0 || Constants.USER_ORIENT == 180) ? (Constants.USER_ORIENT + 180) % 360 : Constants.USER_ORIENT , true, this);
                else users.get(i).SetPos(shift * (i + 1), Constants.USER_INIT_Y, Constants.USER_ORIENT, true, this);
            users.get(i).rewrite(this, true);
            //sd.addTank(Constants.USER_INIT_X+100*(i+1), Constants.USER_INIT_Y, Constants.USER_ORIENT, users.get(i).getId());
        }

//        for (int i = 0; i < shls.length; i++) {
//            shls[i].disable();
//        }
        shls.clear();

        //  генерация типов тайлов
        for (int i = 0; i < Constants.WINDOW_WIDTH; i += Constants.TILE_SIZE) {
            for (int j = 0; j < Constants.WINDOW_HEIGHT; j += Constants.TILE_SIZE) {
                if (j < Constants.TILE_SIZE || j > Constants.WINDOW_HEIGHT-Constants.TILE_SIZE*2) {
                    tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE].rewrite(0);
                } else {
                    int t = Math.round((float) Math.random() * 5) - 1;
                    if (t < 0 || win_rounds==Constants.BOSS_WIN_ROUND) t = 0;
                    tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE].rewrite(t);
                }
            }
        }

        init_time = 0;
        //begin=true;
        enem_pos = 0;
        //net_send_count=Constants.GAME_NET_SEND_PERIOD;

        //  генерация врагов
        if (game_type == Constants.GAME_TYPE_PvB) {
            for (Enemy en : enemies) {
                if (en.getEngine_id() != -1) ms.stop("enemyengine", en.getEngine_id());
            }

            enemies.clear();
            bb.renew(500,-1000);

            if(win_rounds==Constants.BOSS_WIN_ROUND) {
                //bb=new BigBoss(300,300);
                //bb.renew(500,-1000);
                bb.setPos(Constants.WINDOW_WIDTH/2,Constants.WINDOW_HEIGHT+300);
                if (game_mode == Constants.GAME_MODE_SERVER) sd.addBoss(Float.valueOf(Constants.WINDOW_WIDTH/2),Float.valueOf(Constants.WINDOW_HEIGHT+300), Float.valueOf(180), Float.valueOf(180), 0f);
            } else {

                for (int i = 0; i < count; i++) {
                    Enemy en = new Enemy(Constants.TILE_SIZE/2 + enem_pos * (Constants.WINDOW_WIDTH-Constants.TILE_SIZE)/2, Constants.WINDOW_HEIGHT-Constants.TILE_SIZE/2, Math.round((float) Math.random() * 2) + 1);
                    enemies.add(en);
                    if (game_mode == Constants.GAME_MODE_SERVER)
                        sd.addEnemy(en.getPosition().x, en.getPosition().y, en.getOrient(), 0, 0, en.getType(), i);
                    enem_pos++;
                    if (enem_pos > 2) enem_pos = 0;
                }
            }
        }

        //  сброс бонусов
        for (Bonus b : bs) b.rewrite();

        re.renew();     //  обновление номера финальной картинки

        //  рассылка настроек клиентам при многопользовательской игре
        if (game_mode == Constants.GAME_MODE_SERVER) {
            sd.addTiles(tiles);
            sd.SetStatus(Constants.GAME_BEGIN);
            sendBroadcastMessage(sd);
            log("Broadcast manual cells, users, enemies");
//            try {
//                Thread.sleep(100);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            setBegin(false);
            sendBroadcastMessage(sd);
            log("Broadcast manual start round");
        }

        ms.play_melody();
        if(win_rounds==Constants.BOSS_WIN_ROUND) win_rounds=0;
    }

    //  генерация врагов на клиенте
    void InitEnemiesClient(int x, int y, int orient) {
        users.get(0).SetPos(x, y, orient, true, this);

        for (user us : users) {
            us.rewrite(this, false);
        }
        for (Bonus b : bs) b.ClientStop();

        ms.play_melody();
        //bb.renew(500,-1000);

        re.renew();     //  обновление номера финальной картинки
        endcount=0;
    }

    //  конец раунда на клиенте
    void StageClientEnd() {
        ms.stop_melody();
        getBb().stopSound();

//        for (int i = 0; i < shls.length; i++) {
//            shls[i].disable();
//        }
        shls.clear();

        for (Enemy en : enemies) {
            if (en.getEngine_id() != -1) ms.stop("enemyengine", en.getEngine_id());
        }
        log("Clear enemies");
        enemies.clear();
    }

    //  запуск взрыва
    public void StartExplosion(float x, float y, int i, boolean isuser) {
        for (int k = 0; k < explosions.length; k++) {
            if (!explosions[k].isActive()) {
                explosions[k].enable(x, y, i, isuser);
                if (!isuser && i == -1) getMs().play("explosion_boss");
                else getMs().play("explosion");
                break;
            }
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public Explosion[] getExplosions() {
        return explosions;
    }

    public Bonus[] getBs() {
        return bs;
    }

    public ArrayList<shell> getShls() {
        return shls;
    }

    public tile[][] getTiles() {
        return tiles;
    }

    public user getTank() {
        return users.get(0);
    }

    public boolean isBegin() {
        return begin;
    }

    public void setBegin(boolean begin) {
        this.begin = begin;
        if (begin) {
            if (ms != null) ms.stop_melody();
            if (sd != null) sd.SetStatus(Constants.GAME_STOP);
        } else if (sd != null) sd.SetStatus(Constants.GAME_START);
    }

    public boolean isPause() {
        return pause;
    }

    public Mysounds getMs() {
        return ms;
    }

    public int[][] GetTilesType() {
        int[][] ts = new int[tiles.length][tiles[0].length];
        for (int i = 0; i < Constants.WINDOW_WIDTH; i += Constants.TILE_SIZE) {
            for (int j = 0; j < Constants.WINDOW_HEIGHT; j += Constants.TILE_SIZE) {
                ts[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE] = tiles[i / Constants.TILE_SIZE][j / Constants.TILE_SIZE].getType();
            }
        }
        return ts;
    }

    public SendData getSd() {
        return sd;
    }

    public int getGame_mode() {
        return game_mode;
    }

    public void setGame_mode(int game_mode) {
        this.game_mode = game_mode;
    }

    public MyServer getMserv() {
        return mserv;
    }

    //  запуск сервера
    public boolean InitServer(int port, String username) {
        if (mserv != null) mserv.Close();
        //if(mserv.equals(null)) {
        mserv = new MyServer(port, this);
        sd = new SendData(Constants.WINDOW_WIDTH / Constants.TILE_SIZE, Constants.WINDOW_HEIGHT / Constants.TILE_SIZE, lock, this);
        users.get(0).setName(username);
        return mserv.getServer().isBound();
        //}
        //return false;
    }

    //  запуск клиента
    public boolean InitClient(String server, int port, String username) {
        if (mcl != null) mcl.Close();
        mcl = new MyClient(server, port, this);
        if (mcl.getSock() == null) return false;

        sd = new SendData(Constants.WINDOW_WIDTH / Constants.TILE_SIZE, Constants.WINDOW_HEIGHT / Constants.TILE_SIZE, lock, this);
        sd.addTank(users.get(0).getPosition().x, users.get(0).getPosition().y, users.get(0).getOrient(), users.get(0).getId());
        log("Add tank (InitClient) "+getClass().getName());
        users.get(0).setName(username);
        sd.addName(users.get(0).getId(), users.get(0).getName());
        mcl.sendMessage(sd);
        sd.ClearData();                             //   чтобы эти данные при следующей отправке не вылезли.

        return mcl.getSock().isConnected();

    }

    public OptForm getOpf() {
        return opf;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public ArrayList<user> getUsers() {
        return users;
    }

    //  отправить всем текущее расположение объектов
    public void sendBroadcastMessage(final SendData senddata) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                mserv.sendBroadcastMessage(senddata);
            }
        });
        th.start();
        //System.out.println("Broadcast manual");
        //log("Broadcast manual");
        try {
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Text getTx() {
        return tx;
    }

    public void log(String message, Level level) {
        logger.log(level, message);
    }

    public void log(String message) {
        logger.log(Level.INFO, message);
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public int getGame_type() {
        return game_type;
    }

    public void setGame_type(int game_type) {
        this.game_type = game_type;
    }

    public void DeleteUsers() {
        List<user> to_remove = new ArrayList<user>();
        for (int i = 1; i < getUsers().size(); i++) {
            to_remove.add(getUsers().get(i));
        }
        for (user c : to_remove) getUsers().remove(c);
    }

    public void setEnemy_count(int enemy_count) {
        this.enemy_count = enemy_count;
    }

    public int getEnemy_count() {
        return enemy_count;
    }

    public void setEndcount(int endcount) {
        this.endcount = endcount;
    }

    public RoundEnd getRe() {
        return re;
    }

    public void AddShell(float x, float y, int dx, int dy, String h, int fp) {
        shell s=new shell(x, y, dx, dy, h, fp, this);
        shls.add(s);
        getMs().play("shot");
    }

    public void AddShell(float x, float y, double dx, double dy, String h, int fp) {
        shell s=new shell(x, y, dx, dy, h, fp, this);
        shls.add(s);
        getMs().play("shot");
    }

    public Texture getTextureshell() {
        return textureshell;
    }

    public BigBoss getBb() {
        return bb;
    }

    public MainMenu getMmenu() {
        return mmenu;
    }
}
