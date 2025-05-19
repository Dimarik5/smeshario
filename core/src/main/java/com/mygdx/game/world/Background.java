package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {
    private Texture texture; //текстура фона
    private float scrollSpeed = 960f; // Скорость прокрутки фона (пикселей в секунду)
    private float offsetX = 0; //смещение фона по Х
    private float textureWidth; //ширина текстуры
    private float textureHeight; //высота текстуры

    public Background(String texturePath) {
        texture = new Texture(Gdx.files.internal(texturePath)); //загрузка текстуры фона
        textureWidth = texture.getWidth();
        textureHeight = texture.getHeight();
    }

    // Обновляет положение фона
    public void update(float deltaTime) {
        offsetX -= scrollSpeed * deltaTime; // Уменьшаем смещение с учетом времени и скорости
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        float scale = camera.viewportHeight / textureHeight; //масштабируем для заполнения экрана по высоте
        float scaledWidth = textureWidth * scale; //ширина текстуры

        // Рассчитываем позиции для бесшовного скроллинга
        // offsetX будет постоянно уменьшаться. Нам нужно найти первую видимую позицию.
        // camera.position.x - camera.viewportWidth / 2  -- это левый край камеры
        float cameraLeftX = camera.position.x - camera.viewportWidth / 2f;

        // Начальная точка отрисовки фона, учитывающая общий сдвиг offsetX
        // и сдвигающаяся так, чтобы покрыть левый край камеры.
        float currentX = offsetX;
        while (currentX + scaledWidth < cameraLeftX) {
            currentX += scaledWidth;
        }
        // Возможно, нужно сдвинуть еще левее, если offsetX был очень большим
        while (currentX > cameraLeftX) {
            currentX -= scaledWidth;
        }

        // Отрисовываем все видимые фрагменты фона
        // УБРАН batch.begin();
        for (float x = currentX; x < cameraLeftX + camera.viewportWidth; x += scaledWidth) {
            batch.draw(texture,
                x,
                0,
                scaledWidth,
                camera.viewportHeight);
        }
        // УБРАН batch.end();
    }

    public void dispose() {
        if (texture != null) { // Добавил проверку на null для безопасности
            texture.dispose();
        }
    }
}
