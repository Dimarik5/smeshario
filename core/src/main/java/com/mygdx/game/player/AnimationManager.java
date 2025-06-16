package com.mygdx.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class AnimationManager implements Disposable {
    private final Animation<TextureRegion> runningAnimation;
    private final Animation<TextureRegion> jumpingAnimation;
    // Добавляем списки для хранения созданных текстур
    private final Array<Texture> runningTextures;
    private final Array<Texture> jumpingTextures;

    private float runningStateTime;
    private float jumpingStateTime;

    public AnimationManager(String runningPath, String jumpingPath) {
        this.runningTextures = new Array<>();
        // Передаем список текстур для заполнения в createAnimation
        this.runningAnimation = createAnimation(runningPath, runningTextures, 1/24f, Animation.PlayMode.LOOP);

        this.jumpingTextures = new Array<>();
        // Передаем список текстур для заполнения в createAnimation
        this.jumpingAnimation = createAnimation(jumpingPath, jumpingTextures, 1/24f, Animation.PlayMode.LOOP);

        this.runningStateTime = 0f;
        this.jumpingStateTime = 0f;
    }

    // Модифицируем метод createAnimation для сохранения текстур
    private Animation<TextureRegion> createAnimation(String path, Array<Texture> texturesList, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= 24; i++) {
            Texture texture = new Texture(Gdx.files.internal(path + "/" + i + ".png"));
            texturesList.add(texture); // Добавляем созданную текстуру в список
            frames.add(new TextureRegion(texture));
        }
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(playMode);
        return animation;
    }

    // Обновляем время только для активной анимации
    public void update(float deltaTime, boolean isJumping) {
        if (isJumping) {
            jumpingStateTime += deltaTime;
        } else {
            runningStateTime += deltaTime;
        }
    }

    public TextureRegion getFrame(boolean isJumping) {
        return isJumping ?
            jumpingAnimation.getKeyFrame(jumpingStateTime) : // Используем jumpingStateTime
            runningAnimation.getKeyFrame(runningStateTime);   // Используем runningStateTime
    }

    // Метод для сброса времени анимации бега
    public void resetRunningAnimation() {
        runningStateTime = 0f;
    }

    // Метод для сброса времени анимации прыжка
    public void resetJumpingAnimation() {
        jumpingStateTime = 0f;
    }

    // Проверяет, закончилась ли анимация прыжка
    public boolean isJumpFinished() {
        return jumpingAnimation.isAnimationFinished(jumpingStateTime);
    }

    @Override
    public void dispose() {
        // Теперь освобождаем текстуры из наших списков
        disposeTextures(runningTextures);
        disposeTextures(jumpingTextures);
    }

    // Метод для освобождения всех текстур из списка
    private void disposeTextures(Array<Texture> textures) {
        for (Texture texture : textures) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear(); // Очищаем список после освобождения текстур
    }
}
