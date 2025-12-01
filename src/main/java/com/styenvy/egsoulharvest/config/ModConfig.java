package com.styenvy.egsoulharvest.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // Soul Recycler Settings
    public static final ModConfigSpec.IntValue SOUL_RECYCLER_FE_PER_TICK;
    public static final ModConfigSpec.IntValue SOUL_RECYCLER_MAX_STORAGE;
    public static final ModConfigSpec.IntValue SOUL_RECYCLER_MAX_TRANSFER;

    // Soul Harvester Settings
    public static final ModConfigSpec.IntValue HARVESTER_LOOT_INTERVAL;
    public static final ModConfigSpec.IntValue HARVESTER_INVENTORY_SIZE;

    static {
        BUILDER.push("Soul Recycler");

        SOUL_RECYCLER_FE_PER_TICK = BUILDER
                .comment("Amount of FE generated per tick when the Soul Recycler is active")
                .defineInRange("fePerTick", 100, 1, 10000);

        SOUL_RECYCLER_MAX_STORAGE = BUILDER
                .comment("Maximum FE storage capacity of the Soul Recycler")
                .defineInRange("maxStorage", 10000, 1000, 1000000);

        SOUL_RECYCLER_MAX_TRANSFER = BUILDER
                .comment("Maximum FE transfer rate per tick")
                .defineInRange("maxTransfer", 1000, 100, 100000);

        BUILDER.pop();

        BUILDER.push("Soul Harvester");

        HARVESTER_LOOT_INTERVAL = BUILDER
                .comment("Number of ticks between loot generation (20 ticks = 1 second)")
                .defineInRange("lootInterval", 100, 20, 6000);

        HARVESTER_INVENTORY_SIZE = BUILDER
                .comment("Number of inventory slots in the harvester (vanilla chest = 27)")
                .defineInRange("inventorySize", 27, 9, 54);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
