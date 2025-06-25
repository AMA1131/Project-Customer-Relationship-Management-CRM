package factory;

import model.Entity;

public abstract class AbstractEntity {
    public abstract Entity createEntity(Object... args);
}
