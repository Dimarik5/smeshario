package com.mygdx.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Класс главного меню игры.
 * Содержит кнопки для навигации: Новая игра, Персонаж, Настройки, Выход.
 */
public class MainMenu {

    // Текстуры фона и текстовых элементов
    private Texture menuBackground;      // Фон главного меню
    private Texture titleTexture;       // Логотип игры
    private Texture betaVersionTexture; // Текст "Beta версия"
    private Texture copyrightTexture;   // Текст копирайта

    // Текстуры кнопок (обычное состояние, наведение, нажатие)
    private Texture newGameButton, newGameButtonHover, newGameButtonPressed;
    private Texture characterButton, characterButtonHover, characterButtonPressed;
    private Texture settingsButton, settingsButtonHover, settingsButtonPressed;
    private Texture exitButton, exitButtonHover, exitButtonPressed;

    // Границы кнопок для обработки ввода
    private Rectangle newGameBounds;    // Границы кнопки "Новая игра"
    private Rectangle characterBounds;  // Границы кнопки "Персонаж"
    private Rectangle settingsBounds;   // Границы кнопки "Настройки"
    private Rectangle exitBounds;       // Границы кнопки "Выход"

    // Состояния кнопок
    private boolean isNewGameHovered = false;   // Наведение на "Новую игру"
    private boolean isNewGamePressed = false;   // Нажатие "Новой игры"
    private boolean isCharacterHovered = false; // Наведение на "Персонажа"
    private boolean isCharacterPressed = false; // Нажатие "Персонажа"
    private boolean isSettingsHovered = false;  // Наведение на "Настройки"
    private boolean isSettingsPressed = false;  // Нажатие "Настроек"
    private boolean isExitHovered = false;     // Наведение на "Выход"
    private boolean isExitPressed = false;     // Нажатие "Выхода"

    /**
     * Конструктор главного меню.
     *
     * @param virtualWidth   Ширина виртуального экрана
     * @param virtualHeight  Высота виртуального экрана
     */
    public MainMenu(float virtualWidth, float virtualHeight) {
        // Загрузка фона и текстовых элементов
        loadBackgroundAndText();

        // Загрузка текстур кнопок
        loadButtonTextures();

        // Настройка размеров и позиций кнопок
        setupButtonBounds(virtualWidth, virtualHeight);
    }

    /**
     * Загрузка фона и текстовых элементов меню.
     */
    private void loadBackgroundAndText() {
        menuBackground = new Texture("start/start_background.png");
        titleTexture = new Texture("start/main_menu/logo_text.png");
        betaVersionTexture = new Texture("start/main_menu/beta.png");
        copyrightTexture = new Texture("start/main_menu/copyright.png");
    }

    /**
     * Загрузка текстур для всех кнопок меню.
     */
    private void loadButtonTextures() {
        // Кнопка "Новая игра"
        newGameButton = new Texture("start/main_menu/buttons/new_game.png");
        newGameButtonHover = new Texture("start/main_menu/buttons/new_game_hover.png");
        newGameButtonPressed = new Texture("start/main_menu/buttons/new_game_click.png");

        // Кнопка "Персонаж"
        characterButton = new Texture("start/main_menu/buttons/character.png");
        characterButtonHover = new Texture("start/main_menu/buttons/character_hover.png");
        characterButtonPressed = new Texture("start/main_menu/buttons/character_click.png");

        // Кнопка "Настройки"
        settingsButton = new Texture("start/main_menu/buttons/settings.png");
        settingsButtonHover = new Texture("start/main_menu/buttons/settings_hover.png");
        settingsButtonPressed = new Texture("start/main_menu/buttons/settings_click.png");

        // Кнопка "Выход"
        exitButton = new Texture("start/main_menu/buttons/exit.png");
        exitButtonHover = new Texture("start/main_menu/buttons/exit_hover.png");
        exitButtonPressed = new Texture("start/main_menu/buttons/exit_click.png");
    }

    /**
     * Настройка размеров и позиций кнопок меню.
     *
     * @param virtualWidth   Ширина виртуального экрана
     * @param virtualHeight  Высота виртуального экрана
     */
    private void setupButtonBounds(float virtualWidth, float virtualHeight) {
        float buttonWidth = 400f;          // Ширина кнопок
        float buttonHeight = 100f;         // Высота кнопок
        float verticalSpacing = 15f;       // Отступ между кнопками
        float totalHeight = 4 * buttonHeight + 3 * verticalSpacing; // Общая высота всех кнопок с отступами
        float startY = (virtualHeight - totalHeight) / 2 - 150f;    // Начальная позиция по Y (смещена вниз)

        // Позиционирование кнопок снизу вверх:
        // Выход -> Настройки -> Персонаж -> Новая игра

        // Кнопка "Новая игра" (верхняя)
        newGameBounds = new Rectangle(
            (virtualWidth - buttonWidth) / 2,
            startY + 3 * (buttonHeight + verticalSpacing),
            buttonWidth,
            buttonHeight
        );

        // Кнопка "Персонаж"
        characterBounds = new Rectangle(
            (virtualWidth - buttonWidth) / 2,
            startY + 2 * (buttonHeight + verticalSpacing),
            buttonWidth,
            buttonHeight
        );

        // Кнопка "Настройки"
        settingsBounds = new Rectangle(
            (virtualWidth - buttonWidth) / 2,
            startY + (buttonHeight + verticalSpacing),
            buttonWidth,
            buttonHeight
        );

        // Кнопка "Выход" (нижняя)
        exitBounds = new Rectangle(
            (virtualWidth - buttonWidth) / 2,
            startY,
            buttonWidth,
            buttonHeight
        );
    }

    /**
     * Отрисовка главного меню.
     *
     * @param batch          SpriteBatch для отрисовки
     * @param virtualWidth   Ширина виртуального экрана
     * @param virtualHeight  Высота виртуального экрана
     */
    public void render(SpriteBatch batch, float virtualWidth, float virtualHeight) {
        // Отрисовка фона
        batch.draw(menuBackground, 0, 0, virtualWidth, virtualHeight);

        // Отрисовка логотипа (в верхней части экрана)
        drawLogo(batch, virtualWidth, virtualHeight);

        // Отрисовка всех кнопок меню
        drawAllButtons(batch);

        // Отрисовка текстовой информации (версия и копирайт)
        drawTextInfo(batch);
    }

    /**
     * Отрисовка логотипа игры.
     */
    private void drawLogo(SpriteBatch batch, float virtualWidth, float virtualHeight) {
        float logoY = virtualHeight - titleTexture.getHeight() - 150f;
        batch.draw(
            titleTexture,
            (virtualWidth - titleTexture.getWidth()) / 2,
            logoY,
            titleTexture.getWidth(),
            titleTexture.getHeight()
        );
    }

    /**
     * Отрисовка всех кнопок меню.
     */
    private void drawAllButtons(SpriteBatch batch) {
        // Кнопка "Новая игра"
        drawButton(
            batch,
            newGameButton, newGameButtonHover, newGameButtonPressed,
            newGameBounds, isNewGameHovered, isNewGamePressed
        );

        // Кнопка "Персонаж"
        drawButton(
            batch,
            characterButton, characterButtonHover, characterButtonPressed,
            characterBounds, isCharacterHovered, isCharacterPressed
        );

        // Кнопка "Настройки"
        drawButton(
            batch,
            settingsButton, settingsButtonHover, settingsButtonPressed,
            settingsBounds, isSettingsHovered, isSettingsPressed
        );

        // Кнопка "Выход"
        drawButton(
            batch,
            exitButton, exitButtonHover, exitButtonPressed,
            exitBounds, isExitHovered, isExitPressed
        );
    }

/**
 * Отрисовка текстовой информации (версия и копирайт).
 */
private void drawTextInfo(SpriteBatch batch) {
    // Отрисовка версии (в левом нижнем углу)
    batch.draw(
        betaVersionTexture,
        20f, 20f,
        betaVersionTexture.getWidth() * 1.2f,
        betaVersionTexture.getHeight() * 1.2f
    );

    // Отрисовка копирайта (по центру внизу)
    float copyrightWidth = copyrightTexture.getWidth() * 1.2f;
    float copyrightHeight = copyrightTexture.getHeight() * 1.2f;
    float copyrightX = exitBounds.x + (exitBounds.width - copyrightWidth) / 2;
    float copyrightY = 20f;

    batch.draw(
        copyrightTexture,
        copyrightX,
        copyrightY,
        copyrightWidth,
        copyrightHeight
    );
}

    /**
     * Отрисовка кнопки с учетом состояния (наведение/нажатие).
     *
     * @param batch       SpriteBatch для отрисовки
     * @param normal      Текстура обычного состояния
     * @param hover       Текстура при наведении
     * @param pressed     Текстура при нажатии
     * @param bounds      Границы кнопки
     * @param isHovered   Флаг наведения
     * @param isPressed   Флаг нажатия
     */
    private void drawButton(SpriteBatch batch, Texture normal, Texture hover, Texture pressed,
                            Rectangle bounds, boolean isHovered, boolean isPressed) {
        Texture current = normal;
        if (isPressed) current = pressed;
        else if (isHovered) current = hover;

        batch.draw(current, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /**
     * Обновление состояния кнопок на основе ввода пользователя.
     *
     * @param touchX     Координата X касания
     * @param touchY     Координата Y касания
     * @param isTouched  Флаг наличия касания
     */
    public void updateInput(float touchX, float touchY, boolean isTouched) {
        // Обновление состояния кнопки "Новая игра"
        updateButtonState(touchX, touchY, isTouched,
            newGameBounds,
            () -> isNewGameHovered = true,
            () -> isNewGameHovered = false,
            () -> isNewGamePressed = true,
            () -> isNewGamePressed = false);

        // Обновление состояния кнопки "Персонаж"
        updateButtonState(touchX, touchY, isTouched,
            characterBounds,
            () -> isCharacterHovered = true,
            () -> isCharacterHovered = false,
            () -> isCharacterPressed = true,
            () -> isCharacterPressed = false);

        // Обновление состояния кнопки "Настройки"
        updateButtonState(touchX, touchY, isTouched,
            settingsBounds,
            () -> isSettingsHovered = true,
            () -> isSettingsHovered = false,
            () -> isSettingsPressed = true,
            () -> isSettingsPressed = false);

        // Обновление состояния кнопки "Выход"
        updateButtonState(touchX, touchY, isTouched,
            exitBounds,
            () -> isExitHovered = true,
            () -> isExitHovered = false,
            () -> isExitPressed = true,
            () -> isExitPressed = false);
    }

    /**
     * Обновление состояния конкретной кнопки.
     */
    private void updateButtonState(float touchX, float touchY, boolean isTouched,
                                   Rectangle bounds,
                                   Runnable onHover, Runnable onHoverExit,
                                   Runnable onPress, Runnable onPressExit) {
        if (bounds.contains(touchX, touchY)) {
            onHover.run();
            if (isTouched) {
                onPress.run();
            } else {
                onPressExit.run();
            }
        } else {
            onHoverExit.run();
            onPressExit.run();
        }
    }

// Методы проверки нажатия кнопок
    /**
     * Проверка нажатия кнопки "Новая игра".
     */
    public boolean isNewGameClicked(float touchX, float touchY) {
        return newGameBounds.contains(touchX, touchY);
    }

    /**
     * Проверка нажатия кнопки "Персонаж".
     */
    public boolean isCharacterClicked(float touchX, float touchY) {
        return characterBounds.contains(touchX, touchY);
    }

    /**
     * Проверка нажатия кнопки "Настройки".
     */
    public boolean isSettingsClicked(float touchX, float touchY) {
        return settingsBounds.contains(touchX, touchY);
    }

    /**
     * Проверка нажатия кнопки "Выход".
     */
    public boolean isExitClicked(float touchX, float touchY) {
        return exitBounds.contains(touchX, touchY);
    }

    /**
     * Освобождение ресурсов.
     */
    public void dispose() {
        // Освобождение текстур фона и текста
        menuBackground.dispose();
        titleTexture.dispose();
        betaVersionTexture.dispose();
        copyrightTexture.dispose();

        // Освобождение текстур кнопок
        newGameButton.dispose();
        newGameButtonHover.dispose();
        newGameButtonPressed.dispose();

        characterButton.dispose();
        characterButtonHover.dispose();
        characterButtonPressed.dispose();

        settingsButton.dispose();
        settingsButtonHover.dispose();
        settingsButtonPressed.dispose();

        exitButton.dispose();
        exitButtonHover.dispose();
        exitButtonPressed.dispose();
    }
}

