/*
 * Copyright (c) 2018. Belov Igor.
 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;

public class Constants {
    static final int WINDOW_WIDTH=1200;
    static final int WINDOW_HEIGHT=700;
    static final int ROUNDEND_PIC_WIDTH=500;
    static final int ROUNDEND_PIC_HEIGHT=350;
    static final float ROUNDEND_WORD_RATIO=2f;

    static final int SCORE_WIDTH=120;
    static final int SCORE_GAP=6;
    static final float SCORE_TANK_SIZE=0.66f;

    static final Color TEAM_2_COLOR=Color.YELLOW;

    static final int USER_FIRE_RATE=20;
    static final float USER_SPEED=2f;
    static int USER_DURABILITY=1;
    static final int USER_DURABILITY_EASY=3;
    static final int USER_DURABILITY_MEDIUM=2;
    static final int USER_DURABILITY_HARD=1;
    static final int USER_ORIENT=0;
    static final int USER_INIT_X=225;
    static final int USER_INIT_Y=25;
    static final int USER_FIRE_POWER=1;

    static final int BONUS_SHOW_TIME=600;
    static final int BONUS_HIDE_TIME=4000;
    static final int BONUS_ACT_TIME=1200;
    static  final float BONUS_SMALL_SCALE=3f;
    static final int BONUS_RAPID_FIRE=2;
    static final int BONUS_ARMOUR_PIERCING=3;
    static final int BONUS_SERGEANT=1;
    static final int BONUS_GENERAL=3;
    static final int BONUS_INVULNERABILITY=1000;
    static final int BONUS_SPEED=2;

    static final int INIT_DELAY_GROUP=120;
    static final int END_DELAY_TIME=200;
    static final int INIT_SPARK_TIME=64;

    static final int TILE_BRICK_DURABILITY=1;
    static final int TILE_STEEL_DURABILITY=3;
    static final int TILE_TOUGH_DURABILITY=100;

    static final int ENEMY_FIRE_RATE=40;
    static final int ENEMY_FIRE_POWER=1;
    static final float ENEMY_MODERATE_SPEED=2f;
    static final float ENEMY_FAST_SPEED=4f;
    static final float ENEMY_TOUGH_SPEED=1f;
    static final int ENEMY_MODERATE_DURABILITY=2;
    static final int ENEMY_FAST_DURABILITY=1;
    static final int ENEMY_TOUGH_DURABILITY=3;
    static int ENEMY_MAX_VISIBLE=10;
    static final int ENEMY_MAX_VISIBLE_EASY=6;
    static final int ENEMY_MAX_VISIBLE_MEDIUM=8;
    static final int ENEMY_MAX_VISIBLE_HARD=10;
    static final int ENEMY_ACTION_DELAY=20;

    static final int BOSS_FIRE_RATE=40;
    static final int BOSS_FIRE_POWER=1;
    static final float BOSS_SPEED=1f;
    static final float BOSS_ROTATE_SPEED=1f;
    static final float BOSS_TURRET_SPEED=1f;
    static int BOSS_DURABILITY=1000;
    static final int BOSS_DURABILITY_EASY=300;
    static final int BOSS_DURABILITY_MEDIUM=600;
    static final int BOSS_DURABILITY_HARD=900;
    static final int BOSS_ACTION_DELAY=20;
    static final int BOSS_TARGETING_MAX=400;
    static final int BOSS_MOVING_MAX=400;
    static final float BOSS_SCALE=0.75f;
    static final int BOSS_WIN_ROUND=3;
    static final float BOSS_BONUS_SCALE=0.5f;
    static final int BOSS_EXPLOSION_TIME=100;

    static final int EXPLOSION_TIME=100;

    static final int GAME_MODE_SINGLE=1;
    static final int GAME_MODE_SERVER=2;
    static final int GAME_MODE_CLIENT=3;
    static final int GAME_TYPE_PvB=1;
    static final int GAME_TYPE_PvP=2;

    static final int GAME_NET_SEND_PERIOD=100;
    static final int GAME_BEGIN=2;
    static final int GAME_START=0;
    static final int GAME_STOP=1;
    static final int GAME_PAUSE=3;
    static final int GAME_UNPAUSE=4;
    static final int GAME_ENDSTAGE_WIN=5;
    static final int GAME_ENDSTAGE_FAIL=6;
    static final int GAME_DOUBLE_TOUCH_DELAY=500;
    static final int GAME_DOUBLE_TOUCH_DELTA=2;

    static final float BULLET_SPEED=16f;
}
