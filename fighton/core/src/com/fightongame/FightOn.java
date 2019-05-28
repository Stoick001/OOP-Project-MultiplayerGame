package com.fightongame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.IOException;

public class FightOn extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public Client client;
    public Thread clientThread;
    public int player;
    public String oponentId;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();

        try {
            client = new Client(this);

            clientThread = new Thread(client);
            clientThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.setScreen(new SignUpScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
