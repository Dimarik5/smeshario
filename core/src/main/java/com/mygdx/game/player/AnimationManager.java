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
    private float runningStateTime; // Отдельное время для бега
    private float jumpingStateTime; // Отдельное время для прыжка

    public AnimationManager(String runningPath, String jumpingPath) {
        this.runningAnimation = createAnimation(runningPath, 1/24f, Animation.PlayMode.LOOP);
        this.jumpingAnimation = createAnimation(jumpingPath, 1/24f, Animation.PlayMode.LOOP); // Прыжок тоже может быть LOOP, если он короткий и должен повторяться в воздухе, или NORMAL, если он однократный. Для вашей проблемы это не так важно, как сброс stateTime.
        this.runningStateTime = 0f;
        this.jumpingStateTime = 0f;
    }

    private Animation<TextureRegion> createAnimation(String path, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>();
        // Убедитесь, что у вас действительно 24 кадра для каждой анимации,
        // или сделайте количество кадров параметром или определите его динамически.
        for (int i = 1; i <= 24; i++) {
            Texture texture = new Texture(Gdx.files.internal(path + "/" + i + ".png"));
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

    @Override
    public void dispose() {
        disposeAnimation(runningAnimation);
        disposeAnimation(jumpingAnimation);
    }

    // Немного улучшенная версия disposeAnimation
    private void disposeAnimation(Animation<TextureRegion> animation) {
        // getKeyFrames() возвращает TextureRegion[], а не Object[]
        TextureRegion[] frames = animation.getKeyFrames();
        for (TextureRegion frame : frames) {
            if (frame != null && frame.getTexture() != null) {
                frame.getTexture().dispose();
            }
        }
    }
}
