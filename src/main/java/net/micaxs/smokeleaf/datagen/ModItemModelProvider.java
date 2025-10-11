package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SmokeleafIndustries.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // --- Random Items
        basicItem(ModItems.HEMP_HAMMER.get());
        basicItem(ModItems.PLANT_ANALYZER.get());

        basicItem(ModItems.UNFINISHED_HEMP_CORE.get());
        basicItem(ModItems.HEMP_CORE.get());
        basicItem(ModItems.HEMP_SEEDS.get());
        basicItem(ModItems.HEMP_LEAF.get());
        basicItem(ModItems.HEMP_FIBERS.get());
        basicItem(ModItems.HEMP_FABRIC.get());
        basicItem(ModItems.HEMP_STICK.get());
        basicItem(ModItems.EMPTY_BAG.get());
        basicItem(ModItems.JOINT.get());
        basicItem(ModItems.BLUNT.get());
        basicItem(ModItems.BONG.get());
        basicItem(ModItems.WEED_COOKIE.get());
        basicItem(ModItems.HASH_BROWNIE.get());
        basicItem(ModItems.HERB_CAKE.get());
        basicItem(ModItems.DAB_RIG.get());
        basicItem(ModItems.EMPTY_TINCTURE.get());
        basicItem(ModFluids.HEMP_OIL_BUCKET.get());
        basicItem(ModFluids.HASH_OIL_BUCKET.get());
        basicItem(ModFluids.HASH_OIL_SLUDGE_BUCKET.get());
        basicItem(ModItems.HASH_OIL_TINCTURE.get());
        basicItem(ModItems.BASE_EXTRACT.get());
        basicItem(ModItems.HEMP_COAL.get());

        basicItem(ModItems.BUTTER.get());
        basicItem(ModItems.INFUSED_BUTTER.get());

        basicItem(ModItems.BIO_COMPOSITE.get());
        basicItem(ModItems.HEMP_PLASTIC.get());
        basicItem(ModItems.CAT_URINE_BOTTLE.get());

        dnaStrandModels();

        buttonItem(ModBlocks.HEMP_STONE_BUTTON, ModBlocks.HEMP_STONE);
        buttonItem(ModBlocks.HEMP_PLANK_BUTTON, ModBlocks.HEMP_PLANKS);
        basicItem(ModBlocks.HEMP_PLANK_DOOR.asItem());

        fenceItem(ModBlocks.HEMP_PLANK_FENCE, ModBlocks.HEMP_PLANKS);
        wallItem(ModBlocks.HEMP_STONE_WALL, ModBlocks.HEMP_STONE);
        wallItem(ModBlocks.HEMP_BRICK_WALL, ModBlocks.HEMP_BRICKS);
        wallItem(ModBlocks.HEMP_CHISELED_STONE_WALL, ModBlocks.HEMP_CHISELED_STONE);




        // --- Tobacco Items
        basicItem(ModItems.TOBACCO.get());
        basicItem(ModItems.TOBACCO_LEAF.get());
        basicItem(ModItems.TOBACCO_SEEDS.get());
        basicItem(ModItems.DRIED_TOBACCO_LEAF.get());


        // --- Fertilizer Items
        basicItem(ModItems.WORM_CASTINGS.get());
        basicItem(ModItems.COMPOST.get());
        basicItem(ModItems.MYCORRHIZAE.get());
        basicItem(ModItems.DOLOMITE_LIME.get());
        basicItem(ModItems.BLOOD_MEAL.get());
        basicItem(ModItems.FISH_EMULSION.get());
        basicItem(ModItems.PHOSPHORUS_POWDER.get());
        basicItem(ModItems.BAT_GUANO.get());
        basicItem(ModItems.KELP_MEAL.get());
        basicItem(ModItems.WOOD_ASH.get());
        basicItem(ModItems.BLOOM_BOOSTER.get());
        basicItem(ModItems.FRUIT_FINISHER.get());
        basicItem(ModItems.NITROGEN_BOOST.get());
        basicItem(ModItems.POTASH_BOOST.get());
        basicItem(ModItems.BALANCED_BOOST.get());
        basicItem(ModItems.PHOSPHORUS_REDUCER.get());
        basicItem(ModItems.POTASSIUM_REDUCER.get());


        // --- Weed Items

        // White Widow
        basicItem(ModItems.WHITE_WIDOW_SEEDS.get());
        basicItem(ModItems.WHITE_WIDOW_BUD.get());
        basicItem(ModItems.WHITE_WIDOW_WEED.get());
        basicItem(ModItems.WHITE_WIDOW_EXTRACT.get());
        basicItem(ModItems.WHITE_WIDOW_BAG.get());
        basicItem(ModItems.WHITE_WIDOW_GUMMY.get());
        // Bubble Kush
        basicItem(ModItems.BUBBLE_KUSH_SEEDS.get());
        basicItem(ModItems.BUBBLE_KUSH_BUD.get());
        basicItem(ModItems.BUBBLE_KUSH_WEED.get());
        basicItem(ModItems.BUBBLE_KUSH_EXTRACT.get());
        basicItem(ModItems.BUBBLE_KUSH_BAG.get());
        basicItem(ModItems.BUBBLE_KUSH_GUMMY.get());
        // Lemon Haze
        basicItem(ModItems.LEMON_HAZE_SEEDS.get());
        basicItem(ModItems.LEMON_HAZE_BUD.get());
        basicItem(ModItems.LEMON_HAZE_WEED.get());
        basicItem(ModItems.LEMON_HAZE_EXTRACT.get());
        basicItem(ModItems.LEMON_HAZE_BAG.get());
        basicItem(ModItems.LEMON_HAZE_GUMMY.get());
        // Sour Diesel
        basicItem(ModItems.SOUR_DIESEL_SEEDS.get());
        basicItem(ModItems.SOUR_DIESEL_BUD.get());
        basicItem(ModItems.SOUR_DIESEL_WEED.get());
        basicItem(ModItems.SOUR_DIESEL_EXTRACT.get());
        basicItem(ModItems.SOUR_DIESEL_BAG.get());
        basicItem(ModItems.SOUR_DIESEL_GUMMY.get());
        // Blue Ice
        basicItem(ModItems.BLUE_ICE_SEEDS.get());
        basicItem(ModItems.BLUE_ICE_BUD.get());
        basicItem(ModItems.BLUE_ICE_WEED.get());
        basicItem(ModItems.BLUE_ICE_EXTRACT.get());
        basicItem(ModItems.BLUE_ICE_BAG.get());
        basicItem(ModItems.BLUE_ICE_GUMMY.get());
        // Bubblegum
        basicItem(ModItems.BUBBLEGUM_SEEDS.get());
        basicItem(ModItems.BUBBLEGUM_BUD.get());
        basicItem(ModItems.BUBBLEGUM_WEED.get());
        basicItem(ModItems.BUBBLEGUM_EXTRACT.get());
        basicItem(ModItems.BUBBLEGUM_BAG.get());
        basicItem(ModItems.BUBBLEGUM_GUMMY.get());
        // Purple Haze
        basicItem(ModItems.PURPLE_HAZE_SEEDS.get());
        basicItem(ModItems.PURPLE_HAZE_BUD.get());
        basicItem(ModItems.PURPLE_HAZE_WEED.get());
        basicItem(ModItems.PURPLE_HAZE_EXTRACT.get());
        basicItem(ModItems.PURPLE_HAZE_BAG.get());
        basicItem(ModItems.PURPLE_HAZE_GUMMY.get());
        // OG Kush
        basicItem(ModItems.OG_KUSH_SEEDS.get());
        basicItem(ModItems.OG_KUSH_BUD.get());
        basicItem(ModItems.OG_KUSH_WEED.get());
        basicItem(ModItems.OG_KUSH_EXTRACT.get());
        basicItem(ModItems.OG_KUSH_BAG.get());
        basicItem(ModItems.OG_KUSH_GUMMY.get());
        // Jack Herer
        basicItem(ModItems.JACK_HERER_SEEDS.get());
        basicItem(ModItems.JACK_HERER_BUD.get());
        basicItem(ModItems.JACK_HERER_WEED.get());
        basicItem(ModItems.JACK_HERER_EXTRACT.get());
        basicItem(ModItems.JACK_HERER_BAG.get());
        basicItem(ModItems.JACK_HERER_GUMMY.get());
        // Gary Payton
        basicItem(ModItems.GARY_PEYTON_SEEDS.get());
        basicItem(ModItems.GARY_PEYTON_BUD.get());
        basicItem(ModItems.GARY_PEYTON_WEED.get());
        basicItem(ModItems.GARY_PEYTON_EXTRACT.get());
        basicItem(ModItems.GARY_PEYTON_BAG.get());
        basicItem(ModItems.GARY_PEYTON_GUMMY.get());
        // Amnesia Haze
        basicItem(ModItems.AMNESIA_HAZE_SEEDS.get());
        basicItem(ModItems.AMNESIA_HAZE_BUD.get());
        basicItem(ModItems.AMNESIA_HAZE_WEED.get());
        basicItem(ModItems.AMNESIA_HAZE_EXTRACT.get());
        basicItem(ModItems.AMNESIA_HAZE_BAG.get());
        basicItem(ModItems.AMNESIA_HAZE_GUMMY.get());
        // AK47
        basicItem(ModItems.AK47_SEEDS.get());
        basicItem(ModItems.AK47_BUD.get());
        basicItem(ModItems.AK47_WEED.get());
        basicItem(ModItems.AK47_EXTRACT.get());
        basicItem(ModItems.AK47_BAG.get());
        basicItem(ModItems.AK47_GUMMY.get());
        // Ghost Train
        basicItem(ModItems.GHOST_TRAIN_SEEDS.get());
        basicItem(ModItems.GHOST_TRAIN_BUD.get());
        basicItem(ModItems.GHOST_TRAIN_WEED.get());
        basicItem(ModItems.GHOST_TRAIN_EXTRACT.get());
        basicItem(ModItems.GHOST_TRAIN_BAG.get());
        basicItem(ModItems.GHOST_TRAIN_GUMMY.get());
        // Grape Ape
        basicItem(ModItems.GRAPE_APE_SEEDS.get());
        basicItem(ModItems.GRAPE_APE_BUD.get());
        basicItem(ModItems.GRAPE_APE_WEED.get());
        basicItem(ModItems.GRAPE_APE_EXTRACT.get());
        basicItem(ModItems.GRAPE_APE_BAG.get());
        basicItem(ModItems.GRAPE_APE_GUMMY.get());
        // Cotton Candy
        basicItem(ModItems.COTTON_CANDY_SEEDS.get());
        basicItem(ModItems.COTTON_CANDY_BUD.get());
        basicItem(ModItems.COTTON_CANDY_WEED.get());
        basicItem(ModItems.COTTON_CANDY_EXTRACT.get());
        basicItem(ModItems.COTTON_CANDY_BAG.get());
        basicItem(ModItems.COTTON_CANDY_GUMMY.get());
        // Banana Kush
        basicItem(ModItems.BANANA_KUSH_SEEDS.get());
        basicItem(ModItems.BANANA_KUSH_BUD.get());
        basicItem(ModItems.BANANA_KUSH_WEED.get());
        basicItem(ModItems.BANANA_KUSH_EXTRACT.get());
        basicItem(ModItems.BANANA_KUSH_BAG.get());
        basicItem(ModItems.BANANA_KUSH_GUMMY.get());
        // Carbon Fiber
        basicItem(ModItems.CARBON_FIBER_SEEDS.get());
        basicItem(ModItems.CARBON_FIBER_BUD.get());
        basicItem(ModItems.CARBON_FIBER_WEED.get());
        basicItem(ModItems.CARBON_FIBER_EXTRACT.get());
        basicItem(ModItems.CARBON_FIBER_BAG.get());
        basicItem(ModItems.CARBON_FIBER_GUMMY.get());
        // Birthday Cake
        basicItem(ModItems.BIRTHDAY_CAKE_SEEDS.get());
        basicItem(ModItems.BIRTHDAY_CAKE_BUD.get());
        basicItem(ModItems.BIRTHDAY_CAKE_WEED.get());
        basicItem(ModItems.BIRTHDAY_CAKE_EXTRACT.get());
        basicItem(ModItems.BIRTHDAY_CAKE_BAG.get());
        basicItem(ModItems.BIRTHDAY_CAKE_GUMMY.get());
        // Blue Cookies
        basicItem(ModItems.BLUE_COOKIES_SEEDS.get());
        basicItem(ModItems.BLUE_COOKIES_BUD.get());
        basicItem(ModItems.BLUE_COOKIES_WEED.get());
        basicItem(ModItems.BLUE_COOKIES_EXTRACT.get());
        basicItem(ModItems.BLUE_COOKIES_BAG.get());
        basicItem(ModItems.BLUE_COOKIES_GUMMY.get());
        // Afghani
        basicItem(ModItems.AFGHANI_SEEDS.get());
        basicItem(ModItems.AFGHANI_BUD.get());
        basicItem(ModItems.AFGHANI_WEED.get());
        basicItem(ModItems.AFGHANI_EXTRACT.get());
        basicItem(ModItems.AFGHANI_BAG.get());
        basicItem(ModItems.AFGHANI_GUMMY.get());
        // Moonbow
        basicItem(ModItems.MOONBOW_SEEDS.get());
        basicItem(ModItems.MOONBOW_BUD.get());
        basicItem(ModItems.MOONBOW_WEED.get());
        basicItem(ModItems.MOONBOW_EXTRACT.get());
        basicItem(ModItems.MOONBOW_BAG.get());
        basicItem(ModItems.MOONBOW_GUMMY.get());
        // Lava Cake
        basicItem(ModItems.LAVA_CAKE_SEEDS.get());
        basicItem(ModItems.LAVA_CAKE_BUD.get());
        basicItem(ModItems.LAVA_CAKE_WEED.get());
        basicItem(ModItems.LAVA_CAKE_EXTRACT.get());
        basicItem(ModItems.LAVA_CAKE_BAG.get());
        basicItem(ModItems.LAVA_CAKE_GUMMY.get());
        // Jelly Rancher
        basicItem(ModItems.JELLY_RANCHER_SEEDS.get());
        basicItem(ModItems.JELLY_RANCHER_BUD.get());
        basicItem(ModItems.JELLY_RANCHER_WEED.get());
        basicItem(ModItems.JELLY_RANCHER_EXTRACT.get());
        basicItem(ModItems.JELLY_RANCHER_BAG.get());
        basicItem(ModItems.JELLY_RANCHER_GUMMY.get());
        // Strawberry Shortcake
        basicItem(ModItems.STRAWBERRY_SHORTCAKE_SEEDS.get());
        basicItem(ModItems.STRAWBERRY_SHORTCAKE_BUD.get());
        basicItem(ModItems.STRAWBERRY_SHORTCAKE_WEED.get());
        basicItem(ModItems.STRAWBERRY_SHORTCAKE_EXTRACT.get());
        basicItem(ModItems.STRAWBERRY_SHORTCAKE_BAG.get());
        basicItem(ModItems.STRAWBERRY_SHORTCAKE_GUMMY.get());
        // Pink Kush
        basicItem(ModItems.PINK_KUSH_SEEDS.get());
        basicItem(ModItems.PINK_KUSH_BUD.get());
        basicItem(ModItems.PINK_KUSH_WEED.get());
        basicItem(ModItems.PINK_KUSH_EXTRACT.get());
        basicItem(ModItems.PINK_KUSH_BAG.get());
        basicItem(ModItems.PINK_KUSH_GUMMY.get());




    }


    private void dnaStrandModels() {
        // Create full variant FIRST so it exists for the override reference
        ModelFile fullModel = getBuilder("dna_strand_full")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/dna_strand_full"));

        // Base model with predicate override to full
        getBuilder("dna_strand")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/dna_strand"))
                .override()
                .predicate(ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "full"), 1.0F)
                .model(fullModel)
                .end();
    }


    public void buttonItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<Block> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

}
