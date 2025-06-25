package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class GenerictIterator<T> implements Iterator<T>{
    private final ArrayList<T> entities;
    private int index = 0;

    /**
     * this security variable avoid removing element without calling next
     */
    private boolean canRemove = false;
    
    public GenerictIterator(ArrayList<T> entities) {
        this.entities = entities;
    }

    @Override
    public boolean hasNext(){
        return index < entities.size();
    }

    /**
     * Returns the 
     */
    @Override
    public T next(){
        if (!this.hasNext()){
            throw new NoSuchElementException();
        }
        canRemove = true;
        return entities.get(index++);
    }

    /**
     * Remove the entity we are visting
     * @throws IllegalStateException
     */
    public void remove(){
        if (!canRemove){
            throw new IllegalStateException("must call next() before remove");
        }
        entities.remove(--index);
        canRemove = false;
    }
}
