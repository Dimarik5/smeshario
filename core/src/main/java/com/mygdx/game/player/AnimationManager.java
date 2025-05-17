package com.mygdx.game.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationManager {
    private Animation<TextureRegion> runningAnimation;
    private Animation<TextureRegion> jumpingAnimation;
    private float stateTime = 0;
    private boolean isJumping = false;
    private boolean gameStarted = false;

    public AnimationManager(String runningPath, String jumpingPath) {
        this.runningAnimation = loadAnimation(runningPath, 0.0416f); // 24 кадра/сек (1/24)
        this.jumpingAnimation = loadAnimation(jumpingPath, 0.0416f);
    }

    private Animation<TextureRegion> loadAnimation(String path, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();

        // Загружаем 24 кадра (1.png, 2.png, ..., 24.png)
        for (int i = 1; i <= 24; i++) {
            String filePath = path + "/" + i + ".png";
            frames.add(new TextureRegion(new Texture(Gdx.files.internal(filePath))));
        }

        return new Animation<>(frameDuration, frames);
    }

    public void update(float deltaTime, boolean isJumping, boolean gameStarted) {
        this.isJumping = isJumping;
        this.gameStarted = gameStarted;

        if (gameStarted) {
            stateTime += deltaTime;
        } else {
            stateTime = 0; // Сбрасываем время для первого кадра
        }
    }

    public TextureRegion getCurrentFrame() {
        if (!gameStarted) {
            return runningAnimation.getKeyFrames()[0]; // Первый кадр до старта
        }
        return isJumping ?
            jumpingAnimation.getKeyFrame(stateTime, false) :
            runningAnimation.getKeyFrame(stateTime, true);
    }

    public void dispose() {
        for (TextureRegion frame : runningAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }
        for (TextureRegion frame : jumpingAnimation.getKeyFrames()) {
            frame.getTexture().dispose();
        }
    }
}
