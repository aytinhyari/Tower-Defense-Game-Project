package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * An abstract class representing animated/active elements in the game.
 */
public abstract class AnimatedElement extends Tile {

    protected float x, y, dx, dy;

    /**
     * Creates an AnimatedElement, given a sprite and x, y coordinates.
     * 
     * @param sprite The sprite for the animated element.
     * @param x The x-coordinate of the element.
     * @param y The y-coordinate of the element.
     */
    public AnimatedElement(PImage sprite, float x, float y) {
        super(sprite);
        this.x = x;
        this.y = y;
    }

    /**
     * Creates an AnimatedElement given an sprite.
     * 
     * @param sprite The sprite for the animated element.
     */
    public AnimatedElement(PImage sprite) {
        super(sprite);
    }

    /**
     * Draws the animated element on the window.
     * 
     * @param app The PApplet handling the window/drawing.
     */
    public void draw(PApplet app) {
        app.image(this.sprite, this.x, this.y);
    }

    /**
     * Move the animated element (incremement x and y values) based on its dx and dy values.
     */
    public void move() {
        this.x += this.dx;
        this.y += this.dy;
    }

    public float getDx() {
        return this.dx;
    }

    public float getDy() {
        return this.dy;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    // For testing
    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
    
}
