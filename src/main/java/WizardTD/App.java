package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.util.*;

/**
 * The main class for the game. 
 * Handles the initialisation of necessary classes.
 * Handles the loading of sprites.
 */
public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    // map elements
    public Tile grass, shrub, paths;
    public ArrayList<PImage> pathSprites;
    public Background background;
    public JSONObject config;

    // animated elements
    public ArrayList<PImage> towerImages;
    public HashMap<String, ArrayList<PImage>> monsterImages;
    public PImage fireballImage;

    // game details
    public GameManager gameManager;
    public WaveManager waveManager;
    public int framesPassed = 0;
    public Level level;
   
    /** 
     * Creates new instance of App, with path to config file already specified.
     */
    public App() {
        this.configPath = "config2.json";
        this.towerImages = new ArrayList<>();
        this.monsterImages = new HashMap<>();
        this.pathSprites = new ArrayList<>();
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
	@Override
    public void setup() {
        frameRate(FPS);
        this.config = loadJSONObject(this.configPath);
        this.level = new Level(config);
        this.waveManager = new WaveManager(config, FPS);
        this.gameManager = new GameManager(this.waveManager, config, this.level.getLevelFileName());

        ArrayList<PImage> gremlinImages = new ArrayList<>();
        ArrayList<PImage> wormImages = new ArrayList<>();
        ArrayList<PImage> beetleImages = new ArrayList<>();

        gremlinImages.add(loadImage("src/main/resources/WizardTD/gremlin.png"));
        wormImages.add(loadImage("src/main/resources/WizardTD/worm.png"));
        beetleImages.add(loadImage("src/main/resources/WizardTD/beetle.png"));
        gremlinImages.addAll(loadImages("gremlin", 1, 6));
        wormImages.addAll(loadImages("gremlin", 1, 6));
        beetleImages.addAll(loadImages("gremlin", 1, 6));

        monsterImages.put("gremlin", gremlinImages);
        monsterImages.put("worm", wormImages);
        monsterImages.put("beetle", beetleImages);

        this.towerImages.addAll(loadImages("tower", 0, 3));
        this.fireballImage = loadImage("src/main/resources/WizardTD/fireball.png");

        this.pathSprites.addAll(loadImages("path", 0, 4));
        this.paths = new Tile(pathSprites);
        this.grass = new Tile(loadImage("src/main/resources/WizardTD/grass.png"));
        this.shrub = new Tile(loadImage("src/main/resources/WizardTD/shrub.png"));

        this.background = new Background(grass, shrub, paths);
        gameManager.setUpWizardHouse(loadImage("src/main/resources/WizardTD/wizard_house.png"));
    }

    /**
     * Load a series of image, given a specified type and starting and ending at given indexes.
     * 
     * @param type The name of the image to load, e.g. path, gremlin
     * @param start The starting index of number that comes after name, e.g. 0 for path0
     * @param end The ending index (exclusive) of number that comes after name, e.g. 4 for path3
     * @return An ArrayList of loaded images.
     */
    public ArrayList<PImage> loadImages(String type, int start, int end) {
        ArrayList<PImage> images = new ArrayList<>();
        for (int i = start; i < end; i++) {
            String imagePath = "src/main/resources/WizardTD/" + type + i + ".png";
            images.add(loadImage(imagePath));
        }
        return images;
    }

    /**
     * Receive key pressed signal from the keyboard.
     * Updates relevant parts of the game based on key pressed (i.e. tower upgrade) if valid.
     */
	@Override
    public void keyPressed(){
        if (gameManager.checkKeyPressed(key)) {
            restartGame();
        }

        else if (key == '\n' && !gameManager.getShowWinMessage()) {
            this.level.setLevelState(false);
        }
    }
    
    /**
     * Called once after every time a mouse button is pressed.
     * Updates relevant parts of the game based on mouse click (i.e. tower upgrade) if valid.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        
        gameManager.checkMouseClick(this, e);
        
    }

    /**
     * Updates logic of each element.
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() { 
        if (!gameManager.checkWin()) {

            this.background.makeBackground(level.getLevelFileName(), this);

            if (!gameManager.sideBar.keyStatus.get("p")) {
                waveManager.waveTimer(framesPassed * gameManager.gameSpeed);
            }

            // Setting up monsters at intervals specified by config
            if (gameManager.allowMonsterSetUp(framesPassed, FPS)) {
                gameManager.setUpMonster(monsterImages);
            }
            
            // call tick for each animated element
            if (gameManager.allowTick()) {
                gameManager.tick(FPS, framesPassed);
                framesPassed++;
            }

            gameManager.setShowWinMessage(this.level.getCurrentLevel() == this.level.getLevels().size() - 1);
            gameManager.draw(this);
        }
        this.level.writeCurrentLevel(this);

        if (gameManager.checkWin()) {
            this.level.startLevelMessage(this);

            if (!level.getPreLevelState()) {
                this.level.nextLevel();
                this.level.setLevelState(true);
                restartGame();

            }
        }
    }

    /**
     * Resets game by creating new instances of classes to set up a new game.
     */
    public void restartGame() {
        this.waveManager = new WaveManager(config, FPS);
        this.gameManager = new GameManager(waveManager, config, this.level.getLevelFileName());
        gameManager.setUpWizardHouse(loadImage("src/main/resources/WizardTD/wizard_house.png"));
        this.framesPassed = 0;
    }


    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }
}