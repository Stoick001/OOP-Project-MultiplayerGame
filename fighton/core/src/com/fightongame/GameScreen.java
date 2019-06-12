package com.fightongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.LinkedList;

public class GameScreen implements Screen {
    final FightOn game;

    private Texture walk, fist, crouch;
    private LinkedList<Animation> animationsPlayer1, animationsPlayer2;
    public float elapsedTime;
    public Player p1;
    public Player p2;

    BitmapFont font;

    public GameScreen(final FightOn game)  {
        this.game = game;

        animationsPlayer1 = new LinkedList<Animation>();
        animationsPlayer2 = new LinkedList<Animation>();

        // LOAD TEXTURES
        walk = new Texture(Gdx.files.internal("animation/walk.png"));

        fist = new Texture(Gdx.files.internal("animation/fist.png"));

        crouch = new Texture(Gdx.files.internal("animation/crouch.png"));


        // SETUP ANIMATIONS
        addAnimation(walk, animationsPlayer1, animationsPlayer2);
        addAnimation(fist, animationsPlayer1, animationsPlayer2);
        addAnimation(crouch, animationsPlayer1, animationsPlayer2);

        if (game.player == 1) {
            p1 = new Player(0, 0, animationsPlayer1, game);
            p2 = new Player(500, 0, animationsPlayer2, game);
        } else {
            p2 = new Player(0, 0, animationsPlayer1, game);
            p1 = new Player(500, 0, animationsPlayer2, game);
        }

        p1.oponent = p2;
        p2.oponent = p1;

        p1.nextFrame(Player.WALK, elapsedTime);
        p2.nextFrame(Player.WALK, elapsedTime);

        font = new BitmapFont();
    }

    private void addAnimation(Texture texture, LinkedList<Animation> animationsPlayer1, LinkedList<Animation> animationsPlayer2) {
        TextureRegion[][] tmpFrames = TextureRegion.split(texture, 256, 256);

        TextureRegion[] frames = new TextureRegion[4];
        TextureRegion[] invertedFrames = new TextureRegion[4];
        TextureRegion tempTextReg;

        int ind = 0;
        for (int i = 0; i < tmpFrames.length; i++) {
            for (int j = 0; j < tmpFrames[0].length; j++) {
                frames[ind] = tmpFrames[i][j];
                tempTextReg = new TextureRegion(tmpFrames[i][j]);
                tempTextReg.flip(true, false);
                invertedFrames[ind] = tempTextReg;
                ind += 1;
            }
        }

        animationsPlayer1.add(new Animation(1f/4f, invertedFrames));
        animationsPlayer2.add(new Animation(1f/4f, frames));
    }

    @Override
    public void render (float delta) {
        Message msg = new Message(Message.PLAY, "p" + game.player, "", game.oponentId);

        elapsedTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.2f, 0.5f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        p1.drawHitbox();
        p2.drawHitbox();

        game.batch.begin();

        font.draw(game.batch, "HP " + p1.health, p1.positionX+100, 300);
        font.draw(game.batch, "HP " + p2.health, p2.positionX+100, 300);

        handlePlayer1(msg);

        game.batch.draw(p1.sprite, p1.positionX, p1.positionY);
        game.batch.draw(p2.sprite, p2.positionX, p2.positionY);

        game.batch.end();

        if (!msg.contents.equals(""))
            game.client.send(msg);
    }

    private void handlePlayer1(Message msg) {
        float time = Gdx.graphics.getDeltaTime();

        if (!p1.animationLock) {
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                p1.moveX(p1.positionX -= 100 * time);
                p1.nextFrame(Player.WALK, elapsedTime);
                msg.contents = Input.Keys.LEFT + "/" + p1.positionX;
            }

            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                p1.moveX(p1.positionX += 100 * time);
                p1.nextFrame(Player.WALK, elapsedTime);
                msg.contents = Input.Keys.RIGHT + "/" + p1.positionX;
            }
        } else {
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            p1.crouch(elapsedTime);
            msg.contents = Input.Keys.DOWN + "/" + p1.positionX;
        } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            p1.fist(elapsedTime);
            msg.contents = Input.Keys.SPACE + "/" + p1.positionX;
        }
    }

    public void handlePlayer2(String data) {
        if (data.equals(""))
            return;

        String[] move = data.split("/");

        System.out.println(p2.positionX);
        if(Input.Keys.RIGHT == Integer.parseInt(move[0])) {
            p2.moveX(Integer.parseInt(move[1]));
            p2.nextFrame(Player.WALK, elapsedTime);
        }

        if(Input.Keys.LEFT == Integer.parseInt(move[0])) {
            p2.moveX(Integer.parseInt(move[1]));
            p2.nextFrame(Player.WALK, elapsedTime);
        }

        if (Input.Keys.DOWN == Integer.parseInt(move[0])) {
            p2.moveX(Integer.parseInt(move[1]));
            p2.crouch(elapsedTime);
        } else if (Input.Keys.SPACE == Integer.parseInt(move[0])) {
            p2.moveX(Integer.parseInt(move[1]));
            p2.fist(elapsedTime);
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose () {

    }

    public String getGameState() {
        StringBuilder stringBuilder = new StringBuilder();
        return "REEE STATE";
    }
}
