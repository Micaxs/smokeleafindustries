package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.TobaccoCropBlock;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.loot.ApplyBudStats;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // Blocks & Stuff
        dropSelf(ModBlocks.HEMP_STONE.get());
        dropSelf(ModBlocks.HEMP_STONE_STAIRS.get());
        this.add(ModBlocks.HEMP_STONE_SLAB.get(), block -> createSlabItemTable(ModBlocks.HEMP_STONE_SLAB.get()));
        dropSelf(ModBlocks.HEMP_STONE_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.HEMP_STONE_BUTTON.get());
        dropSelf(ModBlocks.HEMP_STONE_WALL.get());

        dropSelf(ModBlocks.HEMP_BRICKS.get());
        dropSelf(ModBlocks.HEMP_BRICK_STAIRS.get());
        this.add(ModBlocks.HEMP_BRICK_SLAB.get(), block -> createSlabItemTable(ModBlocks.HEMP_BRICK_SLAB.get()));
        dropSelf(ModBlocks.HEMP_BRICK_WALL.get());

        dropSelf(ModBlocks.HEMP_CHISELED_STONE.get());
        dropSelf(ModBlocks.HEMP_CHISELED_STONE_STAIRS.get());
        this.add(ModBlocks.HEMP_CHISELED_STONE_SLAB.get(), block -> createSlabItemTable(ModBlocks.HEMP_CHISELED_STONE_SLAB.get()));
        dropSelf(ModBlocks.HEMP_CHISELED_STONE_WALL.get());

        dropSelf(ModBlocks.HEMP_PLANKS.get());
        dropSelf(ModBlocks.HEMP_PLANK_STAIRS.get());
        this.add(ModBlocks.HEMP_PLANK_SLAB.get(), block -> createSlabItemTable(ModBlocks.HEMP_PLANK_SLAB.get()));
        dropSelf(ModBlocks.HEMP_PLANK_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.HEMP_PLANK_BUTTON.get());
        dropSelf(ModBlocks.HEMP_PLANK_FENCE.get());
        dropSelf(ModBlocks.HEMP_PLANK_FENCE_GATE.get());
        dropSelf(ModBlocks.HEMP_PLANK_TRAPDOOR.get());
        this.add(ModBlocks.HEMP_PLANK_DOOR.get(), block -> createDoorTable(ModBlocks.HEMP_PLANK_DOOR.get()));

        // Lamps
        dropSelf(ModBlocks.REFLECTOR.get());
        dropSelf(ModBlocks.LED_LIGHT.get());

        // Grow Pot
        dropSelf(ModBlocks.GROW_POT.get());

        // Weed Crops
        addHempCropLoot(ModBlocks.HEMP_CROP, ModItems.HEMP_FIBERS, ModItems.HEMP_SEEDS, ModItems.HEMP_LEAF);

        // Player-made strain crop
        addUnidentifiedCropLoot(ModBlocks.UNIDENTIFIED_WEED_CROP, ModItems.GENERIC_BUD, ModItems.GENERIC_SEEDS, ModItems.HEMP_LEAF);

        addCropLoot(ModBlocks.WHITE_WIDOW_CROP, ModItems.WHITE_WIDOW_BUD, ModItems.WHITE_WIDOW_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.BUBBLE_KUSH_CROP, ModItems.BUBBLE_KUSH_BUD, ModItems.BUBBLE_KUSH_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.LEMON_HAZE_CROP, ModItems.LEMON_HAZE_BUD, ModItems.LEMON_HAZE_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.SOUR_DIESEL_CROP, ModItems.SOUR_DIESEL_BUD, ModItems.SOUR_DIESEL_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.BLUE_ICE_CROP, ModItems.BLUE_ICE_BUD, ModItems.BLUE_ICE_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.PURPLE_HAZE_CROP, ModItems.PURPLE_HAZE_BUD, ModItems.PURPLE_HAZE_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.BUBBLEGUM_CROP, ModItems.BUBBLEGUM_BUD, ModItems.BUBBLEGUM_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.OG_KUSH_CROP, ModItems.OG_KUSH_BUD, ModItems.OG_KUSH_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.JACK_HERER_CROP, ModItems.JACK_HERER_BUD, ModItems.JACK_HERER_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.GARY_PEYTON_CROP, ModItems.GARY_PEYTON_BUD, ModItems.GARY_PEYTON_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.AMNESIA_HAZE_CROP, ModItems.AMNESIA_HAZE_BUD, ModItems.AMNESIA_HAZE_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.AK47_CROP, ModItems.AK47_BUD, ModItems.AK47_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.GHOST_TRAIN_CROP, ModItems.GHOST_TRAIN_BUD, ModItems.GHOST_TRAIN_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.GRAPE_APE_CROP, ModItems.GRAPE_APE_BUD, ModItems.GRAPE_APE_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.COTTON_CANDY_CROP, ModItems.COTTON_CANDY_BUD, ModItems.COTTON_CANDY_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.BANANA_KUSH_CROP, ModItems.BANANA_KUSH_BUD, ModItems.BANANA_KUSH_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.CARBON_FIBER_CROP, ModItems.CARBON_FIBER_BUD, ModItems.CARBON_FIBER_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.BIRTHDAY_CAKE_CROP, ModItems.BIRTHDAY_CAKE_BUD, ModItems.BIRTHDAY_CAKE_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.BLUE_COOKIES_CROP, ModItems.BLUE_COOKIES_BUD, ModItems.BLUE_COOKIES_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.AFGHANI_CROP, ModItems.AFGHANI_BUD, ModItems.AFGHANI_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.MOONBOW_CROP, ModItems.MOONBOW_BUD, ModItems.MOONBOW_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.LAVA_CAKE_CROP, ModItems.LAVA_CAKE_BUD, ModItems.LAVA_CAKE_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.JELLY_RANCHER_CROP, ModItems.JELLY_RANCHER_BUD, ModItems.JELLY_RANCHER_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.STRAWBERRY_SHORTCAKE_CROP, ModItems.STRAWBERRY_SHORTCAKE_BUD, ModItems.STRAWBERRY_SHORTCAKE_SEEDS, ModItems.HEMP_LEAF);
        addCropLoot(ModBlocks.PINK_KUSH_CROP, ModItems.PINK_KUSH_BUD, ModItems.PINK_KUSH_SEEDS, ModItems.HEMP_LEAF);

        // Tobacco Crop
        addSimpleCropLoot(ModBlocks.TOBACCO_CROP, ModItems.TOBACCO_LEAF, ModItems.TOBACCO_SEEDS);


        dropSelf(ModBlocks.GENERATOR.get());
        dropSelf(ModBlocks.GRINDER.get());
        dropSelf(ModBlocks.EXTRACTOR.get());
        dropSelf(ModBlocks.LIQUIFIER.get());
        dropSelf(ModBlocks.MUTATOR.get());
        dropSelf(ModBlocks.SYNTHESIZER.get());
        dropSelf(ModBlocks.SEQUENCER.get());
        dropSelf(ModBlocks.DRYER.get());
        dropSelf(ModBlocks.MIXER.get());

        dropSelf(ModBlocks.DRYING_RACK.get());



    }

    private void addSimpleCropLoot(DeferredBlock<Block> tobaccoCrop,
                                   DeferredItem<Item> tobaccoLeaf,
                                   DeferredItem<Item> tobaccoSeeds) {
        Block block = tobaccoCrop.get();
        CropBlock crop = (CropBlock) block;

        LootItemCondition.Builder isMature = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(TobaccoCropBlock.AGE, crop.getMaxAge())); // avoid protected getAgeProperty()

        this.add(block, LootTable.lootTable()
                // Seeds: 1 guaranteed
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(tobaccoSeeds.get()))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                // Seeds: 25% chance for a second
                .withPool(LootPool.lootPool()
                        .when(LootItemRandomChanceCondition.randomChance(0.25f))
                        .add(LootItem.lootTableItem(tobaccoSeeds.get()))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                // Leaves: only when mature, 1 guaranteed
                .withPool(LootPool.lootPool()
                        .when(isMature)
                        .add(LootItem.lootTableItem(tobaccoLeaf.get()))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                // Leaves: only when mature, 25% chance for a second
                .withPool(LootPool.lootPool()
                        .when(isMature)
                        .when(LootItemRandomChanceCondition.randomChance(0.25f))
                        .add(LootItem.lootTableItem(tobaccoLeaf.get()))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
        );
    }

    private void addCropLoot(net.neoforged.neoforge.registries.DeferredBlock<Block> cropBlock,
                            java.util.function.Supplier<Item> budSupplier,
                            java.util.function.Supplier<Item> seedsSupplier,
                            java.util.function.Supplier<Item> leafSupplier) {
        LootItemCondition.Builder cropIsHarvestable = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BaseWeedCropBlock.AGE, 10));
        LootItemCondition.Builder cropIsBottomSegment = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BaseWeedCropBlock.TOP, false));

        this.add(cropBlock.get(), createCropDropFull(
                budSupplier.get(), seedsSupplier.get(), leafSupplier.get(),
                cropIsHarvestable, cropIsBottomSegment));
    }

    private LootTable.Builder createCropDropFull(Item budItem,
                                                 Item seedsItem,
                                                 Item leafItem,
                                                 LootItemCondition.Builder cropIsHarvestable,
                                                 LootItemCondition.Builder cropIsBottomSegment) {
        return LootTable.lootTable()
                // Always 1 seed (bottom segment)
                .withPool(LootPool.lootPool()
                        .when(cropIsBottomSegment)
                        .add(LootItem.lootTableItem(seedsItem))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                // Buds: when harvestable on bottom segment; count and THC/CBD come from BE
                .withPool(LootPool.lootPool()
                        .when(cropIsHarvestable.and(cropIsBottomSegment))
                        .add(LootItem.lootTableItem(budItem)
                                .apply(ApplyBudStats.apply())))
                // Leaf: unchanged
                .withPool(LootPool.lootPool()
                        .when(cropIsHarvestable.and(cropIsBottomSegment))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .add(LootItem.lootTableItem(leafItem)));
    }

//    private void addCropLoot(DeferredBlock<Block> cropBlock, Supplier<Item> budSupplier, Supplier<Item> seedsSupplier, Supplier<Item> leafSupplier) {
//        LootItemCondition.Builder cropIsHarvestable = LootItemBlockStatePropertyCondition
//                .hasBlockStateProperties(cropBlock.get())
//                .setProperties(StatePropertiesPredicate.Builder.properties()
//                        .hasProperty(BaseWeedCropBlock.AGE, 10)
//                );
//        LootItemCondition.Builder cropIsBottomSegment = LootItemBlockStatePropertyCondition
//                .hasBlockStateProperties(cropBlock.get())
//                .setProperties(StatePropertiesPredicate.Builder.properties()
//                        .hasProperty(BaseWeedCropBlock.TOP, false)
//                );
//
//        this.add(cropBlock.get(), createCropDropFull(budSupplier.get(), seedsSupplier.get(), leafSupplier.get(), cropIsHarvestable, cropIsBottomSegment));
//    }

//    private LootTable.Builder createCropDropFull(Item pBudItem, Item pSeedsItem, Item pLeafItem, LootItemCondition.Builder cropIsHarvestable, LootItemCondition.Builder cropIsBottomSegment) {
//        return LootTable.lootTable()
//                .withPool(LootPool.lootPool()  // 1 Seed drop always
//                        .when(cropIsBottomSegment)
//                        .add(LootItem.lootTableItem(pSeedsItem))
//                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
//                )
//                .withPool(LootPool.lootPool()  // Bud drop with condition
//                        .when(cropIsHarvestable.and(cropIsBottomSegment))
//                        .add(LootItem.lootTableItem(pBudItem)
//                                //.apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.FORTUNE, 0.55F, 1))
//                        )
//                )
//                .withPool(LootPool.lootPool()  // Leaf drop with condition
//                        .when(cropIsHarvestable.and(cropIsBottomSegment))
//                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
//                        .add(LootItem.lootTableItem(pLeafItem)));
//    }


    private void addHempCropLoot(DeferredBlock<Block> cropBlock, Supplier<Item> budSupplier, Supplier<Item> seedsSupplier, Supplier<Item> leafSupplier) {
        LootItemCondition.Builder cropIsHarvestable = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BaseWeedCropBlock.AGE, 10));
        LootItemCondition.Builder cropIsBottomSegment = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BaseWeedCropBlock.TOP, false));

        this.add(cropBlock.get(),
                LootTable.lootTable()
                        // Always 1 seed
                        .withPool(LootPool.lootPool()
                                .when(cropIsBottomSegment)
                                .add(LootItem.lootTableItem(seedsSupplier.get()))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        // 50% chance second seed
                        .withPool(LootPool.lootPool()
                                .when(cropIsBottomSegment)
                                .when(LootItemRandomChanceCondition.randomChance(0.5f))
                                .add(LootItem.lootTableItem(seedsSupplier.get()))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        // Bud
                        .withPool(LootPool.lootPool()
                                .when(cropIsHarvestable.and(cropIsBottomSegment))
                                .add(LootItem.lootTableItem(budSupplier.get())))
                        // Leaf
                        .withPool(LootPool.lootPool()
                                .when(cropIsHarvestable.and(cropIsBottomSegment))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .add(LootItem.lootTableItem(leafSupplier.get())))
        );
    }


    private void addUnidentifiedCropLoot(net.neoforged.neoforge.registries.DeferredBlock<Block> cropBlock,
                                        java.util.function.Supplier<Item> weedSupplier,
                                        java.util.function.Supplier<Item> seedsSupplier,
                                        java.util.function.Supplier<Item> leafSupplier) {
        LootItemCondition.Builder cropIsHarvestable = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BaseWeedCropBlock.AGE, 10));
        LootItemCondition.Builder cropIsBottomSegment = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BaseWeedCropBlock.TOP, false));

        this.add(cropBlock.get(), LootTable.lootTable()
                // Always 1 seed (bottom segment)
                .withPool(LootPool.lootPool()
                        .when(cropIsBottomSegment)
                        .add(LootItem.lootTableItem(seedsSupplier.get())
                                .apply(net.micaxs.smokeleaf.loot.ApplyUnidentifiedStrain.applyFromCrop()))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                // Weed: when harvestable on bottom segment; count derives from crop nutrients, strain derives from BE
                .withPool(LootPool.lootPool()
                        .when(cropIsHarvestable.and(cropIsBottomSegment))
                        .add(LootItem.lootTableItem(weedSupplier.get())
                                .apply(net.micaxs.smokeleaf.loot.ApplyUnidentifiedStrain.applyFromCrop())))
                // Leaf: 1
                .withPool(LootPool.lootPool()
                        .when(cropIsHarvestable.and(cropIsBottomSegment))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                        .add(LootItem.lootTableItem(leafSupplier.get()))));
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
