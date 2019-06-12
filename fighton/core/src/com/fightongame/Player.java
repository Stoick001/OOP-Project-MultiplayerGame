package com.fightongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.LinkedList;

public class Player {
    private static String CROUCH = "crouch";
    private static String FIST = "fist";

    private static String PLAYER1 = "p1";
    private static String PLAYER2 = "p2";

    public static final String WALK = "walk";
    boolean animationLock = false;
    String currentAnimation = "";

    private FightOn game;
    Player oponent;

    ShapeRenderer hitbox;
    Rectangle hitboxRect;
    Rectangle fistRect;
    int hitboxOffset;
    int fistOffset;
    String player;

    int health;

    int positionX;
    int positionY;
    TextureRegion sprite;
    Animation walk;
    Animation crouch;
    Animation fist;

    public Player(int x, int y, LinkedList<Animation> animations, FightOn game) {
        this.game = game;
        health = 10;
        positionX = x;
        positionY = y;
        hitbox = new ShapeRenderer();
        this.walk = animations.get(0);
        this.fist = animations.get(1);
        this.crouch = animations.get(2);

        if (x == 0) {
            player = PLAYER1;
            hitboxOffset = 110;
            fistOffset = 200;
        } else {
            player = PLAYER2;
            hitboxOffset = 90;
            fistOffset = 40;
        }


        hitboxRect = new Rectangle(x+hitboxOffset, y, 50, 230);
    }

    public void drawHitbox() {
        hitbox.begin(ShapeRenderer.ShapeType.Line);
        hitbox.setColor(1, 1, 0, 1);
        hitbox.rect(hitboxRect.getX(), hitboxRect.getY(), hitboxRect.getWidth(), hitboxRect.getHeight());

        if (fistRect != null) {
            hitbox.rect(fistRect.getX(), fistRect.getY(), fistRect.getWidth(), fistRect.getHeight());
        }

        hitbox.end();
    }

    public void nextFrame(String type, float elapsedTime) {
        fistRect = null;
        if (type.equals(WALK)) {
            sprite = (TextureRegion) walk.getKeyFrame(elapsedTime, true);
        }
    }

    public void crouch(float elapsedTime) {
        fistRect = null;
        sprite = (TextureRegion) crouch.getKeyFrame(elapsedTime);
        currentAnimation = CROUCH;
    }

    public void fist(float elapsedTime) {
        sprite = (TextureRegion) fist.getKeyFrame(elapsedTime);
        fistRect = new Rectangle(positionX+fistOffset, 120, 20, 20);
        checkCollision();
    }

    private void checkCollision() {
        if (player.equals(PLAYER1)) {
            if (fistRect.getX()+20 > oponent.hitboxRect.getX()) {
                oponent.health -= 1;
                oponent.moveX(oponent.positionX+10);
            }
        } else {
            if (fistRect.getX() < oponent.hitboxRect.getX()+50) {
                oponent.health -= 1;
                oponent.moveX(oponent.positionX-10);
            }
        }
        checkWin();
    }

    private void checkWin() {
        if (oponent.health <= 0) {
            System.out.println("Winner");
            final Screen screen = game.getScreen();

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new MainMenuScreen(game));
                    screen.dispose();
                }
            });

            Message msg = new Message(Message.END, "p" + game.player, "", game.oponentId);
            game.client.send(msg);
        }
    }

    public void moveX(int position) {
        positionX = position;
        hitboxRect.setX(position+hitboxOffset);
    }
}
