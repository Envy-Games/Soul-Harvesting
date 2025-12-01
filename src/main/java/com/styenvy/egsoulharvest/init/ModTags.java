package com.styenvy.egsoulharvest.init;

import com.styenvy.egsoulharvest.EGSoulHarvest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {

    private ModTags() {
    }

    public static final class Items {
        public static final TagKey<Item> HARVESTER_TOOLS = ItemTags.create(
                ResourceLocation.fromNamespaceAndPath(EGSoulHarvest.MODID, "harvester_tools")
        );

        public static final TagKey<Item> SOUL_RECYCLER_TOOLS = ItemTags.create(
                ResourceLocation.fromNamespaceAndPath(EGSoulHarvest.MODID, "soul_recycler_tools")
        );

        private Items() {
        }
    }
}
