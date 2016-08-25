package com.tynellis.World.Entities.NPC;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.NPC.AiTasks.FaceClosestAi;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.CollectItemsAi;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.StayNearPoint;
import com.tynellis.World.Entities.NPC.AiTasks.RandomWanderAi;
import com.tynellis.World.Entities.NPC.AiTasks.UseChestAi;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Entities.UsableEntity.Chest;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.Containers.Filters.ItemFilter;
import com.tynellis.World.Items.Containers.Filters.NameItemFilter;
import com.tynellis.World.Items.Containers.Filters.TypeItemFilter;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Log;

import java.util.Random;

public class LumberJackNpc extends NpcBase {
    public LumberJackNpc(int x, int y, int z, NpcBase.NpcGender gender, Chest chest, Random random) {
        super(x, y, z, gender, random);
        ItemFilter log, acorn, pineCone;
        log = new TypeItemFilter(new Class[]{Log.class}, ItemFilter.Type.WhiteList);
        acorn = new NameItemFilter(new String[]{"Acorn"}, ItemFilter.Type.WhiteList);
        pineCone = new NameItemFilter(new String[]{"Pine Cone"}, ItemFilter.Type.WhiteList);
        inventory = new Container(10); //new Container(new ItemFilter[] {acorn, log, pineCone, log});
        inventory.setAllSlotFilters(log);
        inventory.getSlot(0).setFilter(acorn);
        inventory.getSlot(1).setFilter(pineCone);
        canPickUpItems = true;

        //new PathfinderAi(x, y, z, 48);

        StayNearPoint ai = new StayNearPoint(x, y, z, 50);
        CollectItemsAi items = new CollectItemsAi(150);
        UseChestAi chestAi = new UseChestAi(chest, new TypeItemFilter(new Class[]{Log.class}, ItemFilter.Type.WhiteList), 400);
        ai.dontInterrupt(items);
        items.dontInterrupt(chestAi);
        pathfinder = ai;
        pathfinder = new RandomWanderAi();
        Ai.addTask(0, new FaceClosestAi(Player.class, 0.25));
        Ai.addTask(1, ai);
        Ai.addTask(2, items);
        Ai.addTask(3, chestAi);
        //Ai.addTask(4, pathfinder);
    }

    @Override
    public ItemPile[] getItemsToDrop(Random rand) {
        return new ItemPile[0];
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }
}
