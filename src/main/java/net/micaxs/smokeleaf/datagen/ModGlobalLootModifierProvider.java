package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, SmokeleafIndustries.MODID);
    }

    @Override
    protected void start() {

        // Add to Grass Blocks
        this.add("hemp_seeds_to_short_grass", new AddItemModifier(new LootItemCondition[]{
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS).build(),
                LootItemRandomChanceCondition.randomChance(0.35f).build()
        }, ModItems.HEMP_SEEDS.get()));

        this.add("hemp_seeds_to_tall_grass", new AddItemModifier(new LootItemCondition[]{
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).build(),
                LootItemRandomChanceCondition.randomChance(0.35f).build()
        }, ModItems.HEMP_SEEDS.get()));

        this.add("tobacco_seeds_to_short_grass", new AddItemModifier(new LootItemCondition[]{
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SHORT_GRASS).build(),
                LootItemRandomChanceCondition.randomChance(0.25f).build()
        }, ModItems.TOBACCO_SEEDS.get()));

        this.add("tobacco_seeds_to_tall_grass", new AddItemModifier(new LootItemCondition[]{
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TALL_GRASS).build(),
                LootItemRandomChanceCondition.randomChance(0.25f).build()
        }, ModItems.TOBACCO_SEEDS.get()));

        // Add Stuff to Chests
        this.add("hemp_seeds_from_jungle_temple", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/jungle_temple")).build(),
                LootItemRandomChanceCondition.randomChance(0.75f).build()
        }, ModItems.HEMP_SEEDS.get()));

        this.add("tobacco_seeds_from_village_plains_chest", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/village/village_plains_house")).build(),
                LootItemRandomChanceCondition.randomChance(0.75f).build()
        }, ModItems.TOBACCO_SEEDS.get()));

        this.add("hemp_seeds_from_village_plains_chest", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/village/village_plains_house")).build(),
                LootItemRandomChanceCondition.randomChance(0.75f).build()
        }, ModItems.HEMP_SEEDS.get()));

        this.add("manual_grinder_from_village_plains_chest", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/village/village_plains_house")).build(),
                LootItemRandomChanceCondition.randomChance(0.4f).build()
        }, ModItems.MANUAL_GRINDER.get()));

        this.add("white_widow_bag_from_trial_chamber_loot", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/trial_chambers/reward_rare")).build(),
                LootItemRandomChanceCondition.randomChance(0.75f).build()
        }, ModItems.WHITE_WIDOW_BAG.get()));

        this.add("bong_from_trial_chamber_loot", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/trial_chambers/reward_unique")).build(),
                LootItemRandomChanceCondition.randomChance(0.75f).build()
        }, ModItems.BONG.get()));

        // Dual Arc Lamp — otherwise only found via the villager trade, so give it a shot at
        // turning up in suspicious sand/gravel (archaeology) and trial chamber loot too.
        this.add("dual_arc_lamp_from_desert_pyramid", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/desert_pyramid")).build(),
                LootItemRandomChanceCondition.randomChance(0.2f).build()
        }, ModItems.DUAL_ARC_LAMP.get()));

        this.add("dual_arc_lamp_from_desert_well", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/desert_well")).build(),
                LootItemRandomChanceCondition.randomChance(0.2f).build()
        }, ModItems.DUAL_ARC_LAMP.get()));

        this.add("dual_arc_lamp_from_ocean_ruin_warm", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/ocean_ruin_warm")).build(),
                LootItemRandomChanceCondition.randomChance(0.2f).build()
        }, ModItems.DUAL_ARC_LAMP.get()));

        this.add("dual_arc_lamp_from_ocean_ruin_cold", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/ocean_ruin_cold")).build(),
                LootItemRandomChanceCondition.randomChance(0.2f).build()
        }, ModItems.DUAL_ARC_LAMP.get()));

        this.add("dual_arc_lamp_from_trail_ruins_common", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_common")).build(),
                LootItemRandomChanceCondition.randomChance(0.2f).build()
        }, ModItems.DUAL_ARC_LAMP.get()));

        this.add("dual_arc_lamp_from_trail_ruins_rare", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_rare")).build(),
                LootItemRandomChanceCondition.randomChance(0.3f).build()
        }, ModItems.DUAL_ARC_LAMP.get()));

        this.add("dual_arc_lamp_from_trial_chamber_loot", new AddItemModifier(new LootItemCondition[]{
                new LootTableIdCondition.Builder(ResourceLocation.withDefaultNamespace("chests/trial_chambers/reward_common")).build(),
                LootItemRandomChanceCondition.randomChance(0.3f).build()
        }, ModItems.DUAL_ARC_LAMP.get()));


    }
}
