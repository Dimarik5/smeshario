package com.mygdx.game.obstacles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Obstacle {
    private final Texture texture;
    private float x;
    private final float y;
    private final float width;
    private final float height;
    private final Rectangle bounds;
    private final float boundsInsetX = 55f; // Уменьшает ширину хитбокса
    private final float boundsInsetY = 30f; // Уменьшает высоту хитбокса
    private final float speed = 960f; // Скорость движения (должна совпадать со скоростью фона)

    public Obstacle(float x, float y, String texturePath, float height) {
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.x = x;
        this.y = y;
        this.height = height;
        float aspectRatio = texture.getWidth() / (float) texture.getHeight();
        this.width = height * aspectRatio;
        //штука для хитбокса
        this.bounds = new Rectangle(
            x + boundsInsetX,
            y + boundsInsetY,
            width - 2 * boundsInsetX,
            height - 2 * boundsInsetY
        );


    }

    public void update(float deltaTime) {
        x -= speed * deltaTime; // Движемся влево
        bounds.setPosition(x + boundsInsetX, y + boundsInsetY); // Обновляем позицию с учётом отступа
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return x;
    }

    public float getWidth() {
        return width;
    }

    public void dispose() {
        texture.dispose();
    }
}
