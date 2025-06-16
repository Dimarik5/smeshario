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
    private final float boundsInset = 123f;
    private final float boundsShiftX = 25f;

    private final float spriteWidth;
    private final float spriteHeight;
    private final AnimationManager animationManager;

    private float verticalVelocity;
    private boolean isJumping; // Флаг — прыгает ли сейчас персонаж
    private final float jumpVelocity = 1800f; // Значение скорости прыжка
    private final float gravity = -3600f; // Значение гравитации (ускорения вниз)
    private final float groundY; // Y-координата "земли", на которой стоит персонаж

    private final Sound jumpSound;
    private float soundVolume = 1.0f; // Добавлено поле для громкости звука

    public Player(float x, float y, float screenHeight) {
        this.spriteHeight = 480;
        this.position = new Vector2(x, y);
        this.groundY = y;
        // Загружаем один кадр, чтобы вычислить соотношение сторон спрайта
        TextureRegion sampleFrame = new TextureRegion(new Texture(Gdx.files.internal("characters/krosh/running/1.png")));
        float aspectRatio = sampleFrame.getRegionWidth() / (float)sampleFrame.getRegionHeight();
        this.spriteWidth = spriteHeight * aspectRatio;
        sampleFrame.getTexture().dispose(); // Очищаем текстуру, чтобы не держать в памяти

        this.animationManager = new AnimationManager(
            "characters/krosh/running",
            "characters/krosh/jumping"
        );
        this.jumpSound = Gdx.audio.newSound(Gdx.files.internal("music/jump_sound.ogg"));
        this.verticalVelocity = 0;
        this.isJumping = false;

        // Создаем границы столкновения (хитбоксы)
        this.bounds = new Rectangle(
            position.x + boundsShiftX + boundsInset,
            position.y + boundsInset,
            spriteWidth - 2 * boundsInset,
            spriteHeight - 2 * boundsInset
        );
    }

    // Добавлен метод для установки громкости звука
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0, Math.min(1, volume)); // Ограничиваем значение от 0 до 1
    }

    // Обновление состояния персонажа — вызывается каждый кадр
    public void update(float deltaTime) {
        // Обработка прыжка: изменение позиции по вертикали
        if (isJumping) {
            verticalVelocity += gravity * deltaTime;
            position.y += verticalVelocity * deltaTime;

            // Если упал ниже земли — остановить прыжок
            if (position.y <= groundY) {
                position.y = groundY;
                verticalVelocity = 0;
                isJumping = false;
                animationManager.resetRunningAnimation();
            }
        }
        // Обновление анимации
        animationManager.update(deltaTime, isJumping);
        bounds.setPosition(position.x + boundsShiftX + boundsInset, position.y + boundsInset);
    }

    public void jump() {
        if (!isJumping) {
            verticalVelocity = jumpVelocity;
            isJumping = true;
            animationManager.resetJumpingAnimation();
            jumpSound.play(soundVolume); // Используем текущую громкость при воспроизведении
        }
    }

    // Отрисовка текущего кадра анимации
    public void render(SpriteBatch batch) {
        TextureRegion frame = animationManager.getFrame(isJumping);
        batch.draw(frame, position.x, position.y, spriteWidth, spriteHeight);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    // Сброс позиции игрока
    public void resetPosition() {
        position.y = groundY;
        verticalVelocity = 0;
        isJumping = false;
        animationManager.resetRunningAnimation(); // Возврат к бегу
    }

    public void dispose() {
        animationManager.dispose();
        jumpSound.dispose();
    }
}
