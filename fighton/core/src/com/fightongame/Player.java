package com.fightongame;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {
    public static final String WALK = "walk";

    int positionX;
    int positionY;
    TextureRegion sprite;
    Animation walk;

    public Player(int x, int y, Animation walk) {
        positionX = x;
        positionY = y;
        this.walk = walk;
    }

    public void nextFrame(String type, float elapsedTime) {
        if (type.equals(WALK)) {
            sprite = (TextureRegion) walk.getKeyFrame(elapsedTime, true);
        }
    }
}
