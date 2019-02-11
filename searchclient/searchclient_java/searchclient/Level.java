/**
 * Added new class to save the static level information
 */

package searchclient;

import java.util.ArrayList;

public class Level {

    private static char[][] goalsList;
    private static boolean[][] wallsList;
    private static int max_col;
    private static int max_row;

    public Level(int max_row, int max_col){
        this.max_col = max_col;
        this.max_row = max_row;
        this.goalsList = new char[max_row][max_col];
        this.wallsList = new boolean[max_row][max_col];
    }

    public void addGoal(char goal, int row, int col) {
        this.goalsList[row][col] = goal;
    }

    public void addWall(boolean wall, int row, int col) {
        this.wallsList[row][col] = wall;
    }

    public static char getGoal(int row, int col) {
        return goalsList[row][col];
    }

    public static boolean getWall(int row, int col) {
        return wallsList[row][col];
    }

    public static boolean[] getWallVertical(int row) {
        return wallsList[row];
    }

    public static int getMaxCol() {
        return max_col;
    }

    public static int getMaxRow() {
        return max_row;
    }

    public static char[][] getGoals() {
        char[][] goals = new char[goalsList.length][max_col];

        int i = 0;
        for(char[] row: goalsList){
            goals[i] = row;
        }
        return goals;
    }

    public static boolean[][] getWalls() {
        boolean[][] walls = new boolean[wallsList.length][max_col];

        int i = 0;
        for(boolean[] row: wallsList){
            walls[i] = row;
        }
        return walls;
    }

}
