package searchclient;

public abstract class Node {
    private int row;
    private int col;
    private char name;

    public Node(int row, int col, char name){
        this.row = row;
        this.col = col;
        this.name = name;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getName() {
        return name;
    }
}