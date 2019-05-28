package com.fightongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen implements Screen {
    final FightOn game;

    private Texture walk;
    private TextureRegion[] walkFrames;
    private Animation animationWalkPlayer1, animationWalkPlayer2;
    public float elapsedTime;
    public Player p1;
    public Player p2;

    public GameScreen(final FightOn game)  {
        this.game = game;

        walk = new Texture(Gdx.files.internal("animation/walk.png"));
        walkFrames = new TextureRegion[4];

        TextureRegion[][] tmpFrames = TextureRegion.split(walk, 256, 256);

        TextureRegion[] walkFramesInvert = new TextureRegion[4];
        TextureRegion tempTextReg;

        int ind = 0;
        for (int i = 0; i < tmpFrames.length; i++) {
            for (int j = 0; j < tmpFrames[0].length; j++) {
                walkFrames[ind] = tmpFrames[i][j];
                tempTextReg = new TextureRegion(tmpFrames[i][j]);
                tempTextReg.flip(true, false);
                walkFramesInvert[ind] = tempTextReg;
                ind += 1;
            }
        }

        animationWalkPlayer1 = new Animation(1f/4f, walkFramesInvert);
        animationWalkPlayer2 = new Animation(1f/4f, walkFrames);

        if (game.player == 1) {
            p1 = new Player(0, 0, animationWalkPlayer1);
            p2 = new Player(500, 0, animationWalkPlayer2);
        } else {
            p2 = new Player(0, 0, animationWalkPlayer1);
            p1 = new Player(500, 0, animationWalkPlayer2);
        }

        p1.nextFrame(Player.WALK, elapsedTime);
        p2.nextFrame(Player.WALK, elapsedTime);
    }

    @Override
    public void render (float delta) {
        Message msg = new Message(Message.PLAY, "p" + game.player, "", game.oponentId);

        elapsedTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.2f, 0.5f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        game.batch.begin();


        handlePlayer1(msg);

        game.batch.draw(p1.sprite, p1.positionX, p1.positionY);
        game.batch.draw(p2.sprite, p2.positionX, p2.positionY);

        game.batch.end();

        if (!msg.contents.equals(""))
            game.client.send(msg);
    }

    private void handlePlayer1(Message msg) {
        float time = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            p1.positionX -= 100 * time;
            p1.nextFrame(Player.WALK, elapsedTime);
            msg.contents = Input.Keys.LEFT + "/" + p1.positionX;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            p1.positionX += 100 * time;
            p1.nextFrame(Player.WALK, elapsedTime);
            msg.contents = Input.Keys.RIGHT + "/" + p1.positionX;
        }
    }

    public void handlePlayer2(String data) {
        if (data.equals(""))
            return;

        String[] move = data.split("/");

        System.out.println(p2.positionX);
        if(Input.Keys.RIGHT == Integer.parseInt(move[0])) {
            p2.positionX = Integer.parseInt(move[1]);
            p2.nextFrame(Player.WALK, elapsedTime);
        }

        if(Input.Keys.LEFT == Integer.parseInt(move[0])) {
            p2.positionX = Integer.parseInt(move[1]);
            p2.nextFrame(Player.WALK, elapsedTime);
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
