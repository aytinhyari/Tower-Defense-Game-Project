package WizardTD;

import processing.core.PImage;
import processing.core.PApplet;
import processing.data.JSONObject;

/**
 * Represents wizard house, generating mana over time with capability to activate mana pool spell.
 */
public class WizardHouse extends AnimatedElement {
    private int manaCap, manaPerSecond, manaPoolCost, manaPoolCostIncrease;
    private float poolSpellCapMul, manaGainedMul, mana;
    private JSONObject details;
    private boolean poolActivated;

    private static final int manaBarLength = 320;
    private static final int manaBarWidth = 20;

    /**
     * Creates WizardHouse instance with given sprite, position and configuration details.
     * 
     * @param sprite Image representing the WizardHouse.
     * @param x The x coordinate of the WizardHouse.
     * @param y The y coordinate of the WizardHouse.
     * @param details JSON Object containing details for the WizardHouse.
     */
    public WizardHouse(PImage sprite, int x, int y, JSONObject details) {
        super(sprite, x, y);
        this.details = details;
        this.poolActivated = false;
    
        loadConfigDetails();
    }

    /**
     * Loads the configuration details specified in JSON Object.
     */
    public void loadConfigDetails() {
        this.mana = details.getInt("initial_mana");
        this.manaCap = details.getInt("initial_mana_cap");
        this.manaPerSecond = details.getInt("initial_mana_gained_per_second");
        this.manaPoolCost = details.getInt("mana_pool_spell_initial_cost");
        this.manaPoolCostIncrease = details.getInt("mana_pool_spell_cost_increase_per_use");
        this.poolSpellCapMul = details.getFloat("mana_pool_spell_cap_multiplier");
        this.manaGainedMul = details.getFloat("mana_pool_spell_mana_gained_multiplier");
    }

    /**
     * Draws WizardHouse to window, along with the mana health bar.
     */
    public void draw(PApplet app) {
        app.textSize(20);  
        app.fill(0); 
        app.text("MANA: ", 315, 28);
        
        // drawing white part of health bar
        app.fill(255, 255, 255);
        app.stroke(0);
        app.strokeWeight(2);
        app.rect(390, 10, manaBarLength, manaBarWidth);

        // drawing blue part of health bar
        app.fill(0, 214, 214);
        app.stroke(0);
        app.strokeWeight(2);
        float blueBarLength = mana/manaCap * manaBarLength;
        app.rect(390, 10, blueBarLength, manaBarWidth);

        // writing mana out of total mana
        if (mana <= 0) {
            mana = 0;
        }

        String manaDisplay = Math.round(mana) + " / " + manaCap;
        app.fill(0);
        app.text(manaDisplay, 480, 28);

        app.image(sprite, x + xShift, y + yShift);
    }

    /**
     * Adds specified amount to mana.
     * If amount specified is positive and mana pool has been activated at least once,
     * a multiplier is applied to amount before it is added to mana.
     */
    public void addMana(float amount) {
        if (amount > 0 && poolActivated) {
            amount *= manaGainedMul;
        }

        if (amount + getMana() > manaCap) {
            this.mana = manaCap;
        } else {
            this.mana += amount;
        }
    }

    /**
     * Increases mana gained multiplier, deducts the cost of the mana pool from
     * mana, and increases mana cap and mana pool cost
     */
    public void activateManaPool() {
        if (poolActivated) {
            this.manaGainedMul += 0.1;
        }
        this.poolActivated = true;
        addMana(-manaPoolCost);
        manaCap *= poolSpellCapMul;
        manaPoolCost += manaPoolCostIncrease;
    }

    public int getManaPoolCost() {
        return this.manaPoolCost;
    }

    public float getMana() {
        return this.mana;
    }

    public int getManaPerSecond() {
        return this.manaPerSecond;
    }

    public int getManaCap() {
        return this.manaCap;
    }

    public float getManaGainedMul() {
        return this.manaGainedMul;
    }

}