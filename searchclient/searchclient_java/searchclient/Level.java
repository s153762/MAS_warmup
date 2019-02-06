/**
 * Added new class to save the static level information
 */

package searchclient;

import java.util.ArrayList;

public class Level {

    private static ArrayList<char[]> goalsList;
    private static ArrayList<boolean[]> wallsList;
    private static int max_col;
    private static int max_row;

    public Level(int max_col){
        this.max_col = max_col;
        this.max_row = 0;
        this.goalsList = new ArrayList<char[]>();
        this.wallsList = new ArrayList<boolean[]>();
    }

    public void addGoal(char goal, int row, int col){
        // Dynamic amount of rows
        while(goalsList.size() <= row){
            char[] goalRow = new char[max_col];
            goalsList.add(goalRow);
        }
        this.goalsList.get(row)[col] = goal;
        this.max_row = wallsList.size();
    }

    public void addWall(boolean wall, int row, int col){
        // Dynamic amount of rows
        while(wallsList.size() <= row){
            boolean[] goalRow = new boolean[max_col];
            wallsList.add(goalRow);
        }
        this.wallsList.get(row)[col] = wall;
        this.max_row = wallsList.size();
    }

    public static char getGoal(int row, int col){
        return goalsList.get(row)[col];
    }

    public static boolean getWall(int row, int col) {
        return wallsList.get(row)[col];
    }

    public static boolean[] getWallVertical(int row) {
        return wallsList.get(row);
    }


    public static int getMaxCol() {
        return max_col;
    }

    public static int getMaxRow() {
        return max_row;
    }

    public static char[][] getGoals() {
        char[][] goals = new char[goalsList.size()][max_col];

        int i = 0;
        for(char[] row: goalsList){
            goals[i] = row;
        }
        return goals;
    }

    public static boolean[][] getWalls() {
        boolean[][] walls = new boolean[wallsList.size()][max_col];

        int i = 0;
        for(boolean[] row: wallsList){
            walls[i] = row;
        }
        return walls;
    }

}
