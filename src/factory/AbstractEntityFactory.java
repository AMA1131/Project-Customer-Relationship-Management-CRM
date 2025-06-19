package factory;

import model.Entity;

public abstract class AbstractEntityFactory {
    public abstract Entity createEntity(Object... args);
}
