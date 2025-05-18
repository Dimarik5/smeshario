package com.mygdx.game.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectionScreen {
    private Texture background;

    // Кнопка Back с состояниями
    private Texture backButton, backButtonHover, backButtonPressed;
    private Rectangle backBounds;
    private boolean isBackHovered = false;
    private boolean isBackPressed = false;

    // Кнопки слайдера без состояний
    private Texture leftArrow;
    private Texture rightArrow;
    private Rectangle leftArrowBounds;
    private Rectangle rightArrowBounds;

    private List<Texture> characterTextures;
    private int currentCharacterIndex = 0;
    private float virtualWidth;
    private float virtualHeight;

    public CharacterSelectionScreen(float virtualWidth, float virtualHeight) {
        this.virtualWidth = virtualWidth;
        this.virtualHeight = virtualHeight;

        // Загрузка текстур
        background = new Texture("start/character/background.png");

        // Кнопка Back с состояниями
        backButton = new Texture("start/back.png");
        backButtonHover = new Texture("start/back_hover.png");
        backButtonPressed = new Texture("start/back_click.png");

        // Кнопки слайдера без состояний
        leftArrow = new Texture("start/character/buttons/left.png");
        rightArrow = new Texture("start/character/buttons/right.png");

        // Текстуры персонажей
        characterTextures = new ArrayList<>();
        characterTextures.add(new Texture("start/character/krosh.png"));
        characterTextures.add(new Texture("start/character/nusha.png"));
        characterTextures.add(new Texture("start/character/barash.png"));
        characterTextures.add(new Texture("start/character/ezhik.png"));

        setupButtonBounds();
    }

    private void setupButtonBounds() {
        float backButtonWidth = 300f;  // Увеличиваем ширину
        float backButtonHeight = 120f; // Увеличиваем высоту
        float backButtonX = 165f;       // Смещаем ближе к левому краю
        float backButtonY = virtualHeight - backButtonHeight - 65f; // Смещаем ближе к верхнему краю

        backBounds = new Rectangle(backButtonX, backButtonY, backButtonWidth, backButtonHeight);

        float arrowWidth = 180f;    // Увеличиваем ширину стрелок
        float arrowHeight = 180f;   // Увеличиваем высоту стрелок
        float arrowYPosition = virtualHeight / 2 - arrowHeight / 2 - 35; // Центрируем по вертикали

        // Левая стрелка - смещаем ближе к персонажу
        leftArrowBounds = new Rectangle(
            virtualWidth / 2 - 650f,  // Увеличиваем отступ от центра
            arrowYPosition,
            arrowWidth,
            arrowHeight
        );

        // Правая стрелка - смещаем ближе к персонажу
        rightArrowBounds = new Rectangle(
            virtualWidth / 2 + 450f,  // Уменьшаем отступ от центра
            arrowYPosition,
            arrowWidth,
            arrowHeight
        );
    }

    public void render(SpriteBatch batch) {
        // Фон
        batch.draw(background, 0, 0, virtualWidth, virtualHeight);// Персонаж
        Texture currentCharacter = characterTextures.get(currentCharacterIndex);
        float charWidth = currentCharacter.getWidth() * 1f;
        float charHeight = currentCharacter.getHeight() * 1f;
        batch.draw(currentCharacter,
            (virtualWidth - charWidth) / 2,
            (virtualHeight - charHeight) / 2 - 63f,
            charWidth,
            charHeight
        );

        // Кнопка Back с состояниями
        Texture backCurrent = backButton;
        if (isBackPressed) backCurrent = backButtonPressed;
        else if (isBackHovered) backCurrent = backButtonHover;
        batch.draw(backCurrent, backBounds.x, backBounds.y, backBounds.width, backBounds.height);

        // Кнопки слайдера без состояний
        batch.draw(leftArrow, leftArrowBounds.x, leftArrowBounds.y, leftArrowBounds.width, leftArrowBounds.height);
        batch.draw(rightArrow, rightArrowBounds.x, rightArrowBounds.y, rightArrowBounds.width, rightArrowBounds.height);
    }

    public void updateInput(float touchX, float touchY, boolean isTouched) {
        // Обработка только для кнопки Back
        isBackHovered = backBounds.contains(touchX, touchY);
        if (isBackHovered && isTouched) isBackPressed = true;
        else isBackPressed = false;
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
        currentCharacterIndex = (currentCharacterIndex + 1) % characterTextures.size();
    }

    public void previousCharacter() {
        currentCharacterIndex = (currentCharacterIndex - 1 + characterTextures.size()) % characterTextures.size();
    }

    public int getSelectedCharacterIndex() {
        return currentCharacterIndex;
    }

    public void dispose() {
        background.dispose();
        backButton.dispose();
        backButtonHover.dispose();
        backButtonPressed.dispose();
        leftArrow.dispose();
        rightArrow.dispose();
        for (Texture texture : characterTextures) {
            texture.dispose();
        }
    }
}

