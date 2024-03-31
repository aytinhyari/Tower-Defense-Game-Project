package WizardTD;

import processing.core.PImage;
import processing.core.PApplet;
import processing.data.JSONObject;
import java.util.ArrayList;

/**
 * Represents a tower in the game which can be upgraded.
 */
public class Tower extends AnimatedElement {
    private ArrayList<PImage> towerSprites;
    private int range, level, rangeUpgrades, damageUpgrades, fireSpeedUpgrades;
    private float damage, initialDamage, fireSpeed;
    private int rangeCost, damageCost, fireSpeedCost;
    

    /**
     * Creates a new instance of Tower.
     * 
     * @param towerSprites The list of tower sprites to represent different levels.
     * @param x The x coordinate of the tower.
     * @param y The y coordinate of the tower.
     * @param details JSON object containing tower details.
     */
    public Tower(ArrayList<PImage> towerSprites, int x, int y, JSONObject details) {
        super(towerSprites.get(0), x, y);
        this.towerSprites = towerSprites;
        
        this.range = details.getInt("initial_tower_range");
        System.out.println(this.range);
        this.initialDamage = details.getFloat("initial_tower_damage");
        this.damage = this.initialDamage;
        this.fireSpeed = details.getFloat("initial_tower_firing_speed");

        this.rangeCost = 20;
        this.damageCost = 20;
        this.fireSpeedCost = 20;

    }

    /**
     * Draws the tower onto the screen, and calls for upgrade table if upgrades are selected.
     * 
     * @param app The PApplet window where tower is drawn.
     * @param rangeButton True if range upgrade is selected, false otherwise.
     * @param speedButton True if speed upgrade is selected, false othetwise.
     * @param damageButton True if damage upgrade is selected, false otherwise.
     */
    public void draw(PApplet app, Boolean rangeButton, Boolean speedButton, Boolean damageButton) {
        boolean hoverCheck = (app.mouseX >= x) && (app.mouseX <= x + sprite.width) &&
        (app.mouseY >= y) && (app.mouseY <= y + sprite.height);

        if (hoverCheck) {
            app.noFill();
            app.stroke(255, 255, 8);
            app.strokeWeight(2);
            app.ellipse(x + sprite.width/2, y + sprite.height/2, range * 2, range * 2);
            drawUpgradeCosts(app, rangeButton, speedButton, damageButton);

        }
        checkUpgrades();
        app.image(this.sprite, this.x, this.y);  
        drawUpgrades(app);  
    }

    /**
     * Upgrades range of the tower.
     * Increases upgrade cost by 10.
     */
    public void upgradeRange() {
        this.range += 32;
        this.rangeUpgrades++;
        this.rangeCost += 10;
    }

    /**
     * Upgrades damage dealt on enemies by fireballs shot from the tower.
     * Increases upgrade cost by 10.
     */
    public void upgradeDamage() {
        this.damage += this.initialDamage * 0.5;
        this.damageUpgrades++;
        this.damageCost += 10;
    }

    /**
     * Upgrades fire speed of the tower.
     * Increases upgrade cost by 10.
     */
    public void upgradeFireSpeed() {
        this.fireSpeed += 0.5;
        this.fireSpeedUpgrades++;
        this.fireSpeedCost += 10;
    }

    /**
     * Checks if tower can be leveled up based on current upgrades.
     * If tower can be leveled up, sprite is changed and level attribute is updated.
     */
    public void checkUpgrades() {
        if (rangeUpgrades >= 1 && damageUpgrades >= 1 && fireSpeedUpgrades >= 1) {
            this.level = 1;
            this.sprite = towerSprites.get(this.level);
        
        }  if (rangeUpgrades >= 2 && damageUpgrades >= 2 && fireSpeedUpgrades >= 2) {
            this.level = 2;
            this.sprite = towerSprites.get(this.level);
        }

    }

    /**
     * Draws upgrades onto tower, such as O's for range and X's for damage.
     * 
     * @param app The PApplet window where upgrades are drawn.
     */
    private void drawUpgrades(PApplet app) {
        // drawing range upgrades
        app.fill(189, 16, 198);
        for (int i = 0; i < rangeUpgrades - this.level; i++) {
            app.textSize(10);
            app.text("O", x + i*8, y + 5);
        }

        // drawing damage upgrades
        for (int i = 0; i < damageUpgrades - this.level; i++) {
            app.textSize(10);
            app.text("X", x + i*8, y + sprite.height);
        }

        // drawing speed upgrades
        if (fireSpeedUpgrades - level != 0) {
            app.strokeWeight((float)0.5* (fireSpeedUpgrades - this.level + 2));
            app.noFill();
            app.stroke(123, 181, 255);
            app.rect(x + 5, y + 6, 21, 20, 2);
        }
        

    }

    /**
     * Draws upgrade table indicating the upgrade cost of each upgrade selected.
     * If no upgrade is selected, does nothing.
     * 
     * @param app The PApplet window for rendering.
     * @param rangeButton True if range upgrade is selected, false otherwise.
     * @param speedButton True if speed upgrade is selected, false otherwise.
     * @param damageButton True if damage upgrade is selected, false otherwise.
     */
    private void drawUpgradeCosts(PApplet app, Boolean rangeButton, Boolean speedButton, Boolean damageButton) {
        if (!rangeButton && !speedButton && !damageButton) {
            return;
        }

        app.stroke(0);
        app.strokeWeight(1);
        app.fill(255);
        app.rect(650, 550, 105, 20);
        app.textSize(13);
        app.fill(0);
        app.text("Upgrade cost", 655, 565);

        int count = 0;
        int totalCost = 0;
        if (rangeButton) {
            count++;
            totalCost += rangeCost;
        } if (speedButton) {
            count++;
            totalCost += fireSpeedCost;
        } if (damageButton) {
            count++;
            totalCost += damageCost;
        }

        app.fill(255);
        app.rect(650, 570, 105, 20 * count);
        count = 0;
        app.fill(0);

        if (rangeButton) {
            app.text("range: " + rangeCost, 655, ++count*20 + 565);
        } if (speedButton) {
            app.text("speed: " + fireSpeedCost, 655, ++count*20 + 565);
        } if (damageButton) {
            app.text("damage: " + damageCost, 655, ++count*20 + 565);
        }

        app.fill(255);
        app.rect(650, 550 + 20 * ++count, 105, 20);
        app.fill(0);
        app.text("Total: " + totalCost, 655, count*20 + 565);

    }

    public int getRangeCost() {
        return this.rangeCost;
    }

    public int getSpeedCost() {
        return this.fireSpeedCost;
    }

    public int getDamageCost() {
        return this.damageCost;
    }

    public float getDamage() {
        return this.damage;
    }

    public float getSpeed() {
        return this.fireSpeed;
    }

    public float getRange() {
        return this.range;
    } 

    /**
     * Given a list of towers, checks if a tower exists at given x and y coordinates.
     * 
     * @param towers List of towers to be checked.
     * @param xCheck X coordinate to be checked.
     * @param yCheck Y coordinate to be checked.
     * @return True if a tower exists at given coordinates, false otherwise.
     */
    public static boolean towerExists(ArrayList<Tower> towers, int xCheck, int yCheck) {
        for (Tower tower : towers) {
            if (tower.x == xCheck && tower.y == yCheck) {
                return true;
            }
        }

        return false;
    }

    public float getInitialDamage() {
        return this.initialDamage;
    }

    public int getLevel() {
        return this.level;
    }
}