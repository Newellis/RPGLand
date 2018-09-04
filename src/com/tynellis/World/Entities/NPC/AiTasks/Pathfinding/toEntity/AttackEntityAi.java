package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toEntity;

import com.tynellis.World.Entities.NPC.AiTasks.FaceClosestAi;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class AttackEntityAi extends FollowEntityAi {
    private FaceClosestAi faceClosest;

    public AttackEntityAi(Class type, int range, int minRange) {
        super(type, range, minRange);
        faceClosest = new FaceClosestAi(type, 1);

    }

    public boolean performTask(Region region, Random random, NpcBase entity) {
        if (findTarget(region, entity)) {
            faceClosest.performTask(region, random, entity);
            if (shouldAttack(region, entity)) {
                return attack(region, random, entity);
            }
        }
        boolean task = super.performTask(region, random, entity);
        if (!task) {
            closest = null;
        }
        return task;
    }

    protected boolean shouldAttack(Region region, NpcBase npc) {
        return npc.canHit(region, closest);
    }

    private boolean attack(Region region, Random random, NpcBase entity) {
        if (!closest.isDead()) {
            entity.attack(region, random);
            return true;
        }
        return false;
    }
}
