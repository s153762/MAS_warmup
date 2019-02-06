package searchclient;

public class Level {

    private char[][] goals;
    private boolean[][] walls;

    public Level(int max_row, int max_col){
        this.goals = new char[max_row][max_col];
        this.walls = new boolean[max_row][max_col];
    }

    public void setGoal(char goal, int row, int col){
        this.goals[row][col] = goal;
    }

    public void setWall(boolean wall, int row, int col){
        this.walls[row][col] = wall;
    }

    public char getGoal(int row, int col){
        return this.goals[row][col];
    }

    public boolean getWall(int row, int col) {
        return this.walls[row][col];
    }

    public boolean[] getWallVertical(int row) {
        return this.walls[row];
    }

    public int getMaxRow() {
        return this.walls.length;
    }

    public int getMaxCol() {
        return this.walls[0].length;
    }

    public char[][] getGoals() {
        return this.goals;
    }

    public boolean[][] getWalls() {
        return this.walls;
    }
}
