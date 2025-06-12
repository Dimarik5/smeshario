// Main.java
package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music; // Импорт для музыки
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.menu.MainMenu;
import com.mygdx.game.menu.CharacterSelectionScreen;
import com.mygdx.game.player.Player;
import com.mygdx.game.world.Background;
import com.mygdx.game.obstacles.Obstacle;

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private Background background;
    private MainMenu mainMenu;
    private CharacterSelectionScreen characterSelectionScreen;
    private Random random = new Random();
    private Array<Obstacle> obstacles;
    private float obstacleTimer = 0f;
    private float obstacleInterval = getRandomInterval(); //рандомно добавляем препятствия
    private final float groundY = 150f;

    private static final float VIRTUAL_WIDTH = 1920;
    private static final float VIRTUAL_HEIGHT = 1080;

    private enum GameState {
        MAIN_MENU,
        CHARACTER_SELECTION,
        IN_GAME
    }
    //метод для генерации интервала препятствий
    private float getRandomInterval() {
        return 2f + random.nextFloat() * (4f - 1f); // от 1 до 4 секунд
    }

    private GameState currentState = GameState.MAIN_MENU;

    private enum PressedButtonType {
        NONE,
        NEW_GAME, CHARACTER, SETTINGS, EXIT, // Для MainMenu
        BACK, LEFT_ARROW, RIGHT_ARROW       // Для CharacterSelectionScreen
    }
    private PressedButtonType currentPressedButton = PressedButtonType.NONE;
    private boolean fingerIsCurrentlyDown = false; // Отслеживать, нажат ли палец
    private final Vector3 touchPosition = new Vector3(); // Для переиспользования, чтобы не создавать объект каждый кадр

    // ----- НОВЫЕ ПОЛЯ ДЛЯ МУЗЫКИ -----
    private Music menuMusic;
    private Music gameplayMusic;
    private Music currentPlayingMusic; // Хранит ссылку на текущую играющую музыку

    // Для будущих настроек: путь к текущей музыке геймплея
    private String currentGameplayMusicPath = "music/tema_krosha.ogg"; // Замени на имя твоего файла по умолчанию
    private float musicVolume = 0.5f; // Громкость музыки (0.0f - 1.0f), можно будет менять в настройках

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        obstacles = new Array<>();

        mainMenu = new MainMenu(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        characterSelectionScreen = new CharacterSelectionScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        background = new Background("environment/background.png");
        player = new Player(100, 150, VIRTUAL_HEIGHT);

        // Загрузка музыки
        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu_music.ogg")); // Замени на имя твоего файла
            menuMusic.setLooping(true);
            menuMusic.setVolume(musicVolume);

            // Загружаем музыку для геймплея по умолчанию
            gameplayMusic = Gdx.audio.newMusic(Gdx.files.internal(currentGameplayMusicPath));
            gameplayMusic.setLooping(true);
            gameplayMusic.setVolume(musicVolume);

        } catch (Exception e) {
            Gdx.app.error("MusicLoader", "Couldn't load music", e);
            // Обработка ошибки: можно установить музыку в null или загрузить "запасной" вариант
            if (menuMusic == null) Gdx.app.log("MusicLoader", "Menu music failed to load.");
            if (gameplayMusic == null) Gdx.app.log("MusicLoader", "Gameplay music failed to load.");
        }

        // Устанавливаем начальное состояние и запускаем соответствующую музыку
        // Вместо прямого присваивания currentState, используем сеттер, чтобы централизовать логику смены музыки
        setCurrentState(GameState.MAIN_MENU, true); // true - для первоначального запуска музыки
    }

    // Сеттер для currentState, который также управляет музыкой
    private void setCurrentState(GameState newState) {
        setCurrentState(newState, false);
    }

    private void setCurrentState(GameState newState, boolean forcePlay) {
        if (this.currentState == newState && !forcePlay) {
            return; // Состояние не изменилось, и не нужно форсировать перезапуск музыки
        }
        GameState previousState = this.currentState;
        this.currentState = newState;

        // Логика смены музыки при смене состояния
        if (previousState != newState || forcePlay) {
            playMusicForCurrentState();
        }
    }

    private void playMusicForCurrentState() {
        if (currentPlayingMusic != null) {
            currentPlayingMusic.stop();
        }

        if (currentState == GameState.MAIN_MENU || currentState == GameState.CHARACTER_SELECTION) {
            currentPlayingMusic = menuMusic;
        } else if (currentState == GameState.IN_GAME) {
            // Если gameplayMusic не загружена (например, из-за ошибки), ничего не играем
            // или можно попробовать загрузить музыку по умолчанию снова
            currentPlayingMusic = gameplayMusic;
        }

        if (currentPlayingMusic != null) {
            currentPlayingMusic.setLooping(true); // На всякий случай, если меняли
            currentPlayingMusic.setVolume(musicVolume); // Устанавливаем актуальную громкость
            currentPlayingMusic.play();
        }
    }

    // Метод для смены музыки геймплея (будет вызываться из настроек)
    public void changeGameplayMusic(String newMusicFile) {
        this.currentGameplayMusicPath = "music/" + newMusicFile; // Предполагаем, что файлы в папке music

        if (gameplayMusic != null) {
            boolean wasPlaying = gameplayMusic.isPlaying();
            if (wasPlaying) gameplayMusic.stop();
            gameplayMusic.dispose(); // Освобождаем ресурсы старой музыки
        }

        try {
            gameplayMusic = Gdx.audio.newMusic(Gdx.files.internal(currentGameplayMusicPath));
            gameplayMusic.setLooping(true);
            gameplayMusic.setVolume(musicVolume);

            // Если мы сейчас в игре, то сразу обновляем currentPlayingMusic и запускаем новый трек
            if (currentState == GameState.IN_GAME) {
                if (currentPlayingMusic != null && currentPlayingMusic != menuMusic) { // Если играла старая геймплейная музыка
                    currentPlayingMusic.stop(); // Останавливаем ее (хотя она уже должна быть gameplayMusic)
                }
                currentPlayingMusic = gameplayMusic;
                if (currentPlayingMusic != null) { // Проверка на случай ошибки загрузки новой музыки
                    currentPlayingMusic.play();
                }
            }
        } catch (Exception e) {
            Gdx.app.error("MusicLoader", "Couldn't load new gameplay music: " + currentGameplayMusicPath, e);
            gameplayMusic = null; // или можно попытаться загрузить музыку по умолчанию
            // Если currentPlayingMusic была старой gameplayMusic, она уже остановлена и dispose'нута
            // Если мы в игре, то музыка просто перестанет играть или нужно вернуть музыку по умолчанию.
            // Для простоты пока оставим так.
        }
    }

    // Метод для изменения громкости (будет вызываться из настроек)
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume)); // Ограничиваем громкость от 0 до 1
        if (menuMusic != null) menuMusic.setVolume(this.musicVolume);
        if (gameplayMusic != null) gameplayMusic.setVolume(this.musicVolume);
        // Если currentPlayingMusic - это одна из них, ее громкость уже обновится.
        // Если нет, можно обновить явно:
        // if (currentPlayingMusic != null) currentPlayingMusic.setVolume(this.musicVolume);
    }

    @Override
    public void render() {
        // 1. Обработка ввода для изменения состояния игры (логика нажатия/отпускания)
        handleGameInputLogic();

        // 2. Обновление визуальных состояний кнопок (hover/pressed)
        updateButtonVisualStates();

        float deltaTime = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        switch (currentState) {
            case MAIN_MENU:
                mainMenu.render(batch, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
                break;
            case CHARACTER_SELECTION:
                characterSelectionScreen.render(batch);
                break;
            case IN_GAME:
                batch.end(); // Завершаем batch, начатый в начале render()
                updateGame(deltaTime);
                renderGame(); // renderGame() должен управлять своим batch
                return;       // Важно: выйти из render(), чтобы не вызвать внешний batch.end()
        }

        batch.end(); // Этот batch.end() для MAIN_MENU и CHARACTER_SELECTION
    }

    // Метод для обновления визуального состояния кнопок (hover, pressed)
    // Этот метод раньше назывался updateInputStates
    private void updateButtonVisualStates() {
        touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPosition);
        boolean isTouched = Gdx.input.isTouched();

        if (currentState == GameState.MAIN_MENU) {
            mainMenu.updateInput(touchPosition.x, touchPosition.y, isTouched);
        } else if (currentState == GameState.CHARACTER_SELECTION) {
            // Предполагаем, что CharacterSelectionScreen имеет аналогичный метод updateInput
            characterSelectionScreen.updateInput(touchPosition.x, touchPosition.y, isTouched);
        }
    }

    private void updateGame(float deltaTime) {
        background.update(deltaTime);
        player.update(deltaTime);
        // Обновляем таймер и создаем новые препятствия
        obstacleTimer += deltaTime;
        if (obstacleTimer >= obstacleInterval) {
            obstacleTimer = 0;
            spawnObstacle();
            obstacleInterval = getRandomInterval(); //перезадаем интервал для генерации
        }

        // Обновляем и удаляем пройденные препятствия
        for (Iterator<Obstacle> it = obstacles.iterator(); it.hasNext();) {
            Obstacle obstacle = it.next();
            obstacle.update(deltaTime); // Нужно добавить метод update в класс Obstacle


            // Удаляем препятствия, которые ушли за экран
            if (obstacle.getX() + obstacle.getWidth() < camera.position.x - camera.viewportWidth/2) {
                it.remove();
                obstacle.dispose();
            }
        }
    }

    private void spawnObstacle() {
        float spawnX = camera.position.x + camera.viewportWidth / 2;

        // Случайный выбор между beehive и pit
        String texturePath;
        float height;

        if (random.nextBoolean()) {
            texturePath = "environment/beehive.png";
            height = 380f;//высота улья
        } else {
            texturePath = "environment/pit.png";
            height = 120f; //высота ямы
        }

        obstacles.add(new Obstacle(spawnX, groundY, texturePath, height));;
    }

    private void renderGame() {
        // camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0); // Эта строка была в твоем исходном коде. Если камера игры статична, это нормально.
        // Если камера должна следовать за игроком или двигаться, ее позицию нужно обновлять в updateGame.
        camera.update(); // Обновляем камеру, если она может двигаться или меняться

        // renderGame должен управлять своим SpriteBatch, так как он вызывается после batch.end()
        // из основного цикла render() (когда currentState == IN_GAME)
        batch.setProjectionMatrix(camera.combined);
        batch.begin(); // Начинаем batch ЗДЕСЬ, один раз для всей игровой сцены

        // Background теперь просто рисует, не управляя batch
        background.render(batch, camera);
        //рендер препятствий
        for (Obstacle obstacle : obstacles) {
            obstacle.render(batch);
        }

        // Игрок тоже просто рисует (убедись, что player.render тоже не управляет batch)
        player.render(batch);

        // Заканчиваем batch ЗДЕСЬ
        batch.end();
    }

    // Этот метод раньше назывался handleInput. Переименован для ясности.
    // Обрабатывает логику нажатий и отпусканий для изменения состояния игры.
    private void handleGameInputLogic() {
        // Игровой ввод (не UI), например, прыжок
        if (currentState == GameState.IN_GAME) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                player.jump();
            }
        }

        // Обработка НАЖАТИЯ (Touch Down) для UI
        if (Gdx.input.justTouched()) {
            fingerIsCurrentlyDown = true;
            currentPressedButton = PressedButtonType.NONE; // Сброс на случай, если предыдущее нажатие не было обработано

            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPosition);

            switch (currentState) {
                case MAIN_MENU:
                    if (mainMenu.isNewGameClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.NEW_GAME;
                    } else if (mainMenu.isCharacterClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.CHARACTER;
                    } else if (mainMenu.isSettingsClicked(touchPosition.x, touchPosition.y)) { // Предполагаем, что isSettingsClicked есть
                        currentPressedButton = PressedButtonType.SETTINGS;
                    } else if (mainMenu.isExitClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.EXIT;
                    }
                    break;
                case CHARACTER_SELECTION:
                    // Аналогично для CharacterSelectionScreen
                    if (characterSelectionScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.BACK;
                    } else if (characterSelectionScreen.isLeftArrowClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.LEFT_ARROW;
                    } else if (characterSelectionScreen.isRightArrowClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.RIGHT_ARROW;
                    }
                    break;
            }
        }

        // Обработка ОТПУСКАНИЯ (Touch Up) для UI
        // Проверяем, был ли палец нажат (fingerIsCurrentlyDown) и СЕЙЧАС он отпущен (!Gdx.input.isTouched())
        if (fingerIsCurrentlyDown && !Gdx.input.isTouched()) {
            fingerIsCurrentlyDown = false; // Сбрасываем флаг нажатия

            // Получаем позицию отпускания
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPosition);

            switch (currentState) {
                case MAIN_MENU:
                    if (currentPressedButton == PressedButtonType.NEW_GAME && mainMenu.isNewGameClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.IN_GAME);
                    } else if (currentPressedButton == PressedButtonType.CHARACTER && mainMenu.isCharacterClicked(touchPosition.x, touchPosition.y)) {
                        currentState = GameState.CHARACTER_SELECTION;
                    } else if (currentPressedButton == PressedButtonType.SETTINGS && mainMenu.isSettingsClicked(touchPosition.x, touchPosition.y)) {
                        Gdx.app.log("MainMenu", "Settings button action!"); // Заглушка для настроек
                        // currentState = GameState.SETTINGS_SCREEN; // Если есть экран настроек
                    } else if (currentPressedButton == PressedButtonType.EXIT && mainMenu.isExitClicked(touchPosition.x, touchPosition.y)) {
                        Gdx.app.exit();
                    }
                    break;
                case CHARACTER_SELECTION:
                    if (currentPressedButton == PressedButtonType.BACK && characterSelectionScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                        currentState = GameState.MAIN_MENU;
                    } else if (currentPressedButton == PressedButtonType.LEFT_ARROW && characterSelectionScreen.isLeftArrowClicked(touchPosition.x, touchPosition.y)) {
                        characterSelectionScreen.previousCharacter();
                    } else if (currentPressedButton == PressedButtonType.RIGHT_ARROW && characterSelectionScreen.isRightArrowClicked(touchPosition.x, touchPosition.y)) {
                        characterSelectionScreen.nextCharacter();
                    }
                    break;
            }
            currentPressedButton = PressedButtonType.NONE; // Сбрасываем после обработки, чтобы избежать повторного срабатывания
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (player != null) player.dispose();
        if (background != null) background.dispose();
        if (mainMenu != null) mainMenu.dispose();
        if (characterSelectionScreen != null) characterSelectionScreen.dispose();

        // Освобождаем ресурсы музыки
        if (menuMusic != null) menuMusic.dispose();
        if (gameplayMusic != null) gameplayMusic.dispose();
        // currentPlayingMusic - это ссылка на один из вышеуказанных объектов, ее отдельно освобождать не надо
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }
        obstacles.clear();
    }

    // Дополнительно можно добавить методы жизненного цикла для музыки, если нужно
    // например, при сворачивании приложения музыку можно ставить на паузу
    @Override
    public void pause() {
        super.pause(); // Если наследуешься от ApplicationAdapter, можно оставить пустым или вызвать super
        if (currentPlayingMusic != null && currentPlayingMusic.isPlaying()) {
            currentPlayingMusic.pause();
        }
    }

    @Override
    public void resume() {
        super.resume();
        // Возобновляем музыку, только если она была на паузе и должна играть в текущем состоянии
        // Это более сложная логика, если пользователь мог изменить состояние, пока игра была свернута.
        // Простой вариант - просто пытаться воспроизвести:
        if (currentPlayingMusic != null && !currentPlayingMusic.isPlaying()) {
            // Перед play() убедиться, что именно эта музыка должна играть
            // playMusicForCurrentState(); // Это может быть слишком грубо, лучше запоминать, была ли она на паузе
            // Пока что просто:
            currentPlayingMusic.play();
        }
    }
}
