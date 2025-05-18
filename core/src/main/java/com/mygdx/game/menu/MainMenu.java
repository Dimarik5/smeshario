package com.mygdx.game.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MainMenu {
    // Текстуры фона и логотипа
    private Texture menuBackground;
    private Texture titleTexture;
    private Texture betaVersionTexture;

    // Текстуры кнопок (обычное состояние, наведение, нажатие)
    private Texture newGameButton, newGameButtonHover, newGameButtonPressed;
    private Texture characterButton, characterButtonHover, characterButtonPressed;
    private Texture settingsButton, settingsButtonHover, settingsButtonPressed;
    private Texture exitButton, exitButtonHover, exitButtonPressed;

    // Границы кнопок
    private Rectangle newGameBounds;
    private Rectangle characterBounds;
    private Rectangle settingsBounds;
    private Rectangle exitBounds;

    // Состояния кнопок
    private boolean isNewGameHovered = false;
    private boolean isNewGamePressed = false;
    private boolean isCharacterHovered = false;
    private boolean isCharacterPressed = false;
    private boolean isSettingsHovered = false;
    private boolean isSettingsPressed = false;
    private boolean isExitHovered = false;
    private boolean isExitPressed = false;

    public MainMenu(float virtualWidth, float virtualHeight) {
        // Загрузка фона и текста
        menuBackground = new Texture("start/start_background.png");
        titleTexture = new Texture("start/main_menu/logo_text.png");
        betaVersionTexture = new Texture("start/main_menu/beta.png");

        // Загрузка текстур кнопок
        loadButtonTextures();

        // Настройка размеров и позиций кнопок
        setupButtonBounds(virtualWidth, virtualHeight);
    }

    private void loadButtonTextures() {
        // New Game
        newGameButton = new Texture("start/main_menu/buttons/new_game.png");
        newGameButtonHover = new Texture("start/main_menu/buttons/new_game_hover.png");
        newGameButtonPressed = new Texture("start/main_menu/buttons/new_game_click.png");

        // Character
        characterButton = new Texture("start/main_menu/buttons/character.png");
        characterButtonHover = new Texture("start/main_menu/buttons/character_hover.png");
        characterButtonPressed = new Texture("start/main_menu/buttons/character_click.png");

        // Settings
        settingsButton = new Texture("start/main_menu/buttons/settings.png");
        settingsButtonHover = new Texture("start/main_menu/buttons/settings_hover.png");
        settingsButtonPressed = new Texture("start/main_menu/buttons/settings_click.png");

        // Exit
        exitButton = new Texture("start/main_menu/buttons/exit.png");
        exitButtonHover = new Texture("start/main_menu/buttons/exit_hover.png");
        exitButtonPressed = new Texture("start/main_menu/buttons/exit_click.png");
    }

    private void setupButtonBounds(float virtualWidth, float virtualHeight) {
        float buttonWidth = 400f;
        float buttonHeight = 100f;
        float verticalSpacing = 15f; // Уменьшенный отступ между кнопками
        float totalHeight = 4 * buttonHeight + 3 * verticalSpacing;
        float startY = (virtualHeight - totalHeight) / 2 - 150f; // Позиционирование кнопок ниже

        newGameBounds = new Rectangle(
            (virtualWidth - buttonWidth) / 2,
            startY + 3 * (buttonHeight + verticalSpacing),
            buttonWidth,
            buttonHeight
        );

        characterBounds = new Rectangle(
            (virtualWidth - buttonWidth) / 2,
            startY + 2 * (buttonHeight + verticalSpacing),
            buttonWidth,
            buttonHeight
        );

        settingsBounds = new Rectangle(
            (virtualWidth - buttonWidth) / 2,
            startY + (buttonHeight + verticalSpacing),
            buttonWidth,
            buttonHeight
        );

        exitBounds = new Rectangle((virtualWidth - buttonWidth) / 2,
            startY,
            buttonWidth,
            buttonHeight
        );
    }

    public void render(SpriteBatch batch, float virtualWidth, float virtualHeight) {
        // Отрисовка фона
        batch.draw(menuBackground, 0, 0, virtualWidth, virtualHeight);

        // Отрисовка логотипа (опущен ниже)
        float logoY = virtualHeight - titleTexture.getHeight() - 150f;
        batch.draw(titleTexture,
            (virtualWidth - titleTexture.getWidth()) / 2,
            logoY,
            titleTexture.getWidth(),
            titleTexture.getHeight()
        );

        // Отрисовка кнопок с учетом состояния
        drawButton(batch, newGameButton, newGameButtonHover, newGameButtonPressed,
            newGameBounds, isNewGameHovered, isNewGamePressed);
        drawButton(batch, characterButton, characterButtonHover, characterButtonPressed,
            characterBounds, isCharacterHovered, isCharacterPressed);
        drawButton(batch, settingsButton, settingsButtonHover, settingsButtonPressed,
            settingsBounds, isSettingsHovered, isSettingsPressed);
        drawButton(batch, exitButton, exitButtonHover, exitButtonPressed,
            exitBounds, isExitHovered, isExitPressed);

        // Отрисовка версии
        batch.draw(betaVersionTexture, 20f, 20f,
            betaVersionTexture.getWidth() * 1.2f,
            betaVersionTexture.getHeight() * 1.2f);
    }

    private void drawButton(SpriteBatch batch, Texture normal, Texture hover, Texture pressed,
                            Rectangle bounds, boolean isHovered, boolean isPressed) {
        Texture current = normal;
        if (isPressed) current = pressed;
        else if (isHovered) current = hover;

        batch.draw(current, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void updateInput(float touchX, float touchY, boolean isTouched) {
        // New Game
        isNewGameHovered = newGameBounds.contains(touchX, touchY);
        if (isNewGameHovered && isTouched) isNewGamePressed = true;
        else isNewGamePressed = false;

        // Character
        isCharacterHovered = characterBounds.contains(touchX, touchY);
        if (isCharacterHovered && isTouched) isCharacterPressed = true;
        else isCharacterPressed = false;

        // Settings
        isSettingsHovered = settingsBounds.contains(touchX, touchY);
        if (isSettingsHovered && isTouched) isSettingsPressed = true;
        else isSettingsPressed = false;

        // Exit
        isExitHovered = exitBounds.contains(touchX, touchY);
        if (isExitHovered && isTouched) isExitPressed = true;
        else isExitPressed = false;
    }

    // Методы проверки нажатия
    public boolean isNewGameClicked(float touchX, float touchY) {
        return newGameBounds.contains(touchX, touchY);
    }

    public boolean isCharacterClicked(float touchX, float touchY) {
        return characterBounds.contains(touchX, touchY);
    }

    public boolean isSettingsClicked(float touchX, float touchY) {
        return settingsBounds.contains(touchX, touchY);
    }

    public boolean isExitClicked(float touchX, float touchY) {
        return exitBounds.contains(touchX, touchY);
    }

    public void dispose() {
        menuBackground.dispose();
        titleTexture.dispose();
        betaVersionTexture.dispose();

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

