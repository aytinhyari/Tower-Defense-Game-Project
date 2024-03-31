package WizardTD;

import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PApplet;

/**
 * Manages the game levels, including level progression and layout.
 */
public class Level {

    private int currentLevel;
    private boolean preLevelState;
    private JSONObject config;
    private JSONArray levels;

    /**
     * Creates a new instance of Level.
     * 
     * @param config The configuration object containing details of game levels.
     */
    public Level(JSONObject config) {
        this.currentLevel = 0;
        this.preLevelState = true;
        this.config = config;

        if (this.config.get("layout") instanceof String) {
            this.levels = new JSONArray();
            this.levels.append(config.getString("layout"));

        } else if (this.config.get("layout") instanceof JSONArray) {
            this.levels = config.getJSONArray("layout");
        }   
    }

    /**
     * Sets the state of the current level (if user is playing or waiting in between levels).
     * 
     * @param state True for pre-level state, false otherwise
     */
    public void setLevelState(boolean state) {
        this.preLevelState = state;
    }

    public String getLevelFileName() {
        return levels.getString(currentLevel);
    }

    /**
     * Move on to next level, if there is another level left.
     */
    public void nextLevel() {
        if (currentLevel != levels.size() - 1) {
            currentLevel++;
        }

    }

    /**
     * Write current level to the window.
     * 
     * @param app The PApplet window where message will be written.
     */
    public void writeCurrentLevel(PApplet app) {
        app.textSize(15);
        String text = "GAME LEVEL: " + (currentLevel + 1);
        app.fill(0);
        app.text(text, 645, 60);
        app.noFill();
    }

    /**
     * Write message indicating player has won current level and can advance to next level.
     * 
     * @param app The PApplet window where message will be written.
     * @return True if message was written, false if not.
     */
    public boolean startLevelMessage(PApplet app) {
        if (currentLevel != levels.size() - 1) { 
            app.textSize(30);
            app.fill(0);
            app.text("Level " + (currentLevel + 1) + " complete!", 240, 250);
            app.text("Press ENTER to start level " + (currentLevel + 2), 200, 280);
            return true;

        }

        return false;
    }

    public boolean getPreLevelState() {
        return this.preLevelState;
    }

    public int getCurrentLevel() {
        return this.currentLevel;
    }

    public JSONArray getLevels() {
        return this.levels;
    }
}