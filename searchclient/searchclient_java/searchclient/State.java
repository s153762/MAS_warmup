package searchclient;

import java.util.*;

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

    public HashMap<String, Character> boxesMap;

    public State parent;
    public Command action;

    private int g;

    private int _hash = 0;

    public State(State parent) {
        this.parent = parent;
        // Boxes as HashMap
        this.boxesMap = new HashMap<>();

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
        for(String key: this.boxesMap.keySet()){
            int[] position = keyToPosition(key);
            char g = Level.getGoal(position[0], position[1]);
            char b = Character.toLowerCase(this.boxesMap.get(key));
            if (g > 0 && b == g) {
                return true;
            }
        }
        return false;
    }

    private String positionToKey(int row, int col) {
        return "("+row+","+col+")";
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

                        // Boxes as HashMap
                        n.boxesMap.put(positionToKey(newBoxRow,newBoxCol),this.boxesMap.get(positionToKey(newAgentRow,newAgentCol)));
                        n.boxesMap.remove(positionToKey(newAgentRow,newAgentCol));
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

                        // Boxes as HashMap
                        n.boxesMap.put(positionToKey(this.agentRow,this.agentCol),this.boxesMap.get(positionToKey(boxRow,boxCol)));
                        n.boxesMap.remove(positionToKey(boxRow,boxCol));
                        expandedStates.add(n);
                    }
                }
            }
        }
        Collections.shuffle(expandedStates, RNG);
        return expandedStates;
    }

    private boolean cellIsFree(int row, int col) {
        return !Level.getWall(row,col) && boxesToList(this.boxesMap)[row][col] == 0;

    }

    private boolean boxAt(int row, int col) {
        return boxesToList(this.boxesMap)[row][col] > 0;
    }

    private State ChildState() {
        State copy = new State(this);

        // Boxes as HashMap
        copy.boxesMap.putAll(this.boxesMap);
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

            // Boxes as HashMap
            result = prime * result + Arrays.deepHashCode(boxesToList(this.boxesMap));
            result = prime * result + Arrays.deepHashCode(Level.getGoalsList());
            result = prime * result + Arrays.deepHashCode(Level.getWallsList());
            this._hash = result;
        }
        return this._hash;
    }

    private char[][] boxesToList(HashMap<String, Character> boxesMap) {
        char[][] map = new char[Level.getMaxRow()][Level.getMaxCol()];
        for(String key:boxesMap.keySet()){
            if (boxesMap.get(key) != null){
                int[] position = keyToPosition(key);
                map[position[0]][position[1]] = boxesMap.get(key);
            } else {
                System.err.println("boxesToList: "+key+", "+boxesMap.get(key)+" - ");
            }

        }
        return map;
    }

    public int[] keyToPosition(String key) {
        int[] position = new int[2];
        String[] temp = key.split(",");

        // find row
        temp[0] = temp[0].replace("(","");
        position[0] = Integer.parseInt(temp[0]);

        // Find col
        temp[1] = temp[1].replace(")","");
        position[1] = Integer.parseInt(temp[1]);

        return position;
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
        return Arrays.deepEquals(boxesToList(this.boxesMap), boxesToList(other.boxesMap));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < Level.getMaxRow(); row++) {
            if (!Level.getWallVertical(row)[0]) {
                break;
            }
            for (int col = 0; col < Level.getMaxCol(); col++) {
                if (this.boxesMap.get(positionToKey(row,col)) > 0) {
                    s.append(this.boxesMap.get(positionToKey(row,col)));
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
        // HashMap Boxes
        this.boxesMap.put(positionToKey(row,col),chr);
    }


    public int getPathCost() {
        return pathCost;
    }

    public void setPathCost(int pathCost) {
        this.pathCost = pathCost;
    }
}