package searchclient;

import java.util.LinkedList;
import java.util.ListIterator;

public class OrderedStateList<T extends State> extends LinkedList<T> {

    private static final long serialVersionUID = 1L;


    public boolean orderedAdd(T element) {
        ListIterator<T> itr = listIterator();
        while(true) {
            if (itr.hasNext() == false) {
                itr.add(element);
                return(true);
            }

            T elementInList = itr.next();
            if (elementInList.getPathCost() > element.getPathCost()) {
                itr.previous();
                itr.add(element);
                System.out.println("Adding");
                return(true);
            }
        }
    }
}