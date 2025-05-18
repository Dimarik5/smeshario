package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.player.Player;
import com.mygdx.game.world.Background;


public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private Background background;

    private static final float VIRTUAL_WIDTH = 1920;
    private static final float VIRTUAL_HEIGHT = 1080;
    private boolean gameStarted = false;

    @Override
    public void create() {
        boolean fullscreen = Gdx.graphics.isFullscreen();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        background = new Background("environment/background.png");
        player = new Player(100, 150, VIRTUAL_HEIGHT); // X=100, Y=100 (над землей)
    }

    @Override
    public void render() {
        handleInput();
        float deltaTime = Gdx.graphics.getDeltaTime();

        // Обновление только при старте игры
        if (gameStarted) {
            background.update(deltaTime); // Только фон обновляется
            player.update(deltaTime);     // Физика прыжка
        }

        // Очистка экрана\
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Фиксированная камера
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        camera.update();

        // Отрисовка
        batch.setProjectionMatrix(camera.combined);
        background.render(batch, camera);

        // Персонаж рисуется в фиксированной позиции
        batch.begin();
        player.render(batch);
        batch.end();
    }

    private void handleInput() {
        if (!gameStarted && Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            gameStarted = true;
        }

        if (gameStarted && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        background.dispose();
    }
}
