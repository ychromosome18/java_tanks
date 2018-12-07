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

public class tile {
    private Vector2 position;
    private int type;
    private int durability;
    private boolean pass;
    private boolean destroy;
    private boolean shootable;
    private Rectangle rect;
    private static Texture brick=null;
    private static Texture steel;
    private static Texture forest;
    private static Texture water;
    private static Sprite sprite;


    public tile(float x, float y, int t) {
        position=new Vector2(x, y);
        if(brick==null) {
            brick = new Texture("brick.png");
            steel = new Texture("metal-small.jpg");
            forest = new Texture("forest-sprite.png");
            water = new Texture("water-sprite2.jpg");
            sprite = new Sprite(forest);
            sprite.setAlpha(0.5f);
        }
        rect= new Rectangle(x,y,brick.getWidth(),brick.getHeight());
    }

    public void rewrite(int t) {
        type=t;
        switch(t) {
            case 0:    //empty
                pass=true;
                destroy=false;
                shootable=true;
                durability=Constants.TILE_TOUGH_DURABILITY;
                break;
            case 1:    //brick
                pass=false;
                destroy=true;
                shootable=false;
                durability=Constants.TILE_BRICK_DURABILITY;
                break;
            case 2:    //steel
                pass=false;
                destroy=false;
                shootable=false;
                durability=Constants.TILE_STEEL_DURABILITY;
                break;
            case 3:    //forest
                pass=true;
                destroy=false;
                shootable=true;
                durability=Constants.TILE_TOUGH_DURABILITY;
                break;
            case 4:    //water
                pass=false;
                destroy=false;
                shootable=true;
                durability=Constants.TILE_TOUGH_DURABILITY;
                break;
        }
    }

    public void disable() {
        type=0;
    }

    public boolean BulletHit(shell sh, Mysounds ms) {
        if(rect.contains(sh.getPosition()) && !shootable) {
            if(durability<=sh.getFire_power()) {
                type=0;
                pass=true;
                destroy=false;
                shootable=true;
                durability=Constants.TILE_TOUGH_DURABILITY;
                sh.disable();
                ms.play("shot-brick");
                return true;
            } else {
                sh.disable();
                ms.play("shot-metal");
                return true;
            }
        }
        return false;
    }

    public void render(SpriteBatch batch) {
        switch (type) {
            case 1:
                batch.draw(brick, position.x, position.y);
                break;
            case 2:
                batch.draw(steel, position.x, position.y);
                break;
            case 3:
                sprite.setPosition(position.x, position.y);
//                Rectangle r=sprite.getBoundingRectangle();
//                float f[]=sprite.getVertices();
//                sprite.setRotation(45);
//                Rectangle r1=sprite.getBoundingRectangle();
//                float f2[]=sprite.getVertices();
                sprite.draw(batch);
                //Intersector.overlaps()
                break;
            case 4:
                batch.draw(water, position.x, position.y);
                break;
        }
    }

    public void dispose() {
        brick.dispose();
        steel.dispose();
        forest.dispose();
        water.dispose();
    }

    public int getWidth() { return brick.getWidth(); }

    public int getHeight() { return brick.getHeight(); }

    public int getType() {
        return type;
    }

    public boolean isPassorDestroy() {
        return (destroy || pass);
    }

    public boolean isPass() {
        return pass;
    }

    public Vector2 getPosition() {
        return position;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public boolean isShootable() {
        return shootable;
    }

    //public boolean
}
