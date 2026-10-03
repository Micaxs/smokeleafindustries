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
        basicItem(ModItems.GUMMY_MOLD.get());
        basicItem(ModItems.STRAIN_BOOK.get());
        basicItem(ModItems.SMOKELEAF_GUIDE.get());

        basicItem(ModItems.UNFINISHED_HEMP_CORE.get());
        basicItem(ModItems.HEMP_CORE.get());
        basicItem(ModItems.HEMP_SEEDS.get());
        basicItem(ModItems.HEMP_LEAF.get());
        basicItem(ModItems.HEMP_FIBERS.get());
        basicItem(ModItems.HEMP_FABRIC.get());
        basicItem(ModItems.HEMP_STICK.get());

        basicItem(ModItems.BAJA_HOODIE_HELMET.get());
        basicItem(ModItems.BAJA_HOODIE_CHESTPLATE.get());
        basicItem(ModItems.BAJA_HOODIE_LEGGINGS.get());
        basicItem(ModItems.BAJA_HOODIE_BOOTS.get());
        basicItem(ModItems.REINFORCED_BAJA_HOODIE_HELMET.get());
        basicItem(ModItems.REINFORCED_BAJA_HOODIE_CHESTPLATE.get());
        basicItem(ModItems.REINFORCED_BAJA_HOODIE_LEGGINGS.get());
        basicItem(ModItems.REINFORCED_BAJA_HOODIE_BOOTS.get());

        basicItem(ModItems.EMPTY_BAG.get());
        basicItem(ModItems.JOINT.get());
        basicItem(ModItems.BLUNT.get());
        basicItem(ModItems.BONG.get());
        basicItem(ModItems.WEED_COOKIE.get());
        basicItem(ModItems.HASH_BROWNIE.get());
        basicItem(ModItems.HERB_CAKE.get());
        basicItem(ModItems.DAB_RIG.get());
        basicItem(ModItems.EMPTY_TINCTURE.get());
        basicItem(ModFluids.HASH_OIL_BUCKET.get());
        basicItem(ModFluids.HASH_OIL_SLUDGE_BUCKET.get());


        basicItem(ModItems.HASH_OIL_TINCTURE.get());
        basicItem(ModItems.BASE_EXTRACT.get());
        // Generic strain-data carriers — two layers: base (leafColor, tint 0) + mask (colorArgb, tint 1)
        getBuilder("seeds")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_seeds_base"))
                .texture("layer1", modLoc("item/generic_seeds_mask"));
        getBuilder("bud")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_bud_base"))
                .texture("layer1", modLoc("item/generic_bud_mask"));
        getBuilder("weed")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_weed_base"))
                .texture("layer1", modLoc("item/generic_weed_mask"));
        getBuilder("extract")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_extract_base"))
                .texture("layer1", modLoc("item/generic_extract_mask"));

        // Bag — 4-layer model; layers 1 and 2 are tinted by strain colors via bagItemColor
        getBuilder("bag")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_bag_bg"))
                .texture("layer1", modLoc("item/generic_bag_weed"))
                .texture("layer2", modLoc("item/generic_bag_weed_mask"))
                .texture("layer3", modLoc("item/generic_bag_top_overlay"));

        // Gummy — 3-layer model: bg (leafColor), mask1 (colorArgb), mask2 (lighter strain tone)
        getBuilder("gummy")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_gummy_bg"))
                .texture("layer1", modLoc("item/generic_gummy_mask1"))
                .texture("layer2", modLoc("item/generic_gummy_mask2"));

        // Gummy Worm — same 3-layer scheme as the gummy bear
        getBuilder("gummy_worm")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_gummy_worm_bg"))
                .texture("layer1", modLoc("item/generic_gummy_worm_mask1"))
                .texture("layer2", modLoc("item/generic_gummy_worm_mask2"));

        basicItem(ModItems.GUMMY_WORM_MOLD.get());

        // Pipes / Pipe Wrench — flat 2D item icons; the in-world block form uses the custom
        // PipeGeometryLoader dynamic model instead (see assets/.../models/block/pipe.json).
        basicItem(ModItems.ITEM_PIPE.get());
        basicItem(ModItems.FLUID_PIPE.get());
        basicItem(ModItems.ENERGY_PIPE.get());
        basicItem(ModItems.PIPE_WRENCH.get());

        // Unidentified items share the generic textures (STRAIN_DATA tinting applies the same way)
        getBuilder("unidentified_bud")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_bud_base"))
                .texture("layer1", modLoc("item/generic_bud_mask"));
        getBuilder("unidentified_weed")
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/generic_weed_base"))
                .texture("layer1", modLoc("item/generic_weed_mask"));
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
        basicItem(ModItems.EMPTY_VIAL.get());
        basicItem(ModItems.BLOOM_BOOSTER.get());
        basicItem(ModItems.FRUIT_FINISHER.get());
        basicItem(ModItems.NITROGEN_BOOST.get());
        basicItem(ModItems.POTASH_BOOST.get());
        basicItem(ModItems.BALANCED_BOOST.get());
        basicItem(ModItems.PHOSPHORUS_REDUCER.get());
        basicItem(ModItems.POTASSIUM_REDUCER.get());


        // --- Weed Items

        // White Widow
        basicItem(ModItems.WHITE_WIDOW_BAG.get());
        basicItem(ModItems.WHITE_WIDOW_GUMMY.get());
        // Bubble Kush
        basicItem(ModItems.BUBBLE_KUSH_GUMMY.get());
        // Lemon Haze
        basicItem(ModItems.LEMON_HAZE_GUMMY.get());
        // Sour Diesel
        basicItem(ModItems.SOUR_DIESEL_GUMMY.get());
        // Blue Ice
        basicItem(ModItems.BLUE_ICE_GUMMY.get());
        // Bubblegum
        basicItem(ModItems.BUBBLEGUM_GUMMY.get());
        // Purple Haze
        basicItem(ModItems.PURPLE_HAZE_GUMMY.get());
        // OG Kush
        basicItem(ModItems.OG_KUSH_GUMMY.get());
        // Jack Herer
        basicItem(ModItems.JACK_HERER_GUMMY.get());
        // Gary Payton
        basicItem(ModItems.GARY_PEYTON_GUMMY.get());
        // Amnesia Haze
        basicItem(ModItems.AMNESIA_HAZE_GUMMY.get());
        // AK47
        basicItem(ModItems.AK47_GUMMY.get());
        // Ghost Train
        basicItem(ModItems.GHOST_TRAIN_GUMMY.get());
        // Grape Ape
        basicItem(ModItems.GRAPE_APE_GUMMY.get());
        // Cotton Candy
        basicItem(ModItems.COTTON_CANDY_GUMMY.get());
        // Banana Kush
        basicItem(ModItems.BANANA_KUSH_GUMMY.get());
        // Carbon Fiber
        basicItem(ModItems.CARBON_FIBER_GUMMY.get());
        // Birthday Cake
        basicItem(ModItems.BIRTHDAY_CAKE_GUMMY.get());
        // Blue Cookies
        basicItem(ModItems.BLUE_COOKIES_GUMMY.get());
        // Afghani
        basicItem(ModItems.AFGHANI_GUMMY.get());
        // Moonbow
        basicItem(ModItems.MOONBOW_GUMMY.get());
        // Lava Cake
        basicItem(ModItems.LAVA_CAKE_GUMMY.get());
        // Jelly Rancher
        basicItem(ModItems.JELLY_RANCHER_GUMMY.get());
        // Strawberry Shortcake
        basicItem(ModItems.STRAWBERRY_SHORTCAKE_GUMMY.get());
        // Pink Kush
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
