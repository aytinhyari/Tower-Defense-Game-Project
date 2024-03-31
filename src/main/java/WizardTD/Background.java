package WizardTD;

import java.util.*;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import processing.core.PImage;
import processing.core.PApplet;

/**
 * Handles drawing of the board (shrub, grass and path).
 */
public class Background {
    private int pixelX;
    private int pixelY;
    private Tile grass, shrub, paths;

    private static final int WIDTH = 20;
    private static final int LENGTH = 20;


    /** 
     * Creates an instance of Background, given TIle objects grass, shrub and paths.
     * Sets a default value of 0 for pixelX and 40 for pixelY.
     * @param grass Tile object representing grass on the map.
     * @param shrub Tile object representing shrub on the map.
     * @param paths Tile object representing paths on the map.
     */
    public Background(Tile grass, Tile shrub, Tile paths) {
        pixelX = 0;
        pixelY = 40;
        this.grass = grass;
        this.shrub = shrub;
        this.paths = paths;
    }

    /**
     * Draws background based on the contents of filename given onto PApplet instance passed.
     * 
     * @param filename      The name of the .txt file with details about background.
     * @param app           The PApplet instance/window where background will be drawn.
     */
    public void makeBackground(String filename, PApplet app) {
        char[][] layout = obtainMap(filename);

        for (int i = 0; i < LENGTH; i++) {
            for (int j = 0; j < WIDTH; j++) {
                char currentChar = layout[i][j];
                    
                if (currentChar == 'S') {
                    shrub.draw(app, pixelX, pixelY);
                } else if (currentChar == 'X') {
                    Tile chosenPath = findSuitablePath(layout, i, j, app);
                    chosenPath.draw(app, pixelX, pixelY);
                } else {
                    grass.draw(app, pixelX, pixelY);
                } 
                     
                adjustPixelX();
                }
            adjustPixelY();
            resetPixelX();
            } 
                
            resetPixelX();
            resetPixelY(); 
        } 
   

    /**
     * Incrementes pixelX by 32.
     */
    private void adjustPixelX() {
        pixelX += 32;

    }

    /**
     * Resets pixelX back to default value, 0.
     */
    private void resetPixelX() {
        pixelX = 0;
    }

    /**
     * Resets pixelY back to default value, 40.
     */
    private void resetPixelY() {
        pixelY = 40;
    }

    /**
     * Increments pixelY by 32.
     */
    private void adjustPixelY() {
        pixelY += 32;
    }

    /**
     * Generates a two-dimensional 20x20 char array of given filename. 
     * 
     * @param filename      The filename of file to be read to generate the array.
     * @return              A 20x20 2D char array containing the characters in the given file.
     */
    public static char[][] obtainMap(String filename) {
        char[][] mapLayout = new char[LENGTH][WIDTH];
        File file = new File(filename);
        try (Scanner mapDetails = new Scanner(file);) {
        
        for (int i = 0; i < LENGTH; i++) {
            // Creating a string with 20 whitespace in case there are no more lines in txt file
            String line = new String(new char[20]).replace('\0', ' ');

            if (mapDetails.hasNext()) {
                line = mapDetails.nextLine();
            } 
            
            int length = line.length();
            
            if (line.length() > 20) {
                length = 20;
            }

            for (int j = 0; j < length; j++) {
                mapLayout[i][j] = line.charAt(j);
            }
            
        }
    
        // replacing null with space (grass)
        for (int a = 0; a < mapLayout.length; a++) {
            for (int b = 0; b < mapLayout[0].length; b++) {
                if (mapLayout[a][b] == '\0') {
                    mapLayout[a][b] = ' ';
                }
            }
    
        }
        return mapLayout;

    } catch (FileNotFoundException e) {
        System.out.println(e);
    }
    
    return mapLayout;
    
    }

    /**
     * Finds a suitable path tile based on the neighbouring map elements.
     * Ensures path orientation is correct, and calls for rotation of path images if necessary.
     * 
     * @param map The map represented as a 2D array of characters.
     * @param lineIndex The index of the current line in the map.
     * @param index The index of the current position in the line.
     * @param app The PApplet instance for image rotation.
     * @return A suitable path tile containing suitable sprite for path drawing.
     */
    private Tile findSuitablePath(char[][] map, int lineIndex, int index, PApplet app) {
        boolean checkLeft = (index > 0) && (map[lineIndex][index - 1] == 'X');
        boolean checkRight = (index < map[lineIndex].length - 1) && (map[lineIndex][index + 1] == 'X');
        boolean checkUp = (lineIndex > 0) && (map[lineIndex - 1][index] == 'X');
        boolean checkDown = (lineIndex < map.length - 1) && (map[lineIndex + 1][index] == 'X');


        if (checkLeft && checkRight && checkUp && checkDown) {
            this.paths.setCurrentSprite(3);
            return this.paths;
        } else if (checkLeft && checkRight && checkUp) {
            return new Tile(rotateImageByDegrees(paths.getSprite(2), 180, app));
        } else if (checkLeft && checkRight && checkDown) {
            this.paths.setCurrentSprite(2);
            return this.paths;
        } else if (checkUp && checkDown && checkLeft ) {
            return new Tile(rotateImageByDegrees(paths.getSprite(2), 90, app));
        } else if (checkUp && checkDown && checkRight) {
            return new Tile(rotateImageByDegrees(paths.getSprite(2), 270, app));
        } else if (checkUp && checkRight) {
            return new Tile(rotateImageByDegrees(paths.getSprite(1), 180, app));
        } else if (checkUp && checkLeft) {
            return new Tile(rotateImageByDegrees(paths.getSprite(1), 90, app));
        } else if (checkDown && checkRight) {
            return new Tile(rotateImageByDegrees(paths.getSprite(1), 270, app));
        } else if (checkDown && checkLeft) {
            this.paths.setCurrentSprite(1);
            return this.paths;
        } else if (checkUp || checkDown) {
            return new Tile(rotateImageByDegrees(paths.getSprite(0), 90, app));
        } else {
            this.paths.setCurrentSprite(0);
            return this.paths;
        }

    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * Rotates a given image clockwise by a given angle.
     * 
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    private PImage rotateImageByDegrees(PImage pimg, double angle, PApplet app) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = app.createImage(newWidth, newHeight, App.ARGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }

        return result;
    }
}
