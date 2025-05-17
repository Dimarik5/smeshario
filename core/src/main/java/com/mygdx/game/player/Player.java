package com.mygdx.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private final Vector2 position; // Фиксированная позиция по X
    private final float spriteWidth;
    private final float spriteHeight;
    private final AnimationManager animationManager;

    // Физика прыжка
    private float verticalVelocity;
    private boolean isJumping;
    private final float jumpVelocity = 750f;
    private final float gravity = -1800f;
    private final float groundY;

    public Player(float x, float y, float screenHeight) {
        this.spriteHeight = screenHeight / 4f; // 1/4 высоты экрана
        this.position = new Vector2(x, y);
        this.groundY = y; // Начальная Y позиция = уровень земли

        // Рассчитываем ширину с сохранением пропорций
        TextureRegion sampleFrame = new TextureRegion(new Texture(Gdx.files.internal("characters/krosh/running/1.png")));
        float aspectRatio = sampleFrame.getRegionWidth() / (float)sampleFrame.getRegionHeight();
        this.spriteWidth = spriteHeight * aspectRatio;
        sampleFrame.getTexture().dispose();

        this.animationManager = new AnimationManager(
            "characters/krosh/running",
            "characters/krosh/jumping"
        );
        this.verticalVelocity = 0;
        this.isJumping = false;
    }

    public void update(float deltaTime) {
        // Только вертикальное движение (прыжок)
        if (isJumping) {
            verticalVelocity += gravity * deltaTime;
            position.y += verticalVelocity * deltaTime;

            // Проверка земли
            if (position.y <= groundY) {
                position.y = groundY;
                verticalVelocity = 0;
                isJumping = false;
            }
        }

        animationManager.update(deltaTime);
    }

    public void jump() {
        if (!isJumping) {
            verticalVelocity = jumpVelocity;
            isJumping = true;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = animationManager.getFrame(isJumping);
        batch.draw(frame, position.x, position.y, spriteWidth, spriteHeight);
    }

    public void dispose() {
        animationManager.dispose();
    }
}
