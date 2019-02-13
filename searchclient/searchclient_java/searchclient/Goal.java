package searchclient;

public class Goal {
    private int row;
    private int col;
    private char goalName;

    public Goal(int row, int col, char goalName){
        this.row = row;
        this.col = col;
        this.goalName = goalName;
    }

    public char getGoalName() {
        return goalName;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

}
