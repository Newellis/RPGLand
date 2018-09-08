package com.tynellis.World.Entities.UsableEntity.using_interfaces;

import com.tynellis.Menus.InGameMenus.Inventory;
import com.tynellis.World.Entities.Player;

public interface UsingInterface {
    void stopUsing();

    Inventory getMenu(Player user);
}
