/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class shell {
    private Vector2 position;
    private Vector2 direction;
    private Rectangle rect;
    private float speed;
    private boolean active;
    private String hero;
    private int fire_power;

    public shell(float x, float y, int dx, int dy, String h, int fp, MyGdxGame mg) {
        position = new Vector2(x, y);
        direction = new Vector2(dx, dy);
        rect = new Rectangle();
        SetRect(mg);
        speed = Constants.BULLET_SPEED;
        active = true;
        hero = h;
        fire_power = fp;
        if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) mg.getSd().addBullet(x, y, dx, dy, h, fp);
        if (h != null) {
            if (mg.getGame_mode() == Constants.GAME_MODE_CLIENT && h.equals(mg.getTank().getId()))
                mg.getSd().addBullet(x, y, dx, dy, h, fp);
        }
    }

    public shell(float x, float y, double dx, double dy, String h, int fp, MyGdxGame mg) {
        position = new Vector2(x, y);
        direction = new Vector2((float) dx, (float) dy);
        rect = new Rectangle();
        SetRect(mg);
        speed = Constants.BULLET_SPEED;
        active = true;
        hero = h;
        fire_power = fp;
        if (mg.getGame_mode() == Constants.GAME_MODE_SERVER) mg.getSd().addBullet(x, y, dx, dy, h, fp);
//        if(h!=null) {
//            if(mg.getGame_mode() == Constants.GAME_MODE_CLIENT && h.equals(mg.getTank().getId())) mg.getSd().addBullet(x, y, dx, dy, h, fp);
//        }
    }

    public void disable() {
        active = false;
    }

    public boolean update(MyGdxGame mg) {
        if (active) {
            ArrayList<Enemy> enemies = mg.getEnemies();
            //user tank=mg.getTank();
            tile[][] tiles = mg.getTiles();
            ArrayList<user> users = mg.getUsers();

            position.mulAdd(direction, speed);
            SetRect(mg);

            if (position.x > Constants.WINDOW_WIDTH || position.x < 0 || position.y < 0 || position.y > Constants.WINDOW_HEIGHT) {
                disable();
            } else {
                if (active) {
                    for (shell s : mg.getShls()) {
                        if (s.getDirection().x == -direction.x && direction.x != 0 || s.getDirection().y == -direction.y && direction.y != 0) {
                            if (rect.contains(s.getPosition())) {
                                disable();
                                s.disable();
                                break;
                            }
                        }
                    }

                    if (!active) return active;
                    for (int k = 0; k < Constants.WINDOW_WIDTH; k += 50) {
                        for (int j = 0; j < Constants.WINDOW_HEIGHT; j += 50) {
                            if (tiles[k / 50][j / 50].BulletHit(this, mg.getMs())) break;
                        }
                    }

                    if (!active) return active;
                    if (hero != null) {
                        if (mg.getGame_type() == Constants.GAME_TYPE_PvB) {
                            for (int j = 0; j < enemies.size(); j++) {
                                if (enemies.get(j).active) {
                                    if (enemies.get(j).getRect().contains(position)) {
                                        enemies.get(j).setDurability(enemies.get(j).getDurability() - getFire_power(), mg, hero);
                                        disable();
                                        mg.getMs().play("shot-tank");

                                        break;
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).rect.contains(position) && users.get(i).isAlive() && !users.get(i).getId().equals(hero)) {
                                    users.get(i).setDurability(users.get(i).getDurability() - getFire_power(), mg, hero);
                                    disable();
                                    mg.getMs().play("shot-tank");

                                    break;
                                }
                            }
                        }
                    } else {
                        if (users != null) {
                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).rect.contains(position) && users.get(i).isAlive()) {
                                    users.get(i).setDurability(users.get(i).getDurability() - getFire_power(), mg, null);
                                    disable();
                                    mg.getMs().play("shot-tank");
                                }
                            }
                        }
                    }
                    if (!active) return active;
                    if (mg.getBb().isAlive()) {
                        if(Intersector.isPointInPolygon(mg.getBb().getBoundRect(),0, mg.getBb().getBoundRect().length, position.x, position.y)) {
                            mg.getBb().setDurability(mg.getBb().getDurability()-getFire_power(), mg);
                            disable();
                            mg.getMs().play("shot-tank");
                        }
                    }
                }
            }
        }
        return active;
    }

    public int getFire_power() {
        return fire_power;
    }

    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public boolean isHero() {
        return hero != null;
    }

    public void SetRect(MyGdxGame mg) {
        float h = Constants.BULLET_SPEED * 2;
        if (direction.x > 0) rect.set(position.x, position.y, h, mg.getTextureshell().getHeight());
        else if (direction.x < 0) rect.set(position.x - h, position.y, h, mg.getTextureshell().getHeight());
        else if (direction.y > 0) rect.set(position.x, position.y, mg.getTextureshell().getWidth(), h);
        else if (direction.y < 0) rect.set(position.x, position.y - h, mg.getTextureshell().getWidth(), h);
    }
}
