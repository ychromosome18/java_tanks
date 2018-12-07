/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bonus {
    private int count;
    private Vector2 position;
    private byte type;
    private static Texture[] images=null;
    private static Sprite[] sprites;
    private Rectangle rect;
    private boolean end;

    Bonus(byte t) {
        if(images==null) {
            images=new Texture[8];
            sprites = new Sprite[8];
            images[0]=new Texture("b0.png");
            sprites[0] = new Sprite(images[0]);
            images[1]=new Texture("b1.png");
            sprites[1] = new Sprite(images[1]);
            images[2]=new Texture("b2.png");
            sprites[2] = new Sprite(images[2]);
            images[3]=new Texture("b3.png");
            sprites[3] = new Sprite(images[3]);
            images[4]=new Texture("b4.png");
            sprites[4] = new Sprite(images[4]);
            images[5]=new Texture("b5.png");
            sprites[5] = new Sprite(images[5]);
            images[6]=new Texture("b6.png");
            sprites[6] = new Sprite(images[6]);
            images[7]=new Texture("b7.png");
            sprites[7] = new Sprite(images[7]);
        }
        position=new Vector2();
        type=t;
        end=true;
    }

    public void rewrite() {
        position.x=Math.round((float) Math.random() * (Constants.WINDOW_WIDTH-images[0].getWidth()));
        position.y=Math.round((float) Math.random() * (Constants.WINDOW_HEIGHT-images[0].getHeight()));
        rect = new Rectangle(position.x, position.y, images[0].getWidth(), images[0].getHeight());
        end=true;
        //count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME);
        switch (type) {
            case 0:         //скрострельность
                count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME/4);
                break;
            case 1:         //бронебойность
                count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME/2);
                break;
            case 2:         //генерал
                count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME);
                break;
            case 3:         //неуязвимость?
                count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME);
                break;
            case 4:         //скорость
                count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME/4);
                break;
            case 5:         //+1  прочность
                count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME/2);
                break;
            case 6:        //каюк себе
                count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME/8);
                break;
            case 7:        //каюк врагам
                count=Math.round((float) Math.random() * Constants.BONUS_HIDE_TIME);
                break;
        }
    }

    public void update(MyGdxGame mg) {
        if (count > 0) {
            count--;
        } else {
            if (end) {
                if(mg.getGame_mode()!=Constants.GAME_MODE_CLIENT) {
                    count = Constants.BONUS_SHOW_TIME;
                    if (mg.getGame_mode() == Constants.GAME_MODE_SERVER)
                        mg.getSd().addBonus(position.x, position.y, count, type);
                    mg.getMs().play("bonus_drop");
                    end = !end;
                }
            } else {
                if(mg.getGame_mode()!=Constants.GAME_MODE_CLIENT) rewrite();
                else end = !end;
            }
            //end = !end;
        }
    }

    public void CheckPick(user tank, MyGdxGame mg) {
        if(!end && getRect()!=null) {
            if (tank.rect.contains(getRect().getX() + 25, getRect().getY() + 25)) {
                mg.getMs().play("take_bonus");
                switch (type) {
                    case 0:
                        tank.setFireRate(tank.getFireRate()/Constants.BONUS_RAPID_FIRE);         //скрострельность
                        break;
                    case 1:
                        tank.setFire_power(tank.getFire_power()*Constants.BONUS_ARMOUR_PIERCING);   //бронебойность
                        break;
                    case 2:
                        tank.setDurability(tank.getDurability()+Constants.BONUS_GENERAL, mg);       //генерал
                        break;
                    case 3:
                        tank.setDurability(tank.getDurability()+Constants.BONUS_INVULNERABILITY, mg);       //неуязвимость?
                        break;
                    case 4:
                        tank.setSpeed(tank.getSpeed()*Constants.BONUS_SPEED);       //скорость
                        break;
                    case 5:
                        tank.setDurability(tank.getDurability()+Constants.BONUS_SERGEANT, mg);       //+1  прочность
                        break;
                    case 6:
                        tank.setDurability(0, mg);       //каюк себе
                        //mg.StartExplosion(tank.position.x, tank.position.y, -1, true);
                        break;
                    case 7:                 //каюк врагам
                        if(mg.getGame_type()==Constants.GAME_TYPE_PvB) {
                            for (int i = 0; i < mg.getEnemies().size(); i++) {
                                if (mg.getEnemies().get(i).isVisible()) {
                                    mg.getEnemies().get(i).setDurability(0, mg, tank.getId());
                                    //tank.AddKills(mg.getEnemies().get(i).getType());
                                }
                            }
                            if(mg.getBb().isVisible()) {
                                mg.getBb().setDurability(mg.getBb().getDurability()/2, mg);
                                mg.StartExplosion(mg.getBb().getPosition().x, mg.getBb().getPosition().y, 1, false);
                            }
                        } else {
                            for (int i = 0; i < mg.getUsers().size(); i++) {
                                if (mg.getUsers().get(i).isVisible() && mg.getUsers().get(i).getTeamid() != tank.getTeamid()) {
                                    mg.getUsers().get(i).setDurability(0, mg, tank.getId());
                                    //tank.AddKills(2);
                                }
                            }
                        }
                        break;
                }
                tank.AddBonus(type);
                mg.log("AddBonus "+type+", tank "+tank.getId());
                setEnd(true);
            }
        }
    }

    public void CheckPick(Enemy tank, MyGdxGame mg, int enemy_i) {
        if(!end && getRect()!=null) {
            if (tank.getRect().contains(getRect().getX() + 25, getRect().getY() + 25)) {
                mg.getMs().play("take_bonus");
                switch (type) {
                    case 0:
                        tank.setFireRate(tank.getFireRate()/Constants.BONUS_RAPID_FIRE);         //скрострельность
                        break;
                    case 1:
                        tank.setFire_power(tank.getFire_power()*Constants.BONUS_ARMOUR_PIERCING);   //бронебойность
                        break;
                    case 2:
                        tank.setDurability(tank.getDurability()+Constants.BONUS_GENERAL, mg);       //генерал
                        break;
                    case 3:
                        tank.setDurability(tank.getDurability()+Constants.BONUS_INVULNERABILITY, mg);       //неуязвимость?
                        break;
                    case 4:
                        tank.setSpeed(tank.getSpeed()*Constants.BONUS_SPEED);       //скорость
                        break;
                    case 5:
                        tank.setDurability(tank.getDurability()+Constants.BONUS_SERGEANT, mg);       //+1  прочность
                        break;
                    case 6:
                        tank.setDurability(0, mg, null);       //каюк себе
                        break;
                    case 7:                 //каюк врагам
                        for(int i=0;i<mg.getUsers().size();i++) {
                            if(mg.getUsers().get(i).isAlive()) {
                                mg.getUsers().get(i).setDurability(0, mg, null);
                                //mg.StartExplosion(mg.getUsers().get(i).position.x, mg.getUsers().get(i).position.y, i, true);
                            }
                        }
                        break;
                }
                tank.AddBonus(type);
                mg.log("AddBonus "+type+", enemy "+enemy_i);
                setEnd(true);
            }
        }
    }

    public void CheckPick(MyGdxGame mg) {
        if(!end && getRect()!=null && mg.getBb().isAlive()) {
            BigBoss bb=mg.getBb();
            if (Intersector.isPointInPolygon(mg.getBb().getBoundRect(),0, mg.getBb().getBoundRect().length, getRect().getX() + 25, getRect().getY() + 25)) {
                mg.getMs().play("take_bonus");
                switch (type) {
                    case 0:
                        bb.setFireRate(bb.getFireRate()/Constants.BONUS_RAPID_FIRE);         //скрострельность
                        break;
                    case 1:
                        bb.setFire_power(bb.getFire_power()*Constants.BONUS_ARMOUR_PIERCING);   //бронебойность
                        break;
                    case 2:
                        bb.setDurability(bb.getDurability()+Constants.BONUS_GENERAL, mg);       //генерал
                        break;
                    case 3:
                        bb.setDurability(bb.getDurability()+Constants.BONUS_INVULNERABILITY, mg);       //неуязвимость?
                        break;
                    case 4:
                        bb.setSpeed(bb.getSpeed()*Constants.BONUS_SPEED);       //скорость
                        bb.setRotate_speed(bb.getRotate_speed()*Constants.BONUS_SPEED);
                        break;
                    case 5:
                        bb.setDurability(bb.getDurability()+Constants.BONUS_SERGEANT, mg);       //+1  прочность
                        break;
                    case 6:
                        bb.setDurability(bb.getDurability()/2, mg);       //    хиты пополам
                        mg.StartExplosion(bb.getPosition().x, bb.getPosition().y, 1, false);
                        break;
                    case 7:                 //каюк врагам
                        for(int i=0;i<mg.getUsers().size();i++) {
                            if(mg.getUsers().get(i).isAlive()) {
                                mg.getUsers().get(i).setDurability(0, mg, null);
                                //mg.StartExplosion(mg.getUsers().get(i).position.x, mg.getUsers().get(i).position.y, i, true);
                            }
                        }
                        break;
                }
                bb.AddBonus(type);
                mg.log("AddBonus "+type+", big boss ");
                setEnd(true);
            }
        }
    }

    public void DeBonus(user tank, MyGdxGame mg) {
        switch (type) {
            case 0:
                tank.setFireRate(tank.getFireRate() * Constants.BONUS_RAPID_FIRE);         //скрострельность
                break;
            case 1:
                tank.setFire_power(tank.getFire_power() / Constants.BONUS_ARMOUR_PIERCING);   //бронебойность
                break;
            case 2:
                if(tank.getDurability()>Constants.BONUS_GENERAL)
                    tank.setDurability(tank.getDurability() - Constants.BONUS_GENERAL, mg);       //генерал
                else tank.setDurability(1, mg);
                break;
            case 3:
                if(tank.getDurability()>Constants.BONUS_INVULNERABILITY) tank.setDurability(tank.getDurability()-Constants.BONUS_INVULNERABILITY+2, mg);
                else tank.setDurability(3, mg);       //неуязвимость?
                break;
            case 4:
                tank.setSpeed(tank.getSpeed() / Constants.BONUS_SPEED);       //скорость
                break;
            case 5:
                if(tank.getDurability()>1)
                    tank.setDurability(tank.getDurability() - Constants.BONUS_SERGEANT, mg);       //+1  прочность
                break;
        }
        mg.log("DeBonus "+type+", tank "+tank.getId());
    }

    public void DeBonus(Enemy tank, MyGdxGame mg) {
        switch (type) {
            case 0:
                tank.setFireRate(tank.getFireRate() * Constants.BONUS_RAPID_FIRE);         //скрострельность
                break;
            case 1:
                tank.setFire_power(tank.getFire_power() / Constants.BONUS_ARMOUR_PIERCING);   //бронебойность
                break;
            case 2:
                if(tank.getDurability()>Constants.BONUS_GENERAL)
                    tank.setDurability(tank.getDurability() - Constants.BONUS_GENERAL, mg);       //генерал
                else tank.setDurability(1, mg);
                break;
            case 3:
                if(tank.getDurability()>Constants.BONUS_INVULNERABILITY) tank.setDurability(tank.getDurability()-Constants.BONUS_INVULNERABILITY+2, mg);
                else tank.setDurability(3, mg);       //неуязвимость?
                break;
            case 4:
                tank.setSpeed(tank.getSpeed() / Constants.BONUS_SPEED);       //скорость
                break;
            case 5:
                if(tank.getDurability()>1)
                    tank.setDurability(tank.getDurability() - Constants.BONUS_SERGEANT, mg);       //+1  прочность
                break;
        }
        mg.log("DeBonus "+type+", enemy ");
    }

    public void DeBonus(BigBoss tank, MyGdxGame mg) {
        switch (type) {
            case 0:
                tank.setFireRate(tank.getFireRate() * Constants.BONUS_RAPID_FIRE);         //скрострельность
                break;
            case 1:
                tank.setFire_power(tank.getFire_power() / Constants.BONUS_ARMOUR_PIERCING);   //бронебойность
                break;
            case 2:
                if(tank.getDurability()>Constants.BONUS_GENERAL)
                    tank.setDurability(tank.getDurability() - Constants.BONUS_GENERAL, mg);       //генерал
                else tank.setDurability(1, mg);
                break;
            case 3:
                if(tank.getDurability()>Constants.BONUS_INVULNERABILITY)
                    tank.setDurability(tank.getDurability() - Constants.BONUS_INVULNERABILITY, mg);
                else tank.setDurability(3, mg);                                                       //неуязвимость?
                break;
            case 4:
                tank.setSpeed(tank.getSpeed() / Constants.BONUS_SPEED);       //скорость
                tank.setRotate_speed(tank.getRotate_speed()/Constants.BONUS_SPEED);
                break;
            case 5:
                if(tank.getDurability()>1)
                    tank.setDurability(tank.getDurability() - Constants.BONUS_SERGEANT, mg);       //+1  прочность
                break;
        }
        mg.log("DeBonus boss");
    }

    public boolean isEnd() {
        return end;
    }

    public static void dispose() {
        if(images!=null) {
            for (Texture im:images) {
                im.dispose();
            }
        }
    }

    public void render(SpriteBatch batch) {
        if(!isEnd()) {
            sprites[type].setAlpha(0.5f+((float)(count%50))/100);
            sprites[type].setPosition(position.x, position.y);
            sprites[type].setScale(1f);
            sprites[type].draw(batch);
        }
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setEnd(boolean end) {
        this.end = end;
        if(end) rewrite();
    }

    public static Sprite GetSprite(int i) {
        return sprites[i];
    }

    public void Enable(int x, int y, int count) {
        position.x=x;
        position.y=y;
        this.count=count;
        rect = new Rectangle(position.x, position.y, images[0].getWidth(), images[0].getHeight());
        end=false;
    }

    public void ClientStop()
    {
        end=true;
        count=10000;
    }

    public float getSpriteHeight() {
        return sprites[0].getHeight();
    }
 }
