package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toEntity;

import com.tynellis.World.Entities.NPC.AiTasks.FaceClosestAi;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.World;

public class AttackEntityAi extends FollowEntityAi {
    FaceClosestAi faceClosest;

    public AttackEntityAi(Class type, int range, int minRange) {
        super(type, range, minRange);
        faceClosest = new FaceClosestAi(type, 1);

    }

    public boolean performTask(World world, NpcBase entity) {
        if (findTarget(world, entity)) {
            faceClosest.performTask(world, entity);
            if (entity.canHit(world, closest)) {
                return attack(world, entity);
            }
        }
        boolean task = super.performTask(world, entity);
        if (!task) {
            closest = null;
        }
        return task;
    }

    private boolean attack(World world, NpcBase entity) {
        if (!closest.isDead()) {
            entity.attack(world);
            return true;
        }
        return false;
    }
}
