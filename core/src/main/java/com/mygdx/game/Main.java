package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.menu.MainMenu;
import com.mygdx.game.player.Player;
import com.mygdx.game.world.Background;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private Background background;
    private MainMenu mainMenu;

    private static final float VIRTUAL_WIDTH = 1920;
    private static final float VIRTUAL_HEIGHT = 1080;
    private boolean gameStarted = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        mainMenu = new MainMenu(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        background = new Background("environment/background.png");
        player = new Player(100, 150, VIRTUAL_HEIGHT);
    }

    @Override
    public void render() {
        handleInput();
        updateInputStates(); // Добавлен вызов обновления состояний кнопок

        float deltaTime = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (!gameStarted) {
            mainMenu.render(batch, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        } else {
            batch.end();
            updateGame(deltaTime);
            renderGame();
            return;
        }

        batch.end();
    }

    private void updateInputStates() {
        // Преобразуем координаты касания в виртуальные координаты
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        // Обновляем состояния кнопок (наведение и нажатие)
        boolean isTouched = Gdx.input.isTouched();
        mainMenu.updateInput(touchPos.x, touchPos.y, isTouched);
    }

    private void updateGame(float deltaTime) {
        background.update(deltaTime);
        player.update(deltaTime);
    }

    private void renderGame() {
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        camera.update();

        background.render(batch, camera);

        batch.begin();
        player.render(batch);
        batch.end();
    }

    private void handleInput() {
        if (!gameStarted) {
            if (Gdx.input.justTouched()) {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);

                if (mainMenu.isNewGameClicked(touchPos.x, touchPos.y)) {
                    gameStarted = true;
                }
                else if (mainMenu.isCharacterClicked(touchPos.x, touchPos.y)) {
                    Gdx.app.log("Menu", "Character button clicked");
                }
                else if (mainMenu.isSettingsClicked(touchPos.x, touchPos.y)) {
                    Gdx.app.log("Menu", "Settings button clicked");
                }
                else if (mainMenu.isExitClicked(touchPos.x, touchPos.y)) {
                    Gdx.app.exit();
                }
            }
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.jump();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        background.dispose();
        mainMenu.dispose();
    }
}
