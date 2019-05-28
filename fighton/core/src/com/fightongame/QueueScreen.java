package com.fightongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class QueueScreen implements Screen {
    final FightOn game;
    private Skin skin;
    private Stage stage;

    public QueueScreen(final FightOn game) {
        this.game = game;

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());

        final Label waiting = new Label("Waiting for match...", skin, "default");
        waiting.setSize(300, 50);
        waiting.setPosition(300, 275);
        waiting.setName("Waiting");

        final TextButton quit = new TextButton("Quit", skin, "default");
        quit.setSize(200, 50);
        quit.setPosition(300, 200);



        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Message msg = new Message(Message.QUEE, "", "quit", "server");

                game.client.send(msg);
            }
        });


        stage.addActor(waiting);
        stage.addActor(quit);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.5f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
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
    public void dispose() {

    }
}
