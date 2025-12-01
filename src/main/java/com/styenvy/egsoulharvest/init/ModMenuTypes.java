package com.styenvy.egsoulharvest.init;

import com.styenvy.egsoulharvest.EGSoulHarvest;
import com.styenvy.egsoulharvest.menu.HarvesterMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
            DeferredRegister.create(Registries.MENU, EGSoulHarvest.MODID);

    public static final Supplier<MenuType<HarvesterMenu>> HARVESTER_MENU = MENU_TYPES.register("harvester_menu",
            () -> IMenuTypeExtension.create(HarvesterMenu::new));
}
