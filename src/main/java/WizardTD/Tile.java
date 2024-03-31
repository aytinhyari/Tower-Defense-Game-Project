package WizardTD;

import processing.core.PImage;
import processing.core.PApplet;
import java.util.ArrayList;

/**
 * Represents a tile in the game, which can have one or multiple sprites.
 */
public class Tile {

    protected static final int TILE_LENGTH = 32;
    protected PImage sprite;
    protected ArrayList<PImage> sprites;
    protected float xShift, yShift;
    
    /**
     * Creates a Tile instance given a single sprite.
     * Calculates the sprites x and y shift to ensure it is centered
     * on a 32 x 32 pixel tile.
     * 
     * @param sprite Given sprite for tile.
     */
    public Tile(PImage sprite) {
        this.sprite = sprite;
        this.xShift = (TILE_LENGTH - this.sprite.width)/2;
        this.yShift = (TILE_LENGTH - this.sprite.height)/2;
    }

    /**
     * Creates a Tile instance given a list of sprites.
     * Sets current sprite to first element in the list of sprites.
     * 
     * @param sprites List of sprites for tile.
     */
    public Tile(ArrayList<PImage> sprites) {
        this(sprites.get(0));
        this.sprites = sprites;
    }

    /**
     * Draws current sprite to window with given x and y coordinates.
     * 
     * @param app The PApplet window where sprite is drawn.
     * @param x The x coordinate where sprite is drawn.
     * @param y The y coordinate where sprite is drawn. 
     */
    public void draw(PApplet app, int x, int y) {
        app.image(sprite, x, y);
    }

    public PImage getSprite() {
        return this.sprite;
    }

    /**
     * Retrieves sprite from sprite list at a given index.
     * 
     * @param index Index of sprite in sprite list
     * @return The sprite specified. If the Tile does not have
     * a list of sprites, it returns its current sprite instead.
     */
    public PImage getSprite(int index) {
        if (this.sprites != null) {
            return this.sprites.get(index);
        } else {
            return this.sprite;
        }
    }

    public ArrayList<PImage> getSprites() {
        return this.sprites;
    }

    public float getXShift() {
        return this.xShift;
    }

    public float getYShift() {
        return this.yShift;
    }

    /**
     * Sets current sprite to sprite from list at specified index. If the Tile does
     * not have a list of sprites, does nothing.
     * 
     * @param index Index of sprite to be set as current sprite.
     */
    public void setCurrentSprite(int index) {
        if (this.sprites != null) {
            this.sprite = this.sprites.get(index);
        }
    } 
}