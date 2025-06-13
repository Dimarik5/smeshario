package com.mygdx.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SettingsScreen {

    // Текстуры
    private Texture background;
    private Texture sliderBody;
    private Texture sliderController;
    private Texture[] musicTextures;
    private Texture choiceBox;
    private Texture choiceBoxSelected;
    private Texture backButton;
    private Texture backButtonHover;
    private Texture backButtonPressed;

    // Границы элементов
    private Rectangle musicSliderBounds;
    private Rectangle soundSliderBounds;
    private Rectangle[] musicChoiceBounds;
    private Rectangle backButtonBounds;

    // Состояния
    private boolean isMusicSliderDragging = false;
    private boolean isSoundSliderDragging = false;
    private int selectedMusicIndex = 0;
    private boolean isBackHovered = false;
    private boolean isBackPressed = false;

    // Позиции ползунков (0-1)
    private float musicVolume;
    private float soundVolume;

    // Ссылка на текущую играющую музыку
    private Music currentMusic;

    // Размеры виртуального экрана
    private float virtualWidth;
    private float virtualHeight;

    public SettingsScreen(float virtualWidth, float virtualHeight, float initialMusicVolume, float initialSoundVolume) {
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;
        this.musicVolume = initialMusicVolume;
        this.soundVolume = initialSoundVolume;

        // Загрузка текстур
        background = new Texture("start/settings/background.png");
        sliderBody = new Texture("start/settings/slider_body.png");
        sliderController = new Texture("start/settings/slider_controller.png");

        // Загрузка текстур музыки
        musicTextures = new Texture[] {
            new Texture("start/settings/music_playlist/obormot.png"),
            new Texture("start/settings/music_playlist/pogonya.png"),
            new Texture("start/settings/music_playlist/tema_krosha.png")
        };

        choiceBox = new Texture("start/settings/music_playlist/choice_box.png");
        choiceBoxSelected = new Texture("start/settings/music_playlist/choice_box_selected.png");

        backButton = new Texture("start/back.png");
        backButtonHover = new Texture("start/back_hover.png");
        backButtonPressed = new Texture("start/back_click.png");

        setupBounds();
    }

    private void setupBounds() {
        float sliderWidth = 400f;
        float sliderHeight = 30f;
        float musicChoiceWidth = 300f;
        float musicChoiceHeight = 80f;
        float backButtonWidth = 200f;
        float backButtonHeight = 80f;

        musicSliderBounds = new Rectangle(
            virtualWidth / 2 - sliderWidth / 2,
            virtualHeight * 0.6f,
            sliderWidth,
            sliderHeight
        );

        soundSliderBounds = new Rectangle(
            virtualWidth / 2 - sliderWidth / 2,
            virtualHeight * 0.5f,
            sliderWidth,
            sliderHeight
        );

        musicChoiceBounds = new Rectangle[3];
        float startY = virtualHeight * 0.3f;
        float spacing = 20f;

        for (int i = 0; i < 3; i++) {
            musicChoiceBounds[i] = new Rectangle(
                virtualWidth / 2 - musicChoiceWidth / 2,
                startY - i * (musicChoiceHeight + spacing),
                musicChoiceWidth,
                musicChoiceHeight
            );
        }

        backButtonBounds = new Rectangle(
            50f,
            virtualHeight - backButtonHeight - 50f,
            backButtonWidth,
            backButtonHeight
        );
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, virtualWidth, virtualHeight);// Отрисовка слайдеров
        batch.draw(sliderBody, musicSliderBounds.x, musicSliderBounds.y, musicSliderBounds.width, musicSliderBounds.height);
        float musicControllerX = musicSliderBounds.x + musicVolume * (musicSliderBounds.width - 30);
        batch.draw(sliderController, musicControllerX, musicSliderBounds.y - 10, 30, 50);

        batch.draw(sliderBody, soundSliderBounds.x, soundSliderBounds.y, soundSliderBounds.width, soundSliderBounds.height);
        float soundControllerX = soundSliderBounds.x + soundVolume * (soundSliderBounds.width - 30);
        batch.draw(sliderController, soundControllerX, soundSliderBounds.y - 10, 30, 50);

        // Отрисовка выбора музыки
        for (int i = 0; i < 3; i++) {
            if (i == selectedMusicIndex) {
                batch.draw(choiceBoxSelected, musicChoiceBounds[i].x - 50, musicChoiceBounds[i].y, 50, musicChoiceBounds[i].height);
            } else {
                batch.draw(choiceBox, musicChoiceBounds[i].x - 50, musicChoiceBounds[i].y, 50, musicChoiceBounds[i].height);
            }
            batch.draw(musicTextures[i], musicChoiceBounds[i].x, musicChoiceBounds[i].y, musicChoiceBounds[i].width, musicChoiceBounds[i].height);
        }

        // Отрисовка кнопки назад
        Texture currentBackTexture = backButton;
        if (isBackPressed) {
            currentBackTexture = backButtonPressed;
        } else if (isBackHovered) {
            currentBackTexture = backButtonHover;
        }
        batch.draw(currentBackTexture, backButtonBounds.x, backButtonBounds.y, backButtonBounds.width, backButtonBounds.height);
    }

    public void updateInput(float touchX, float touchY, boolean isTouched) {
        isBackHovered = backButtonBounds.contains(touchX, touchY);

        if (isBackHovered && isTouched) {
            isBackPressed = true;
        } else {
            isBackPressed = false;
        }

        if (isTouched) {
            for (int i = 0; i < 3; i++) {
                if (musicChoiceBounds[i].contains(touchX, touchY)) {
                    selectedMusicIndex = i;
                    break;
                }
            }
        }

        if (isTouched) {
            if (musicSliderBounds.contains(touchX, touchY)) {
                isMusicSliderDragging = true;
            }
            if (soundSliderBounds.contains(touchX, touchY)) {
                isSoundSliderDragging = true;
            }
        } else {
            isMusicSliderDragging = false;
            isSoundSliderDragging = false;
        }

        // Обновление громкости музыки в реальном времени
        if (isMusicSliderDragging) {
            musicVolume = (touchX - musicSliderBounds.x) / musicSliderBounds.width;
            musicVolume = Math.max(0, Math.min(1, musicVolume));
            if (currentMusic != null) {
                currentMusic.setVolume(musicVolume);
            }
        }

        if (isSoundSliderDragging) {
            soundVolume = (touchX - soundSliderBounds.x) / soundSliderBounds.width;
            soundVolume = Math.max(0, Math.min(1, soundVolume));
        }
    }

    public void setCurrentMusic(Music music) {
        this.currentMusic = music;
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    public boolean isBackClicked(float touchX, float touchY) {
        return backButtonBounds.contains(touchX, touchY);
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public int getSelectedMusicIndex() {
        return selectedMusicIndex;
    }

    public void dispose() {
        background.dispose();
        sliderBody.dispose();
        sliderController.dispose();
        for (Texture tex : musicTextures) {
            tex.dispose();
        }
        choiceBox.dispose();
        choiceBoxSelected.dispose();
        backButton.dispose();
        backButtonHover.dispose();
        backButtonPressed.dispose();
    }
}

