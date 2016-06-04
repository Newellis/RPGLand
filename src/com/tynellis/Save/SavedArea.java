package com.tynellis.Save;

import com.tynellis.World.Area;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.World;

import java.io.Serializable;
import java.util.ArrayList;

public class SavedArea implements Serializable {
    private Area area;
    private ArrayList<Entity> entities = new ArrayList<Entity>();

    public SavedArea(Area area, ArrayList<Entity> entitiesInArea){
        this.area = area;
        entities = entitiesInArea;
    }

    public Area getArea() {
        return area;
    }

    public void addEntitiesTo(World world) {
        if (entities.size() > 0) {
            for(Entity e: entities){
                world.addEntity(e);
            }
        }
    }
}
