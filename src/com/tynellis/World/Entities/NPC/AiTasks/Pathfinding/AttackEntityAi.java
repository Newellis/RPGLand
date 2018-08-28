package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.NPC.AiTasks.FaceClosestAi;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.World;

public class AttackEntityAi extends FollowEntityAi {
    FaceClosestAi faceClosest;

    public AttackEntityAi(Class type, int range, int minRange) {
        super(type, range, minRange);
        faceClosest = new FaceClosestAi(type, 1);

    }

    public boolean performTask(World world, KillableEntity entity) {
        if (findTarget(world, entity)) {
            if (pathIsValid(world, entity) && getPathLength() <= 1) {
                faceClosest.performTask(world, entity);
                return attack(world, entity);
            }
        }
        boolean task = super.performTask(world, entity);
        if (!task) {
            closest = null;
        }
        return task;
    }

    private boolean attack(World world, KillableEntity entity) {
        if (!closest.isDead()) {
            ((KillableEntity) closest).DamageBy(new DamageSource(new Damage(Damage.Types.SLICING, 5)), world.getRand());//todo make NPC do attack
            return true;
        }
        return false;
    }
}
