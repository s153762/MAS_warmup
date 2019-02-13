package searchclient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;


public abstract class Heuristic implements Comparator<State> {
    public Heuristic(State initialState) {
        // Here's a chance to pre-process the static parts of the level.
        // Already done ;)

    }

    public int h(State n) {
        int sum = 0;
        for(Box box: n.boxes.values()){
            ArrayList<Integer> distanceToGoals = new ArrayList<>();
            for(Goal goal:Level.getGoals()){
                if (Character.toLowerCase(box.getName()) == goal.getName()){
                    distanceToGoals.add(Math.abs(goal.getCol() - box.getCol()) + Math.abs(goal.getRow() - box.getRow()));
                }
            }
            if (!distanceToGoals.isEmpty()) {
                sum += Collections.min(distanceToGoals);
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
