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
        float firstX = ((camera.position.x - camera.viewportWidth/2) + offsetX) % scaledWidth;
        if (firstX > 0) firstX -= scaledWidth;

        // Отрисовываем все видимые фрагменты фона
        batch.begin();
        for (float x = firstX; x < camera.viewportWidth; x += scaledWidth) {
            batch.draw(texture,
                x,
                0,
                scaledWidth,
                camera.viewportHeight);
        }
        batch.end();
    }

    public void dispose() {
        texture.dispose();
    }
}
