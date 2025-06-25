package model.composite_interaction;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class InteractionHistoryIterator implements Iterator<Interaction> {
    private final Queue<Interaction> queue = new LinkedList<>();

    public InteractionHistoryIterator(InteractionHistory root) {
        align(root);
    }

    private void align(InteractionHistory history) {
        for (InteractionComponent component : history.getInteractions()) {
            if (component instanceof Interaction) {
                queue.add((Interaction) component);
            } else if( component instanceof InteractionHistory) {
                align((InteractionHistory) component);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    /**
     * @throws NoSuchElementException means that the root contains no simple interactions
     */
    @Override
    public Interaction next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException("No interactions left.");
        }
        return queue.poll();
    }

    public void remove() {

    }
}
