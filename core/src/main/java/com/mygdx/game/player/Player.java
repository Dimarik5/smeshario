package com.mygdx.game.player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private Vector2 velocity;
    private float playerHeight; // 1/4 высоты экрана
    private AnimationManager animationManager;
    private boolean isJumping = false;
    private boolean gameStarted = false;

    // Физические параметры (можно менять)
    private final float gravity = -20f; // Гравитация (можно регулировать)
    private final float jumpForce = 10f; // Сила прыжка (можно регулировать)

    public Player(float startX, float startY, float screenHeight) {
        this.playerHeight = screenHeight / 4;
        this.position = new Vector2(startX, startY);
        this.velocity = new Vector2(0, 0);
        this.animationManager = new AnimationManager(
            "characters/krosh/running",
            "characters/krosh/jumping"
        );
    }

    public Vector2 getPosition() {
        return position;
    }

    public void update(float deltaTime) {
        // Физика прыжка
        velocity.y += gravity * deltaTime;
        position.mulAdd(velocity, deltaTime);

        // Проверка земли
        if (position.y <= 0) {
            position.y = 0;
            velocity.y = 0;
            isJumping = false;
        }

        animationManager.update(deltaTime, isJumping, gameStarted);
    }

    public void jump() {
        if (!isJumping && gameStarted) {
            velocity.y = jumpForce;
            isJumping = true;
        }
    }

    public void startGame() {
        this.gameStarted = true;
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animationManager.getCurrentFrame();
        if (currentFrame != null) {
            float aspectRatio = currentFrame.getRegionWidth() / (float) currentFrame.getRegionHeight();
            float width = playerHeight * aspectRatio;

            batch.draw(currentFrame,
                position.x, position.y,
                width, playerHeight);
        }
    }


    public void dispose() {
        animationManager.dispose();
    }
}
