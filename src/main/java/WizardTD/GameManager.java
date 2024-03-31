package WizardTD;

import java.util.ArrayList;

import processing.core.PImage;
import processing.event.MouseEvent;
import processing.data.JSONObject;
import java.util.HashMap;
import processing.core.PApplet;

/**
 * Manages all aspects of the game, such as wave timing, gameplay actions and drawing onto the screen.
 */
public class GameManager {

    public WaveManager waveManager;
    public SideBar sideBar;
    public JSONObject config;

    public ArrayList<Monster> monsters;
    public ArrayList<Tower> towers;
    public ArrayList<Fireball> fireballs;
    public  WizardHouse wizardHouse;
    private boolean showWinMessage;

    public int gameSpeed = 1;
    public String levelFile;


    /**
     * Creates a new instance of GameManager.
     * Initialises empty lists of monsters, towers and fireballs that will be added to as the game progresses.
     * 
     * @param waveManager Instance of WaveManager handling waves, including timing and determining when monsters are spawned.
     * @param details Configuration details about the game.
     * @param levelFile Current level to display in game.
     */
    public GameManager(WaveManager waveManager, JSONObject details, String levelFile) {
        this.monsters = new ArrayList<Monster>();
        this.towers = new ArrayList<Tower>();
        this.fireballs = new ArrayList<Fireball>();
        this.waveManager = waveManager;
        this.config = details;
        int towerCost = details.getInt("tower_cost");
        int manaPoolCost = details.getInt("mana_pool_spell_initial_cost");
        this.sideBar = new SideBar(towerCost, manaPoolCost);
        this.levelFile = levelFile;
    }

    /**
     * Sets up a new monster for the game.
     * 
     * @param monsterImages Lists of images of different types of monsters.
     */
    public void setUpMonster(HashMap<String, ArrayList<PImage>> monsterImages) {
        if (waveManager.currentMonsterQuantity >= waveManager.currentMonsters.getInt("quantity")) {
            waveManager.nextMonsterElement();
            waveManager.currentMonsterQuantity = 0;
        }

        char[][] currentMap = Background.obtainMap(levelFile);
        ArrayList<int[]> path = PathCalculations.getValidPath(currentMap);
        int gremlinX = path.get(0)[0];
        int gremlinY = path.get(0)[1];
        ArrayList<PImage> images = monsterImages.get(waveManager.currentMonsters.get("type"));
        waveManager.incrementMonsterQuantity();
        Monster monsterToAdd = new Monster(images, gremlinX, gremlinY, path);
        this.monsters.add(monsterToAdd); 
        monsterToAdd.loadConfigDetails(waveManager.monsters.getJSONObject(waveManager.monsterIndex));
    }

    /**
     * Sets up a new tower for the game based on user input (mouse click or key press).
     * 
     * @param e The mouse event representing the user's click.
     * @param towerImages Images of tower to be displayed at different levels.
     * @param fireballImage The image representing fireballs.
     */
    public void setUpTower(MouseEvent e, ArrayList<PImage> towerImages, PImage fireballImage) {
        
        if (e.getX() <= App.WIDTH - 120 && e.getY() >= 40 && wizardHouse.getMana() >= config.getInt("tower_cost")) {
            
            int towerX = (int)Math.floor(e.getX() / 32) * 32;
            int towerY = (int)Math.floor(e.getY() / 32) * 32 + 8; // adding 8 because y starts at 40
            // Checking that there is grass at chosen tile
            char[][] map = Background.obtainMap(levelFile);
            
            if (map[(towerY - 8)/32 - 1][towerX/32] == ' ' && !Tower.towerExists(towers, towerX, towerY)) {
                Tower towerToAdd = new Tower(towerImages, towerX, towerY, config);
                towers.add(towerToAdd);
                fireballs.add(new Fireball(fireballImage, towerToAdd));
                wizardHouse.addMana(-config.getInt("tower_cost"));
            }
        }

    }

    /**
     * Sets up the Wizard's house on the game map.
     * 
     * @param wizardHouseSprite The image representing the Wizard House.
     */
    public void setUpWizardHouse(PImage wizardHouseSprite) {
        char map[][] = Background.obtainMap(levelFile);
        
        outerLoop:
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j] == 'W') {
                    int wizardHouseX = 32 * j;
                    int wizardHouseY = 40 + 32 * i;
                    this.wizardHouse = new WizardHouse(wizardHouseSprite, wizardHouseX, wizardHouseY, config);
                    break outerLoop;
                }
            }
        }   
    }

    /**
     * Checks if current level has been won by the player.
     * 
     * @return True if the current level has been beaten, false otherwise.
     */
    public boolean checkWin() {
        if (monsters.size() == 0) {
            if (waveManager.wavesDone) {
                return true;
            }   
        }

        return false;
    }
      
    /**
     * Checks if current level has been lost by the player.
     * 
     * @return True if current level has been lost, false otherwise.
     */
    public boolean checkGameOver() {
        if (wizardHouse.getMana() <= 0) {
            return true;
        }

        return false;
    }

    /**
     * Updates the position of game elements based on how many frames have passed.
     * 
     * @param FPS The frames per second of the game.
     * @param framesPassed The number of frames passed since the beginning of the level.
     */
    public void tick(int FPS, int framesPassed) {
        ArrayList<Monster> monstersToRemove = new ArrayList<>();
        
        for (int i = 0; i < gameSpeed; i++) {
            
            for (int j = 0; j < monsters.size(); j++) {
                Monster gremlin = monsters.get(j);
                if (!gremlin.getAlive() && !gremlin.getReachedDest()) {
                    wizardHouse.addMana(gremlin.getManaGainedOnKill());
                    monstersToRemove.add(gremlin);
                
                } else if (gremlin.getReachedDest()) {
                    wizardHouse.addMana((int)-gremlin.getHp());
                    checkGameOver();
                    gremlin.restart();
                
                } else {
                    gremlin.tick();
                }
            }

            this.monsters = Monster.removeMonsters(this.monsters, monstersToRemove);
    
            for (Fireball fireball : fireballs) {
                fireball.tick(FPS, framesPassed, monsters);
            }
        }

        if (framesPassed * gameSpeed % 60 == 0 && framesPassed * gameSpeed > 0) {
            wizardHouse.addMana(wizardHouse.getManaPerSecond());
        }

    } 
    
    /**
     * Draws all game elements and writes relevant text on the window.
     * 
     * @param app The PApplet window elements will be drawn on.
     */
    public void draw(PApplet app) {
        for (Monster monster : monsters) {
            monster.draw(app);
        }

        for (Fireball fireball : fireballs) {
            fireball.draw(app);
        }
        
        app.fill(132, 115, 74);
        app.noStroke();
        app.rect(0, 0, App.WIDTH, App.TOPBAR);
        app.rect(640, 0, App.SIDEBAR, App.HEIGHT);

        waveManager.writeWaveText(app);
        sideBar.draw(app);

        wizardHouse.draw(app);
        

        for (Tower tower : towers) {
            Boolean rangeButton = sideBar.keyStatus.get("1");
            Boolean speedButton = sideBar.keyStatus.get("2");
            Boolean damageButton = sideBar.keyStatus.get("3");
            tower.draw(app, rangeButton, speedButton, damageButton);
        }

        if (checkGameOver()) {
            app.textSize(30);
            app.fill(0);
            app.text("YOU LOST\nPress 'r' to restart", 240, 250);
        } else if (checkWin() && showWinMessage) {
            app.textSize(30);
            app.fill(0);
            app.text("YOU WIN", 240, 250);
        }
    }

    /**
     * Checks if game elements can be updated in the current frame.
     * 
     * @return True if elements can be updated, false otherwise.
     */
    public boolean allowTick() {
        return !sideBar.keyStatus.get("p") && !checkGameOver() && !checkWin();
    }

    /**
     * Checks if a monster can be set up, based on wave details about monsters and frames passed.
     * 
     * @param framesPassed The number of frames passed since the beginning of the level.
     * @param FPS The number of frames per second of the game.
     * @return True if a new monster can be set up, false otherwise.
     */
    public boolean allowMonsterSetUp(int framesPassed, int FPS) {
        float durationBetweenMonsters = waveManager.durationBetweenMonsters();
        
        if (!waveManager.preWaveState && !sideBar.keyStatus.get("p") &&(framesPassed * gameSpeed >= 48) 
        && (framesPassed * gameSpeed % ((int)(durationBetweenMonsters * FPS)) == 0)) {
            return true;
        }

        return false;

    }

    /**
     * Updates game actions based on keyboard input.
     * 
     * @param key The key pressed by the player.
     * @return True if keyboard input indicates restart (and this is valid), false otherwise.
     */
    public boolean checkKeyPressed(char key) {
        sideBar.updateKeyStatus(String.valueOf(key));
        
        if (key == 'r' && checkGameOver() && wizardHouse.getMana() <= 0) {
            return true;
        }

        // Updating speed in case status of f key has changed
        if (sideBar.keyStatus.get("f")) {
            gameSpeed = 2;
        } else {
            gameSpeed = 1;
        }
        
        if (key == 'm' && wizardHouse.getMana() > wizardHouse.getManaPoolCost()) {
            wizardHouse.activateManaPool();
            sideBar.setManaPoolCost(wizardHouse.getManaPoolCost());
            sideBar.updateKeyStatus(String.valueOf(key));

        }

        return false;
    }

    /**
     * Updates game actions based on mouse clicks.
     * 
     * @param app The instance of App running the game.
     * @param e The mouse event representing the user's click.
     */
    public void checkMouseClick(App app, MouseEvent e) {
        
        // Speeding up game via mouse click
        String key = sideBar.checkButtonClicked(e.getX(), e.getY());
        if (key == "f") {
            if (sideBar.keyStatus.get("f")) {
                gameSpeed = 2;
            } else {
                gameSpeed = 1;
            }
        }

        if (key == "m" && wizardHouse.getMana() > wizardHouse.getManaPoolCost()) {
            wizardHouse.activateManaPool();
            sideBar.setManaPoolCost(wizardHouse.getManaPoolCost());
        }
        
        // Setting up new tower
        if (sideBar.keyStatus.get("t")) {
            setUpTower(e, app.towerImages, app.fireballImage);
        } 
        
        // upgrading an existing tower (including one newly set up)
        for (Tower tower : towers) {
            int xClicked = (int)Math.floor(e.getX() / 32) * 32;
            int yClicked = (int)Math.floor(e.getY() / 32) * 32 + 8; // adding 8 because y starts at 40
                
            if (xClicked == tower.x && yClicked == tower.y) {
                
                if (sideBar.keyStatus.get("1") && wizardHouse.getMana() > tower.getRangeCost()) {
                    wizardHouse.addMana(-tower.getRangeCost());
                    tower.upgradeRange();
                
                } if (sideBar.keyStatus.get("2") && wizardHouse.getMana() > tower.getSpeedCost()) {
                    wizardHouse.addMana(-tower.getSpeedCost());
                    tower.upgradeFireSpeed();
                    
                } if (sideBar.keyStatus.get("3") && wizardHouse.getMana() > tower.getDamageCost()) {
                    wizardHouse.addMana(-tower.getDamageCost());
                    tower.upgradeDamage();
                    
                }
            }
        } 
    }

    /**
     * Sets the name of the current game level file.
     * 
     * @param filename The name of the current level file.
     */
    public void setLevelFile(String filename) {
        this.levelFile = filename;
    }

    /**
     * Sets whether the win message should be displayed.
     * 
     * @param state True to show win message, false otherwise.
     */
    public void setShowWinMessage(boolean state) {
        this.showWinMessage = state;
    }

    /**
     * Retrieves whether the win message is currently displayed.
     * 
     * @return True if win message is displayed, false otherwise.
     */
    public boolean getShowWinMessage() {
        return this.showWinMessage;
    }
}