package com.mygdx.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectionScreen {
    private Texture background;
    private Texture backButton, backButtonHover, backButtonPressed;
    private Rectangle backBounds;
    private boolean isBackHovered = false;
    private boolean isBackPressed = false;
    private Texture leftArrow;
    private Texture rightArrow;
    private Rectangle leftArrowBounds;
    private Rectangle rightArrowBounds;
    private List<Texture> characterTextures;
    private List<Sound> characterSounds;
    private int currentCharacterIndex = 0;
    private float virtualWidth;
    private float virtualHeight;
    private long currentSoundId = -1;
    private boolean firstTimeOpened = true;
    private float soundVolume = 1.0f;

    public CharacterSelectionScreen(float virtualWidth, float virtualHeight) {
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;
        background = new Texture("start/character/background.png");
        backButton = new Texture("start/back.png");
        backButtonHover = new Texture("start/back_hover.png");
        backButtonPressed = new Texture("start/back_click.png");
        leftArrow = new Texture("start/character/buttons/left.png");
        rightArrow = new Texture("start/character/buttons/right.png");
        characterTextures = new ArrayList<>();
        characterTextures.add(new Texture("start/character/krosh.png"));
        characterTextures.add(new Texture("start/character/nusha.png"));
        characterTextures.add(new Texture("start/character/barash.png"));
        characterTextures.add(new Texture("start/character/ezhik.png"));
        characterSounds = new ArrayList<>();
        characterSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/krosh_phrase.ogg")));
        characterSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/nusha_phrase.ogg")));
        characterSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/barash_phrase.ogg")));
        characterSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/ezhik_phrase.ogg")));
        setupButtonBounds();
    }

    public void setSoundVolume(float volume) {
        this.soundVolume = volume;
    }

    private void setupButtonBounds() {
        float backButtonWidth = 300f;
        float backButtonHeight = 120f;
        float backButtonX = 165f;
        float backButtonY = virtualHeight - backButtonHeight - 65f;
        backBounds = new Rectangle(backButtonX, backButtonY, backButtonWidth, backButtonHeight);

        float arrowWidth = 180f;
        float arrowHeight = 180f;
        float arrowYPosition = virtualHeight / 2 - arrowHeight / 2 - 35;
        leftArrowBounds = new Rectangle(virtualWidth / 2 - 650f, arrowYPosition, arrowWidth, arrowHeight);
        rightArrowBounds = new Rectangle(virtualWidth / 2 + 450f, arrowYPosition, arrowWidth, arrowHeight);
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, virtualWidth, virtualHeight);
        Texture currentCharacter = characterTextures.get(currentCharacterIndex);
        float charWidth = currentCharacter.getWidth() * 1f;
        float charHeight = currentCharacter.getHeight() * 1f;
        batch.draw(currentCharacter,
            (virtualWidth - charWidth) / 2,
            (virtualHeight - charHeight) / 2 - 63f,
            charWidth,
            charHeight);

        Texture backCurrent = backButton;
        if (isBackPressed) backCurrent = backButtonPressed;
        else if (isBackHovered) backCurrent = backButtonHover;
        batch.draw(backCurrent, backBounds.x, backBounds.y, backBounds.width, backBounds.height);

        batch.draw(leftArrow, leftArrowBounds.x, leftArrowBounds.y, leftArrowBounds.width, leftArrowBounds.height);
        batch.draw(rightArrow, rightArrowBounds.x, rightArrowBounds.y, rightArrowBounds.width, rightArrowBounds.height);
    }

    public void updateInput(float touchX, float touchY, boolean isTouched) {
        isBackHovered = backBounds.contains(touchX, touchY);
        if (isBackHovered && isTouched) {
            isBackPressed = true;
            stopCurrentSound();
        } else {
            isBackPressed = false;
        }
    }

    public boolean isBackClicked(float touchX, float touchY) {
        return backBounds.contains(touchX, touchY);
    }

    public boolean isLeftArrowClicked(float touchX, float touchY) {
        return leftArrowBounds.contains(touchX, touchY);
    }

    public boolean isRightArrowClicked(float touchX, float touchY) {
        return rightArrowBounds.contains(touchX, touchY);
    }

    public void nextCharacter() {
        stopCurrentSound();
        currentCharacterIndex = (currentCharacterIndex + 1) % characterTextures.size();
        playCharacterSound();
    }

    public void previousCharacter() {
        stopCurrentSound();
        currentCharacterIndex = (currentCharacterIndex - 1 + characterTextures.size()) % characterTextures.size();
        playCharacterSound();
    }

    private void playCharacterSound() {
        Sound sound = characterSounds.get(currentCharacterIndex);
        currentSoundId = sound.play(soundVolume);
    }

    public void playCurrentCharacterSound() {
        if (!firstTimeOpened) {
            playCharacterSound();
        }
    }

    private void stopCurrentSound() {
        if (currentSoundId != -1) {
            Sound currentSound = characterSounds.get(currentCharacterIndex);
            currentSound.stop(currentSoundId);
            currentSoundId = -1;
        }
    }

    public void reset() {
        stopCurrentSound();
        currentCharacterIndex = 0;
        firstTimeOpened = false;
    }

    public int getSelectedCharacterIndex() {
        return currentCharacterIndex;
    }

    public void dispose() {
        stopCurrentSound();
        background.dispose();
        backButton.dispose();
        backButtonHover.dispose();
        backButtonPressed.dispose();
        leftArrow.dispose();
        rightArrow.dispose();
        for (Texture texture : characterTextures) {
            texture.dispose();
        }
        for (Sound sound : characterSounds) {
            if (sound != null) sound.dispose();
        }
    }
}

