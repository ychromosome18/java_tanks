/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class Enemy {
    private Vector2 position;
    private Vector2 direction;
    private Vector2 last_tile;
    private Rectangle rect;
    private float speed;
    private int action_delay;
    private int orient;
    private int fireRate;
    private int fireCounter;
    private int texwid, texhei;
    private int fire_power;
    //private int deltax,deltay;
    private int type;
    int durability;
    boolean active;
    boolean visible, killed;
    private Queue<Object> queueDE;
    private Queue<Object> queueShot;
    private ArrayDeque<Integer[]> path;
    private static Texture tex_average = null;
    private static Texture tex_fast = null;
    private static Texture tex_big = null;
    private ArrayList<Integer[]> bon_img = null;
    private Spark sprk;
    private long engine_id;

    public Enemy(float x, float y, int tp) {
        if (tex_average == null) tex_average = new Texture("enemy_average.png");
        if (tex_fast == null) tex_fast = new Texture("enemy_fast.png");
        if (tex_big == null) tex_big = new Texture("enemy_big.png");
        position = new Vector2(x, y);
        direction = new Vector2(0, 0);
        last_tile = new Vector2();
        rect = new Rectangle(x - tex_average.getWidth() / 2, y - tex_average.getHeight() / 2, tex_average.getWidth(), tex_average.getHeight());

        type = tp;
        fireCounter = 40;
        fireRate = Constants.ENEMY_FIRE_RATE;
        fire_power = Constants.ENEMY_FIRE_POWER;
        active = false;
        visible = false;
        killed = false;
        texwid = tex_average.getWidth();
        texhei = tex_average.getHeight();
        orient = 180;
        action_delay = 0;
        queueShot = new LinkedList<Object>();
        queueDE = new LinkedList<Object>();
        bon_img = new ArrayList<Integer[]>();
        path = new ArrayDeque<Integer[]>();
        engine_id = -1;

        switch (type) {
            case 1:     //fast
                speed = Constants.ENEMY_FAST_SPEED;
                durability = Constants.ENEMY_FAST_DURABILITY;
                //action_delay=0;
                break;
            case 2:     //moderate
                speed = Constants.ENEMY_MODERATE_SPEED;
                durability = Constants.ENEMY_MODERATE_DURABILITY;
                //action_delay=0;
                break;
            case 3:     //tough
                speed = Constants.ENEMY_TOUGH_SPEED;
                durability = Constants.ENEMY_TOUGH_DURABILITY;
                //action_delay=0;
                break;
        }
        sprk = new Spark();
    }

    public void Activate(Mysounds ms) {
        active = true;
        visible = true;
        if (ms != null) sprk.enable(position.x, position.y, ms);
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAlive() {
        return !killed;
    }

    public boolean isVisible() {
        return visible;
    }

    public void Deactivate() {
        active = false;
        killed = true;
        if (sprk.isActive()) sprk.setActive(false);
    }

    public void Hide() {
        visible = false;
        //killed=true;
    }

    public void render(SpriteBatch batch) {
        if (sprk.isActive()) {
            sprk.render(batch);
        } else {
            if (visible) {
                switch (type) {
                    case 1:
                        batch.draw(tex_fast, position.x - texwid / 2, position.y - texhei / 2, texwid / 2, texhei / 2, texwid, texhei, 1, 1, orient, 0, 0, texwid, texhei, false, false);
                        break;
                    case 2:
                        batch.draw(tex_average, position.x - texwid / 2, position.y - texhei / 2, texwid / 2, texhei / 2, texwid, texhei, 1, 1, orient, 0, 0, texwid, texhei, false, false);
                        break;
                    case 3:
                        batch.draw(tex_big, position.x - texwid / 2, position.y - texhei / 2, texwid / 2, texhei / 2, texwid, texhei, 1, 1, orient, 0, 0, texwid, texhei, false, false);
                        break;
                }
            }
        }
    }

    public void renderBonus(SpriteBatch batch) {
        if (visible) {
            for (int i = 0; i < bon_img.size(); i++) {
                Bonus.GetSprite(bon_img.get(i)[0]).setSize(1f*Constants.TILE_SIZE / Constants.BONUS_SMALL_SCALE, 1f*Constants.TILE_SIZE / Constants.BONUS_SMALL_SCALE);
                if (bon_img.get(i)[1] < 300)
                    Bonus.GetSprite(bon_img.get(i)[0]).setAlpha(0.5f + ((float) (bon_img.get(i)[1] % Constants.TILE_SIZE)) / 100);
                else Bonus.GetSprite(bon_img.get(i)[0]).setAlpha(1f);
                Bonus.GetSprite(bon_img.get(i)[0]).setPosition(position.x + i * Constants.TILE_SIZE*1f / Constants.BONUS_SMALL_SCALE + texwid / 2, position.y + texhei / 2);
                Bonus.GetSprite(bon_img.get(i)[0]).draw(batch);
                Bonus.GetSprite(bon_img.get(i)[0]).setSize(1f*Constants.TILE_SIZE, 1f*Constants.TILE_SIZE);
            }
        }
    }

    public void fire(MyGdxGame mg) {
                switch (orient) {
                    case 0:
                        mg.AddShell(position.x - mg.getTextureshell().getWidth()/2, position.y + texhei / 2, 0, 1, null, fire_power);
                        break;
                    case 90:
                        mg.AddShell(position.x - texhei / 2, position.y - mg.getTextureshell().getHeight()/2, -1, 0, null, fire_power);
                        break;
                    case 180:
                        mg.AddShell(position.x - mg.getTextureshell().getWidth()/2, position.y - texhei / 2, 0, -1, null, fire_power);
                        break;
                    case 270:
                        mg.AddShell(position.x + texhei / 2, position.y - mg.getTextureshell().getHeight()/2, 1, 0, null, fire_power);
                        break;
                }
    }

    public boolean AlreadyShot(int px, int py, int porient) {
        for (Object item : queueShot) {
            int[] a = ((int[]) item);
            if (a[0] == px && a[1] == py && a[2] == porient) return true;
        }
        return false;
    }

    public void RememberShot(int px, int py, int porient) {
        int[] ps2 = new int[]{px, py, porient};
        if (queueShot.size() > 2) queueShot.poll();
        queueShot.add(ps2);
    }

    public void update(MyGdxGame mg) {
        if (sprk.isActive()) {
            sprk.update();
        } else if (mg.getGame_mode() == Constants.GAME_MODE_CLIENT) {
            int nextx = (int) (position.x + direction.x * (speed + texwid / 2)) / Constants.TILE_SIZE;
            int nexty = (int) (position.y + direction.y * (speed + texhei / 2)) / Constants.TILE_SIZE;
            if (nextx < 0 || nextx > mg.getTiles().length - 1) direction.x = 0;
            else if (nexty < 0 || nexty > mg.getTiles()[0].length - 1) direction.y = 0;
            else if (!mg.getTiles()[nextx][nexty].isPass()) direction.set(0, 0);

            position.mulAdd(direction, speed);
        } else {
            if (action_delay > 0) {
                action_delay--;
            } else {
                user tank = mg.getTank();
                tile[][] tiles = mg.getTiles();

                Integer[] to_remove = null;
                for (Integer[] item : bon_img) {
                    if (item[1] > 0) {
                        item[1]--;
                    } else {
                        to_remove = item;
                        mg.getBs()[to_remove[0]].DeBonus(this, mg);
                    }
                }
                bon_img.remove(to_remove);

                //Boolean decision = false;
                if (position.x <= 0) {
                    position.x = texwid / 2;
                    direction.x = 1;
                } else if (position.x >= Constants.WINDOW_WIDTH) {
                    position.x = Constants.WINDOW_WIDTH - texwid / 2;
                    direction.x = -1;
                } else if (position.y <= 0) {
                    position.y = texhei / 2;
                    direction.y = 1;
                } else if (position.y >= Constants.WINDOW_HEIGHT) {
                    position.y = Constants.WINDOW_HEIGHT - texhei / 2;
                    direction.y = -1;
                }

                int locx = (int) position.x / Constants.TILE_SIZE;
                int locy = (int) position.y / Constants.TILE_SIZE;
                //int distx = (int) (position.x - tank.position.x);
                //int disty = (int) (position.y - tank.position.y);
                //FindPath(tiles, (int) tank.position.x / 50, (int) tank.position.y / 50);

                fireCounter++;
                //if(position.y<=texhei/2 && direction.y==-1) direction.y=0;
                position.mulAdd(direction, speed);
                rect.setCenter(position.x, position.y);
                //if(mg.getGame_mode()==Constants.GAME_MODE_SERVER)  mg.getSd().addTank(position.x, position.y, orient, id);

                if (direction.x != 0 || direction.y != 0) {
                    if (engine_id == -1) engine_id = mg.getMs().loop("enemyengine");
                    else mg.getMs().resume("enemyengine", engine_id);
                } else {
                    if (engine_id != -1) mg.getMs().pause("enemyengine", engine_id);
                }

                if (Math.abs(tiles[locx][locy].getPosition().x + Constants.TILE_SIZE/2 - position.x) <= speed / 2 && Math.abs(tiles[locx][locy].getPosition().y + Constants.TILE_SIZE/2 - position.y) <= speed / 2) {
                    int dist = 10000, distx = 0, disty = 0, us_dist;
                    if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) {
                        for (user usr : mg.getUsers()) {
                            if (usr.isAlive()) {
                                us_dist = Math.abs((int) (position.x - usr.getPosition().x)) + Math.abs((int) (position.y - usr.getPosition().y));
                                if (dist > us_dist) {
                                    dist = us_dist;
                                    distx = (int) usr.getPosition().x / Constants.TILE_SIZE;
                                    disty = (int) usr.getPosition().y / Constants.TILE_SIZE;
                                }
                            }
                        }
                    }

                    if (tank.isAlive()) {
                        us_dist = Math.abs((int) (position.x - tank.getPosition().x)) + Math.abs((int) (position.y - tank.getPosition().y));
                        if (dist > us_dist) {
                            distx = (int) tank.getPosition().x / Constants.TILE_SIZE;
                            disty = (int) tank.getPosition().y / Constants.TILE_SIZE;
                        }
                    }

                    if (!FindPath(tiles, distx, disty)) {
                        //something random
                    }
                    if (!path.isEmpty()) {
                        Integer[] p = path.getFirst();
                        if (p[0] == locx && p[1] == locy) {
                            path.pop();
                            p = path.getFirst();
                        }
                        if (p[0] < locx) {
                            direction.set(-1, 0);
                            orient = 90;
                        } else if (p[0] > locx) {
                            direction.set(1, 0);
                            orient = 270;
                        } else if (p[1] < locy) {
                            direction.set(0, -1);
                            orient = 180;
                        } else if (p[1] > locy) {
                            direction.set(0, 1);
                            orient = 0;
                        }
                        if (path.size() == 1 || !tiles[locx + (int) direction.x][locy + (int) direction.y].isPass()) {
                            direction.set(0, 0);
                        }

                    }

                    if (fireCounter >= fireRate /*&& !AlreadyShot(locx, locy, orient)*/) {                                                   // пиф-паф
                        fireCounter = 0;
                        fire(mg);
                        RememberShot(locx, locy, orient);
                        action_delay = Constants.ENEMY_ACTION_DELAY;
                    }
                } else if(direction.x==0 && direction.y==0 && position.y==Constants.WINDOW_HEIGHT-Constants.TILE_SIZE/2) {
                    direction.x = -1;
                    orient = 90;
                }
            }
        }
    }

    static public void dispose() {
        if (tex_average != null) tex_average.dispose();
        if (tex_fast != null) tex_fast.dispose();
        if (tex_big != null) tex_big.dispose();

    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability, MyGdxGame mg) {
        this.durability = durability;
        if (durability < 1) {
            mg.StartExplosion(position.x, position.y, mg.getEnemies().indexOf(this), false);
            Deactivate();
            if (engine_id != -1) mg.getMs().stop("enemyengine", engine_id);
        }
    }

    public void setDurability(int durability, MyGdxGame mg, String id) {
        if (durability < 1) {
            if (id != null) {
                for (user t : mg.getUsers()) {
                    if (t.getId().equals(id)) t.AddKills(getType());
                }
            }
            if (mg.getGame_mode() != Constants.GAME_MODE_SINGLE)
                mg.getSd().add_enemy_hit(mg.getEnemies().indexOf(this), id);
        }
        setDurability(durability, mg);
    }

    public int getFireRate() {
        return fireRate;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public int getFire_power() {
        return fire_power;
    }

    public void setFire_power(int fire_power) {
        this.fire_power = fire_power;
    }

    public void AddBonus(int b) {
        bon_img.add(new Integer[]{b, Constants.BONUS_ACT_TIME});
    }

    public int getType() {
        return type;
    }

    public Rectangle getRect() {
        return rect;
    }

    public Vector2 getPosition() {
        return position;
    }

    public static Texture getTex_fast() {
        return tex_fast;
    }

    public static Texture getTex_big() {
        return tex_big;
    }

    public static Texture getTex_average() {
        return tex_average;
    }

    public static void InitTextures() {
        if (tex_average == null) tex_average = new Texture("enemy_average.png");
        if (tex_fast == null) tex_fast = new Texture("enemy_fast.png");
        if (tex_big == null) tex_big = new Texture("enemy_big.png");
    }

    public long getEngine_id() {
        return engine_id;
    }

    public int getOrient() {
        return orient;
    }

    public void SetPos(int x, int y, int orient, int dx, int dy, int type, Mysounds ms) {
        if (!killed) {
            position.x = x;
            position.y = y;
            rect.setCenter(position.x, position.y);
            this.orient = orient;
            direction.x = dx;
            direction.y = dy;
            if (type == -1 && !active) Activate(ms);
        }
    }

    public Vector2 getDirection() {
        return direction;
    }

    public int getAction_delay() {
        return action_delay;
    }

    public boolean FindPath(tile[][] til, int fx, int fy) {
        int maxx = til.length;
        int maxy = til[0].length;
        int[][] t = new int[maxx][maxy];
        boolean path_finded = false;

        for (int i = 0; i < maxx; i++) {
            Arrays.fill(t[i], -1);
        }
        t[(int) position.x / Constants.TILE_SIZE][(int) position.y / Constants.TILE_SIZE] = 0;

        for (int k = 0; k < maxx * 2 + maxy; k++) {
            for (int i = 0; i < maxx; i++) {
                for (int j = 0; j < maxy; j++) {

                    if (t[i][j] != -1) {
                        if (i == fx && j == fy) {
                            path_finded = true;
                            break;
                        }
                        if (i > 0) {
                            if (til[i - 1][j].isPassorDestroy() && t[i - 1][j] == -1) t[i - 1][j] = t[i][j] + 1;
                        }
                        if (i < maxx - 1) {
                            if (til[i + 1][j].isPassorDestroy() && t[i + 1][j] == -1) t[i + 1][j] = t[i][j] + 1;
                        }
                        if (j > 0) {
                            if (til[i][j - 1].isPassorDestroy() && t[i][j - 1] == -1) t[i][j - 1] = t[i][j] + 1;
                        }
                        if (j < maxy - 1) {
                            if (til[i][j + 1].isPassorDestroy() && t[i][j + 1] == -1) t[i][j + 1] = t[i][j] + 1;
                        }

                    }
                }
                if (path_finded) break;
            }
            if (path_finded) break;
        }
        if (path_finded) {
//            System.out.println();
//            for (int i = 0; i < maxx; i++) {
//                for (int j = 0; j < maxy; j++) {
//                    System.out.print("  "+t[i][j]);
//                }
//                System.out.println();
//            }
            //Queue<Integer[]> path = new LinkedList<Integer[]>();
            path.clear();
            int qx = fx, qy = fy;
            Integer p[] = {qx, qy};
            path.push(p);
            for (int i = 0; i < maxx * 2 + maxy; i++) {
                if (t[qx][qy] == 1) break;
                if (qx > 0) {
                    if (t[qx - 1][qy] + 1 == t[qx][qy]) {
                        Integer pos[] = {qx - 1, qy};
                        path.push(pos);
                        qx--;
                        continue;
                    }
                }
                if (qx < maxx - 1) {
                    if (t[qx + 1][qy] + 1 == t[qx][qy]) {
                        Integer pos[] = {qx + 1, qy};
                        path.push(pos);
                        qx++;
                        continue;
                    }
                }
                if (qy > 0) {
                    if (t[qx][qy - 1] + 1 == t[qx][qy]) {
                        Integer pos[] = {qx, qy - 1};
                        path.push(pos);
                        qy--;
                        continue;
                    }
                }
                if (qy < maxy - 1) {
                    if (t[qx][qy + 1] + 1 == t[qx][qy]) {
                        Integer pos[] = {qx, qy + 1};
                        path.push(pos);
                        qy++;
                        continue;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
