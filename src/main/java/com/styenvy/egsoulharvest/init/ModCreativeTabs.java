package com.styenvy.egsoulharvest.init;

import com.styenvy.egsoulharvest.EGSoulHarvest;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EGSoulHarvest.MODID);

    public static final Supplier<CreativeModeTab> SOUL_HARVESTER_TAB = CREATIVE_TABS.register("soul_harvester_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + EGSoulHarvest.MODID))
                    .icon(() -> new ItemStack(ModItems.SOUL_RECYCLER.get()))
                    .displayItems((parameters, output) -> {
                        // Soul Recycler
                        output.accept(ModItems.SOUL_RECYCLER.get());

                        // Soul Harvesters
                        output.accept(ModItems.CAVE_SPIDER_HARVESTER.get());
                        output.accept(ModItems.SPIDER_HARVESTER.get());
                        output.accept(ModItems.ZOMBIE_HARVESTER.get());
                        output.accept(ModItems.SKELETON_HARVESTER.get());
                        output.accept(ModItems.BLAZE_HARVESTER.get());
                        output.accept(ModItems.MAGMA_CUBE_HARVESTER.get());
                    })
                    .build());
}
