package com.tynellis.debug;

public class Debug {
    private boolean state = false;
    Type types[] = new Type[]{Type.PATH, Type.AREAS, Type.COLLISION};

    public enum Type {
        COLLISION, //shows hitboxes of entities
        AREAS,  //shows area boundaries
        TILES,  //shows tile boundaries
        SAVE,   //prints info about saving to and loading from a file
        FLY,    //sets player to flying
        PATH,   //shows pathfinding paths
    }

    public boolean State() {
        return state;
    }

    public void setState(boolean newState) {
        state = newState;
    }

    public boolean isType(Type type) {
        for (Type typ : types) {
            if (typ == type) {
                return true;
            }
        }
        return false;
    }
}
