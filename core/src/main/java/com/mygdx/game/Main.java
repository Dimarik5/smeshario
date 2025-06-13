package com.mygdx.game;

import java.util.Iterator;
import java.util.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
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

public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private boolean debugHitboxes = false;
    private Animation<TextureRegion> pitDeathAnimation;
    private Animation<TextureRegion> beehiveDeathAnimation;
    private float deathAnimationTime = 0f;
    private boolean showPitAnimation = false;
    private boolean showBeehiveAnimation = false;
    private OrthographicCamera camera;
    private Player player;
    private Background background;
    private MainMenu mainMenu;
    private Sound gameOverSound;
    private CharacterSelectionScreen characterSelectionScreen;
    private SettingsScreen settingsScreen;
    private Random random = new Random();
    private Array<Obstacle> obstacles;
    private float obstacleTimer = 0f;
    private float obstacleInterval = getRandomInterval();
    private final float groundY = 150f;
    private boolean gameOver = false;
    private Texture gameOverTexture;
    private int score;
    private float scoreTimeAccumulator;
    private Array<Integer> scoreHistory;
    private BitmapFont scoreFont;
    private BitmapFont deathScoreFont;
    private FreeTypeFontGenerator fontGenerator;
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
    private Sound buttonPopSound;
    private static final float VIRTUAL_WIDTH = 1920;
    private static final float VIRTUAL_HEIGHT = 1080;

    private enum GameState {
        MAIN_MENU,
        CHARACTER_SELECTION,
        IN_GAME,
        GAME_OVER,
        SETTINGS
    }

    private GameState currentState = GameState.MAIN_MENU;

    private enum PressedButtonType {
        NONE,
        NEW_GAME, CHARACTER, SETTINGS, EXIT,
        BACK, LEFT_ARROW, RIGHT_ARROW,
        MENU, RETRY
    }

    private PressedButtonType currentPressedButton = PressedButtonType.NONE;
    private boolean fingerIsCurrentlyDown = false;
    private final Vector3 touchPosition = new Vector3();
    private Music menuMusic;
    private Music gameplayMusic;
    private Music currentPlayingMusic;
    private String currentGameplayMusicPath = "music/tema_krosha.ogg";
    private float musicVolume = 0.5f;
    private float soundVolume = 0.5f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        obstacles = new Array<>();
        // Загрузите звук для кнопок
        buttonPopSound = Gdx.audio.newSound(Gdx.files.internal("music/button_pop.ogg"));

        mainMenu = new MainMenu(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        characterSelectionScreen = new CharacterSelectionScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        settingsScreen = new SettingsScreen(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, musicVolume, soundVolume);
        background = new Background("environment/background.png");
        player = new Player(100, 150, VIRTUAL_HEIGHT);

        // Load death animations
        Array<TextureRegion> pitFrames = new Array<>();
        for (int i = 1; i <= 24; i++) {
            Texture frame = new Texture(Gdx.files.internal("game_over/pit_animation/" + i + ".png"));
            pitFrames.add(new TextureRegion(frame));
        }
        pitDeathAnimation = new Animation<>(1f / 24f, pitFrames, Animation.PlayMode.LOOP);

        Array<TextureRegion> beehiveFrames = new Array<>();
        for (int i = 1; i <= 24; i++) {
            Texture frame = new Texture(Gdx.files.internal("game_over/beehive_animation/" + i + ".png"));
            beehiveFrames.add(new TextureRegion(frame));
        }
        beehiveDeathAnimation = new Animation<>(1f / 24f, beehiveFrames, Animation.PlayMode.LOOP);

        // Load button textures
        menuButtonTexture = new Texture(Gdx.files.internal("game_over/menu.png"));
        menuButtonHoverTexture = new Texture(Gdx.files.internal("game_over/menu_hover.png"));
        menuButtonClickTexture = new Texture(Gdx.files.internal("game_over/menu_click.png"));
        retryButtonTexture = new Texture(Gdx.files.internal("game_over/retry.png"));
        retryButtonHoverTexture = new Texture(Gdx.files.internal("game_over/retry_hover.png"));
        retryButtonClickTexture = new Texture(Gdx.files.internal("game_over/retry_click.png"));

        // Set button positions and sizes
        float buttonWidth = 300;
        float buttonHeight = 150;
        float menuButtonX = VIRTUAL_WIDTH / 2 - buttonWidth - 50;
        float retryButtonX = VIRTUAL_WIDTH / 2 + 50;
        float buttonsY = 300;
        menuButtonBounds = new Rectangle(menuButtonX, buttonsY, buttonWidth, buttonHeight);
        retryButtonBounds = new Rectangle(retryButtonX, buttonsY, buttonWidth, buttonHeight);

        // Initialize fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/smeshariki2007fixed_regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 70;
        parameter.color = Color.valueOf("FF8000");
        parameter.characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,:!?()[]{}\\\\<>|/@#^&*-_=+\\\"'\\\\ \n";
        scoreFont = generator.generateFont(parameter);

        FreeTypeFontGenerator.FreeTypeFontParameter deathFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        deathFontParameter.size = 90;
        deathFontParameter.color = Color.WHITE;
        deathFontParameter.characters = parameter.characters;
        deathScoreFont = generator.generateFont(deathFontParameter);
        fontGenerator = generator;

        score = 0;
        scoreTimeAccumulator = 0;
        scoreHistory = new Array<>();

        // Load sounds and music
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

        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("music/game_over.ogg"));
        setCurrentState(GameState.MAIN_MENU, true);
    }

    private void setCurrentState(GameState newState) {
        setCurrentState(newState, false);
    }

    private void setCurrentState(GameState newState, boolean forcePlay) {
        if (this.currentState == newState && !forcePlay) {
            return;
        }

        GameState previousState = this.currentState;
        this.currentState = newState;

        if (newState == GameState.IN_GAME) {
            score = 0;
            scoreTimeAccumulator = 0;
        } else if (newState == GameState.CHARACTER_SELECTION) {
            characterSelectionScreen.reset();
            // Воспроизводим звук только при переходе из главного меню
            if (previousState == GameState.MAIN_MENU) {
                characterSelectionScreen.playCurrentCharacterSound();
            }
        }

        if (previousState != newState || forcePlay) {
            playMusicForCurrentState();
        }
    }

    private void playMusicForCurrentState() {
        if (currentPlayingMusic != null) {
            currentPlayingMusic.stop();
        }
        if (currentPlayingMusic != null) {
            currentPlayingMusic.setVolume(musicVolume);
            settingsScreen.setCurrentMusic(currentPlayingMusic);
        }
        if (currentState == GameState.MAIN_MENU || currentState == GameState.CHARACTER_SELECTION || currentState == GameState.SETTINGS) {
            currentPlayingMusic = menuMusic;
        } else if (currentState == GameState.IN_GAME) {
            currentPlayingMusic = gameplayMusic;
        } else {
            currentPlayingMusic = null;
        }

        if (currentPlayingMusic != null) {
            currentPlayingMusic.setLooping(true);
            currentPlayingMusic.setVolume(musicVolume);
            currentPlayingMusic.play();
        }
    }

    private void resetGame() {
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }
        obstacles.clear();
        player.resetPosition();
        gameOver = false;
        showPitAnimation = false;
        showBeehiveAnimation = false;
        deathAnimationTime = 0f;
        obstacleTimer = 0f;
        obstacleInterval = getRandomInterval();
    }

    private int getMaxScore() {
        int max = 0;
        for (int s : scoreHistory) {
            if (s > max) max = s;
        }
        return max;
    }

    @Override
    public void render() {
        handleGameInputLogic();
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
            case SETTINGS:
                settingsScreen.render(batch);
                break;
            case IN_GAME:
                batch.end();
                updateGame(deltaTime);
                renderGame();
                batch.begin();
                scoreFont.draw(batch, "Очки: " + score, 20, VIRTUAL_HEIGHT - 20);
                batch.end();

                if (debugHitboxes) {
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
                return;
            case GAME_OVER:
                batch.draw(gameOverTexture,
                    camera.position.x - camera.viewportWidth / 2f,
                    0,
                    camera.viewportWidth,camera.viewportHeight);

                deathAnimationTime += deltaTime;
                TextureRegion currentFrame = null;
                if (showPitAnimation) {
                    currentFrame = pitDeathAnimation.getKeyFrame(deathAnimationTime, true);
                } else if (showBeehiveAnimation) {
                    currentFrame = beehiveDeathAnimation.getKeyFrame(deathAnimationTime, true);
                }

                if (currentFrame != null) {
                    float animationWidth = 200;
                    float animationHeight = 200;
                    float x = camera.position.x - animationWidth / 2f;
                    float y = camera.viewportHeight / 2f - animationHeight / 2f;
                    batch.draw(currentFrame, x, y, animationWidth, animationHeight);
                }

                // Draw buttons
                Texture currentMenuButtonTexture = isMenuButtonClicked ? menuButtonClickTexture :
                    (isMenuButtonHovered ? menuButtonHoverTexture : menuButtonTexture);
                Texture currentRetryButtonTexture = isRetryButtonClicked ? retryButtonClickTexture :
                    (isRetryButtonHovered ? retryButtonHoverTexture : retryButtonTexture);

                batch.draw(currentMenuButtonTexture, menuButtonBounds.x, menuButtonBounds.y, menuButtonBounds.width, menuButtonBounds.height);
                batch.draw(currentRetryButtonTexture, retryButtonBounds.x, retryButtonBounds.y, retryButtonBounds.width, retryButtonBounds.height);

                // Draw scores
                scoreFont.setColor(Color.WHITE);
                int lastScore = scoreHistory.size > 0 ? scoreHistory.peek() : 0;
                int bestScore = getMaxScore();
                float leftX = camera.position.x - 400;
                float rightX = camera.position.x + 300;
                float y = 700;
                deathScoreFont.draw(batch, String.valueOf(lastScore), leftX, y);
                deathScoreFont.draw(batch, String.valueOf(bestScore), rightX, y);
                break;
        }
        batch.end();

        if (debugHitboxes && (currentState == GameState.MAIN_MENU || currentState == GameState.CHARACTER_SELECTION ||
            currentState == GameState.GAME_OVER || currentState == GameState.SETTINGS)) {
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
    }

    private void checkCollision() {
        for (Obstacle obstacle : obstacles) {
            if (player.getBounds().overlaps(obstacle.getBounds())) {
                boolean isPit = obstacle.getBounds().getHeight() < 150;
                String path = isPit ? "game_over/pit_background.png" : "game_over/beehive_background.png";
                gameOverTexture = new Texture(Gdx.files.internal(path));
                gameOver = true;
                deathAnimationTime = 0f;
                showPitAnimation = isPit;
                showBeehiveAnimation = !isPit;

                if (gameOverSound != null) {
                    gameOverSound.play(soundVolume);
                }

                scoreHistory.add(score);
                setCurrentState(GameState.GAME_OVER);
                break;
            }
        }
    }

    private void updateButtonVisualStates() {
        touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPosition);
        boolean isTouched = Gdx.input.isTouched();

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

    private void updateGame(float deltaTime) {
        background.update(deltaTime);
        player.update(deltaTime);
        obstacleTimer += deltaTime;

        if (obstacleTimer >= obstacleInterval) {
            obstacleTimer = 0;
            spawnObstacle();
            obstacleInterval = getRandomInterval();
        }

        for (Iterator<Obstacle> it = obstacles.iterator(); it.hasNext();) {
            Obstacle obstacle = it.next();
            obstacle.update(deltaTime);
            if (obstacle.getX() + obstacle.getWidth() < camera.position.x - camera.viewportWidth/2) {
                it.remove();
                obstacle.dispose();
            }
        }

        checkCollision();
        scoreTimeAccumulator += deltaTime;
        while (scoreTimeAccumulator >= 0.1f) {
            score += 1;
            scoreTimeAccumulator -= 0.1f;
        }
    }

    private void spawnObstacle() {
        float spawnX = camera.position.x + camera.viewportWidth / 2;
        if (random.nextBoolean()) {
            String texturePath = "environment/beehive.png";
            float height = 286f;
            obstacles.add(new Obstacle(spawnX, groundY+70, texturePath, height));
        } else {
            String texturePath = "environment/pit.png";
            float height = 110f;
            obstacles.add(new Obstacle(spawnX, groundY+45, texturePath, height));
        }
    }

    private void renderGame() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (gameOver && gameOverTexture != null) {
            batch.draw(gameOverTexture,
                camera.position.x - camera.viewportWidth / 2f,
                0,
                camera.viewportWidth,
                camera.viewportHeight);
        } else {
            background.render(batch, camera);
            for (Obstacle obstacle : obstacles) {
                obstacle.render(batch);
            }
            player.render(batch);
        }
        batch.end();
    }

    private void handleGameInputLogic() {
        if (currentState == GameState.IN_GAME) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                player.jump();
            }
        }


        if (Gdx.input.justTouched()) {
            fingerIsCurrentlyDown = true;
            currentPressedButton = PressedButtonType.NONE;
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPosition);

            // Воспроизведите звук при нажатии на любую кнопку
            buttonPopSound.play(soundVolume);

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

        if (fingerIsCurrentlyDown && !Gdx.input.isTouched()) {
            fingerIsCurrentlyDown = false;
            touchPosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPosition);

            switch (currentState) {
                case MAIN_MENU:
                    if (currentPressedButton == PressedButtonType.NEW_GAME && mainMenu.isNewGameClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.IN_GAME);
                    } else if (currentPressedButton == PressedButtonType.CHARACTER && mainMenu.isCharacterClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.CHARACTER_SELECTION);
                    } else if (currentPressedButton == PressedButtonType.SETTINGS && mainMenu.isSettingsClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.SETTINGS);
                    } else if (currentPressedButton == PressedButtonType.EXIT && mainMenu.isExitClicked(touchPosition.x, touchPosition.y)) {
                        Gdx.app.exit();
                    }
                    break;
                case CHARACTER_SELECTION:
                    if (currentPressedButton == PressedButtonType.BACK && characterSelectionScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                        setCurrentState(GameState.MAIN_MENU);
                    } else if (currentPressedButton == PressedButtonType.LEFT_ARROW && characterSelectionScreen.isLeftArrowClicked(touchPosition.x, touchPosition.y)) {
                        characterSelectionScreen.previousCharacter();
                    } else if (currentPressedButton == PressedButtonType.RIGHT_ARROW && characterSelectionScreen.isRightArrowClicked(touchPosition.x, touchPosition.y)) {
                        characterSelectionScreen.nextCharacter();
                    }
                    break;
                case SETTINGS:
                    if (currentPressedButton == PressedButtonType.BACK && settingsScreen.isBackClicked(touchPosition.x, touchPosition.y)) {
                        // Update music and sound volumes from settings
                        musicVolume = settingsScreen.getMusicVolume();
                        soundVolume = settingsScreen.getSoundVolume();

                        // Update current music volume
                        if (currentPlayingMusic != null) {
                            currentPlayingMusic.setVolume(musicVolume);
                        }

                        // Update selected music if changed
                        int selectedMusic = settingsScreen.getSelectedMusicIndex();
                        String[] musicPaths = {
                            "music/obormot.ogg",
                            "music/pogonya.ogg",
                            "music/tema_krosha.ogg"
                        };

                        if (selectedMusic >= 0 && selectedMusic < musicPaths.length && !currentGameplayMusicPath.equals(musicPaths[selectedMusic])) {
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
                    if (currentPressedButton == PressedButtonType.MENU && menuButtonBounds.contains(touchPosition.x, touchPosition.y)) {
                        resetGame();
                        setCurrentState(GameState.MAIN_MENU);
                    } else if (currentPressedButton == PressedButtonType.RETRY && retryButtonBounds.contains(touchPosition.x, touchPosition.y)) {
                        resetGame();
                        setCurrentState(GameState.IN_GAME);
                    }
                    break;
            }
            currentPressedButton = PressedButtonType.NONE;
        }
    }

    private float getRandomInterval() {
        return 1.5f + random.nextFloat();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (player != null) player.dispose();
        if (background != null) background.dispose();
        if (mainMenu != null) mainMenu.dispose();
        if (characterSelectionScreen != null) characterSelectionScreen.dispose();
        if (settingsScreen != null) settingsScreen.dispose();
        if (menuMusic != null) menuMusic.dispose();
        if (gameplayMusic != null) gameplayMusic.dispose();

        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }

        for (Object frameObj : pitDeathAnimation.getKeyFrames()) {
            if (frameObj instanceof TextureRegion frame) {
                frame.getTexture().dispose();
            }
        }

        for (Object frameObj : beehiveDeathAnimation.getKeyFrames()) {
            if (frameObj instanceof TextureRegion frame) {
                frame.getTexture().dispose();
            }
        }

        if (gameOverTexture != null) {
            gameOverTexture.dispose();
        }

        menuButtonTexture.dispose();
        menuButtonHoverTexture.dispose();
        menuButtonClickTexture.dispose();
        retryButtonTexture.dispose();
        retryButtonHoverTexture.dispose();
        retryButtonClickTexture.dispose();
        shapeRenderer.dispose();

        if (scoreFont != null) scoreFont.dispose();
        if (deathScoreFont != null) deathScoreFont.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
        if (gameOverSound != null) gameOverSound.dispose();
        obstacles.clear();
    }

    @Override
    public void pause() {
        super.pause();
        if (currentPlayingMusic != null && currentPlayingMusic.isPlaying()) {
            currentPlayingMusic.pause();
        }
    }

    @Override
    public void resume() {
        super.resume();
        if (currentPlayingMusic != null && !currentPlayingMusic.isPlaying()) {
            currentPlayingMusic.play();
        }
    }
}
