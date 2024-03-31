package WizardTD;

import processing.core.PImage;
import processing.core.PApplet;
import java.util.*;
import processing.data.JSONObject;

/**
 * Represents a monster in the game, handling movement and health.
 */
public class Monster extends AnimatedElement {
    private ArrayList<PImage> sprites;
    private int pointer;
    private int deathAnimationCounter;
    private float startX, startY, xDest, yDest, armour, speed, hp, totalHp;
    private ArrayList<int[]> path;
    private int currentLocation, mana_gained_on_kill;
    private boolean alive, reachedDestination;
    
    /**
     * Creates a new instance of a Monster given sprites, starting position and path.
     * 
     * @param sprites List of images for monster, where first is used when monster is alive and the
     * remaining are used for death animation.
     * @param x Starting x coordinate of monster.
     * @param y Starting y coordinate of monster.
     * @param path List of arrays representing the path the monster will traverse.
     */
    public Monster(ArrayList<PImage> sprites, float x, float y, ArrayList<int[]> path) {
        super(sprites.get(0));
        this.sprites = sprites;
        this.startX = x * 32 + xShift;
        this.startY = y * 32 + 40 + yShift;
        this.x = startX;
        this.y = startY;
        this.path = path;
        this.xDest = this.x;
        this.yDest = this.y;
        this.alive = true;
        
    }

    /**
     * Loads configuration details for the monster from a JSON object.
     * 
     * @param details the JSON object to be read.
     */
    public void loadConfigDetails(JSONObject details) {
        this.totalHp = details.getFloat("hp");
        this.hp = this.totalHp;
        this.mana_gained_on_kill = details.getInt("mana_gained_on_kill");
        this.speed = details.getFloat("speed");
        this.armour = details.getFloat("armour");
    }

    /**
     * Updates the monster's position and state.
     * 
     * @return True if additional methods were called to ensure monster
     * maintained speed while staying on its path (for testing purposes), false otherwise.
     */
    public boolean tick() {
        boolean movementAdjusted = false;

        if (this.alive) {
            
            // checking if monster has enough hp, calling death animation if not
            if (this.hp <= 0) {
                monsterDeath();
                return false;
            }

            if (adjustMovementX()) {
                movementAdjusted = true;
            } else if (adjustMovementY()) {
                movementAdjusted = true;
            }

            if (this.x == this.xDest && this.y == this.yDest) {
                determineDirection();
            }

            if (!movementAdjusted) {
                move();
            }
        }

        return movementAdjusted;
    }
  
    /**
     * Draws the monster on the screen, along with its health bar.
     * 
     * @param app The PApplet window where monster will be drawn.
     */
    public void draw(PApplet app) {
        app.image(sprite, this.x, this.y);
        
        // drawing health bar only if monsters hp is greater than 0
        if (this.hp > 0) {
            // drawing red section of health bar
            app.noStroke();
            app.fill(230, 8, 7);
            app.rect(this.x, this.y - 5, this.sprite.width, 2);

            // drawing green section of health bar
            float greenBarLength = (float)(hp)/totalHp * this.sprite.width;
            app.fill(41, 255, 74);
            app.rect(this.x, this.y - 5, greenBarLength, 2);
        }
    }


    /**
     * Increment monster's x coordinate by a given amount.
     * 
     * @param shift The amount that x coordinate is incremented by.
     */
    public void moveX(float shift) {
        this.x += shift;
    }

    /**
     * Increment monster's y coordinate by a given amount.
     * 
     * @param shift The amount that x coordinate is incremented by.
     */
    public void moveY(float shift) {
        this.y += shift;
    }

    /**
     * Determine dx and dy (the x and y direction) that the monster must
     * travel in to remain on the path.
     */
    public void determineDirection() {
        if (currentLocation == path.size() - 1) {
            this.reachedDestination = true;
            this.alive = false;
            return;
        }
        
        int[] currentPoint = path.get(currentLocation);
        int[] nextPoint = path.get(currentLocation + 1);
        
        this.xDest = nextPoint[0] * 32 + xShift;
        this.yDest = nextPoint[1] * 32 + 40 + yShift;
       
        this.dx = (nextPoint[0] - currentPoint[0]) * speed;
        this.dy = (nextPoint[1] - currentPoint[1]) * speed;

        currentLocation++;

    }

    /**
     * Decrease monster's hp by a given amount, after armour has been applied.
     * 
     * @param damage The amount of damage monster will take (before it is reduced by armour).
     */
    public void hit(float damage) {
        this.hp -= (damage * armour);
    }

    /**
     * Handles sprites for monster death animation and updates alive status.
     */ 
    public void monsterDeath() {
        if (pointer == sprites.size() - 1) {
            this.alive = false;
            return;
        }
        
        if (pointer == 0) {
            this.sprites.get(++pointer);
        }
        
        deathAnimationCounter++;
        // since each image in the death animation lasts 4 frames
        if (deathAnimationCounter == 4) {
            this.sprite = sprites.get(++pointer);
            this.deathAnimationCounter = 0;

        }
    }

    /**
     * Handles case where monster may need to move left/right and then up/down in one frame.
     * Ensures monster's speed is maintained while this happens.
     * 
     * @return True, if monster had to move left/right then up/down in one frame.
     */
    public boolean adjustMovementX() {
        
        // if monster is currently moving left or right
        if (this.dx != 0 && Math.abs(xDest - x) < speed && Math.abs(xDest - x) != 0) {
            if (currentLocation == path.size() - 1) {
                moveX(xDest - x);
                moveY(yDest - y);
                this.reachedDestination = true;
                this.alive = false;
                return true;
            }
            
            // future point is point that comes after next point
            float futureDy = speed * (path.get(currentLocation + 1)[1] - path.get(currentLocation)[1]);

            // if monster needs to switch direction from left/right to up/down
            if (futureDy != 0) {
                moveX((this.dx/speed) * Math.abs(xDest - x));
                moveY(((futureDy/speed) * Math.abs(speed - (Math.abs(xDest - x)))));

                this.dy = futureDy;
                this.dx = 0;
                    
            } else {
                moveX(this.dx);
            }
            currentLocation++;
            xDest = path.get(currentLocation)[0] * 32 + xShift;
            yDest = path.get(currentLocation)[1] * 32 + yShift + 40;
            return true;

        } else {
            return false;
        }
    }

    /**
     * Handles case where monster may need to move up/down and then left/right in one frame.
     * Ensures monster's speed is maintained while this happens.
     * 
     * @return True, if monster had to move up/downtthen left/right in one frame.
     */
    public boolean adjustMovementY() {
        if (this.dy != 0 && Math.abs(yDest - y) < speed && Math.abs(yDest - y) != 0) {
            if (currentLocation == path.size() - 1) {
                moveX(xDest - x);
                moveY(yDest - y);
                this.reachedDestination = true;
                this.alive = false;
                return true;
            }
            
            // future point is point that comes after next point
            float futureDx = speed * (path.get(currentLocation + 1)[0] - path.get(currentLocation)[0]);
            // if monster needs to switch direction from up/down to left/right
            if (futureDx != 0) {
                moveY((this.dy/speed) * Math.abs(yDest - y));
                moveX(((futureDx/speed) * Math.abs(speed - (Math.abs(yDest - y)))));

                this.dy = 0;
                this.dx = futureDx;
            } else {
                moveY(this.dy);
            }

            currentLocation++;
            xDest = path.get(currentLocation)[0] * 32 + xShift;
            yDest = path.get(currentLocation)[1] * 32 + yShift + 40;

            return true;

        } else {
            return false;
        }

    }

    /**
     * Resets monster coordinates, velocity, alive and location status
     * in the event that monster is banished from the Wizard House.
     */
    public void restart() {
        this.x = this.startX;
        this.y = this.startY;
        this.alive = true;
        this.reachedDestination = false;
        this.dx = 0;
        this.dy = 0;
        this.xDest = this.x;
        this.yDest = this.y;
        this.currentLocation = 0;
    }

    /**
     * Removes a list of monsters from another list of monsters.
     * 
     * @param list List of monsters where monsters will be removed.
     * @param toRemove The list of monsters to remove.
     * @return Updated list after specified monsters have been removed.
     */
    public static ArrayList<Monster> removeMonsters(ArrayList<Monster> list, ArrayList<Monster> toRemove) {
        for (Monster monster : toRemove) {
            list.remove(monster);
        }

        return list;
    }

    public int getManaGainedOnKill() {
        return this.mana_gained_on_kill;
    }

    public boolean getReachedDest() {
        return this.reachedDestination;
    }

    public boolean getAlive() {
        return this.alive;
    }

    public float getHp() {
        return this.hp;
    }

    // For testing only
    
    public float getStartX() {
        return this.startX;
    }

    public float getStartY() {
        return this.startY;
    }

    public void setSpeed(float speed) {
        this.dx = (this.dx/this.speed) * speed;
        this.dy = (this.dy/this.speed) * speed;
        this.speed = speed;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public void setArmour(float armour) {
        this.armour = armour;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getArmour() {
        return this.armour;
    }

    public void setAlive(boolean state) {
        this.alive = state;
    }

    public void addPath(int[] element) {
        this.path.add(element);
    }
    
}