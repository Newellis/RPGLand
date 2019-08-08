package com.tynellis.World.Entities.Living.villagers;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.Ai.AiTask;
import com.tynellis.World.Entities.Living.Ai.FaceClosestAi;
import com.tynellis.World.Entities.Living.Ai.Pathfinding.Core.PathfindInRangeAi;
import com.tynellis.World.Entities.Living.Ai.Pathfinding.toEntity.CollectItemsFromEntityAi;
import com.tynellis.World.Entities.Living.Ai.Pathfinding.toEntity.UseChestAi;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Entities.Plants.Tree;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Entities.UsableEntity.Chest;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.Containers.Filters.ItemFilter;
import com.tynellis.World.Items.Containers.Filters.NameItemFilter;
import com.tynellis.World.Items.Containers.Filters.TypeItemFilter;
import com.tynellis.World.Items.Materials.Log;
import com.tynellis.World.Items.weapons.Axe;

import java.util.Random;

public class LumberJackNpc extends Villager {
    public LumberJackNpc(int x, int y, int z, LivingEntity.NpcGender gender, Chest chest, Random random) {
        super(x, y, z, gender, random);
        ItemFilter log, acorn, pineCone;
        log = new TypeItemFilter(new Class[]{Log.class}, ItemFilter.Type.WhiteList);
        acorn = new NameItemFilter(new String[]{"Acorn"}, ItemFilter.Type.WhiteList);
        pineCone = new NameItemFilter(new String[]{"Pine Cone"}, ItemFilter.Type.WhiteList);
        equipWeapon(new Axe("Tree Feller", 35, 5, 2));
        inventory = new Container(10); //new Container(new ItemFilter[] {acorn, log, pineCone, log});
        inventory.setAllSlotFilters(log);
        inventory.getSlot(0).setFilter(acorn);
        inventory.getSlot(1).setFilter(pineCone);
        canPickUpItems = true;

        //new PathfinderAi(x, y, z, 48);

        pathfinder = new PathfindInRangeAi(50, x, y, z);

        AiTask items = new CollectItemsFromEntityAi(Tree.class, 50, 1);
        UseChestAi chestAi = new UseChestAi(chest, new TypeItemFilter(new Class[]{Log.class}, ItemFilter.Type.WhiteList), 400);
        items.dontInterrupt(chestAi);
        //pathfinder = items;//new AttackEntityAi(Tree.class, 15, 1);

        //pathfinder = chestAi.pathfinder;
        Ai.addTask(0, new FaceClosestAi(Player.class, 0.25));
        //Ai.addTask(1, ai);
        Ai.addTask(2, items);
        Ai.addTask(3, chestAi);
        //Ai.addTask(4, pathfinder);

        //put all ai above this point
//        Ai.addTask(1000, new RandomWanderAi());
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }
}
