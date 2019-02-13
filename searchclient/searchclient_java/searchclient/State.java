package searchclient;

import java.util.*;

public class State {
    private static final Random RNG = new Random(1);
    private int g;
    private int _hash = 0;
    private int pathCost;

    public int agentRow;
    public int agentCol;

    public HashMap<String, Box> boxes;
    public State parent;
    public Command action;


    // Arrays are indexed from the top-left of the level, with first index being row and second being column.
    // Row 0: (0,0) (0,1) (0,2) (0,3) ...
    // Row 1: (1,0) (1,1) (1,2) (1,3) ...
    // Row 2: (2,0) (2,1) (2,2) (2,3) ...
    // ...
    // (Start in the top left corner, first go down, then go right)
    // E.g. this.walls[2] is an array of booleans having size MAX_COL.
    // this.walls[row][col] is true if there's a wall at (row, col)
    //


    public State(State parent) {
        this.parent = parent;
        this.boxes = new HashMap<>();
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
        for (Box box : boxes.values()) {
            char g = Level.getGoal(box.getRow(), box.getCol());
            char b = Character.toLowerCase(box.getName());
            if (g > 0 && b != g) {
                return false;
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
                        n = this.updateBoxes(n,newBoxRow,newBoxCol,newAgentRow,newAgentCol);
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
                        n = this.updateBoxes(n,this.agentRow,this.agentCol,boxRow,boxCol);
                        expandedStates.add(n);
                    }
                }
            }
        }
        Collections.shuffle(expandedStates, RNG);
        return expandedStates;
    }

    private State updateBoxes(State n, int newBoxRow, int newBoxCol, int newAgentRow, int newAgentCol) {
        String newBox = coordinatesToKey(newBoxRow,newBoxCol);
        String newAgent = coordinatesToKey(newAgentRow,newAgentCol);

        n.boxes.get(newBox).setName(this.boxes.get(newAgent).getName());
        n.boxes.remove(newAgent);
        return n;
    }

    private boolean cellIsFree(int row, int col) {
        if (boxes.get(coordinatesToKey(row,col)) != null){
            return false;
        }
        return !Level.getWall(row,col);
    }

    private boolean boxAt(int row, int col) {
        if (boxes.get(coordinatesToKey(row,col)) != null){
            return true;
        }
        return false;
    }

    private State ChildState() {
        State copy = new State(this);
        System.arraycopy(this.boxes, 0, copy.boxes, 0, Level.getMaxCol());
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


    private char[][] boxesToList(HashMap<String,Box> boxes) {
        char[][] boxList = new char[Level.getMaxRow()][Level.getMaxCol()];
        for(Box box:boxes.values()){
            boxList[box.getRow()][box.getCol()] = box.getName();
        }
        return boxList;
    }

    @Override
    public int hashCode() {
        if (this._hash == 0) {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.agentCol;
            result = prime * result + this.agentRow;
            result = prime * result + Arrays.deepHashCode(this.boxesToList(this.boxes));
            result = prime * result + Arrays.deepHashCode(Level.getGoalsList());
            result = prime * result + Arrays.deepHashCode(Level.getWallsList());
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
        return Arrays.deepEquals(boxesToList(this.boxes), boxesToList(other.boxes));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < Level.getMaxRow(); row++) {
            if (!Level.getWallVertical(row)[0]) {
                break;
            }
            for (int col = 0; col < Level.getMaxCol(); col++) {
                if (boxAt(row,col)) {
                    s.append(this.getBox(row,col).getName());
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

    private Box getBox(int row, int col) {
        return boxes.get(coordinatesToKey(row,col));
    }

    public void addBox(char chr, int row, int col) {
        // Dynamic amount of rows
        this.boxes.put(coordinatesToKey(row,col),new Box(row,col,chr));

        boolean test = (coordinatesToKey(row,col).equals(coordinatesToKey(row,col)));
        Box box = boxes.get(coordinatesToKey(row,col));
        System.err.println("State: adding box "+chr+" to "+coordinatesToKey(row,col)+", testing: "+test+" print box :"+box.getName()+box.getRow()+box.getCol());
    }


    public int getPathCost() {
        return pathCost;
    }

    public void setPathCost(int pathCost) {
        this.pathCost = pathCost;
    }

    private String coordinatesToKey(int row, int col){
        return "("+row+","+col+")";
    }
}