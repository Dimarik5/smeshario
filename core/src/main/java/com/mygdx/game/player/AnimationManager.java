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
    private float stateTime;

    public AnimationManager(String runningPath, String jumpingPath) {
        this.runningAnimation = createAnimation(runningPath, 1/24f, Animation.PlayMode.LOOP);
        this.jumpingAnimation = createAnimation(jumpingPath, 1/24f, Animation.PlayMode.LOOP);
        this.stateTime = 0f;
    }

    private Animation<TextureRegion> createAnimation(String path, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= 24; i++) {
            Texture texture = new Texture(Gdx.files.internal(path + "/" + i + ".png"));
            frames.add(new TextureRegion(texture));
        }
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(playMode);
        return animation;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public TextureRegion getFrame(boolean isJumping) {
        return isJumping ?
            jumpingAnimation.getKeyFrame(stateTime) :
            runningAnimation.getKeyFrame(stateTime);
    }

    @Override
    public void dispose() {
        disposeAnimation(runningAnimation);
        disposeAnimation(jumpingAnimation);
    }

    private void disposeAnimation(Animation<TextureRegion> animation) {
        for (TextureRegion region : animation.getKeyFrames()) {
            region.getTexture().dispose();
        }
    }
}
