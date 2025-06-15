package com.mygdx.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Main;

public class SettingsScreen {
    private Texture background;
    private Texture sliderBody;
    private Texture sliderController;
    private Texture[] musicTextures;
    private Texture choiceBox;
    private Texture choiceBoxSelected;
    private Texture backButton;
    private Texture backButtonHover;
    private Texture backButtonPressed;

    private Rectangle musicSliderBounds;
    private Rectangle soundSliderBounds;
    private Rectangle[] musicChoiceBounds;
    private Rectangle[] musicSelectionButtonBounds;
    private Rectangle backButtonBounds;

    private boolean isMusicSliderDragging = false;
    private boolean isSoundSliderDragging = false;
    private int selectedMusicIndex = 0;
    private boolean isBackHovered = false;
    private boolean isBackPressed = false;

    private float musicVolume;
    private float soundVolume;
    private Music currentMusic;
    private float virtualWidth;
    private float virtualHeight;
    private Main main;

    public SettingsScreen(float virtualWidth, float virtualHeight,
                          float initialMusicVolume, float initialSoundVolume,
                          Main main) {
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;
        this.musicVolume = initialMusicVolume;
        this.soundVolume = initialSoundVolume;
        this.main = main;

        // Загрузка текстур с исходным масштабом
        background = new Texture("start/settings/background.png");
        sliderBody = new Texture("start/settings/slider_body.png");
        sliderController = new Texture("start/settings/slider_controller.png");

        musicTextures = new Texture[] {
            new Texture("start/settings/music_playlist/tema_krosha.png"),
            new Texture("start/settings/music_playlist/pogonya.png"),
            new Texture("start/settings/music_playlist/obormot.png")
        };

        choiceBox = new Texture("start/settings/music_playlist/choice_box.png");
        choiceBoxSelected = new Texture("start/settings/music_playlist/choice_box_selected.png");

        backButton = new Texture("start/back.png");
        backButtonHover = new Texture("start/back_hover.png");
        backButtonPressed = new Texture("start/back_click.png");

        setupBounds();
    }

    private void setupBounds() {
        // Размеры ползунков (сохраняем исходные размеры текстур)
        float sliderWidth = 400f;
        float sliderHeight = 30f;

        // Размеры элементов выбора музыки
        float musicChoiceWidth = 740f;
        float musicChoiceHeight = 120f;
        float choiceBoxWidth = 73f;
        float choiceBoxHeight = 73f;

        // Размеры кнопки "Назад"
        float backButtonWidth = 300f;
        float backButtonHeight = 120f;

        // Расположение ползунка для музыки (левая половина экрана)
        float sliderOffset = 80f;
        musicSliderBounds = new Rectangle(
            virtualWidth / 4 - sliderWidth / 2 + sliderOffset,
            virtualHeight * 0.66f,
            sliderWidth,
            sliderHeight
        );

        // Расположение ползунка для звука (правая половина экрана)
        float soundSliderOffset = 75f;
        soundSliderBounds = new Rectangle(
            virtualWidth * 3/4 - sliderWidth / 2 - soundSliderOffset,
            virtualHeight * 0.66f,
            sliderWidth,
            sliderHeight
        );

        // Расположение элементов выбора музыки
        // Расположение элементов выбора музыки
        musicChoiceBounds = new Rectangle[3];
        musicSelectionButtonBounds = new Rectangle[3];
        float startY = virtualHeight * 0.35f;
        float spacing = 20f;
        float leftPadding = 25f; // Отступ от левого края прямоугольника

        for (int i = 0; i < 3; i++) {
            // Прямоугольник для фона элемента выбора музыки
            musicChoiceBounds[i] = new Rectangle(
                virtualWidth / 2 - musicChoiceWidth / 2,
                startY - i * (musicChoiceHeight + spacing),
                musicChoiceWidth,
                musicChoiceHeight
            );

            // Кнопка выбора (чекбокс) - теперь слева от текста, центрирована по высоте
            musicSelectionButtonBounds[i] = new Rectangle(
                musicChoiceBounds[i].x + leftPadding, // слева с отступом
                musicChoiceBounds[i].y + (musicChoiceHeight - choiceBoxHeight) / 2, // центрировано по высоте
                choiceBoxWidth,
                choiceBoxHeight
            );
        }

        // Расположение кнопки "Назад"
        float backButtonX = 165f;
        float backButtonY = virtualHeight - backButtonHeight - 65f;
        backButtonBounds = new Rectangle(
            backButtonX,
            backButtonY,
            backButtonWidth,
            backButtonHeight
        );
    }

    public void render(SpriteBatch batch) {
        // Отрисовка фона
        batch.draw(background, 0, 0, virtualWidth, virtualHeight);

        // Отрисовка ползунка музыки
        batch.draw(sliderBody,
            musicSliderBounds.x,
            musicSliderBounds.y,
            musicSliderBounds.width,
            musicSliderBounds.height);

        float musicControllerX = musicSliderBounds.x + musicVolume * (musicSliderBounds.width - 30);
        batch.draw(sliderController,
            musicControllerX - 15, // Смещение для центрирования (новый размер 60x60)
            musicSliderBounds.y - 15,
            60, 60); // Увеличенный и более круглый размер

        // Отрисовка ползунка звука
        batch.draw(sliderBody,
            soundSliderBounds.x,
            soundSliderBounds.y,
            soundSliderBounds.width,
            soundSliderBounds.height);

        float soundControllerX = soundSliderBounds.x + soundVolume * (soundSliderBounds.width - 30);
        batch.draw(sliderController,
            soundControllerX - 15,
            soundSliderBounds.y - 15,
            60, 60);

        // Отрисовка элементов выбора музыки
        for (int i = 0; i < 3; i++) {
            // Сначала рисуем прямоугольник фона
            batch.draw(musicTextures[i],
                musicChoiceBounds[i].x,musicChoiceBounds[i].y,
                musicChoiceBounds[i].width,
                musicChoiceBounds[i].height);

            // Затем рисуем кнопку выбора (чекбокс) справа
            Texture currentChoiceBox = (i == selectedMusicIndex) ? choiceBoxSelected : choiceBox;
            batch.draw(currentChoiceBox,
                musicSelectionButtonBounds[i].x,
                musicSelectionButtonBounds[i].y,
                musicSelectionButtonBounds[i].width,
                musicSelectionButtonBounds[i].height);
        }

        // Отрисовка кнопки "Назад"
        Texture currentBackTexture = backButton;
        if (isBackPressed) {
            currentBackTexture = backButtonPressed;
        } else if (isBackHovered) {
            currentBackTexture = backButtonHover;
        }
        batch.draw(currentBackTexture,
            backButtonBounds.x,
            backButtonBounds.y,
            backButtonBounds.width,
            backButtonBounds.height);
    }

    public void updateInput(float touchX, float touchY, boolean isTouched) {
        // Обновление состояния кнопки "Назад"
        isBackHovered = backButtonBounds.contains(touchX, touchY);
        isBackPressed = isBackHovered && isTouched;

        // Обработка выбора музыки
        if (isTouched) {
            for (int i = 0; i < 3; i++) {
                if (musicSelectionButtonBounds[i].contains(touchX, touchY)) {
                    selectedMusicIndex = i;
                    break;
                }
            }
        }

        // Обработка перетаскивания ползунков
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

        // Обновление значений громкости
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
            if (main != null) {
                main.updateGlobalSoundVolume(soundVolume);
            }
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

