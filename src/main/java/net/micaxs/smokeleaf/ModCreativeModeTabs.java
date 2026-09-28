package net.micaxs.smokeleaf;

import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.component.ModDataComponentTypes;
import net.micaxs.smokeleaf.fluid.ModFluids;
import net.micaxs.smokeleaf.item.ModItems;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SmokeleafIndustries.MODID);

    /** Creates a GENERIC item stack carrying the preset StrainData for the given strain id. */
    private static ItemStack strainedStack(Item item, String strainId) {
        StrainData data = StrainRegistry.get(strainId).orElse(null);
        if (data == null) return new ItemStack(item);
        ItemStack stack = new ItemStack(item);
        ModDataComponentTypes.STRAIN_DATA.set(stack, data);
        ModDataComponentTypes.STRAIN_ID.set(stack, strainId);
        return stack;
    }

    /** All 25 named strains, in the order they should appear within each item-type group. */
    private static final String[] STRAIN_IDS = {
            "white_widow", "bubble_kush", "lemon_haze", "sour_diesel", "blue_ice",
            "bubblegum", "purple_haze", "og_kush", "jack_herer", "gary_peyton",
            "amnesia_haze", "ak47", "ghost_train", "grape_ape", "cotton_candy",
            "banana_kush", "carbon_fiber", "birthday_cake", "blue_cookies", "afghani",
            "moonbow", "lava_cake", "jelly_rancher", "strawberry_shortcake", "pink_kush"
    };

    /** Adds one strained stack of {@code item} for every strain in {@link #STRAIN_IDS}. */
    private static void addItemForAllStrains(CreativeModeTab.Output output, Item item) {
        for (String strainId : STRAIN_IDS) {
            output.accept(strainedStack(item, strainId));
        }
    }


    public static final Supplier<CreativeModeTab> SMOKELEAF_ITEMS_TAB = CREATIVE_MODE_TAB.register("smokeleaf_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.HEMP_CORE.get()))
                    .title(Component.translatable("creativetab.smokeleafindustries.smokeleaf_items_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        // Hemp processing chain: raw material -> intermediates -> the Hemp Core
                        // hub item every machine needs.
                        output.accept(ModItems.HEMP_LEAF.get());
                        output.accept(ModItems.HEMP_FIBERS.get());
                        output.accept(ModItems.HEMP_STICK.get());
                        output.accept(ModItems.HEMP_FABRIC.get());
                        output.accept(ModItems.BIO_COMPOSITE.get());
                        output.accept(ModItems.HEMP_COAL.get());
                        output.accept(ModItems.HEMP_PLASTIC.get());
                        output.accept(ModItems.UNFINISHED_HEMP_CORE.get());
                        output.accept(ModItems.HEMP_CORE.get());

                        // Baja Hoodie armor (woven from Hemp Fabric) and its netherite-tier upgrade.
                        output.accept(ModItems.BAJA_HOODIE_HELMET.get());
                        output.accept(ModItems.BAJA_HOODIE_CHESTPLATE.get());
                        output.accept(ModItems.BAJA_HOODIE_LEGGINGS.get());
                        output.accept(ModItems.BAJA_HOODIE_BOOTS.get());
                        output.accept(ModItems.REINFORCED_BAJA_HOODIE_HELMET.get());
                        output.accept(ModItems.REINFORCED_BAJA_HOODIE_CHESTPLATE.get());
                        output.accept(ModItems.REINFORCED_BAJA_HOODIE_LEGGINGS.get());
                        output.accept(ModItems.REINFORCED_BAJA_HOODIE_BOOTS.get());

                        // Building blocks, one family at a time (base block, then its variants).
                        output.accept(ModBlocks.HEMP_STONE.get());
                        output.accept(ModBlocks.HEMP_STONE_SLAB.get());
                        output.accept(ModBlocks.HEMP_STONE_STAIRS.get());
                        output.accept(ModBlocks.HEMP_STONE_PRESSURE_PLATE.get());
                        output.accept(ModBlocks.HEMP_STONE_BUTTON.get());
                        output.accept(ModBlocks.HEMP_STONE_WALL.get());

                        output.accept(ModBlocks.HEMP_PLANKS.get());
                        output.accept(ModBlocks.HEMP_PLANK_SLAB.get());
                        output.accept(ModBlocks.HEMP_PLANK_STAIRS.get());
                        output.accept(ModBlocks.HEMP_PLANK_PRESSURE_PLATE.get());
                        output.accept(ModBlocks.HEMP_PLANK_BUTTON.get());
                        output.accept(ModBlocks.HEMP_PLANK_FENCE.get());
                        output.accept(ModBlocks.HEMP_PLANK_FENCE_GATE.get());
                        output.accept(ModBlocks.HEMP_PLANK_DOOR.get());
                        output.accept(ModBlocks.HEMP_PLANK_TRAPDOOR.get());

                        output.accept(ModBlocks.HEMP_BRICKS.get());
                        output.accept(ModBlocks.HEMP_BRICK_SLAB.get());
                        output.accept(ModBlocks.HEMP_BRICK_STAIRS.get());
                        output.accept(ModBlocks.HEMP_BRICK_WALL.get());

                        output.accept(ModBlocks.HEMP_CHISELED_STONE.get());
                        output.accept(ModBlocks.HEMP_CHISELED_STONE_SLAB.get());
                        output.accept(ModBlocks.HEMP_CHISELED_STONE_STAIRS.get());
                        output.accept(ModBlocks.HEMP_CHISELED_STONE_WALL.get());

                        for (DyeColor color : DyeColor.values()) {
                            output.accept(ModBlocks.HEMP_WOOL.get(color).get());
                        }

                        // Tobacco.
                        output.accept(ModItems.TOBACCO.get());
                        output.accept(ModItems.TOBACCO_LEAF.get());
                        output.accept(ModItems.DRIED_TOBACCO_LEAF.get());

                        // Lighting & grow equipment.
                        output.accept(ModBlocks.REFLECTOR.get());
                        output.accept(ModItems.HPS_LAMP.get());
                        output.accept(ModItems.DUAL_ARC_LAMP.get());
                        output.accept(ModBlocks.LED_LIGHT.get());
                        output.accept(ModBlocks.GROW_POT.get());

                        // Machines, in roughly the order they're used along the processing chain.
                        output.accept(ModBlocks.GENERATOR.get());
                        output.accept(ModBlocks.GRINDER.get());
                        output.accept(ModBlocks.EXTRACTOR.get());
                        output.accept(ModBlocks.LIQUIFIER.get());
                        output.accept(ModBlocks.MIXER.get());
                        output.accept(ModBlocks.MUTATOR.get());
                        output.accept(ModBlocks.STRAIN_MODIFIER.get());
                        output.accept(ModBlocks.SYNTHESIZER.get());
                        output.accept(ModBlocks.SEQUENCER.get());
                        output.accept(ModBlocks.DRYER.get());
                        output.accept(ModBlocks.DRYING_RACK.get());
                        output.accept(ModBlocks.GUMMY_MACHINE.get());

                        // Pipes & wiring tools.
                        output.accept(ModItems.ITEM_PIPE.get());
                        output.accept(ModItems.FLUID_PIPE.get());
                        output.accept(ModItems.ENERGY_PIPE.get());
                        output.accept(ModItems.PIPE_WRENCH.get());

                        // Hand tools & reference books.
                        output.accept(ModItems.HEMP_HAMMER.get());
                        output.accept(ModItems.MANUAL_GRINDER.get());
                        output.accept(ModItems.PLANT_ANALYZER.get());
                        output.accept(ModItems.STRAIN_BOOK.get());
                        output.accept(ModItems.SMOKELEAF_GUIDE.get());

                        // Crafting reagents.
                        output.accept(ModItems.BASE_EXTRACT.get());
                        output.accept(ModItems.DNA_STRAND.get());
                        output.accept(ModItems.GUMMY_MOLD.get());
                        output.accept(ModItems.GUMMY_WORM_MOLD.get());
                        output.accept(ModItems.EMPTY_BAG.get());
                        output.accept(ModItems.EMPTY_VIAL.get());

                        // Smoking accessories.
                        output.accept(ModItems.JOINT.get());
                        output.accept(ModItems.BLUNT.get());
                        output.accept(ModItems.BONG.get());
                        output.accept(ModItems.DAB_RIG.get());

                        // Food.
                        output.accept(ModItems.BUTTER.get());
                        output.accept(ModItems.INFUSED_BUTTER.get());
                        output.accept(ModItems.HERB_CAKE.get());
                        output.accept(ModItems.HASH_BROWNIE.get());
                        output.accept(ModItems.WEED_COOKIE.get());

                        // Fluids.
                        output.accept(ModFluids.HASH_OIL_BUCKET.get());
                        output.accept(ModFluids.HASH_OIL_SLUDGE_BUCKET.get());

                        // Nutrients & soil amendments.
                        output.accept(ModItems.WORM_CASTINGS.get());
                        output.accept(ModItems.COMPOST.get());
                        output.accept(ModItems.MYCORRHIZAE.get());
                        output.accept(ModItems.DOLOMITE_LIME.get());
                        output.accept(ModItems.BLOOD_MEAL.get());
                        output.accept(ModItems.PHOSPHORUS_POWDER.get());
                        output.accept(ModItems.BAT_GUANO.get());
                        output.accept(ModItems.KELP_MEAL.get());
                        output.accept(ModItems.WOOD_ASH.get());
                        output.accept(ModItems.FISH_EMULSION.get());
                        output.accept(ModItems.BLOOM_BOOSTER.get());
                        output.accept(ModItems.FRUIT_FINISHER.get());
                        output.accept(ModItems.NITROGEN_BOOST.get());
                        output.accept(ModItems.POTASH_BOOST.get());
                        output.accept(ModItems.BALANCED_BOOST.get());
                        output.accept(ModItems.PHOSPHORUS_REDUCER.get());
                        output.accept(ModItems.POTASSIUM_REDUCER.get());
                    }).build());

    public static final Supplier<CreativeModeTab> SMOKELEAF_HERB_TAB = CREATIVE_MODE_TAB.register("smokeleaf_herb_tab",
            () -> CreativeModeTab.builder().icon(() -> strainedStack(ModItems.GENERIC_BUD.get(), "amnesia_haze"))
                    .title(Component.translatable("creativetab.smokeleafindustries.smokeleaf_herb_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.TOBACCO_SEEDS.get());
                        output.accept(ModItems.HEMP_SEEDS.get());

                        // Unidentified items
//                        output.accept(ModItems.UNIDENTIFIED_SEEDS.get());
//                        output.accept(ModItems.UNIDENTIFIED_BUD.get());
//                        output.accept(ModItems.UNIDENTIFIED_WEED.get());

                        // All 25 named strains, grouped by item type rather than by strain:
                        // every seed, then every bud, then every weed, and so on.
                        addItemForAllStrains(output, ModItems.GENERIC_SEEDS.get());
                        addItemForAllStrains(output, ModItems.GENERIC_BUD.get());
                        addItemForAllStrains(output, ModItems.GENERIC_WEED.get());
                        addItemForAllStrains(output, ModItems.GENERIC_EXTRACT.get());
                        addItemForAllStrains(output, ModItems.GENERIC_BAG.get());
                        addItemForAllStrains(output, ModItems.GENERIC_GUMMY.get());
                        addItemForAllStrains(output, ModItems.GENERIC_GUMMY_WORM.get());
                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

}


