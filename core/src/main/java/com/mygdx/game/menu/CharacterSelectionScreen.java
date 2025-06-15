package com.mygdx.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Экран выбора персонажа.
 * Позволяет игроку выбирать между несколькими персонажами,
 * просматривать их изображения и прослушивать характерные звуки.
 */
public class CharacterSelectionScreen {

    // Текстуры интерфейса
    private Texture background;              // Фон экрана выбора персонажа
    private Texture backButton;              // Кнопка "Назад" (обычное состояние)
    private Texture backButtonHover;         // Кнопка "Назад" (при наведении)
    private Texture backButtonPressed;       // Кнопка "Назад" (при нажатии)
    private Texture leftArrow;              // Стрелка влево
    private Texture rightArrow;             // Стрелка вправо

    // Границы элементов для обработки ввода
    private Rectangle backBounds;           // Границы кнопки "Назад"
    private Rectangle leftArrowBounds;      // Границы стрелки влево
    private Rectangle rightArrowBounds;     // Границы стрелки вправо

    // Состояния кнопки "Назад"
    private boolean isBackHovered = false;  // Флаг наведения на кнопку "Назад"
    private boolean isBackPressed = false;  // Флаг нажатия кнопки "Назад"

    // Данные персонажей
    private List<Texture> characterTextures; // Текстуры персонажей
    private List<Sound> characterSounds;     // Звуки персонажей
    private int currentCharacterIndex = 0;   // Индекс текущего персонажа

    // Размеры виртуального экрана
    private float virtualWidth;              // Ширина виртуального экрана
    private float virtualHeight;             // Высота виртуального экрана

    // Аудио
    private long currentSoundId = -1;        // ID текущего воспроизводимого звука
    private boolean firstTimeOpened = true;  // Флаг первого открытия экрана
    private float soundVolume = 1.0f;        // Громкость звуков персонажей

    /**
     * Конструктор экрана выбора персонажа.
     *
     * @param virtualWidth   Ширина виртуального экрана
     * @param virtualHeight  Высота виртуального экрана
     */
    public CharacterSelectionScreen(float virtualWidth, float virtualHeight) {
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;

        // Загрузка текстур интерфейса
        loadInterfaceTextures();

        // Загрузка данных персонажей
        loadCharactersData();

        // Настройка границ элементов интерфейса
        setupButtonBounds();
    }

    /**
     * Загрузка текстур интерфейса.
     */
    private void loadInterfaceTextures() {
        background = new Texture("start/character/background.png");
        backButton = new Texture("start/back.png");
        backButtonHover = new Texture("start/back_hover.png");
        backButtonPressed = new Texture("start/back_click.png");
        leftArrow = new Texture("start/character/buttons/left.png");
        rightArrow = new Texture("start/character/buttons/right.png");
    }

    /**
     * Загрузка данных персонажей (текстур и звуков).
     */
    private void loadCharactersData() {
        // Инициализация списков
        characterTextures = new ArrayList<>();
        characterSounds = new ArrayList<>();

        // Загрузка текстур персонажей
        characterTextures.add(new Texture("start/character/krosh.png"));
        characterTextures.add(new Texture("start/character/nusha.png"));
        characterTextures.add(new Texture("start/character/barash.png"));
        characterTextures.add(new Texture("start/character/ezhik.png"));

        // Загрузка звуков персонажей
        characterSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/krosh_phrase.ogg")));
        characterSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/nusha_phrase.ogg")));
        characterSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/barash_phrase.ogg")));
        characterSounds.add(Gdx.audio.newSound(Gdx.files.internal("music/ezhik_phrase.ogg")));
    }

    /**
     * Настройка границ элементов интерфейса.
     */
    private void setupButtonBounds() {
        // Настройка кнопки "Назад"
        float backButtonWidth = 300f;
        float backButtonHeight = 120f;
        float backButtonX = 165f;
        float backButtonY = virtualHeight - backButtonHeight - 65f;
        backBounds = new Rectangle(backButtonX, backButtonY, backButtonWidth, backButtonHeight);

        // Настройка стрелок переключения персонажей
        float arrowWidth = 180f;
        float arrowHeight = 180f;
        float arrowYPosition = virtualHeight / 2 - arrowHeight / 2 - 35;

        // Левая стрелка (смещена влево от центра)
        leftArrowBounds = new Rectangle(
            virtualWidth / 2 - 650f,
            arrowYPosition,
            arrowWidth,
            arrowHeight
        );

        // Правая стрелка (смещена вправо от центра)
        rightArrowBounds = new Rectangle(
            virtualWidth / 2 + 450f,
            arrowYPosition,
            arrowWidth,
            arrowHeight
        );
    }

    /**
     * Установка громкости звуков персонажей.
     *
     * @param volume Новая громкость (0.0 - 1.0)
     */
    public void setSoundVolume(float volume) {
        this.soundVolume = volume;
    }

    /**
     * Отрисовка экрана выбора персонажа.
     *
     * @param batch SpriteBatch для отрисовки
     */
    public void render(SpriteBatch batch) {
        // Отрисовка фона
        batch.draw(background, 0, 0, virtualWidth, virtualHeight);

        // Отрисовка текущего персонажа
        drawCurrentCharacter(batch);

        // Отрисовка кнопки "Назад" с учетом состояния
        drawBackButton(batch);

        // Отрисовка стрелок переключения
        drawArrows(batch);
    }

    /**
     * Отрисовка текущего персонажа.
     */
    private void drawCurrentCharacter(SpriteBatch batch) {
        Texture currentCharacter = characterTextures.get(currentCharacterIndex);
        float charWidth = currentCharacter.getWidth() * 1f;
        float charHeight = currentCharacter.getHeight() * 1f;

        batch.draw(
            currentCharacter,
            (virtualWidth - charWidth) / 2,
            (virtualHeight - charHeight) / 2 - 63f,
            charWidth,
            charHeight
        );
    }

    /**
     * Отрисовка кнопки "Назад".
     */
    private void drawBackButton(SpriteBatch batch) {
        Texture backCurrent = backButton;
        if (isBackPressed) backCurrent = backButtonPressed;
        else if (isBackHovered) backCurrent = backButtonHover;

        batch.draw(
            backCurrent,
            backBounds.x,
            backBounds.y,
            backBounds.width,
            backBounds.height
        );
    }

    /**
     * Отрисовка стрелок переключения персонажей.
     */
    private void drawArrows(SpriteBatch batch) {
        batch.draw(
            leftArrow,
            leftArrowBounds.x,
            leftArrowBounds.y,
            leftArrowBounds.width,
            leftArrowBounds.height
        );

        batch.draw(
            rightArrow,
            rightArrowBounds.x,
            rightArrowBounds.y,
            rightArrowBounds.width,
            rightArrowBounds.height
        );
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
        isBackHovered = backBounds.contains(touchX, touchY);

        if (isBackHovered && isTouched) {
            isBackPressed = true;stopCurrentSound();
        } else {
            isBackPressed = false;
        }
    }

    /**
     * Проверка нажатия кнопки "Назад".
     */
    public boolean isBackClicked(float touchX, float touchY) {
        return backBounds.contains(touchX, touchY);
    }

    /**
     * Проверка нажатия стрелки влево.
     */
    public boolean isLeftArrowClicked(float touchX, float touchY) {
        return leftArrowBounds.contains(touchX, touchY);
    }

    /**
     * Проверка нажатия стрелки вправо.
     */
    public boolean isRightArrowClicked(float touchX, float touchY) {
        return rightArrowBounds.contains(touchX, touchY);
    }

    /**
     * Переключение на следующего персонажа.
     */
    public void nextCharacter() {
        stopCurrentSound();
        currentCharacterIndex = (currentCharacterIndex + 1) % characterTextures.size();
        playCharacterSound();
    }

    /**
     * Переключение на предыдущего персонажа.
     */
    public void previousCharacter() {
        stopCurrentSound();
        currentCharacterIndex = (currentCharacterIndex - 1 + characterTextures.size()) % characterTextures.size();
        playCharacterSound();
    }

    /**
     * Воспроизведение звука текущего персонажа.
     */
    private void playCharacterSound() {
        Sound sound = characterSounds.get(currentCharacterIndex);
        currentSoundId = sound.play(soundVolume);
    }

    /**
     * Воспроизведение звука текущего персонажа (публичный метод).
     */
    public void playCurrentCharacterSound() {
        if (!firstTimeOpened) {
            playCharacterSound();
        }
    }

    /**
     * Остановка текущего звука персонажа.
     */
    private void stopCurrentSound() {
        if (currentSoundId != -1) {
            Sound currentSound = characterSounds.get(currentCharacterIndex);
            currentSound.stop(currentSoundId);
            currentSoundId = -1;
        }
    }

    /**
     * Сброс состояния экрана.
     */
    public void reset() {
        stopCurrentSound();
        currentCharacterIndex = 0;
        firstTimeOpened = false;
    }

    /**
     * Получение индекса выбранного персонажа.
     */
    public int getSelectedCharacterIndex() {
        return currentCharacterIndex;
    }

    /**
     * Освобождение ресурсов.
     */
    public void dispose() {
        stopCurrentSound();

        // Освобождение текстур интерфейса
        background.dispose();
        backButton.dispose();
        backButtonHover.dispose();
        backButtonPressed.dispose();
        leftArrow.dispose();
        rightArrow.dispose();

        // Освобождение текстур персонажей
        for (Texture texture : characterTextures) {
            texture.dispose();
        }

        // Освобождение звуков персонажей
        for (Sound sound : characterSounds) {
            if (sound != null) sound.dispose();
        }
    }
}

