package searchclient;




//
public class Node {
    private int row;
    private int col;
    private Box box;
    private Goal goal;

    public Node (int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean checkGoalandBox(){
        if (goal != null && box != null) {
            if (goal.getName().equals(box.getName())) { //Implement .getcolor here for multiagents
                return true;
            }
            else
                return false;
        }

        return false;
    }

    public int getRow() { return row; }

    public int getCol() { return col; }

    public Box getBox() { return box; }

    public void setBox(Box box) { this.box = box; }

    public Goal getGoal() { return goal; }

    public void setGoal(Goal goal) { this.goal = goal; }
}
