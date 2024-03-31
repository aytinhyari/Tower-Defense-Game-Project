package WizardTD;

import java.util.*;

/**
 * Represents a coordinate with row and column indices
 */
class Coordinate {
    int row;
    int col;
    Coordinate previous;
    
    /**
     * Creates new instance of Coordinate with specified row, column and Coordinate instance of 
     * previous coordinate.
     * 
     * @param row
     * @param col
     * @param previous
     */
    public Coordinate(int row, int col, Coordinate previous) {
        this.row = row; // y
        this.col = col; // x
        this.previous = previous;
    }
}

/**
 * Responsible for pathfinding so that monsters traverse correct path to Wizard House.
 */
public class PathCalculations {
    
    /**
     * Determine the shortest path to 'W' char on given grid when given a starting point.
     * 
     * @param grid A 2D char grid with symbols indicating walkable and unwalkable elements.
     * @param startPoint The point on the grid where path will start.
     * @return ArrayList of coordinates (int[] of length 2) of shortest path from the starting point
     * to the destination 'W'. If no path is found, return null.
     */
    public static ArrayList<int[]> getShortestPath(char[][] grid, int[] startPoint) {
        Coordinate start = new Coordinate(startPoint[1], startPoint[0], null);
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(start);
    
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        visited[start.row][start.col] = true;
    
        while (!queue.isEmpty()) {
            Coordinate p = queue.remove();
    
            // Destination found
            if (grid[p.row][p.col] == 'W') {
                ArrayList<int[]> finalPath = new ArrayList<>();
                while (p.previous != null) {
                    int[] coordinate = { p.col, p.row };
                    finalPath.add(coordinate);
                    p = p.previous; // Move to the previous coordinate
                }
                finalPath.add(startPoint);
                finalPath.add(coordinateBeforeStart(startPoint));
                Collections.reverse(finalPath); // Reverse the path to start from the beginning
                return finalPath;
            }
    
            // Moving up, down, left, right
            int[] dRow = {-1, 1, 0, 0};
            int[] dCol = {0, 0, -1, 1};
    
            for (int i = 0; i < 4; i++) {
                int newRow = p.row + dRow[i];
                int newCol = p.col + dCol[i];
    
                if (isValid(newRow, newCol, grid, visited)) {
                    queue.add(new Coordinate(newRow, newCol, p));
                    visited[newRow][newCol] = true;
                }
            }
        }
        return null;
    }

    /**
     * Check whether a specific point on given grid is walkable/valid.
     * 
     * @param x The x position (row) of the point to be checked.
     * @param y The y position (column) of the point to be checked.
     * @param grid The 2D char array where point is being checked.
     * @param visited A 2D boolean array of visited points.
     * @return True if point is walkable, false if not.
     */
    public static boolean isValid(int x, int y,
                                    char[][] grid,
                                    boolean[][] visited)
    {
        
        if (x >= 0 && y >= 0 && x < grid.length
            && y < grid[0].length && (grid[x][y] == 'X' || grid[x][y] == 'W')
            && visited[x][y] == false) {
        return true;
        }
        return false;
    }

    /**
     * Finds potential start coordinates for monsters on the map.
     * 
     * @param map The map represented as a 2D char array.
     * @return An ArrayList of integer arrays containing potential starting coordinates.
     */
    public static ArrayList<int[]> findStartCoordinates(char[][] map) {
        // Checking top and bottom
        char[] top = map[0];
        char[] bottom = map[map.length - 1];
        ArrayList<int []> startCoordinates = new ArrayList<>();
        
        for (int i = 0; i < top.length; i++) {
            if (top[i] == 'X') {
                int[] coordinates = {i, 0};
                startCoordinates.add(coordinates);
            } if (bottom[i] == 'X') {
                int[] coordinates = {i, map.length - 1};
                startCoordinates.add(coordinates);
            }
        }

        // Checking left and right
        for (int i = 0; i < map.length; i++) {
            if (map[i][0] == 'X') {
                int[] coordinates = {0, i};
                startCoordinates.add(coordinates);
            } if (map[i][map.length - 1] == 'X') {
                int[] coordinates = {map.length - 1, i};
                startCoordinates.add(coordinates);
            }
        }

        return startCoordinates;

    }

    /**
     * Calculates the coordinates before the start position for the monster.
     * so that monster spawns from outside of the map.
     * 
     * @param start The monster's starting coordinate.
     * @return An array of integers [x, y] representing the coordinates before the start position.
     */
    public static int[] coordinateBeforeStart(int[] start) {
        int[] beforeStart = new int[2];
        beforeStart[0] = start[0];
        beforeStart[1] = start[1];

        // if monster is coming from left side of the map
        if (start[0] == 0) {
            beforeStart[0]--;
        
        // if monster is coming from top of the map
        } else if (start[1] == 0) {
            beforeStart[1]--;
        
         // if monster is coming from right side of the map
        } else if (start[0] == 19) {
            beforeStart[0]++;
        
        // if monster is coming from bottom of the map
        } else if (start[1] == 19) {
            beforeStart[1]++;
        }

        return beforeStart;
    }

    /**
     * Randomly selected a valid path for monsters to traverse on the map from
     * all possible valid (and shortest) paths.
     * 
     * @param map The map represented as a 2D char array.
     * @return An ArrayList of integer arrays representing the valid path. If no
     * valid path was found, returns null.
     */
    public static ArrayList<int[]> getValidPath(char[][] map) {
        ArrayList<ArrayList<int[]>> paths = new ArrayList<>();

        ArrayList<int[]> startCoordinates = findStartCoordinates(map);
        
        for (int[] coordinate : startCoordinates) {
            ArrayList<int[]> potentialPath = getShortestPath(map, coordinate);
            
            if (potentialPath != null) {
                paths.add(potentialPath);
            }
        }

        if (paths.size() == 0) {
            return null;
        }

        Random random = new Random();
        int index = random.nextInt(paths.size());

        return paths.get(index);

    }

}