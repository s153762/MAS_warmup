package searchclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class Heuristic implements Comparator<State> {
    public Heuristic(State initialState) {
        // Here's a chance to pre-process the static parts of the level.
        // Already done ;)

    }

    public int h(State n) {
        int sum = 0;
        for(String key :n.boxesMap.keySet()){
            ArrayList<Integer> distanceToGoal = new ArrayList<>();
            for (Goal goal : Level.getGoals()) {
                if(Character.toLowerCase(n.boxesMap.get(key))==goal.getGoalName()){
                    int[] position = n.keyToPosition(key);
                    distanceToGoal.add(Math.abs(goal.getCol() - position[1]) + Math.abs(goal.getRow() - position[0]));
                }
            }
            if (!distanceToGoal.isEmpty()) {
                sum += Collections.min(distanceToGoal);

            }
        }
        
        return sum;
    }

    public abstract int f(State n);

    @Override
    public int compare(State n1, State n2) {
        return this.f(n1) - this.f(n2);
    }

    public static class AStar extends Heuristic {
        public AStar(State initialState) {
            super(initialState);
        }

        @Override
        public int f(State n) {
            return n.g() + this.h(n);
        }

        @Override
        public String toString() {
            return "A* evaluation";
        }
    }

    public static class WeightedAStar extends Heuristic {
        private int W;

        public WeightedAStar(State initialState, int W) {
            super(initialState);
            this.W = W;
        }

        @Override
        public int f(State n) {
            return n.g() + this.W * this.h(n);
        }

        @Override
        public String toString() {
            return String.format("WA*(%d) evaluation", this.W);
        }
    }

    public static class Greedy extends Heuristic {
        public Greedy(State initialState) {
            super(initialState);
        }

        @Override
        public int f(State n) {
            return this.h(n);
        }

        @Override
        public String toString() {
            return "Greedy evaluation";
        }
    }
}
