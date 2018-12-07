/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class user {
    private static Texture tex_average = null;
    private static Texture tex_fast = null;
    private static Texture tex_big = null;
    Vector2 position;
    Rectangle rect;
    private float speed;
    private int orient;
    private int fireRate;
    private int durability;
    private int fire_power;
    private int fireCounter;
    int texwid, texhei;
    private int teamid;
    private int kills[];
    private boolean alive;
    boolean visible;
    private ArrayList<Integer[]> bon_img = null;
    private ArrayDeque<Integer[]> path;
    private Spark sprk;
    private long engine_id;
    private long last_touched;
    private Vector2 last_touch_pos;
    private String id, name;
    private Text tx;

    public user(MyGdxGame mg, String id) {
        if (tex_average == null) {
            tex_average = new Texture("user_average.png");
            tex_fast = new Texture("user_fast.png");
            tex_big = new Texture("user_big.png");
        }
        if (id == null) {
            this.id = UUID.randomUUID().toString();
            //name=System.getProperty("user.name");
        } else this.id = id;
        sprk = new Spark();
        kills = new int[3];
        texwid = tex_average.getWidth();
        texhei = tex_average.getHeight();
        engine_id = -1;
        position = new Vector2(Constants.USER_INIT_X, Constants.USER_INIT_Y);
        mg.log("Set tank default position " + this.getClass().getName());
        rewrite(mg, true);
        rect = new Rectangle(position.x - texwid / 2, position.y - texhei / 2, texwid, texhei);
        bon_img = new ArrayList<Integer[]>();
        path = new ArrayDeque<Integer[]>();
        this.tx = mg.getTx();
        last_touched = 0;
        last_touch_pos = new Vector2(0, 0);
        setTeamid(1);
    }

    public void rewrite(MyGdxGame mg, boolean forsend) {
        //position = new Vector2(Constants.USER_INIT_X, Constants.USER_INIT_Y);
        //if(mg.getGame_mode()==Constants.GAME_MODE_SINGLE) position.set(Constants.USER_INIT_X, Constants.USER_INIT_Y);
        sprk.enable(position.x, position.y, mg.getMs());
        alive = true;
        visible = true;
        fireRate = Constants.USER_FIRE_RATE;
        fireCounter = Constants.USER_FIRE_RATE;
        fire_power = Constants.USER_FIRE_POWER;
        durability = Constants.USER_DURABILITY;
        speed = Constants.USER_SPEED;
        if (bon_img != null) bon_img.clear();
        //  не сбрасывать счёт для PvP
        if (mg.getGame_type() != Constants.GAME_TYPE_PvP) kills[0] = kills[1] = kills[2] = 0;
        if (engine_id != -1) mg.getMs().stop("engine", engine_id);
        engine_id = -1;
        if (mg.getGame_mode() == Constants.GAME_MODE_SERVER && forsend) {
            mg.getSd().addTank(position.x, position.y, orient, id);
            mg.log("Add tank (rewrite) " + position.x + " " + getClass().getName());
            if (name != null) mg.getSd().addName(id, name);
        }

    }

    public void render(SpriteBatch batch) {
        if (sprk.isActive()) {
            sprk.render(batch);
        } else {
            Color cl= batch.getColor();
            if(teamid==2) batch.setColor(Constants.TEAM_2_COLOR);

            switch (durability) {
                case 1:
                    batch.draw(tex_average, position.x - texwid / 2, position.y - texhei / 2, texwid / 2, texhei / 2, texwid, texhei, 1, 1, orient, 0, 0, texwid, texhei, false, false);
                    break;
                case 2:
                    batch.draw(tex_fast, position.x - texwid / 2, position.y - texhei / 2, texwid / 2, texhei / 2, texwid, texhei, 1, 1, orient, 0, 0, texwid, texhei, false, false);
                    break;
                default:
                    batch.draw(tex_big, position.x - texwid / 2, position.y - texhei / 2, texwid / 2, texhei / 2, texwid, texhei, 1, 1, orient, 0, 0, texwid, texhei, false, false);
                    break;
            }
            if(teamid==2) batch.setColor(cl);
        }
    }

    public void renderBonus(SpriteBatch batch) {
        if (!sprk.isActive()) {
            for (int i = 0; i < bon_img.size(); i++) {
                Bonus.GetSprite(bon_img.get(i)[0]).setSize(50f / Constants.BONUS_SMALL_SCALE, 50f / Constants.BONUS_SMALL_SCALE);
                if (bon_img.get(i)[1] < 300)
                    Bonus.GetSprite(bon_img.get(i)[0]).setAlpha(0.5f + ((float) (bon_img.get(i)[1] % 50)) / 100);
                else Bonus.GetSprite(bon_img.get(i)[0]).setAlpha(1f);
                Bonus.GetSprite(bon_img.get(i)[0]).setPosition(position.x + i * 50f / Constants.BONUS_SMALL_SCALE + texwid / 2, position.y + texhei / 2);
                Bonus.GetSprite(bon_img.get(i)[0]).draw(batch);
                Bonus.GetSprite(bon_img.get(i)[0]).setSize(50f, 50f);
            }
            if (name != null)
                tx.render(batch, name, (int) (position.x + texwid / 2), (int) (position.y - texhei / 2) + 12, 1f, Color.CYAN);
        }
    }

    public void dispose() {
        if (tex_average != null) {
            tex_average.dispose();
            tex_fast.dispose();
            tex_big.dispose();
        }
        Spark.Dispose();
    }

    public void Hide() {
        visible = false;
    }

    public void update_net_users(MyGdxGame mg) {
        if (sprk.isActive()) {
            sprk.update();
        } else {
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
        }
    }

    public void update(MyGdxGame mg) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            mg.paus(0, true);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.PLUS) || Gdx.input.isKeyPressed(Input.Keys.EQUALS) || Gdx.input.isKeyPressed(Input.Keys.VOLUME_UP)) {
            mg.getMs().setVolume(mg.getMs().getVolume()+0.05f, mg);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.MINUS) || Gdx.input.isKeyPressed(Input.Keys.VOLUME_DOWN)) {
            mg.getMs().setVolume(mg.getMs().getVolume()-0.05f, mg);
        }

        if (sprk.isActive()) {
            sprk.update();
        } else {
            tile[][] tiles = mg.getTiles();
            //shell[] shls = mg.getShls();
            boolean move = false, pressed = false;
            int cur_width = texwid / 2, cur_height = texhei / 2;
            fireCounter++;

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
            //mg.log("x="+String.valueOf(position.x)+" y="+String.valueOf(position.y));


            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                position.x -= speed;
                orient = 90;
                move = true;
                cur_width = texhei / 2;
                cur_height = texwid / 2;
                pressed = true;
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) && !pressed) {
                position.x += speed;
                orient = 270;
                move = true;
                cur_width = texhei / 2;
                cur_height = texwid / 2;
                pressed = true;
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) && !pressed) {
                position.y -= speed;
                orient = 180;
                move = true;
                cur_width = texwid / 2;
                cur_height = texhei / 2;
                pressed = true;
            }
            if ((Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) && !pressed) {
                position.y += speed;
                orient = 0;
                move = true;
                cur_width = texwid / 2;
                cur_height = texhei / 2;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.L) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                fire(mg);
            }
//            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
//                mg.paus(0,true);
//            }
            if (Gdx.input.isTouched()/*Gdx.input.isButtonPressed(Input.Buttons.LEFT)*/) {
                float deltax = Gdx.input.getX() - position.x;
                float deltay = Gdx.graphics.getHeight() - Gdx.input.getY() - position.y;
                if (Math.abs(deltax) > Math.abs(deltay)) {
                    if (deltax > 0) orient = 270;
                    else orient = 90;
                    fire(mg);
                } else {
                    if (deltay > 0) orient = 0;
                    else orient = 180;
                    fire(mg);
                }
            }

            if (Gdx.input.justTouched()) {
                if ((System.currentTimeMillis() - last_touched) < Constants.GAME_DOUBLE_TOUCH_DELAY && last_touch_pos.dst(Gdx.input.getX(), Gdx.input.getY()) < Constants.GAME_DOUBLE_TOUCH_DELTA) {
                    FindPath(mg.getTiles(), Gdx.input.getX() / 50, (Gdx.graphics.getHeight() - Gdx.input.getY()) / 50);
                }
                last_touched = System.currentTimeMillis();
                last_touch_pos.set(Gdx.input.getX(), Gdx.input.getY());
            }

            if (position.x < cur_width) position.x = cur_width;
            if (position.x > Constants.WINDOW_WIDTH - cur_width) position.x = Constants.WINDOW_WIDTH - cur_width;
            if (position.y < cur_height) position.y = cur_height;
            if (position.y > Constants.WINDOW_HEIGHT - cur_height) position.y = Constants.WINDOW_HEIGHT - cur_height;
            if (move) {
                if (engine_id == -1) engine_id = mg.getMs().loop("engine");
                else mg.getMs().resume("engine", engine_id);

                int my_tile_x = (int) position.x / 50;
                int my_tile_y = (int) position.y / 50;
                switch (orient) {           //  выравнивание для въезда между тайлами
                    case 270:
                        if (my_tile_x < tiles.length - 1 && my_tile_y > 0 && my_tile_y < tiles[0].length - 1) {
                            if (!tiles[my_tile_x + 1][my_tile_y - 1].isPass() && !tiles[my_tile_x + 1][my_tile_y + 1].isPass() && tiles[my_tile_x + 1][my_tile_y].isPass()) {
                                if (Math.abs(position.y - tiles[my_tile_x][my_tile_y].getPosition().y - 25) < speed * 5 && (position.x - tiles[my_tile_x][my_tile_y].getPosition().x - 25) <= speed * 2 && position.x > tiles[my_tile_x][my_tile_y].getPosition().x + 25) {
                                    position.y = tiles[my_tile_x][my_tile_y].getPosition().y + 25;
                                }
                            }
                        }
                        break;
                    case 90:
                        if (my_tile_x > 0 && my_tile_y > 0 && my_tile_y < tiles[0].length - 1) {
                            if (!tiles[my_tile_x - 1][my_tile_y - 1].isPass() && !tiles[my_tile_x - 1][my_tile_y + 1].isPass() && tiles[my_tile_x - 1][my_tile_y].isPass()) {
                                if (Math.abs(position.y - tiles[my_tile_x][my_tile_y].getPosition().y - 25) < speed * 5 && (tiles[my_tile_x][my_tile_y].getPosition().x + 25 - position.x) <= speed * 2 && position.x < tiles[my_tile_x][my_tile_y].getPosition().x + 25) {
                                    position.y = tiles[my_tile_x][my_tile_y].getPosition().y + 25;
                                }
                            }
                        }
                        break;
                    case 0:
                        if (my_tile_x > 0 && my_tile_x < tiles.length - 1 && my_tile_y < tiles[0].length - 1) {
                            if (!tiles[my_tile_x - 1][my_tile_y + 1].isPass() && !tiles[my_tile_x + 1][my_tile_y + 1].isPass() && tiles[my_tile_x][my_tile_y + 1].isPass()) {
                                if (Math.abs(position.x - tiles[my_tile_x][my_tile_y].getPosition().x - 25) < speed * 5 && (position.y - tiles[my_tile_x][my_tile_y].getPosition().y - 25) <= speed * 2 && position.y > tiles[my_tile_x][my_tile_y].getPosition().y + 25) {
                                    position.x = tiles[my_tile_x][my_tile_y].getPosition().x + 25;
                                }
                            }
                        }
                        break;
                    case 180:
                        if (my_tile_x > 0 && my_tile_x < tiles.length - 1 && my_tile_y > 0) {
                            if (!tiles[my_tile_x - 1][my_tile_y - 1].isPass() && !tiles[my_tile_x + 1][my_tile_y - 1].isPass() && tiles[my_tile_x][my_tile_y - 1].isPass()) {
                                if (Math.abs(position.x - tiles[my_tile_x][my_tile_y].getPosition().x - 25) < speed * 5 && (tiles[my_tile_x][my_tile_y].getPosition().y + 25 - position.y) <= speed * 2 && position.y < tiles[my_tile_x][my_tile_y].getPosition().y + 25) {
                                    position.x = tiles[my_tile_x][my_tile_y].getPosition().x + 25;
                                }
                            }
                        }
                        break;
                }

                boolean br = false;
                for (int k = 0; k < Constants.WINDOW_WIDTH; k += 50) {
                    for (int j = 0; j < Constants.WINDOW_HEIGHT; j += 50) {
                        if (!tiles[k / 50][j / 50].isPass()) {
                            if (position.x + cur_width > tiles[k / 50][j / 50].getPosition().x && position.x - cur_width < tiles[k / 50][j / 50].getPosition().x + 50 && position.y + cur_height > tiles[k / 50][j / 50].getPosition().y && position.y - cur_height < tiles[k / 50][j / 50].getPosition().y + 50) {
                                switch (orient) {
                                    case 270:
                                        if (speed > Constants.USER_SPEED)
                                            position.x = tiles[k / 50][j / 50].getPosition().x - cur_width;
                                        else position.x -= speed;
                                        break;
                                    case 90:
                                        if (speed > Constants.USER_SPEED)
                                            position.x = tiles[k / 50][j / 50].getPosition().x + cur_width + tiles[k / 50][j / 50].getWidth();
                                        else position.x += speed;
                                        break;
                                    case 0:
                                        if (speed > Constants.USER_SPEED)
                                            position.y = tiles[k / 50][j / 50].getPosition().y - cur_width;
                                        else position.y -= speed;
                                        break;
                                    case 180:
                                        if (speed > Constants.USER_SPEED)
                                            position.y = tiles[k / 50][j / 50].getPosition().y + cur_width + tiles[k / 50][j / 50].getHeight();
                                        else position.y += speed;
                                        break;
                                }

                                br = true;
                                break;
                            }
                        }
                    }
                    if (br) break;
                }
                if (mg.getGame_mode() == Constants.GAME_MODE_SERVER || mg.getGame_mode() == Constants.GAME_MODE_CLIENT) {
                    mg.getSd().addTank(position.x, position.y, orient, id);
                    mg.log("Add tank (update) " + position.x + " " + getClass().getName());
                }
            } else {
                if (!path.isEmpty()) {
                    if (engine_id == -1) engine_id = mg.getMs().loop("engine");
                    else mg.getMs().resume("engine", engine_id);
                    int my_tile_x = 0, my_tile_y = 0;
                    switch (orient) {
                        case 0:
                            my_tile_x = (int) position.x / 50;
                            my_tile_y = (int) (position.y - 25) / 50;
                            break;
                        case 90:
                            my_tile_x = (int) (position.x + 25) / 50;
                            my_tile_y = (int) position.y / 50;
                            break;
                        case 180:
                            my_tile_x = (int) position.x / 50;
                            my_tile_y = (int) (position.y + 25) / 50;
                            break;
                        case 270:
                            my_tile_x = (int) (position.x - 25) / 50;
                            my_tile_y = (int) position.y / 50;
                            break;
                    }

                    Integer[] p = path.getFirst();
                    if (p[0] == my_tile_x && p[1] == my_tile_y) {
                        path.pop();
                        if (!path.isEmpty()) p = path.getFirst();
                    }
                    if (p[0] < my_tile_x) {
                        position.x -= speed;
                        orient = 90;
                    } else if (p[0] > my_tile_x) {
                        position.x += speed;
                        orient = 270;
                    } else if (p[1] < my_tile_y) {
                        position.y -= speed;
                        orient = 180;
                    } else if (p[1] > my_tile_y) {
                        position.y += speed;
                        orient = 0;
                    }

                } else {
                    if (engine_id != -1) mg.getMs().pause("engine", engine_id);
                }
            }
            rect.setCenter(position.x, position.y);
        }
    }


    public void fire(MyGdxGame mg) {
        if (fireCounter >= fireRate) {
            fireCounter = 0;
            if (orient == 0) mg.AddShell(position.x - 4, position.y + 23, 0, 1, id, fire_power);
            if (orient == 90) mg.AddShell(position.x - 23, position.y - 4, -1, 0, id, fire_power);
            if (orient == 180) mg.AddShell(position.x - 4, position.y - 23, 0, -1, id, fire_power);
            if (orient == 270) mg.AddShell(position.x + 23, position.y - 4, 1, 0, id, fire_power);
        }
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
            mg.StartExplosion(position.x, position.y, mg.getUsers().indexOf(this), true);
            alive = false;
            if (engine_id != -1) mg.getMs().stop("engine", engine_id);
        }
    }

    public void setDurability(int durability, MyGdxGame mg, String killer_id) {
        if (durability < 1) {
            if (id != null) {
                for (user t : mg.getUsers()) {
                    if (t.getId().equals(killer_id)) t.AddKills(2);
                }
            }
            if (mg.getGame_mode() != Constants.GAME_MODE_SINGLE)
                mg.getSd().add_tank_hit(this.id, killer_id);
        }
        setDurability(durability, mg);
    }

    public int getFireRate() {
        return fireRate;
    }

    public void setFireRate(int fireRate) {
        this.fireRate = fireRate;
    }

    public boolean isAlive() {
        return alive;
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

    public void AddKills(int type) {
        kills[type - 1]++;
    }

    public static Texture getTex_average() {
        return tex_average;
    }

    public static Texture getTex_big() {
        return tex_big;
    }

    public static Texture getTex_fast() {
        return tex_fast;
    }

    public int[] getKills() {
        return kills;
    }

    public String getId() {
        return id;
    }

    public void SetPos(int x, int y, int orient, boolean activate, MyGdxGame mg) {
        position.x = x;
        position.y = y;
        rect.setCenter(position.x, position.y);
        this.orient = orient;
        if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) {
            mg.getSd().addTank(position.x, position.y, orient, id);
            mg.log("Add tank (SetPos) " + position.x + " " + getClass().getName());
        }
        //if(activate) rewrite(mg, false);
    }

    public boolean isVisible() {
        return visible;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getOrient() {
        return orient;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean FindPath(tile[][] til, int fx, int fy) {
        int maxx = til.length;
        int maxy = til[0].length;
        int[][] t = new int[maxx][maxy];
        boolean path_finded = false;

        for (int i = 0; i < maxx; i++) {
            Arrays.fill(t[i], -1);
        }
        t[(int) position.x / 50][(int) position.y / 50] = 0;

        for (int k = 0; k < maxx * 2 + maxy; k++) {
            for (int i = 0; i < maxx; i++) {
                for (int j = 0; j < maxy; j++) {

                    if (t[i][j] != -1) {
                        if (i == fx && j == fy) {
                            path_finded = true;
                            break;
                        }
                        if (i > 0) {
                            if (til[i - 1][j].isPass() && t[i - 1][j] == -1) t[i - 1][j] = t[i][j] + 1;
                        }
                        if (i < maxx - 1) {
                            if (til[i + 1][j].isPass() && t[i + 1][j] == -1) t[i + 1][j] = t[i][j] + 1;
                        }
                        if (j > 0) {
                            if (til[i][j - 1].isPass() && t[i][j - 1] == -1) t[i][j - 1] = t[i][j] + 1;
                        }
                        if (j < maxy - 1) {
                            if (til[i][j + 1].isPass() && t[i][j + 1] == -1) t[i][j + 1] = t[i][j] + 1;
                        }

                    }
                }
                if (path_finded) break;
            }
            if (path_finded) break;
        }
        if (path_finded) {
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

    public int getTeamid() {
        return teamid;
    }

    public void setTeamid(int teamid) {
        this.teamid = teamid;
    }

    public int getTexwid() {
        return texwid;
    }

    public int getTexhei() {return texhei;}

    public long getEngine_id() {
        return engine_id;
    }

    public void setEngine_id(long engine_id) {
        this.engine_id = engine_id;
    }
}
