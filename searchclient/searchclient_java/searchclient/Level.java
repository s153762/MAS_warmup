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
    private static ArrayList<Goal> goalCoordinates;

    public Level(int max_row, int max_col){
        this.max_col = max_col;
        this.max_row = max_row;
        this.goalsList = new char[max_row][max_col];
        this.wallsList = new boolean[max_row][max_col];
        this.goalCoordinates = new ArrayList<>();
    }

    public void addGoal(char goal, int row, int col) {
        this.goalsList[row][col] = goal;
        goalCoordinates.add(new Goal(row,col,goal));
    }

    public void addWall(boolean wall, int row, int col) {
        this.wallsList[row][col] = wall;
    }

    public static char getGoal(int row, int col) {
        return goalsList[row][col];
    }

    public static ArrayList<Goal> getGoals(){
        return goalCoordinates;
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

    public static char[][] getGoalsList() {
        return goalsList;
    }
    public static boolean[][] getWallsList() {
        return wallsList;
    }

}
