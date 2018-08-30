package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toEntity;

import com.tynellis.World.Entities.NPC.AiTasks.FaceClosestAi;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.World;

public class AttackEntityAi extends FollowEntityAi {
    private FaceClosestAi faceClosest;

    public AttackEntityAi(Class type, int range, int minRange) {
        super(type, range, minRange);
        faceClosest = new FaceClosestAi(type, 1);

    }

    public boolean performTask(World world, NpcBase entity) {
        if (findTarget(world, entity)) {
            faceClosest.performTask(world, entity);
            if (shouldAttack(world, entity)) {
                return attack(world, entity);
            }
        }
        boolean task = super.performTask(world, entity);
        if (!task) {
            closest = null;
        }
        return task;
    }

    protected boolean shouldAttack(World world, NpcBase npc) {
        return npc.canHit(world, closest);
    }

    private boolean attack(World world, NpcBase entity) {
        if (!closest.isDead()) {
            entity.attack(world);
            return true;
        }
        return false;
    }
}
