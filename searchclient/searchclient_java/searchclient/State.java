package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class State {
    private static final Random RNG = new Random(1);

    public int agentRow;
    public int agentCol;
    private int pathCost;

    // Arrays are indexed from the top-left of the level, with first index being row and second being column.
    // Row 0: (0,0) (0,1) (0,2) (0,3) ...
    // Row 1: (1,0) (1,1) (1,2) (1,3) ...
    // Row 2: (2,0) (2,1) (2,2) (2,3) ...
    // ...
    // (Start in the top left corner, first go down, then go right)
    // E.g. this.walls[2] is an array of booleans having size MAX_COL.
    // this.walls[row][col] is true if there's a wall at (row, col)
    //

    public ArrayList<char[]> boxesList;
    //public char[][] boxes;

    public State parent;
    public Command action;

    private int g;

    private int _hash = 0;

    public State(State parent) {
        this.parent = parent;
        this.boxesList = new ArrayList<char[]>();
        if (parent == null) {
            this.g = 0;
        } else {
            this.g = parent.g() + 1;
        }
    }

    public int g() {
        return this.g;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    public boolean isGoalState() {
        for (int row = 1; row < Level.getMaxRow() - 1; row++) {
            for (int col = 1; col < Level.getMaxCol() - 1; col++) {
                char g = Level.getGoal(row, col);
                char b = Character.toLowerCase(boxesList.get(row)[col]);
                if (g > 0 && b != g) {
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<State> getExpandedStates() {
        ArrayList<State> expandedStates = new ArrayList<>(searchclient.Command.EVERY.length);
        for (Command c : Command.EVERY) {
            // Determine applicability of action
            int newAgentRow = this.agentRow + Command.dirToRowChange(c.dir1);
            int newAgentCol = this.agentCol + Command.dirToColChange(c.dir1);

            if (c.actionType == Command.Type.Move) {
                // Check if there's a wall or box on the cell to which the agent is moving
                if (this.cellIsFree(newAgentRow, newAgentCol)) {
                    State n = this.ChildState();
                    n.action = c;
                    n.agentRow = newAgentRow;
                    n.agentCol = newAgentCol;
                    expandedStates.add(n);
                }
            } else if (c.actionType == Command.Type.Push) {
                // Make sure that there's actually a box to move
                if (this.boxAt(newAgentRow, newAgentCol)) {
                    int newBoxRow = newAgentRow + Command.dirToRowChange(c.dir2);
                    int newBoxCol = newAgentCol + Command.dirToColChange(c.dir2);
                    // .. and that new cell of box is free
                    if (this.cellIsFree(newBoxRow, newBoxCol)) {
                        State n = this.ChildState();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxesList.get(newBoxRow)[newBoxCol] = this.boxesList.get(newAgentRow)[newAgentCol];
                        n.boxesList.get(newAgentRow)[newAgentCol] = 0;
                        expandedStates.add(n);
                    }
                }
            } else if (c.actionType == Command.Type.Pull) {
                // Cell is free where agent is going
                if (this.cellIsFree(newAgentRow, newAgentCol)) {
                    int boxRow = this.agentRow + Command.dirToRowChange(c.dir2);
                    int boxCol = this.agentCol + Command.dirToColChange(c.dir2);
                    // .. and there's a box in "dir2" of the agent
                    if (this.boxAt(boxRow, boxCol)) {
                        State n = this.ChildState();
                        n.action = c;
                        n.agentRow = newAgentRow;
                        n.agentCol = newAgentCol;
                        n.boxesList.get(this.agentRow)[this.agentCol] = this.boxesList.get(boxRow)[boxCol];
                        n.boxesList.get(boxRow)[boxCol] = 0;
                        expandedStates.add(n);
                    }
                }
            }
        }
        Collections.shuffle(expandedStates, RNG);
        return expandedStates;
    }

    private boolean cellIsFree(int row, int col) {
        return !Level.getWall(row,col) && this.boxesList.get(row)[col] == 0;
    }

    private boolean boxAt(int row, int col) {
        return this.boxesList.get(row)[col] > 0;
    }

    private State ChildState() {
        State copy = new State(this);
        int i = 0;
        for (char[] row: this.boxesList) {
            copy.boxesList.add(new char[Level.getMaxCol()]);
            System.arraycopy(row, 0, copy.boxesList.get(i), 0, Level.getMaxCol());
            i++;
        }
        return copy;
    }

    public ArrayList<State> extractPlan() {
		ArrayList<State> plan = new ArrayList<>();
        State n = this;
        while (!n.isInitialState()) {
            plan.add(n);
            n = n.parent;
        }
        Collections.reverse(plan);
        return plan;
    }

    private char[][] boxesToList(ArrayList<char[]> boxesList){
        char[][] boxes = new char[Level.getMaxRow()][Level.getMaxCol()];

        int i = 0;
        for (char[] row: boxesList){
            boxes[i] = row;
            i++;
        }
        return boxes;
    }

    @Override
    public int hashCode() {
        if (this._hash == 0) {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.agentCol;
            result = prime * result + this.agentRow;
            result = prime * result + Arrays.deepHashCode(boxesToList(this.boxesList));
            result = prime * result + Arrays.deepHashCode(Level.getGoals());
            result = prime * result + Arrays.deepHashCode(Level.getWalls());
            this._hash = result;
        }
        return this._hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        State other = (State) obj;
        if (this.agentRow != other.agentRow || this.agentCol != other.agentCol)
            return false;
        return Arrays.deepEquals(boxesToList(this.boxesList), boxesToList(other.boxesList));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < Level.getMaxRow(); row++) {
            if (!Level.getWallVertical(row)[0]) {
                break;
            }
            for (int col = 0; col < Level.getMaxCol(); col++) {
                if (this.boxesList.get(row)[col] > 0) {
                    s.append(this.boxesList.get(row)[col]);
                } else if (Level.getGoal(row,col) > 0) {
                    s.append(Level.getGoal(row,col));
                } else if (Level.getWall(row,col)) {
                    s.append("+");
                } else if (row == this.agentRow && col == this.agentCol) {
                    s.append("0");
                } else {
                    s.append(" ");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }

    public void addBox(char chr, int row, int col) {
        // Dynamic amount of rows
        while(this.boxesList.size() <= row){
            char[] boxRow = new char[Level.getMaxCol()];
            this.boxesList.add(boxRow);
        }
        this.boxesList.get(row)[col] = chr;
    }

    public void updateBoxesArraySize() {
        while(this.boxesList.size() < Level.getMaxRow()){
            char[] boxRow = new char[Level.getMaxCol()];
            this.boxesList.add(boxRow);
        }
    }

    public int getPathCost() {
        return pathCost;
    }

    public void setPathCost(int pathCost) {
        this.pathCost = pathCost;
    }
}