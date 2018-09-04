package com.tynellis.Save;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Regions.Region;

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

    public void addEntitiesTo(Region region) {
        if (entities.size() > 0) {
            for(Entity e: entities){
                region.addEntity(e);
            }
        }
    }
}
