/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class BigBoss {
    private Vector2 position;
    private Vector2 direction;
    private float speed;
    private float appear_speed;
    private float rotate_speed;
    private int action_delay;
    private int phase;
    private float orient;
    private float turret_orient, prev_turret_orient;
    private float need_turret_orient;
    private float need_orient;
    private int fireRate;
    private int fireCounter, randomCounter;
    private int texwid, texhei, turret_texwid, turret_texhei;
    private int fire_power;
    private int durability;
    private int init_time, ang;
    private boolean visible, killed, active, laugh;
    private Texture tex_hull = null;
    private Texture tex_turret = null;
    private static Sprite sprite_hull;
    private ArrayList<Integer[]> bon_img = null;
    MyGdxGame mgame;
    private long engine_id, turret_id;

    public BigBoss(float x, float y, MyGdxGame mg) {
        tex_hull = new Texture("th1.png");
        tex_turret = new Texture("tt1.png");
        sprite_hull = new Sprite(tex_hull);
        bon_img = new ArrayList<Integer[]>();
        texwid = tex_hull.getWidth();
        texhei = tex_hull.getHeight();
        turret_texwid = tex_turret.getWidth();
        turret_texhei = tex_turret.getHeight();
        mgame=mg;
        renew(x, y);
    }

    public void renew(float x, float y) {
        setPosition(new Vector2(x, y));
        direction = new Vector2(0, 0);
        //rect = new Rectangle(x - tex_hull.getWidth() / 2, y - tex_hull.getHeight() / 2, tex_hull.getWidth(), tex_hull.getHeight());

        fireCounter = 0;
        fireRate = Constants.BOSS_FIRE_RATE;
        fire_power = Constants.BOSS_FIRE_POWER;
        durability = Constants.BOSS_DURABILITY;
        speed = Constants.BOSS_SPEED;
        rotate_speed=Constants.BOSS_ROTATE_SPEED;
        //turret_speed = Constants.BOSS_TURRET_SPEED;
        active = false;
        visible = false;
        killed = true;
        laugh=false;

        //setNeed_orient(orient = 180);
        setTurret_orient(180);
        prev_turret_orient=getTurret_orient();
        setNeed_orient(findOrient(true));
        need_turret_orient=180;
        action_delay = 0;
        init_time = 0;
        appear_speed = 1;
        randomCounter = 360;
        ang = 0;
        phase = 0;
        bon_img.clear();

        engine_id=turret_id=-1;
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

    public void Activate() {
        active = true;
        visible = true;
        init_time = 180;
        appear_speed = (getPosition().y - Constants.WINDOW_HEIGHT / 2) / 180;
        soundMan("boss_engine", "start");
        //killed = false;
        //if (ms != null) sprk.enable(position.x, position.y, ms);
    }

    public void Deactivate() {
        if(mgame.getGame_mode()!=Constants.GAME_MODE_SINGLE) mgame.getSd().addBoss((float)-5000,-5000f);
        active = false;
        killed = true;
        soundMan("turret", "stop");
        soundMan("boss_engine", "stop");
        engine_id=turret_id=-1;
        //if (sprk.isActive()) sprk.setActive(false);
    }

    public void Hide() {
        visible = false;
        //killed=true;
    }

    public void dispose() {
        tex_hull.dispose();
        tex_turret.dispose();
    }

    public void render(SpriteBatch batch) {
        if (visible) {
            sprite_hull.setPosition(getPosition().x - texwid / 2, getPosition().y - texhei / 2);
            sprite_hull.setScale(Constants.BOSS_SCALE);
            sprite_hull.setRotation(orient - 90);
            sprite_hull.setColor(1, ((float) durability) / Constants.BOSS_DURABILITY, ((float) durability) / Constants.BOSS_DURABILITY, 1);
            sprite_hull.draw(batch);
            //batch.draw(tex_hull, position.x - texwid / 2, position.y - texhei / 2, texwid / 2, texhei / 2, texwid, texhei, Constants.BOSS_SCALE, Constants.BOSS_SCALE, orient - 90, 0, 0, texwid, texhei, false, false);
            Color c = new Color(batch.getColor());
            batch.setColor(1, ((float) durability) / Constants.BOSS_DURABILITY, ((float) durability) / Constants.BOSS_DURABILITY, 1);
            batch.draw(tex_turret, getPosition().x - turret_texwid / 2, getPosition().y - turret_texhei / 2, turret_texwid / 2, turret_texhei / 2, turret_texwid, turret_texhei, Constants.BOSS_SCALE, Constants.BOSS_SCALE, getTurret_orient() - 90, 0, 0, turret_texwid, turret_texhei, false, false);
            batch.setColor(c);
        }
    }

    public void renderBonus(SpriteBatch batch) {
        if (visible) {
            for (int i = 0; i < bon_img.size(); i++) {
                //Bonus.GetSprite(bon_img.get(i)[0]).setSize(50f / Constants.BONUS_SMALL_SCALE, 50f / Constants.BONUS_SMALL_SCALE);
                if (bon_img.get(i)[1] < 300)
                    Bonus.GetSprite(bon_img.get(i)[0]).setAlpha(0.5f + ((float) (bon_img.get(i)[1] % Constants.TILE_SIZE)) / 100);
                else Bonus.GetSprite(bon_img.get(i)[0]).setAlpha(1f);

                Bonus.GetSprite(bon_img.get(i)[0]).setOrigin(0, -texwid/2);
                Bonus.GetSprite(bon_img.get(i)[0]).setPosition(getPosition().x /*+ i * 50f / Constants.BONUS_SMALL_SCALE - texwid / 2*/ , getPosition().y+texwid/2);
                Bonus.GetSprite(bon_img.get(i)[0]).setRotation((ang+i*30) % 360);
                Bonus.GetSprite(bon_img.get(i)[0]).setScale(Constants.BOSS_BONUS_SCALE);
                Bonus.GetSprite(bon_img.get(i)[0]).draw(batch);
                Bonus.GetSprite(bon_img.get(i)[0]).setRotation(0);
                Bonus.GetSprite(bon_img.get(i)[0]).setOriginCenter();
                Bonus.GetSprite(bon_img.get(i)[0]).setSize(Constants.TILE_SIZE, Constants.TILE_SIZE);
                Bonus.GetSprite(bon_img.get(i)[0]).setScale(1);
            }
            ang+=2;
        }
    }

    public void AddBonus(int b) {
        bon_img.add(new Integer[]{b, Constants.BONUS_ACT_TIME});
    }

    public void setPos(float x, float y) {
        orient = 180;
        setTurret_orient(180);
        action_delay = 0;
        getPosition().set(x, y);
        //rect = new Rectangle(x - tex_hull.getWidth() / 2, y - tex_hull.getHeight() / 2, tex_hull.getWidth(), tex_hull.getHeight());
        fireCounter = 0;
        killed = false;
    }

    public void update(MyGdxGame mg) {
        if(mg.getGame_mode()==Constants.GAME_MODE_CLIENT) {
            switch (phase) {
                case 0:
                    init_time--;
                    getPosition().y -= appear_speed;
                    if (init_time < 100 && !laugh) {
                        mg.getMs().play("evil_laugh");
                        laugh = true;
                    }
                    break;
                case 1:
                    orient=adjustValue(need_orient, orient, rotate_speed);
                    if(orient==need_orient) moveForward();

                    turret_orient=adjustValue(orient, turret_orient, Constants.BOSS_TURRET_SPEED);
                    if(turret_orient==prev_turret_orient) soundMan("turret", "pause");
                    else soundMan("turret", "start");
                    prev_turret_orient=getTurret_orient();

                    checkCollision(mg);
                    break;
                case 2:
                    soundMan("turret", "start");
                    orient=adjustValue(need_orient, orient, rotate_speed);
                    if(orient==need_orient) moveForward();
//                    if(turret_orient==need_turret_orient) soundMan("turret", "pause");
//                    else soundMan("turret", "start");
                    turret_orient=adjustValue(need_turret_orient, turret_orient, Constants.BOSS_TURRET_SPEED);
                    checkCollision(mg);
                    break;
            }
            removeBonus(mg);
        } else {
            switch (phase) {
                case 0:
                    init_time--;
                    getPosition().y -= appear_speed;
                    if (init_time < 100 && !laugh) {
                        mg.getMs().play("evil_laugh");
                        laugh = true;
                    }
                    if (init_time <= 0) {
                        phase = 1;
                        init_time = 300;
                        if (mg.getGame_mode() == Constants.GAME_MODE_SERVER)
                            mg.getSd().addBoss(getPosition().x, getPosition().y, getNeed_orient(), turret_orient, Float.valueOf(phase));
                    }
                    break;
                case 1:
                    init_time--;
                    moveHull(mg);
                    turret_orient=adjustValue(orient, turret_orient, Constants.BOSS_TURRET_SPEED);
                    if(turret_orient==prev_turret_orient) soundMan("turret", "pause");
                    else soundMan("turret", "start");
                    prev_turret_orient=getTurret_orient();

                    if (init_time <= 0) {
                        phase = 2;
                        init_time = (int)(Math.random()*Constants.BOSS_TARGETING_MAX/2)+Constants.BOSS_TARGETING_MAX/2;
                        if (mg.getGame_mode() == Constants.GAME_MODE_SERVER)
                            mg.getSd().addBoss(getPosition().x, getPosition().y, getNeed_orient(), turret_orient, Float.valueOf(phase));
                    }
                    break;
                case 2:
                    init_time--;
                    soundMan("turret", "start");
                    moveHull(mg);
                    int alpha = getAngletoUser(mg);
                    if (mg.getGame_mode() == Constants.GAME_MODE_SERVER)
                        mg.getSd().addBoss_need_turret_orient(Float.valueOf(alpha));
                        //mg.getSd().addBoss(getPosition().x, getPosition().y, getNeed_orient(), null, Float.valueOf(phase), Float.valueOf(alpha));
                    turret_orient=adjustValue(alpha, turret_orient, Constants.BOSS_TURRET_SPEED);
//                    if(turret_orient==prev_turret_orient) soundMan("turret", "pause");
//                    else soundMan("turret", "start");
//                    prev_turret_orient=getTurret_orient();

                    if(turret_orient==alpha) {
                        turret_and_fire(mg);
                    }
            }
            removeBonus(mg);
        }
    }

    private float adjustValue(float need, float exist, float inc) {
        if (exist > need)
            if (exist - need > inc)
                exist-=inc;
            else {
                exist=need;
            }
        else if (exist < need)
            if (need - exist > inc)
                exist+=inc;
            else {
                exist=need;
            }
            return exist;
    }

    private void turret_and_fire(MyGdxGame mg) {
        fire(mg, getTurret_orient());
        if (init_time <= 0) {
            phase = 1;
            init_time = (int)(Math.random()*Constants.BOSS_MOVING_MAX/2)+Constants.BOSS_MOVING_MAX/2;
            if (mg.getGame_mode() == Constants.GAME_MODE_SERVER)
                mg.getSd().addBoss(getPosition().x, getPosition().y, getNeed_orient(), null, Float.valueOf(phase), null);

        }
    }

    private void fire(MyGdxGame mg, float alpha) {
        if (fireCounter >= fireRate) {                                                   // пиф-паф
            fireCounter = 0;
            double alpha_rad = Math.toRadians(alpha+90);
            mg.AddShell(getPosition().x + (float) (turret_texwid / 3 * Math.cos(alpha_rad)), getPosition().y + (float) (turret_texwid * Math.sin(alpha_rad) / 3), Math.cos(alpha_rad), Math.sin(alpha_rad), null, fire_power);
            //action_delay = Constants.ENEMY_ACTION_DELAY;
        }
    }

    private int getAngletoUser(MyGdxGame mg) {
        int smallestAlpha=360;
        for (user tank : mg.getUsers()) {
            if (tank.isAlive()) {
                float x = tank.getPosition().x - getPosition().x;
                float y = tank.getPosition().y - getPosition().y;
                double alpha_rad = Math.atan2(y, x);
                double alpha = Math.toDegrees(alpha_rad);
                //int alphaPos=((int) alpha + 360) % 360 - 90;

                if(Math.abs(intAngle(alpha)-intAngle(getTurret_orient()))<smallestAlpha) smallestAlpha=intAngle(alpha);
                //return ((int) alpha + 360) % 360 - 90;
            }
        }
        if(smallestAlpha==360) return 180; else return smallestAlpha-90;
    }

    private int intAngle(double angle) {
        return ((int) angle + 360) % 360;
    }

    private void checkCollision(MyGdxGame mg) {
        //  если >0, то размер танка считается меньше при столкновении с боссом
        int margin = 5;
        for (user tank : mg.getUsers()) {
            if (tank.isAlive()) {
                if (Intersector.isPointInPolygon(getBoundRect(), 0, getBoundRect().length, tank.getPosition().x - tank.getTexwid() / 2 + margin, tank.getPosition().y - tank.getTexhei() / 2 + margin))
                    tank.setDurability(0, mg, null);
                if (Intersector.isPointInPolygon(getBoundRect(), 0, getBoundRect().length, tank.getPosition().x + tank.getTexwid() / 2 - margin, tank.getPosition().y - tank.getTexhei() / 2 + margin))
                    tank.setDurability(0, mg, null);
                if (Intersector.isPointInPolygon(getBoundRect(), 0, getBoundRect().length, tank.getPosition().x - tank.getTexwid() / 2 + margin, tank.getPosition().y + tank.getTexhei() / 2 - margin))
                    tank.setDurability(0, mg, null);
                if (Intersector.isPointInPolygon(getBoundRect(), 0, getBoundRect().length, tank.getPosition().x + tank.getTexwid() / 2 - margin, tank.getPosition().y + tank.getTexhei() / 2 - margin))
                    tank.setDurability(0, mg, null);
            }
        }
    }

    public float[] getBoundRect() {
        float[] vs_short, vs = sprite_hull.getVertices();
        vs_short = new float[8];
        vs_short[0] = vs[0];
        vs_short[1] = vs[1];
        vs_short[2] = vs[5];
        vs_short[3] = vs[6];
        vs_short[4] = vs[10];
        vs_short[5] = vs[11];
        vs_short[6] = vs[15];
        vs_short[7] = vs[16];

        return vs_short;
    }

    private void moveForward() {
        double angle = Math.toRadians((orient + 360) % 360 + 90);
        direction.set((float) Math.cos(angle), (float) Math.sin(angle));
        getPosition().mulAdd(direction, speed);
    }

    private boolean nearEdge() {
        float halfdiag = (float) (Math.sqrt(texwid * texwid + texhei * texhei) / 2.7);
        if (getPosition().x + halfdiag >= Constants.WINDOW_WIDTH || getPosition().x - halfdiag <= 0 || getPosition().y + halfdiag >= Constants.WINDOW_HEIGHT || getPosition().y - halfdiag <= 0)
            return true;
        else return false;
    }

    private float findOrient(boolean random) {
        if (random) return (float) Math.round(Math.random() * 360);
        else return (orient + 180) % 360;
    }

    private void move(MyGdxGame mg) {
        randomCounter--;
        if (randomCounter == 0) {
            randomCounter = 360;
            setNeed_orient(findOrient(true));
            //if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) mg.getSd().addBoss(getPosition().x, getPosition().y, getNeed_orient(),null,Float.valueOf(phase));
        }
        moveForward();
        if (nearEdge()) {
            setNeed_orient(findOrient(false));
            //if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) mg.getSd().addBoss(getPosition().x, getPosition().y, getNeed_orient(),null,Float.valueOf(phase));
        }
    }

    private void moveHull(MyGdxGame mg) {
        fireCounter++;
        orient=adjustValue(need_orient, orient, rotate_speed);
        if(orient==need_orient) move(mg);
        checkCollision(mg);
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
        //System.out.println(this.durability);
        if (durability < 1) {
            mg.StartExplosion(getPosition().x, getPosition().y, -1, false);
            Deactivate();
            //if (engine_id != -1) mg.getMs().stop("enemyengine", engine_id);
        }
    }

//    public void setDurability(int durability, MyGdxGame mg, String id) {
//        if (durability < 1) {
//            if (id != null) {
//                for (user t : mg.getUsers()) {
//                    if (t.getId().equals(id)) t.AddKills(getType());
//                }
//            }
//            if (mg.getGame_mode() != Constants.GAME_MODE_SINGLE)
//                mg.getSd().add_enemy_hit(mg.getEnemies().indexOf(this), id);
//        }
//        setDurability(durability, mg);
//    }

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

    public void removeBonus(MyGdxGame mg) {
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

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getTurret_orient() {
        return turret_orient;
    }

    public void setTurret_orient(float turret_orient) {
        this.turret_orient = turret_orient;
    }

    public float getNeed_orient() {
        return need_orient;
    }

    public void setNeed_orient(float need_orient) {
        this.need_orient = need_orient;
        //if(mgame.getGame_mode()==Constants.GAME_MODE_SERVER) mgame.getSd().addBoss_need_orient(need_orient);
        if (mgame.getGame_mode() == Constants.GAME_MODE_SERVER) mgame.getSd().addBoss(getPosition().x, getPosition().y, need_orient,turret_orient,Float.valueOf(phase));
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    public void setNeed_turret_orient(float need_turret_orient) {
        this.need_turret_orient = need_turret_orient;
        if(mgame.getGame_mode()==Constants.GAME_MODE_SERVER) mgame.getSd().addBoss_need_turret_orient(need_turret_orient);
    }

    public void soundMan(String dev, String act) {
        long id=-1;
        if(dev.equals("boss_engine")) id=engine_id;
        if(dev.equals("turret")) id=turret_id;

        if(act.equals("start")) {
            if (id == -1) {
                id = mgame.getMs().loop(dev);
                if(dev.equals("boss_engine")) engine_id=id;
                if(dev.equals("turret")) turret_id=id;
            }
            else mgame.getMs().resume(dev, id);
        }
        if(act.equals("pause")) {
            if (id != -1) mgame.getMs().pause(dev, id);
        }
        if(act.equals("stop")) {
            if (id != -1) mgame.getMs().stop(dev, id);
        }
    }

    public float getRotate_speed() {
        return rotate_speed;
    }

    public void setRotate_speed(float rotate_speed) {
        this.rotate_speed = rotate_speed;
    }

    public void pauseSound() {
        soundMan("turret", "pause");
        soundMan("boss_engine", "pause");
    }

    public void resumeSound() {
        soundMan("turret", "pause");
        soundMan("boss_engine", "start");
    }

    public void stopSound() {
        soundMan("turret", "stop");
        soundMan("boss_engine", "stop");
    }
}
