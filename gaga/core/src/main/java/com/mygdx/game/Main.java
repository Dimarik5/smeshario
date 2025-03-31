package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input;

public class Main extends ApplicationAdapter {
    SpriteBatch batch;
    OrthographicCamera camera;
    ShapeRenderer shapeRenderer;
    Vector2 ballPosition;
    Vector2 ballVelocity;
    float ballRadius = 20;
    boolean isJumping;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        shapeRenderer = new ShapeRenderer();
        ballPosition = new Vector2(100, 50);
        ballVelocity = new Vector2(0, 0);
    }

    @Override
    public void render() {
        handleInput();
        updateBall();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.circle(ballPosition.x, ballPosition.y, ballRadius);
        shapeRenderer.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            ballPosition.x -= 15;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            ballPosition.x += 25;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && !isJumping) {
            ballVelocity.y = 20;
            isJumping = true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && isJumping) {
            ballVelocity.y = 20;
        }
    }

    private void updateBall() {
        ballVelocity.y -= 0.5f; // Гравитация
        ballPosition.add(ballVelocity);

        // Проверка на столкновение с землей
        if (ballPosition.y <= ballRadius) {
            ballPosition.y = ballRadius;
            ballVelocity.y = 0;
            isJumping = false;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
