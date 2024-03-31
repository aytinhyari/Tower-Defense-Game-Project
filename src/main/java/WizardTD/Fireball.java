package WizardTD;

import processing.core.PImage;
import processing.core.PApplet;
import java.util.ArrayList;

/**
 * Represents fireball fired by a tower to damage monsters.
 */
public class Fireball extends AnimatedElement{
    private Monster target;
    private Tower tower;

    /**
     * Creates a new FireBall instance.
     * 
     * @param sprite The image representing the fireball.
     * @param tower The tower that fires the fireball.
     */
    public Fireball(PImage sprite, Tower tower) {
        super(sprite);
        this.tower = tower;
        this.x = tower.x + xShift;
        this.y = tower.y + yShift;

    } 

    /** 
     * Updates fireball's position and target where applicable.
     * 
     * @param FPS The frames per second of the game.
     * @param framesPassed The number of frames that have passed since the beginning of the game.
     * @param monsterList A list of monsters currently moving towards the wizard house.
     */
    public void tick(int FPS, int framesPassed, ArrayList<Monster> monsterList) {
        // Finding a target to hit, if target not found already
        int speedCheck = Math.round(FPS * (1/(tower.getSpeed())));
        if (this.target == null && framesPassed % speedCheck == 0) {
            for (Monster monster : monsterList) {
                if (fire(monster)) {
                    setTarget(monster);
                    break;
                }
            }
        }
        // if a target has been set, check if fireball has collided with target
        // if fireball has collided, reset fireball coordinates
        else if (this.target != null) { 
            if (checkCollision() || !this.target.getAlive() || !fire(target)) {
                this.x = tower.x + this.xShift;
                this.y = tower.y + this.yShift;
                this.target.hit(tower.getDamage());
                this.target = null; // preparing for new target to be found
            
            }  else {
                moveToTarget();
                move();

            }
        }

    }

    /**
     * Handles logic for moving fireball towards monster target.
     * Updates dx and dy values based on the current position of the monster.
     */
    public void moveToTarget() {
        this.dx = (this.target.x + this.target.xShift) - this.x;
        this.dy = (this.target.y + this.target.yShift) - this.y;

        // Ensuring the fireball moves at 5 pixels per frame 
        float scaleFactor = (float)Math.sqrt(25/((this.dx*this.dx) + (this.dy*this.dy)));
        
        this.dx = Math.abs(scaleFactor) * this.dx;
        this.dy = Math.abs(scaleFactor) * this.dy;
        }


    /**
     * Determines if fireball can fire at a given monster based on the tower's current range.
     * 
     * @param monster The monster being checked.
     * @return True if the monster is within the tower's range, false otherwise.
     */
    public boolean fire(Monster monster) {
        float xDiff = tower.x - monster.x;
        float yDiff = tower.y - monster.y;
        float distance = (float)Math.sqrt(xDiff*xDiff + yDiff*yDiff);
        if (distance <= tower.getRange()) {
            return true;
        }

        return false;
 
    }

    /**
     * Set the target of the fireball to given monster.
     * 
     * @param target The target monster for the fireball.
     */
    public void setTarget(Monster target) {
        this.target = target;
    }
    
    /**
     * Draw the fireball on the window.
     * 
     * @param app The PApplet window fireball will be drawn on.
     */
    public void draw(PApplet app) {
        app.image(sprite, this.x, this.y);
    }

    /**
     * Check if fireball has collided with its target.
     * 
     * @return True if the fireball has collided with its target, false otherwise.
     */
    public boolean checkCollision() {
        float targetLeft = target.getX();
        float targetRight = target.x + target.getSprite().width;
        float targetTop = target.y;
        float targetBottom = target.y + target.sprite.height;
        float fireballRight = this.x + this.sprite.width;
        float fireballBottom = this.y + this.sprite.height;

        if (fireballRight > targetLeft && this.x < targetRight && fireballBottom > targetTop && this.y < targetBottom) {
            return true;
        }

        return false;
    } 
    
    public Monster getTarget() {
        return this.target;
    }
    
}