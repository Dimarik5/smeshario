package com.mygdx.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Main;

/**
 * Класс экрана настроек игры.
 * Позволяет управлять громкостью музыки и звуков, выбирать музыкальные треки.
 */
public class SettingsScreen {

    // Текстуры для элементов интерфейса
    private Texture background;              // Фон экрана настроек
    private Texture sliderBody;              // Основа ползунка громкости
    private Texture sliderController;        // Контроллер ползунка громкости
    private Texture[] musicTextures;         // Текстуры для музыкальных треков
    private Texture choiceBox;               // Чекбокс выбора музыки (неактивный)
    private Texture choiceBoxSelected;       // Чекбокс выбора музыки (активный)
    private Texture backButton;              // Кнопка "Назад" (обычное состояние)
    private Texture backButtonHover;         // Кнопка "Назад" (при наведении)
    private Texture backButtonPressed;       // Кнопка "Назад" (при нажатии)

    // Границы элементов для обработки ввода
    private Rectangle musicSliderBounds;     // Границы ползунка громкости музыки
    private Rectangle soundSliderBounds;     // Границы ползунка громкости звуков
    private Rectangle[] musicChoiceBounds;   // Границы элементов выбора музыки
    private Rectangle[] musicSelectionButtonBounds; // Границы чекбоксов выбора музыки
    private Rectangle backButtonBounds;      // Границы кнопки "Назад"

    // Флаги состояния элементов
    private boolean isMusicSliderDragging = false; // Флаг перетаскивания ползунка музыки
    private boolean isSoundSliderDragging = false; // Флаг перетаскивания ползунка звуков
    private int selectedMusicIndex = 0;      // Индекс выбранного музыкального трека
    private boolean isBackHovered = false;   // Флаг наведения на кнопку "Назад"
    private boolean isBackPressed = false;   // Флаг нажатия кнопки "Назад"

    // Настройки громкости
    private float musicVolume;               // Текущая громкость музыки (0-1)
    private float soundVolume;               // Текущая громкость звуков (0-1)

    // Аудио
    private Music currentMusic;              // Текущий играющий музыкальный трек

    // Размеры виртуального экрана
    private float virtualWidth;              // Ширина виртуального экрана
    private float virtualHeight;             // Высота виртуального экрана

    // Ссылка на главный класс игры
    private Main main;

    /**
     * Конструктор экрана настроек.
     *
     * @param virtualWidth         Ширина виртуального экрана
     * @param virtualHeight        Высота виртуального экрана
     * @param initialMusicVolume   Начальная громкость музыки
     * @param initialSoundVolume   Начальная громкость звуков
     * @param main                 Ссылка на главный класс игры
     */
    public SettingsScreen(float virtualWidth, float virtualHeight,
                          float initialMusicVolume, float initialSoundVolume,
                          Main main) {
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;
        this.musicVolume = initialMusicVolume;
        this.soundVolume = initialSoundVolume;
        this.main = main;

        // Загрузка текстур
        loadTextures();

        // Настройка границ элементов интерфейса
        setupBounds();
    }

    /**
     * Загрузка текстур для элементов интерфейса.
     */
    private void loadTextures() {
        background = new Texture("start/settings/background.png");
        sliderBody = new Texture("start/settings/slider_body.png");
        sliderController = new Texture("start/settings/slider_controller.png");// Загрузка текстур для музыкальных треков
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
    }

    /**
     * Настройка границ и позиций элементов интерфейса.
     */
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

    /**
     * Отрисовка экрана настроек.
     *
     * @param batch SpriteBatch для отрисовки
     */
    public void render(SpriteBatch batch) {
        // Отрисовка фона
        batch.draw(background, 0, 0, virtualWidth, virtualHeight);

        // Отрисовка ползунка музыки
        drawSlider(batch, musicSliderBounds, musicVolume);

        // Отрисовка ползунка звука
        drawSlider(batch, soundSliderBounds, soundVolume);

        // Отрисовка элементов выбора музыки
        drawMusicSelection(batch);

        // Отрисовка кнопки "Назад"
        drawBackButton(batch);
    }

    /**
     * Отрисовка ползунка громкости.
     *
     * @param batch   SpriteBatch для отрисовки
     * @param bounds  Границы ползунка
     * @param volume  Текущее значение громкости (0-1)
     */
    private void drawSlider(SpriteBatch batch, Rectangle bounds, float volume) {
        // Отрисовка основы ползунка
        batch.draw(sliderBody,
            bounds.x,
            bounds.y,
            bounds.width,
            bounds.height);

        // Вычисление позиции контроллера
        float controllerX = bounds.x + volume * (bounds.width - 30);

        // Отрисовка контроллера ползунка
        batch.draw(sliderController,
            controllerX - 15, // Смещение для центрирования
            bounds.y - 15,
            60, 60); // Увеличенный и более круглый размер
    }

    /**
     * Отрисовка элементов выбора музыки.
     *
     * @param batch SpriteBatch для отрисовки
     */
    private void drawMusicSelection(SpriteBatch batch) {
        for (int i = 0; i < 3; i++) {
            // Отрисовка фона элемента выбора музыки
            batch.draw(musicTextures[i],
                musicChoiceBounds[i].x,
                musicChoiceBounds[i].y,
                musicChoiceBounds[i].width,
                musicChoiceBounds[i].height);

            // Отрисовка чекбокса (активного или неактивного)
            Texture currentChoiceBox = (i == selectedMusicIndex) ? choiceBoxSelected : choiceBox;
            batch.draw(currentChoiceBox,
                musicSelectionButtonBounds[i].x,
                musicSelectionButtonBounds[i].y,
                musicSelectionButtonBounds[i].width,
                musicSelectionButtonBounds[i].height);
        }
    }

    /**
     * Отрисовка кнопки "Назад" с учетом состояния (наведение/нажатие).
     *
     * @param batch SpriteBatch для отрисовки
     */
    private void drawBackButton(SpriteBatch batch) {
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

    /**
     * Обновление состояния элементов на основе ввода пользователя.
     *
     * @param touchX     Координата X касания
     * @param touchY     Координата Y касания
     * @param isTouched  Флаг наличия касания
     */
    public void updateInput(float touchX, float touchY, boolean isTouched) {
        // Обновление состояния кнопки "Назад"
        updateBackButtonState(touchX, touchY, isTouched);

        // Обработка выбора музыки
        handleMusicSelection(touchX, touchY, isTouched);

        // Обработка перетаскивания ползунков
        handleSliderDragging(touchX, touchY, isTouched);

        // Обновление значений громкости
        updateVolumeValues(touchX);
    }

    /**
     * Обновление состояния кнопки "Назад".
     */
    private void updateBackButtonState(float touchX, float touchY, boolean isTouched) {
        isBackHovered = backButtonBounds.contains(touchX, touchY);
        isBackPressed = isBackHovered && isTouched;
    }

    /**
     * Обработка выбора музыкального трека.
     */
    private void handleMusicSelection(float touchX, float touchY, boolean isTouched) {
        if (isTouched) {
            for (int i = 0; i < 3; i++) {
                if (musicSelectionButtonBounds[i].contains(touchX, touchY)) {
                    selectedMusicIndex = i;
                    break;
                }
            }
        }
    }

    /**
     * Обработка перетаскивания ползунков громкости.
     */
    private void handleSliderDragging(float touchX, float touchY, boolean isTouched) {
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
    }

    /*** Обновление значений громкости на основе позиции ползунков.
     */
    private void updateVolumeValues(float touchX) {
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

    /**
     * Установка текущего музыкального трека.
     *
     * @param music Музыкальный трек
     */
    public void setCurrentMusic(Music music) {
        this.currentMusic = music;
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    /**
     * Проверка, была ли нажата кнопка "Назад".
     *
     * @param touchX Координата X касания
     * @param touchY Координата Y касания
     * @return true, если кнопка была нажата
     */
    public boolean isBackClicked(float touchX, float touchY) {
        return backButtonBounds.contains(touchX, touchY);
    }

    /**
     * Получение текущей громкости музыки.
     *
     * @return Громкость музыки (0-1)
     */
    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Получение текущей громкости звуков.
     *
     * @return Громкость звуков (0-1)
     */
    public float getSoundVolume() {
        return soundVolume;
    }

    /**
     * Получение индекса выбранного музыкального трека.
     *
     * @return Индекс трека (0-2)
     */
    public int getSelectedMusicIndex() {
        return selectedMusicIndex;
    }

    /**
     * Освобождение ресурсов.
     */
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
