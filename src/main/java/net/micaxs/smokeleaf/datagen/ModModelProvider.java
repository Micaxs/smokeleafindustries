package net.micaxs.smokeleaf.datagen;

import com.mojang.math.Quadrant;
import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.custom.BaseWeedCropBlock;
import net.micaxs.smokeleaf.block.custom.GrowLightBlock;
import net.micaxs.smokeleaf.block.custom.ReflectorBlock;
import net.micaxs.smokeleaf.block.custom.TobaccoCropBlock;
import net.micaxs.smokeleaf.block.custom.UnidentifiedWeedCropBlock;
import net.micaxs.smokeleaf.client.color.DnaFullProperty;
import net.micaxs.smokeleaf.client.color.StrainTintSource;
import net.micaxs.smokeleaf.client.color.StrainTintSource.Role;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.item.properties.conditional.HasComponent;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Block states, block/item models and client item definitions ({@code assets/<ns>/items/*.json}).
 * Replaces the old NeoForge {@code BlockStateProvider} + {@code ItemModelProvider} pair: since
 * 1.21.4 every item needs a client item definition, which is also where per-layer strain tints
 * ({@link StrainTintSource}) and model conditions (DNA strand full, manual grinder loaded) live.
 */
public class ModModelProvider extends ModelProvider {
    private static final TextureSlot LAYER3 = TextureSlot.create("layer3");
    private static final TextureSlot CROP_BASE = TextureSlot.create("base");
    private static final TextureSlot CROP_MASK = TextureSlot.create("mask");
    private static final ModelTemplate FOUR_LAYERED_ITEM = ModelTemplates.createItem("generated",
            TextureSlot.LAYER0, TextureSlot.LAYER1, TextureSlot.LAYER2, LAYER3);
    private static final ItemTintSource UNTINTED = ItemModelUtils.constantTint(-1);

    public ModModelProvider(PackOutput output) {
        super(output, SmokeleafIndustries.MODID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        // The pipe's blockstate is hand-written (custom "smokeleafindustries:pipe" block state model).
        return super.getKnownBlocks().filter(holder -> holder.value() != ModBlocks.PIPE.get());
    }

    @Override
    protected void registerModels(BlockModelGenerators blocks, ItemModelGenerators items) {
        registerBlocks(blocks);
        registerItems(items);
    }

    // -----------------------------------------------------------------------
    // Blocks
    // -----------------------------------------------------------------------

    private void registerBlocks(BlockModelGenerators blocks) {
        blocks.family(ModBlocks.HEMP_STONE.get())
                .stairs(ModBlocks.HEMP_STONE_STAIRS.get())
                .slab(ModBlocks.HEMP_STONE_SLAB.get())
                .pressurePlate(ModBlocks.HEMP_STONE_PRESSURE_PLATE.get())
                .button(ModBlocks.HEMP_STONE_BUTTON.get())
                .wall(ModBlocks.HEMP_STONE_WALL.get());
        blocks.family(ModBlocks.HEMP_BRICKS.get())
                .stairs(ModBlocks.HEMP_BRICK_STAIRS.get())
                .slab(ModBlocks.HEMP_BRICK_SLAB.get())
                .wall(ModBlocks.HEMP_BRICK_WALL.get());
        blocks.family(ModBlocks.HEMP_CHISELED_STONE.get())
                .stairs(ModBlocks.HEMP_CHISELED_STONE_STAIRS.get())
                .slab(ModBlocks.HEMP_CHISELED_STONE_SLAB.get())
                .wall(ModBlocks.HEMP_CHISELED_STONE_WALL.get());
        blocks.family(ModBlocks.HEMP_PLANKS.get())
                .stairs(ModBlocks.HEMP_PLANK_STAIRS.get())
                .slab(ModBlocks.HEMP_PLANK_SLAB.get())
                .pressurePlate(ModBlocks.HEMP_PLANK_PRESSURE_PLATE.get())
                .button(ModBlocks.HEMP_PLANK_BUTTON.get())
                .fence(ModBlocks.HEMP_PLANK_FENCE.get())
                .fenceGate(ModBlocks.HEMP_PLANK_FENCE_GATE.get());
        blocks.createDoor(ModBlocks.HEMP_PLANK_DOOR.get());
        blocks.createOrientableTrapdoor(ModBlocks.HEMP_PLANK_TRAPDOOR.get());

        for (DeferredBlock<Block> wool : ModBlocks.HEMP_WOOL.values()) {
            blocks.createTrivialCube(wool.get());
        }

        weedCrop(blocks, ModBlocks.HEMP_CROP.get(), "hemp/hemp_stage_");
        unidentifiedWeedCrop(blocks, ModBlocks.UNIDENTIFIED_WEED_CROP.get(), "unidentified/unidentified_stage_");
        tobaccoCrop(blocks, (TobaccoCropBlock) ModBlocks.TOBACCO_CROP.get(), "tobacco/tobacco_stage_");

        // Machines (and the reflector/grow light) use hand-made models in src/main/resources.
        horizontalBooleanVariants(blocks, ModBlocks.LED_LIGHT.get(), GrowLightBlock.FACING, GrowLightBlock.CLICKED, "_on", "_off");
        horizontalBooleanVariants(blocks, ModBlocks.REFLECTOR.get(), ReflectorBlock.FACING, ReflectorBlock.HAS_LAMP, "_lamp", "");
        for (DeferredBlock<Block> machine : java.util.List.of(ModBlocks.GENERATOR, ModBlocks.GRINDER, ModBlocks.EXTRACTOR,
                ModBlocks.LIQUIFIER, ModBlocks.MUTATOR, ModBlocks.SYNTHESIZER, ModBlocks.SEQUENCER, ModBlocks.DRYER,
                ModBlocks.MIXER, ModBlocks.STRAIN_MODIFIER, ModBlocks.GUMMY_MACHINE)) {
            horizontalBooleanVariants(blocks, machine.get(), BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, "_on", "");
        }
        horizontalBlock(blocks, ModBlocks.DRYING_RACK.get(), modBlock("drying_rack"));
        horizontalBlock(blocks, ModBlocks.GROW_POT.get(), modBlock("grow_pot"));

        // Fluid blocks only need a particle texture; the fluid itself renders from its fluid model.
        for (Block fluidBlock : java.util.List.of(ModFluids.HASH_OIL_FLUID_BLOCK.get(), ModFluids.HASH_OIL_SLUDGE_FLUID_BLOCK.get(),
                ModFluids.UNIDENTIFIED_MIXTURE_FLUID_BLOCK.get())) {
            Identifier model = ModelTemplates.PARTICLE_ONLY.create(fluidBlock,
                    TextureMapping.particle(new Material(Identifier.withDefaultNamespace("block/water_still"))), blocks.modelOutput);
            blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(fluidBlock, BlockModelGenerators.plainVariant(model)));
        }
    }

    /**
     * Two-block-tall weed crop: the top half shows the stage for its age; the bottom half stays at
     * the last first-stage texture (6) while the top grows, switching to "6_full" for the final ages.
     */
    private static String bottomStage(int age) {
        if (age <= BaseWeedCropBlock.FIRST_STAGE_MAX_AGE) return Integer.toString(age);
        if (age <= 8) return Integer.toString(BaseWeedCropBlock.FIRST_STAGE_MAX_AGE);
        return BaseWeedCropBlock.FIRST_STAGE_MAX_AGE + "_full";
    }

    private void weedCrop(BlockModelGenerators blocks, Block crop, String texturePrefix) {
        // Several ages share a stage texture; each model may only be generated once.
        java.util.Map<String, Identifier> models = new java.util.HashMap<>();
        blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(crop).with(
                PropertyDispatch.initial(BaseWeedCropBlock.AGE, BaseWeedCropBlock.TOP).generate((age, top) -> {
                    String stage = top ? Integer.toString(age) : bottomStage(age);
                    Identifier model = models.computeIfAbsent(stage, st -> ModelTemplates.CROSS.create(
                            modBlock(texturePrefix.substring(texturePrefix.indexOf('/') + 1) + st),
                            TextureMapping.cross(new Material(modBlock(texturePrefix + st))), blocks.modelOutput));
                    return BlockModelGenerators.plainVariant(model);
                })));
    }

    private void unidentifiedWeedCrop(BlockModelGenerators blocks, Block crop, String texturePrefix) {
        // Two-layer model (base tinted with the strain's leaf color, mask with its strain color).
        java.util.Map<String, Identifier> models = new java.util.HashMap<>();
        ModelTemplate twoLayer = new ModelTemplate(Optional.of(modBlock("crop_two_layer")), Optional.empty(),
                CROP_BASE, CROP_MASK, TextureSlot.PARTICLE);
        blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(crop).with(
                PropertyDispatch.initial(UnidentifiedWeedCropBlock.AGE, UnidentifiedWeedCropBlock.TOP).generate((age, top) -> {
                    String stage = top ? Integer.toString(age) : bottomStage(age);
                    String modelName = "unidentified_stage_" + (top ? age + "_top" : stage) + "_two";
                    Material base = new Material(modBlock(texturePrefix + stage + "_base"));
                    Identifier model = models.computeIfAbsent(modelName, name -> twoLayer.create(modBlock(name), new TextureMapping()
                            .put(CROP_BASE, base)
                            .put(CROP_MASK, new Material(modBlock(texturePrefix + stage + "_mask")))
                            .put(TextureSlot.PARTICLE, base), blocks.modelOutput));
                    return BlockModelGenerators.plainVariant(model);
                })));
    }

    private void tobaccoCrop(BlockModelGenerators blocks, TobaccoCropBlock crop, String texturePrefix) {
        blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(crop).with(
                PropertyDispatch.initial(TobaccoCropBlock.AGE).generate(age -> {
                    Identifier model = ModelTemplates.CROSS.create(modBlock("tobacco_stage_" + age),
                            TextureMapping.cross(new Material(modBlock(texturePrefix + age))), blocks.modelOutput);
                    return BlockModelGenerators.plainVariant(model);
                })));
    }

    /** Matches the old datagen's rotation (Direction#toYRot: south = 0°, west = 90°, north = 180°, east = 270°). */
    private static Variant rotated(Variant variant, Direction facing) {
        return variant.withYRot(switch (facing) {
            case WEST -> Quadrant.R90;
            case NORTH -> Quadrant.R180;
            case EAST -> Quadrant.R270;
            default -> Quadrant.R0;
        });
    }

    private void horizontalBooleanVariants(BlockModelGenerators blocks, Block block, EnumProperty<Direction> facing, BooleanProperty flag,
                                           String trueSuffix, String falseSuffix) {
        String name = blockName(block);
        blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(facing, flag).generate((dir, on) ->
                        BlockModelGenerators.variant(rotated(BlockModelGenerators.plainModel(modBlock(name + (on ? trueSuffix : falseSuffix))), dir)))));
        blocks.registerSimpleItemModel(block, modBlock(name + falseSuffix));
    }

    private void horizontalBlock(BlockModelGenerators blocks, Block block, Identifier model) {
        blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING).generate(dir ->
                        BlockModelGenerators.variant(rotated(BlockModelGenerators.plainModel(model), dir)))));
    }

    // -----------------------------------------------------------------------
    // Items
    // -----------------------------------------------------------------------

    private void registerItems(ItemModelGenerators items) {
        for (Item item : new Item[]{
                ModItems.HEMP_HAMMER.get(), ModItems.PLANT_ANALYZER.get(), ModItems.GUMMY_MOLD.get(), ModItems.STRAIN_BOOK.get(),
                ModItems.SMOKELEAF_GUIDE.get(), ModItems.UNFINISHED_HEMP_CORE.get(), ModItems.HEMP_CORE.get(), ModItems.HEMP_SEEDS.get(),
                ModItems.HEMP_LEAF.get(), ModItems.HEMP_FIBERS.get(), ModItems.HEMP_FABRIC.get(), ModItems.HEMP_STICK.get(),
                ModItems.BAJA_HOODIE_HELMET.get(), ModItems.BAJA_HOODIE_CHESTPLATE.get(), ModItems.BAJA_HOODIE_LEGGINGS.get(),
                ModItems.BAJA_HOODIE_BOOTS.get(), ModItems.REINFORCED_BAJA_HOODIE_HELMET.get(),
                ModItems.REINFORCED_BAJA_HOODIE_CHESTPLATE.get(), ModItems.REINFORCED_BAJA_HOODIE_LEGGINGS.get(),
                ModItems.REINFORCED_BAJA_HOODIE_BOOTS.get(), ModItems.EMPTY_BAG.get(), ModItems.JOINT.get(), ModItems.BLUNT.get(),
                ModItems.BONG.get(), ModItems.WEED_COOKIE.get(), ModItems.HASH_BROWNIE.get(), ModItems.HERB_CAKE.get(),
                ModItems.DAB_RIG.get(), ModItems.EMPTY_TINCTURE.get(), ModFluids.HASH_OIL_BUCKET.get(),
                ModFluids.HASH_OIL_SLUDGE_BUCKET.get(), ModItems.HASH_OIL_TINCTURE.get(), ModItems.BASE_EXTRACT.get(),
                ModItems.GUMMY_WORM_MOLD.get(), ModItems.ITEM_PIPE.get(), ModItems.FLUID_PIPE.get(), ModItems.ENERGY_PIPE.get(),
                ModItems.PIPE_WRENCH.get(), ModItems.HEMP_COAL.get(), ModItems.BUTTER.get(), ModItems.INFUSED_BUTTER.get(),
                ModItems.BIO_COMPOSITE.get(), ModItems.HEMP_PLASTIC.get(), ModItems.CAT_URINE_BOTTLE.get(),
                ModItems.TOBACCO.get(), ModItems.TOBACCO_LEAF.get(), ModItems.TOBACCO_SEEDS.get(), ModItems.DRIED_TOBACCO_LEAF.get(),
                ModItems.WORM_CASTINGS.get(), ModItems.COMPOST.get(), ModItems.MYCORRHIZAE.get(), ModItems.DOLOMITE_LIME.get(),
                ModItems.BLOOD_MEAL.get(), ModItems.FISH_EMULSION.get(), ModItems.PHOSPHORUS_POWDER.get(), ModItems.BAT_GUANO.get(),
                ModItems.KELP_MEAL.get(), ModItems.WOOD_ASH.get(), ModItems.EMPTY_VIAL.get(), ModItems.BLOOM_BOOSTER.get(),
                ModItems.FRUIT_FINISHER.get(), ModItems.NITROGEN_BOOST.get(), ModItems.POTASH_BOOST.get(), ModItems.BALANCED_BOOST.get(),
                ModItems.PHOSPHORUS_REDUCER.get(), ModItems.POTASSIUM_REDUCER.get(),
                ModItems.WHITE_WIDOW_BAG.get(), ModItems.WHITE_WIDOW_GUMMY.get(), ModItems.BUBBLE_KUSH_GUMMY.get(),
                ModItems.LEMON_HAZE_GUMMY.get(), ModItems.SOUR_DIESEL_GUMMY.get(), ModItems.BLUE_ICE_GUMMY.get(),
                ModItems.BUBBLEGUM_GUMMY.get(), ModItems.PURPLE_HAZE_GUMMY.get(), ModItems.OG_KUSH_GUMMY.get(),
                ModItems.JACK_HERER_GUMMY.get(), ModItems.GARY_PEYTON_GUMMY.get(), ModItems.AMNESIA_HAZE_GUMMY.get(),
                ModItems.AK47_GUMMY.get(), ModItems.GHOST_TRAIN_GUMMY.get(), ModItems.GRAPE_APE_GUMMY.get(),
                ModItems.COTTON_CANDY_GUMMY.get(), ModItems.BANANA_KUSH_GUMMY.get(), ModItems.CARBON_FIBER_GUMMY.get(),
                ModItems.BIRTHDAY_CAKE_GUMMY.get(), ModItems.BLUE_COOKIES_GUMMY.get(), ModItems.AFGHANI_GUMMY.get(),
                ModItems.MOONBOW_GUMMY.get(), ModItems.LAVA_CAKE_GUMMY.get(), ModItems.JELLY_RANCHER_GUMMY.get(),
                ModItems.STRAWBERRY_SHORTCAKE_GUMMY.get(), ModItems.PINK_KUSH_GUMMY.get()}) {
            items.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }

        // Generic strain-data carriers — two layers: base (leaf color, tint 0) + mask (strain color, tint 1)
        strainItem(items, ModItems.GENERIC_SEEDS.get(), "generic_seeds", Role.SEEDS_LEAF, Role.SEEDS_COLOR);
        strainItem(items, ModItems.GENERIC_BUD.get(), "generic_bud", Role.BUD_LEAF, Role.BUD_COLOR);
        strainItem(items, ModItems.GENERIC_WEED.get(), "generic_weed", Role.WEED_LEAF, Role.WEED_COLOR);
        Identifier extract = ModelTemplates.TWO_LAYERED_ITEM.create(ModItems.GENERIC_EXTRACT.get(),
                TextureMapping.layered(modItemTexture("generic_extract_base"), modItemTexture("generic_extract_mask")), items.modelOutput);
        // Extract: base layer is untinted; only the mask gets the strain color
        items.itemModelOutput.accept(ModItems.GENERIC_EXTRACT.get(), ItemModelUtils.tintedModel(extract, UNTINTED, strain(Role.EXTRACT_COLOR)));

        // Bag — 4 layers: bg (no tint), weed (leaf color), weed mask (strain color), top overlay (no tint)
        Identifier bag = FOUR_LAYERED_ITEM.create(ModItems.GENERIC_BAG.get(), new TextureMapping()
                .put(TextureSlot.LAYER0, modItemTexture("generic_bag_bg"))
                .put(TextureSlot.LAYER1, modItemTexture("generic_bag_weed"))
                .put(TextureSlot.LAYER2, modItemTexture("generic_bag_weed_mask"))
                .put(LAYER3, modItemTexture("generic_bag_top_overlay")), items.modelOutput);
        items.itemModelOutput.accept(ModItems.GENERIC_BAG.get(),
                ItemModelUtils.tintedModel(bag, UNTINTED, strain(Role.LEAF_COLOR), strain(Role.STRAIN_COLOR), UNTINTED));

        // Gummy — bg (leaf color), mask1 (strain color), mask2 (lighter strain tone)
        Identifier gummy = ModelTemplates.THREE_LAYERED_ITEM.create(ModItems.GENERIC_GUMMY.get(), TextureMapping.layered(
                modItemTexture("generic_gummy_bg"), modItemTexture("generic_gummy_mask1"), modItemTexture("generic_gummy_mask2")), items.modelOutput);
        items.itemModelOutput.accept(ModItems.GENERIC_GUMMY.get(),
                ItemModelUtils.tintedModel(gummy, strain(Role.LEAF_COLOR), strain(Role.STRAIN_COLOR), strain(Role.STRAIN_COLOR_LIGHT)));

        // Gummy Worm — bg untinted, mask1 strain color, mask2 leaf color
        Identifier worm = ModelTemplates.THREE_LAYERED_ITEM.create(ModItems.GENERIC_GUMMY_WORM.get(), TextureMapping.layered(
                modItemTexture("generic_gummy_worm_bg"), modItemTexture("generic_gummy_worm_mask1"), modItemTexture("generic_gummy_worm_mask2")), items.modelOutput);
        items.itemModelOutput.accept(ModItems.GENERIC_GUMMY_WORM.get(),
                ItemModelUtils.tintedModel(worm, UNTINTED, strain(Role.STRAIN_COLOR), strain(Role.LEAF_COLOR)));

        // Mixture bucket (hand-made two-layer model): base uncolored, only the mask gets the strain color
        items.itemModelOutput.accept(ModFluids.UNIDENTIFIED_MIXTURE_BUCKET.get(),
                ItemModelUtils.tintedModel(modItem("unidentified_mixture_bucket"), UNTINTED, strain(Role.STRAIN_COLOR)));

        // DNA strand: switches to the "full" texture once all reagents are in
        Identifier dna = items.createFlatItemModel(ModItems.DNA_STRAND.get(), ModelTemplates.FLAT_ITEM);
        Identifier dnaFull = items.createFlatItemModel(ModItems.DNA_STRAND.get(), "_full", ModelTemplates.FLAT_ITEM);
        items.itemModelOutput.accept(ModItems.DNA_STRAND.get(),
                ItemModelUtils.conditional(new DnaFullProperty(), ItemModelUtils.plainModel(dnaFull), ItemModelUtils.plainModel(dna)));

        // Manual grinder (hand-made models): shows as loaded while it holds something to grind
        items.itemModelOutput.accept(ModItems.MANUAL_GRINDER.get(), ItemModelUtils.conditional(
                new HasComponent(ModDataComponentTypes.MANUAL_GRINDER_CONTENTS.get(), false),
                ItemModelUtils.plainModel(modItem("manual_grinder_full")), ItemModelUtils.plainModel(modItem("manual_grinder"))));

        // Lamps use hand-made 3D models
        items.itemModelOutput.accept(ModItems.HPS_LAMP.get(), ItemModelUtils.plainModel(modItem("hps_lamp")));
        items.itemModelOutput.accept(ModItems.DUAL_ARC_LAMP.get(), ItemModelUtils.plainModel(modItem("dual_arc_lamp")));
    }

    private static void strainItem(ItemModelGenerators items, Item item, String texture, Role base, Role mask) {
        Identifier model = ModelTemplates.TWO_LAYERED_ITEM.create(item,
                TextureMapping.layered(modItemTexture(texture + "_base"), modItemTexture(texture + "_mask")), items.modelOutput);
        items.itemModelOutput.accept(item, ItemModelUtils.tintedModel(model, strain(base), strain(mask)));
    }

    private static ItemTintSource strain(Role role) {
        return new StrainTintSource(role);
    }

    private static Identifier modBlock(String path) {
        return Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "block/" + path);
    }

    private static Identifier modItem(String path) {
        return Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, "item/" + path);
    }

    private static Material modItemTexture(String path) {
        return new Material(modItem(path));
    }

    private static String blockName(Block block) {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
