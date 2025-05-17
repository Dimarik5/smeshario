package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.player.Player;
import com.mygdx.game.world.Background;

public class Main extends ApplicationAdapter {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;

    Player player;
    Background background;

    boolean gameStarted = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        background = new Background("environment/background.png");
        player = new Player(100, 50, 20);
    }

    @Override
    public void render() {
        handleInput();
        update(Gdx.graphics.getDeltaTime()); // Передаем deltaTime

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);

        background.render(batch, camera); // Теперь рендеринг фона внутри его класса

        //отрисовка персонажа
        batch.begin();
        player.render(batch);
        batch.end();
    }

    private void handleInput() {
        if (!gameStarted && Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            gameStarted = true;
            player.startGame(); // Уведомляем персонажа о старте игры
        }

        if (gameStarted && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }
    }

    private void update(float deltaTime) {
        if (!gameStarted) return;

        background.update(deltaTime); // Обновляем фон с deltaTime
        player.update(deltaTime);

        // Камера остается фиксированной относительно игрока
        camera.position.x = camera.viewportWidth / 2; // Фиксированная позиция камеры
        camera.position.y = camera.viewportHeight / 2;
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
    }
}
