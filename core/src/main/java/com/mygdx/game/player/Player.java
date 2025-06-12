package com.mygdx.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;


public class Player {
    private final Vector2 position;
    private final Rectangle bounds;
    private final float boundsInset = 20f; // Уменьшаем хитбокс со всех сторон
    private final float spriteWidth;
    private final float spriteHeight;
    private final AnimationManager animationManager;

    private float verticalVelocity;
    private boolean isJumping;
    private final float jumpVelocity = 1800f; // Сила прыжка
    private final float gravity = -3600f; // Сила гравитации
    private final float groundY;

    private final Sound jumpSound;

    public Player(float x, float y, float screenHeight) {
        this.spriteHeight = 480; // Высота спрайта Кроша
        this.position = new Vector2(x, y);
        this.groundY = y;

        TextureRegion sampleFrame = new TextureRegion(new Texture(Gdx.files.internal("characters/krosh/running/1.png")));
        float aspectRatio = sampleFrame.getRegionWidth() / (float)sampleFrame.getRegionHeight();
        this.spriteWidth = spriteHeight * aspectRatio;
        sampleFrame.getTexture().dispose(); // Не забываем освобождать текстуру после использования

        this.animationManager = new AnimationManager(
                "characters/krosh/running",
                "characters/krosh/jumping"
        );
        this.jumpSound = Gdx.audio.newSound(Gdx.files.internal("music/jump_sound.ogg"));
        this.verticalVelocity = 0;
        this.isJumping = false;
        // При инициализации персонаж на земле и бежит, так что анимация бега должна быть активна
        // Ее stateTime уже 0f по умолчанию в AnimationManager
        this.bounds = new Rectangle(
            position.x + boundsInset,
            position.y + boundsInset,
            spriteWidth - 2 * boundsInset,
            spriteHeight - 2 * boundsInset
        );

    }

    public void update(float deltaTime) {
        boolean wasJumping = isJumping; // Запоминаем состояние до обновления физики

        if (isJumping) {
            verticalVelocity += gravity * deltaTime;
            position.y += verticalVelocity * deltaTime;

            if (position.y <= groundY) {
                position.y = groundY;
                verticalVelocity = 0;
                isJumping = false;
                // Персонаж приземлился, сбрасываем анимацию бега, чтобы она началась с 1 кадра
                animationManager.resetRunningAnimation();
            }
        }

        // Передаем текущее состояние прыжка в AnimationManager
        animationManager.update(deltaTime, isJumping);
        //для хитбокса
        bounds.setPosition(position.x + boundsInset, position.y + boundsInset);
    }

    public void jump() {
        if (!isJumping) {
            verticalVelocity = jumpVelocity;
            isJumping = true;
            // Персонаж начал прыжок, сбрасываем анимацию прыжка
            animationManager.resetJumpingAnimation();
            jumpSound.play(); //воспроизведение звука прыжка при нажатии
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = animationManager.getFrame(isJumping);
        batch.draw(frame, position.x, position.y, spriteWidth, spriteHeight);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        animationManager.dispose();
        jumpSound.dispose();
    }
}
