package WizardTD;

import processing.core.PApplet;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the sidebar in the game for different gameplay actions.
 */
public class SideBar {
    static String[] textInSquares = {"FF", "P", "T", "U1", "U2", "U3", "M"}; 
    static String[] keys = {"f", "p", "t", "1", "2", "3", "m"};
    static int numberOfSquares = 7;
    static int spaceBetweenSquares = 10;

    String[] actionDescriptions = {"2x speed", "PAUSE", "Build\nTower", "Upgrade\nrange", 
                                    "Upgrade\nspeed", "Upgrade\ndamage", "Mana pool\ncost: "};
    int sideLength, squareX, squareY, startingSquareY, towerCost, manaPoolCost;
    HashMap<String, Boolean> keyStatus;

    /**
     * Create a new instance of SideBar with given tower and mana pool cost.
     * 
     * @param towerCost The cost of placing a tower.
     * @param manaPoolCost The current cost of the mana pool.
     */
    public SideBar(int towerCost, int manaPoolCost) {
        this.sideLength = 45;
        this.squareX = 650;
        this.startingSquareY = 100;
        this.squareY = startingSquareY;
        this.towerCost = towerCost;
        this.manaPoolCost = manaPoolCost;
        this.actionDescriptions[6] = "Mana pool\ncost: " + manaPoolCost;

        setUpKeyStatus();
    }

    /**
     * Draw the sidebar on the window.
     * 
     * @param app The PApplet window where sidebar is drawn.
     */
    public void draw(PApplet app) {
        app.stroke(0);
        app.strokeWeight(2);
        app.noFill();

        // Drawing 7 squares
        for (int i = 0; i < numberOfSquares; i++) {
            
            // Drawing a square
            app.rect(squareX, squareY, sideLength, sideLength);

            turnGrey(app, i);
            turnYellow(app, i);
            
            
            // Writing text in square
            app.textSize(25);
            app.fill(0);
            app.text(textInSquares[i], squareX + 5, squareY + 30);
            app.noFill();

            // writing action description next to square
            app.textSize(12);
            app.text(actionDescriptions[i], squareX + 50, squareY + 15);
            squareY += sideLength + spaceBetweenSquares;
        }

        resetSquareY();

    }

    /**
     * Reset Y coordinate of a square (button) to its starting coordinate.
     */
    public void resetSquareY() {
        this.squareY = this.startingSquareY;

    }

    /**
     * Sets up the initial key status of all possible keys to be presed.
     * Maps each potential key to false initially.
     */
    public void setUpKeyStatus() {
        this.keyStatus = new HashMap<>();
        for (String key : keys) {
            keyStatus.put(key, (Boolean)false);
        }
    }

    /** 
    * Updates key status if key passed already exists in hash map
    * Otherwise, do nothing.

    * @param key The key to update status for (if valid).
    */ 
    
    public void updateKeyStatus(String key) {
        if (keyStatus.containsKey(key)) {
            Boolean newStatus = !keyStatus.get(key);
            keyStatus.put(key, newStatus);
        }
    }
    /**
     * Checks if any gameplay actions are clicked with mouse.
     * Updates key status hash map accordingly.
     * 
     * @param xClicked The x coordinate of mouse click on window.
     * @param yClicked The y coordinate of mouse click on window.
     * @return The corresponding key of gameplay action clicked. If no
     * valid action was clicked, return an empty string.
     */
    public String checkButtonClicked(int xClicked, int yClicked) {
        for (int i = 0; i < numberOfSquares; i++) {
            // Checking X coordinate
            if ((xClicked >= squareX) && (xClicked <= squareX + sideLength)) {
                // Checking Y coordinate
                if ((yClicked >= squareY + i * (spaceBetweenSquares + sideLength)) && (yClicked <= squareY + sideLength + i *(spaceBetweenSquares + sideLength))) {
                    updateKeyStatus(keys[i]);
                    return keys[i];
                }
            }
        } 
        return "";
    }

    /**
     * Updates mana pool cost.
     * Updates action description for mana pool to reflect the cost change.
     * 
     * @param cost The new cost of the mana pool.
     */
    public void setManaPoolCost(int cost) {
        this.manaPoolCost = cost;
        this.actionDescriptions[6] = "Mana pool\ncost: " + manaPoolCost;
    }

    /**
     * Draws the cost tool tip for the tower and mana pool gameplay action.
     * 
     * @param text The text to be displayed in the tool tip.
     * @param x The x coordinate where tool tip is displayed.
     * @param y The y coordinate where tool tip is displayed.
     * @param app The PApplet window where tool tip is drawn.
     */
    public void drawToolTip(String text, float x, float y, PApplet app) {
        app.fill(255);
        app.rect(x, y, app.textWidth(text) + 10, 20);

        app.fill(0);
        app.text(text, x + 5, y + 15);

        app.noFill();
    }

    /**
     * Turns square (button) grey if mouse hovers over it and also draws the cost tool tips if 
     * the mouse hovers over the tower or mana pool buttons.
     * 
     * @param app The PApplet window for rendering images.
     * @param i The specific square/button indicated by numbers 0-6.
     */
    public void turnGrey(PApplet app, int i) {
        boolean hoverSquare = (app.mouseX >= squareX) && (app.mouseX <= squareX + sideLength) && 
                                (app.mouseY >= squareY) && (app.mouseY <= squareY + sideLength);
            
        // Turning square grey if mouse hovers over it
        if (hoverSquare) {
            app.fill(206, 206, 206);
            app.rect(squareX, squareY, sideLength, sideLength);
            app.fill(0, 0, 0);
            app.noFill();

            // Showing tooltip for tower and mana pool buttons
            if (i == 2) { // tower square
                drawToolTip("Cost: " + towerCost, squareX - 70, squareY, app);
            } else if (i == 6) {
                drawToolTip("Cost: " + manaPoolCost, squareX - 70, squareY, app);
            }

        }
    }

    /**
     * Turns square yellow if corresponding key is pressed or it is selected via mouse click.
     * 
     * @param app The PApplet window for rendering.
     * @param i The specific square/button indicated by numbers 0-6.
     */
    public void turnYellow(PApplet app, int i) {
        for (Map.Entry<String, Boolean> entry : keyStatus.entrySet()) {
            // i = 6 is the mana pool button, which is automatically applied
            // so it does not need to turn yellow
            if (entry.getKey() == keys[i] && entry.getValue() && keys[i] != "m") {
                app.fill(255, 255, 8);
                app.rect(squareX, squareY, sideLength, sideLength);
                app.fill(0, 0, 0);
                app.noFill();
            }

        }

    }
}