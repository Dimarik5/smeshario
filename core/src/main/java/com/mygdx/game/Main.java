package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; //для отрисовки хитбокса
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
import com.mygdx.game.menu.SettingsScreen;
import com.mygdx.game.player.Player;
import com.mygdx.game.world.Background;
import com.mygdx.game.obstacles.Obstacle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Главный класс игры, наследующий ApplicationAdapter от LibGDX.
 * Управляет всеми состояниями игры, рендерингом и логикой.
 */
public class Main extends ApplicationAdapter {
    // Для хранения текстур анимаций
    private Array<Texture> pitDeathAnimationTextures;
    private Array<Texture> beehiveDeathAnimationTextures;

    // Графические компоненты
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer; //для отрисовки хитбокса
    private OrthographicCamera camera;

    // Анимации смерти
    private Animation<TextureRegion> pitDeathAnimation;
    private Animation<TextureRegion> beehiveDeathAnimation;
    private float deathAnimationTime = 0f;
    private boolean showPitAnimation = false;
    private boolean showBeehiveAnimation = false;

    // Игровые объекты
    private Player player;
    private Background background;
    private Array<Obstacle> obstacles;

    // Состояния игры и меню
    private MainMenu mainMenu;
    private CharacterSelectionScreen characterSelectionScreen;
    private SettingsScreen settingsScreen;

    // Звуки и музыка
    private Sound gameOverSound;
    private Sound buttonPopSound;
    private Music menuMusic;
    private Music gameplayMusic;
    private Music currentPlayingMusic; // Хранит ссылку на текущую играющую музыку
    private String currentGameplayMusicPath = "music/tema_krosha.ogg";
    private float musicVolume = 1f;
    private float soundVolume = 1f;

    // Логика игры
    private Random random = new Random();
    private float obstacleTimer = 0f;
    private float obstacleInterval = getRandomInterval(); //рандомно добавляем препятствия
    private final float groundY = 150f;
    private boolean gameOver = false;
    private Texture gameOverTexture;

    // Система очков
    private int score;
    private float scoreTimeAccumulator;
    private Array<Integer> scoreHistory;

    // Шрифты
    private BitmapFont scoreFont;
    private BitmapFont deathScoreFont;
    private FreeTypeFontGenerator fontGenerator;

    // Кнопки интерфейса
    private Texture menuButtonTexture;
    private Texture menuButtonHoverTexture;
    private Texture menuButtonClickTexture;
    private Texture retryButtonTexture;
    private Texture retryButtonHoverTexture;
    private Texture retryButtonClickTexture;
    private Rectangle menuButtonBounds;
    private Rectangle retryButtonBounds;
    private boolean isMenuButtonHovered = false;
    private boolean isRetryButtonHovered = false;
    private boolean isMenuButtonClicked = false;
    private boolean isRetryButtonClicked = false;

    // Константы
    private static final float VIRTUAL_WIDTH = 1920;
    private static final float VIRTUAL_HEIGHT = 1080;
    private boolean debugHitboxes = false; // Включаем/выключаем отладку отрисовки хитбокса

    /**
     * Перечисление состояний игры
     */
    private enum GameState {
        MAIN_MENU,          // Главное меню
        CHARACTER_SELECTION,// Выбор персонажа
        IN_GAME,           // Игровой процесс
        GAME_OVER,         // Экран после проигрыша
        SETTINGS           // Настройки
    }

    private GameState currentState = GameState.MAIN_MENU;

    /**
     * Перечисление типов нажатых кнопок
     */
    private enum PressedButtonType {
        NONE,               // Ничего не нажато
        NEW_GAME,           // Новая игра
        CHARACTER,          // Выбор персонажа
        SETTINGS,           // Настройки
        EXIT,               // Выход
        BACK,               // Назад
        LEFT_ARROW,         // Стрелка влево
        RIGHT_ARROW,        // Стрелка вправо
        MENU,               // Кнопка меню
        RETRY               // Повторить
    }

    private PressedButtonType currentPressedButton = PressedButtonType.NONE;

    // Управление касаниями
    private boolean fingerIsCurrentlyDown = false; // Отслеживать, нажат ли палец
    private final Vector3 touchPosition = new Vector3(); // Для переиспользования, чтобы не создавать объект каждый кадр

    /**
     * Метод инициализации игры, вызывается при запуске
     */
    @Override
    public void create() {
        // Инициализация графических компонентов
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer(); //для отрисовки хитбокса
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // Инициализация игровых объектов
        obstacles = new Array<>();
        buttonPopSound = Gdx.audio.newSound(Gdx.files.internal("music/button_pop.ogg"));

        // Создание экранов меню
        mainMenu = new MainMenu(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        characterSelectionScreen = new CharacterSelectionScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        settingsScreen = new SettingsScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, musicVolume, soundVolume, this);

        // Инициализация фона и игрока
        background = new Background("environment/background.png");
        player = new Player(100, 150, VIRTUAL_HEIGHT);

        // Загрузка анимаций смерти
        loadDeathAnimations();

        // Загрузка текстур кнопок
        loadButtonTextures();

        // Инициализация шрифтов
        initializeFonts();

        // Инициализация системы очков
        score = 0;
        scoreTimeAccumulator = 0;
        scoreHistory = new Array<>();

        // Загрузка музыки
        loadMusic();

        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("music/game_over.ogg"));

        // Установка начального состояния
        setCurrentState(GameState.MAIN_MENU, true);
    }

    /**
     * Загружает анимации смерти
     */
    private void loadDeathAnimations() {
        // Инициализируем массивы для хранения текстур
        pitDeathAnimationTextures = new Array<>();
        beehiveDeathAnimationTextures = new Array<>();

        // Анимация смерти в яме
        Array<TextureRegion> pitFrames = new Array<>();
        for (int i = 1; i <= 24; i++) {
            Texture frameTexture = new Texture(Gdx.files.internal("game_over/pit_animation/" + i + ".png"));
            pitDeathAnimationTextures.add(frameTexture);
            pitFrames.add(new TextureRegion(frameTexture));
        }
        pitDeathAnimation = new Animation<>(1f / 24f, pitFrames, Animation.PlayMode.LOOP);

        // Анимация смерти от улья
        Array<TextureRegion> beehiveFrames = new Array<>();
        for (int i = 1; i <= 24; i++) {
            Texture frameTexture = new Texture(Gdx.files.internal("game_over/beehive_animation/" + i + ".png"));
            beehiveDeathAnimationTextures.add(frameTexture);
            beehiveFrames.add(new TextureRegion(frameTexture));
        }
        beehiveDeathAnimation = new Animation<>(1f / 24f, beehiveFrames, Animation.PlayMode.LOOP);
    }

    /**
     * Загружает текстуры кнопок
     */
    private void loadButtonTextures() {
        menuButtonTexture = new Texture(Gdx.files.internal("game_over/menu.png"));
        menuButtonHoverTexture = new Texture(Gdx.files.internal("game_over/menu_hover.png"));
        menuButtonClickTexture = new Texture(Gdx.files.internal("game_over/menu_click.png"));
        retryButtonTexture = new Texture(Gdx.files.internal("game_over/retry.png"));
        retryButtonHoverTexture = new Texture(Gdx.files.internal("game_over/retry_hover.png"));
        retryButtonClickTexture = new Texture(Gdx.files.internal("game_over/retry_click.png"));

        // Настройка размеров и позиций кнопок
        float buttonWidth = 300;
        float buttonHeight = 100;
        float buttonsY = 170;
        float buttonGap = 300;
        float totalButtonsWidth = (buttonWidth * 2) + buttonGap;
        float startX = (VIRTUAL_WIDTH - totalButtonsWidth) / 2;

        menuButtonBounds = new Rectangle(startX, buttonsY, buttonWidth, buttonHeight);
        retryButtonBounds = new Rectangle(startX + buttonWidth + buttonGap, buttonsY, buttonWidth, buttonHeight);
    }

    /**
     * Инициализирует шрифты
     */
    // счетчик очков
    private void initializeFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/smeshariki2007fixed_regular.ttf"));

        // Параметры основного шрифта для очков
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 70;
        parameter.color = Color.valueOf("FF8000");
        //чтоб буковки русские были
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,:!?()[]{}\\<>|/@#^&*-_=+\\\"'\\\\ \n";
        scoreFont = generator.generateFont(parameter);

        // Параметры шрифта для экрана смерти
        FreeTypeFontGenerator.FreeTypeFontParameter deathFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        deathFontParameter.size = 90;
        deathFontParameter.color = Color.WHITE;
        deathFontParameter.characters = parameter.characters; // те же символы
        deathScoreFont = generator.generateFont(deathFontParameter);

        fontGenerator = generator; // чтобы потом освободить
    }

    /**
     * Загружает музыку
     */
    private void loadMusic() {
        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu_music.ogg"));
            menuMusic.setLooping(true);
            menuMusic.setVolume(musicVolume);

            gameplayMusic = Gdx.audio.newMusic(Gdx.files.internal(currentGameplayMusicPath));
            gameplayMusic.setLooping(true);
            gameplayMusic.setVolume(musicVolume);
        } catch (Exception e) {
            Gdx.app.error("MusicLoader", "Couldn't load music", e);
            if (menuMusic == null) Gdx.app.log("MusicLoader", "Menu music failed to load.");
            if (gameplayMusic == null) Gdx.app.log("MusicLoader", "Gameplay music failed to load.");
        }
    }

    /**
     * Обновляет громкость звуков в игре
     * @param volume новая громкость звуков
     */
    public void updateGlobalSoundVolume(float volume) {
        this.soundVolume = volume;
        if (characterSelectionScreen != null) {
            characterSelectionScreen.setSoundVolume(volume);
        }
        if (player != null) {
            player.setSoundVolume(volume);
        }
    }

    /**
     * Устанавливает текущее состояние игры
     * @param newState новое состояние
     */
    private void setCurrentState(GameState newState) {
        setCurrentState(newState, false);
    }

    /**
     * Устанавливает текущее состояние игры с возможностью принудительного воспроизведения музыки
     * @param newState новое состояние
     * @param forcePlay принудительно воспроизвести музыку, даже если состояние не изменилось
     */
    private void setCurrentState(GameState newState, boolean forcePlay) {
        if (this.currentState == newState && !forcePlay) {
            return;
        }

        GameState previousState = this.currentState;
        this.currentState = newState;

        // Обработка специальных случаев при смене состояния
        // Если начинаем новую игру — обнуляем очки и таймер
        if (newState == GameState.IN_GAME) {
            // Сброс очков при начале новой игры
            score = 0;
            scoreTimeAccumulator = 0;
        } else if (newState == GameState.CHARACTER_SELECTION) {
            characterSelectionScreen.reset();
            if (previousState == GameState.MAIN_MENU) {characterSelectionScreen.playCurrentCharacterSound();
            }
        }

        // Воспроизведение соответствующей музыки
        if (previousState != newState || forcePlay) {
            playMusicForCurrentState();
        }
    }

    /**
     * Воспроизводит музыку в соответствии с текущим состоянием игры
     */
    private void playMusicForCurrentState() {
        // Если игра окончена, останавливаем любую играющую музыку и выходим.
        if (currentState == GameState.GAME_OVER) {
            if (currentPlayingMusic != null) {
                currentPlayingMusic.stop();
            }
            currentPlayingMusic = null; // Устанавливаем в null, чтобы ничего не играло
            return; // Выйти из метода, чтобы не выполнился код ниже
        }

        if (currentState == GameState.IN_GAME) {
            // Если текущее состояние - игра, и играет меню-музыка, останавливаем её
            if (currentPlayingMusic == menuMusic) {
                menuMusic.stop();
                currentPlayingMusic = gameplayMusic;
            }
        } else {
            // Для всех других состояний (меню, выбор персонажа, настройки)
            if (currentPlayingMusic == null || currentPlayingMusic != menuMusic) {
                if (currentPlayingMusic != null) {
                    currentPlayingMusic.stop();
                }
                currentPlayingMusic = menuMusic;
            }
        }

        // Настройка и запуск текущей музыки
        if (currentPlayingMusic != null) {
            currentPlayingMusic.setLooping(true);
            currentPlayingMusic.setVolume(musicVolume);
            if (!currentPlayingMusic.isPlaying()) {
                currentPlayingMusic.play();
            }
        }

        // Обновление текущей музыки в настройках
        settingsScreen.setCurrentMusic(currentPlayingMusic);
    }

    /**
     * Сбрасывает состояние игры
     */
    private void resetGame() {
        // Очистка препятствий
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }
        obstacles.clear();

        // Сброс игрока и состояния игры
        player.resetPosition();
        gameOver = false;
        showPitAnimation = false;
        showBeehiveAnimation = false;
        deathAnimationTime = 0f;
        obstacleTimer = 0f;
        obstacleInterval = getRandomInterval();
    }

    /**
     * Возвращает максимальное количество очков из истории
     * @return максимальное количество очков
     */
    private int getMaxScore() {
        int max = 0;
        for (int s : scoreHistory) {
            if (s > max) max = s;
        }
        return max;
    }

    /**
     * Основной метод рендеринга, вызывается каждый кадр
     */
    @Override
    public void render() {
        // Обработка ввода и обновление визуального состояния кнопок
        handleGameInputLogic();
        updateButtonVisualStates();

        float deltaTime = Gdx.graphics.getDeltaTime();

        // Очистка экрана
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Рендеринг в зависимости от текущего состояния
        switch (currentState) {
            case MAIN_MENU:
                mainMenu.render(batch, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
                break;
            case CHARACTER_SELECTION:
                characterSelectionScreen.render(batch);
                break;
            case SETTINGS:
                settingsScreen.render(batch);
                break;
            case IN_GAME:
                batch.end(); // Завершаем batch, начатый в начале render()
                updateGame(deltaTime);
                renderGame(); // renderGame() должен управлять своим batch
                batch.begin(); //отрисовка очков
                scoreFont.draw(batch, "Очки: " + score, 20, VIRTUAL_HEIGHT - 20);
                batch.end();

                // Отладка хитбоксов
                // После renderGame() рисуем хитбоксы ShapeRenderer'ом
                if (debugHitboxes) {
                    renderDebugHitboxes();
                }
                return;
            case GAME_OVER:
                renderGameOverScreen(deltaTime);
                break;
        }

        batch.end();

        // Отладка хитбоксов для других состояний
        if (debugHitboxes && (currentState == GameState.MAIN_MENU || currentState == GameState.CHARACTER_SELECTION ||
            currentState == GameState.GAME_OVER || currentState == GameState.SETTINGS)) {
            renderDebugHitboxes();
        }
    }

    /**
     * Рендерит хитбоксы для отладки
     */
    private void renderDebugHitboxes() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        Rectangle playerBounds = player.getBounds();
        shapeRenderer.rect(playerBounds.x, playerBounds.y, playerBounds.width, playerBounds.height);

        for (Obstacle obstacle : obstacles) {
            Rectangle obstacleBounds = obstacle.getBounds();
            shapeRenderer.rect(obstacleBounds.x, obstacleBounds.y, obstacleBounds.width, obstacleBounds.height);
        }

        shapeRenderer.end();
    }

    /**
     * Рендерит экран окончания игры
     * @param deltaTime время с последнего кадра
     */
    private void renderGameOverScreen(float deltaTime) {
        // Фон экрана смерти
        batch.draw(gameOverTexture,
            camera.position.x - camera.viewportWidth / 2f,
            0,
            camera.viewportWidth, camera.viewportHeight);

        // Анимация смерти (яма или улей)
        deathAnimationTime += deltaTime;
        TextureRegion currentFrame = null;

        if (showPitAnimation) {
            currentFrame = pitDeathAnimation.getKeyFrame(deathAnimationTime, true);
        } else if (showBeehiveAnimation) {
            currentFrame = beehiveDeathAnimation.getKeyFrame(deathAnimationTime, true);
        }

        if (currentFrame != null) {
            float animationWidth = 354;
            float animationHeight = 240;
            float x = camera.position.x - animationWidth / 2f;
            float y = camera.viewportHeight / 2f - animationHeight / 2f;
            // Отрисовка последнего и максимального счёта на экране смерти
            batch.draw(currentFrame, x, y, animationWidth, animationHeight);
        }

        // Кнопки меню и повтора
        Texture currentMenuButtonTexture = isMenuButtonClicked ? menuButtonClickTexture :
            (isMenuButtonHovered ? menuButtonHoverTexture : menuButtonTexture);

        Texture currentRetryButtonTexture = isRetryButtonClicked ? retryButtonClickTexture :
            (isRetryButtonHovered ? retryButtonHoverTexture : retryButtonTexture);

        batch.draw(currentMenuButtonTexture, menuButtonBounds.x, menuButtonBounds.y,
            menuButtonBounds.width, menuButtonBounds.height);

        batch.draw(currentRetryButtonTexture, retryButtonBounds.x, retryButtonBounds.y,
            retryButtonBounds.width, retryButtonBounds.height);

        // Отображение очков
        scoreFont.setColor(Color.WHITE); // Установи белый цвет перед рисованием очков:
        int lastScore = scoreHistory.size > 0 ? scoreHistory.peek() : 0;
        int bestScore = getMaxScore();

        // Координаты
        float leftX = camera.position.x - 400; // слева от центра
        float rightX = camera.position.x + 300; // справа от центра
        float y = 700; // высота от нижнего края экрана

        deathScoreFont.draw(batch, String.valueOf(lastScore), leftX, y); // текущий счёт
        deathScoreFont.draw(batch, String.valueOf(bestScore), rightX, y); //рекорд
    }

    /**
     * Проверяет столкновения игрока с препятствиями
     */
    private void checkCollision() {
        for (Obstacle obstacle : obstacles) {
            if (player.getBounds().overlaps(obstacle.getBounds())) {
                // Определение типа препятствия (яма или улей)
                boolean isPit = obstacle.getBounds().getHeight() < 150;
                String path = isPit ? "game_over/pit_background.png" : "game_over/beehive_background.png";

                // Установка параметров окончания игры
                gameOverTexture = new Texture(Gdx.files.internal(path));
                gameOver = true;
                // Сброс таймера и установка нужной анимации
                deathAnimationTime = 0f;
                showPitAnimation = isPit;
                showBeehiveAnimation = !isPit;

                // Воспроизведение звука окончания игры
                if (gameOverSound != null) {
                    gameOverSound.play(soundVolume);
                }
                // Сохранение очков и переход в состояние окончания игры
                scoreHistory.add(score);
                // Переход в состояние GAME_OVER
                setCurrentState(GameState.GAME_OVER);
                break;
            }
        }
    }

    /**
     * Обновляет визуальное состояние кнопок
     */
    private void updateButtonVisualStates() {
        // Получение позиции касания
        touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPosition);
        boolean isTouched = Gdx.input.isTouched();

        // Обновление состояния кнопок в зависимости от текущего экрана
        if (currentState == GameState.MAIN_MENU) {
            mainMenu.updateInput(touchPosition.x, touchPosition.y, isTouched);
        } else if (currentState == GameState.CHARACTER_SELECTION) {
            characterSelectionScreen.updateInput(touchPosition.x, touchPosition.y, isTouched);
        } else if (currentState == GameState.SETTINGS) {
            settingsScreen.updateInput(touchPosition.x, touchPosition.y, isTouched);
            if (settingsScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                currentPressedButton = PressedButtonType.BACK;
            }
        } else if (currentState == GameState.GAME_OVER) {
            // Обновление состояния кнопок меню и повтора
            isMenuButtonHovered = menuButtonBounds.contains(touchPosition.x, touchPosition.y);
            isRetryButtonHovered = retryButtonBounds.contains(touchPosition.x, touchPosition.y);

            if (isTouched) {
                isMenuButtonClicked = isMenuButtonHovered;
                isRetryButtonClicked = isRetryButtonHovered;
            } else {
                isMenuButtonClicked = false;
                isRetryButtonClicked = false;
            }
        }
    }

    /**
     * Обновляет игровую логику
     * @param deltaTime время с последнего кадра
     */
    private void updateGame(float deltaTime) {
        background.update(deltaTime);
        player.update(deltaTime);

        // Генерация препятствий
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
            obstacle.update(deltaTime);

            // Удаляем препятствия, которые ушли за экран
            if (obstacle.getX() + obstacle.getWidth() < camera.position.x - camera.viewportWidth/2) {
                it.remove();
                obstacle.dispose();
            }
        }

        checkCollision();

        // Обновление очков: 10 очков в секунду
        scoreTimeAccumulator += deltaTime;
        while (scoreTimeAccumulator >= 0.1f) { // 0.1 секунды = 10 очков в секунду
            score += 1;
            scoreTimeAccumulator -= 0.1f;
        }
    }

    /**
     * Создает новое препятствие
     */
    private void spawnObstacle() {
        float spawnX = camera.position.x + camera.viewportWidth / 2;

        // Случайный выбор типа препятствия (улей или яма)
        if (random.nextBoolean()) {
            String texturePath = "environment/beehive.png";
            float height = 286f; //высота улья
            obstacles.add(new Obstacle(spawnX, groundY+70, texturePath, height));
        } else {
            String texturePath = "environment/pit.png";
            float height = 110f; //высота ямы
            obstacles.add(new Obstacle(spawnX, groundY+45, texturePath, height));
        }
    }

    /**
     * Рендерит игровые объекты
     */
    private void renderGame() {
        // camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0); // Эта строка была в твоем исходном коде. Если камера игры статична, это нормально.
        // Если камера должна следовать за игроком или двигаться, ее позицию нужно обновлять в updateGame.
        camera.update(); // Обновляем камеру, если она может двигаться или меняться
        // renderGame должен управлять своим SpriteBatch, так как он вызывается после batch.end()
        // из основного цикла render() (когда currentState == IN_GAME)
        batch.setProjectionMatrix(camera.combined);
        batch.begin(); // Начинаем batch ЗДЕСЬ, один раз для всей игровой сцены

        if (gameOver && gameOverTexture != null) {
            // Рендеринг экрана окончания игры
            batch.draw(gameOverTexture,
                camera.position.x - camera.viewportWidth / 2f, // смещение камеры влево
                0,
                camera.viewportWidth,camera.viewportHeight);
        } else {
            // РИСУЕМ ОБЫЧНУЮ ИГРОВУЮ СЦЕНУ
            // 1. Фон
            background.render(batch, camera);
            // 2. Препятствия
            for (Obstacle obstacle : obstacles) {
                obstacle.render(batch);
            }
            // 3. Игрок
            player.render(batch);
        }

        // Заканчиваем batch ЗДЕСЬ (конец отрисовки)
        batch.end();
    }

    /**
     * Обрабатывает ввод пользователя
     */
    // Этот метод раньше назывался handleInput. Переименован для ясности.
    // Обрабатывает логику нажатий и отпусканий для изменения состояния игры.
    private void handleGameInputLogic() {
        // Обработка ввода во время игры (прыжок)
        // Игровой ввод (не UI), например, прыжок
        if (currentState == GameState.IN_GAME) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                player.jump();
            }
        }

        // Обработка касаний
        // Обработка НАЖАТИЯ (Touch Down) для UI
        if (Gdx.input.justTouched()) {
            fingerIsCurrentlyDown = true;
            currentPressedButton = PressedButtonType.NONE; // Сброс на случай, если предыдущее нажатие не было обработано
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPosition);
            buttonPopSound.play(soundVolume);

            // Определение нажатой кнопки в зависимости от текущего состояния
            switch (currentState) {
                case MAIN_MENU:
                    if (mainMenu.isNewGameClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.NEW_GAME;
                    } else if (mainMenu.isCharacterClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.CHARACTER;
                    } else if (mainMenu.isSettingsClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.SETTINGS;
                    } else if (mainMenu.isExitClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.EXIT;
                    }
                    break;
                case CHARACTER_SELECTION:
                    if (characterSelectionScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.BACK;
                    } else if (characterSelectionScreen.isLeftArrowClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.LEFT_ARROW;
                    } else if (characterSelectionScreen.isRightArrowClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.RIGHT_ARROW;
                    }
                    break;
                case SETTINGS:
                    if (settingsScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.BACK;
                    }
                    break;
                case GAME_OVER:
                    if (menuButtonBounds.contains(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.MENU;
                    } else if (retryButtonBounds.contains(touchPosition.x, touchPosition.y)) {
                        currentPressedButton = PressedButtonType.RETRY;
                    }
                    break;
            }
        }

        // Обработка отпускания кнопки
        // Проверяем, был ли палец нажат (fingerIsCurrentlyDown) и СЕЙЧАС он отпущен (!Gdx.input.isTouched())
        if (fingerIsCurrentlyDown && !Gdx.input.isTouched()) {
            fingerIsCurrentlyDown = false; // Сбрасываем флаг нажатия
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0); // Получаем позицию отпускания
            camera.unproject(touchPosition);

            // Выполнение действия в зависимости от нажатой кнопки
            switch (currentState) {
                case MAIN_MENU:
                    if (currentPressedButton == PressedButtonType.NEW_GAME &&
                        mainMenu.isNewGameClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.IN_GAME);
                    } else if (currentPressedButton == PressedButtonType.CHARACTER &&
                        mainMenu.isCharacterClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.CHARACTER_SELECTION);} else if (currentPressedButton == PressedButtonType.SETTINGS &&
                        mainMenu.isSettingsClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.SETTINGS);
                    } else if (currentPressedButton == PressedButtonType.EXIT &&
                        mainMenu.isExitClicked(touchPosition.x, touchPosition.y)) {
                        Gdx.app.exit();
                    }
                    break;
                case CHARACTER_SELECTION:
                    if (currentPressedButton == PressedButtonType.BACK &&
                        characterSelectionScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.MAIN_MENU);
                    } else if (currentPressedButton == PressedButtonType.LEFT_ARROW &&
                        characterSelectionScreen.isLeftArrowClicked(touchPosition.x, touchPosition.y)) {
                        characterSelectionScreen.previousCharacter();
                    } else if (currentPressedButton == PressedButtonType.RIGHT_ARROW &&
                        characterSelectionScreen.isRightArrowClicked(touchPosition.x, touchPosition.y)) {
                        characterSelectionScreen.nextCharacter();
                    }
                    break;
                case SETTINGS:
                    if (currentPressedButton == PressedButtonType.BACK &&
                        settingsScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                        // Сохранение настроек музыки
                        musicVolume = settingsScreen.getMusicVolume();
                        if (currentPlayingMusic != null) {
                            currentPlayingMusic.setVolume(musicVolume);
                        }

                        // Смена музыки, если выбрана другая
                        int selectedMusic = settingsScreen.getSelectedMusicIndex();
                        String[] musicPaths = {
                            "music/tema_krosha.ogg",
                            "music/pogonya.ogg",
                            "music/obormot.ogg"
                        };

                        if (selectedMusic >= 0 && selectedMusic < musicPaths.length &&
                            !currentGameplayMusicPath.equals(musicPaths[selectedMusic])) {
                            currentGameplayMusicPath = musicPaths[selectedMusic];
                            if (gameplayMusic != null) {
                                gameplayMusic.dispose();
                            }
                            gameplayMusic = Gdx.audio.newMusic(Gdx.files.internal(currentGameplayMusicPath));
                            gameplayMusic.setLooping(true);
                            gameplayMusic.setVolume(musicVolume);

                            if (currentState == GameState.IN_GAME) {
                                playMusicForCurrentState();
                            }
                            settingsScreen.setCurrentMusic(gameplayMusic);
                        }

                        setCurrentState(GameState.MAIN_MENU);
                    }
                    break;
                case GAME_OVER:
                    if (currentPressedButton == PressedButtonType.MENU &&
                        menuButtonBounds.contains(touchPosition.x, touchPosition.y)) {
                        resetGame();
                        setCurrentState(GameState.MAIN_MENU);
                    } else if (currentPressedButton == PressedButtonType.RETRY &&
                        retryButtonBounds.contains(touchPosition.x, touchPosition.y)) {
                        resetGame();
                        setCurrentState(GameState.IN_GAME);
                    }
                    break;
            }

            currentPressedButton = PressedButtonType.NONE; // Сбрасываем после обработки, чтобы избежать повторного срабатывания
        }
    }

    /*** Генерирует случайный интервал между препятствиями
     * @return случайный интервал в секундах
     */
    private float getRandomInterval() {
        return 1.5f + random.nextFloat();
    }

    /**
     * Освобождает ресурсы при завершении работы игры
     */
    @Override
    public void dispose() {
        // Освобождение графических ресурсов
        batch.dispose();
        shapeRenderer.dispose();

        // Освобождение ресурсов игровых объектов
        if (player != null) player.dispose();
        if (background != null) background.dispose();

        // Освобождение ресурсов меню
        if (mainMenu != null) mainMenu.dispose();
        if (characterSelectionScreen != null) characterSelectionScreen.dispose();
        if (settingsScreen != null) settingsScreen.dispose();

        // Освобождение музыкальных ресурсов
        // currentPlayingMusic - это ссылка на один из вышеуказанных объектов, ее отдельно освобождать не надо
        if (menuMusic != null) menuMusic.dispose();
        if (gameplayMusic != null) gameplayMusic.dispose();

        // Освобождение ресурсов препятствий
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }

        // Освобождение ресурсов анимаций смерти
        if (pitDeathAnimationTextures != null) {
            for (Texture texture : pitDeathAnimationTextures) {
                texture.dispose();
            }
        }
        if (beehiveDeathAnimationTextures != null) {
            for (Texture texture : beehiveDeathAnimationTextures) {
                texture.dispose();
            }
        }

        // Освобождение текстур
        if (gameOverTexture != null) {
            gameOverTexture.dispose();
        }

        menuButtonTexture.dispose();
        menuButtonHoverTexture.dispose();
        menuButtonClickTexture.dispose();
        retryButtonTexture.dispose();
        retryButtonHoverTexture.dispose();
        retryButtonClickTexture.dispose();

        // Освобождение шрифтов
        if (scoreFont != null) scoreFont.dispose();
        if (deathScoreFont != null) deathScoreFont.dispose();
        if (fontGenerator != null) fontGenerator.dispose();

        // Освобождение звуков
        if (gameOverSound != null) gameOverSound.dispose();
        if (buttonPopSound != null) buttonPopSound.dispose();

        obstacles.clear();
    }

    /**
     * Вызывается при приостановке приложения
     */
    @Override
    public void pause() {
        super.pause();
        if (currentPlayingMusic != null && currentPlayingMusic.isPlaying()) {
            currentPlayingMusic.pause();
        }
    }

    /**
     * Вызывается при возобновлении работы приложения
     */
    @Override
    public void resume() {
        super.resume();
        if (currentPlayingMusic != null && !currentPlayingMusic.isPlaying()) {
            currentPlayingMusic.play();
        }
    }
}
