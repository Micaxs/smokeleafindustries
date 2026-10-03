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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SmokeleafIndustries.MODID);

    /** Creates a GENERIC item stack carrying the preset StrainData for the given strain id. */
    private static ItemStack strainedStack(Item item, String strainId) {
        StrainData data = StrainRegistry.get(strainId).orElse(null);
        if (data == null) return new ItemStack(item);
        ItemStack stack = new ItemStack(item);
        stack.set(ModDataComponentTypes.STRAIN_DATA.get(), data);
        stack.set(ModDataComponentTypes.STRAIN_ID.get(), strainId);
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
                        output.accept(ModItems.HEMP_LEAF);
                        output.accept(ModItems.HEMP_FIBERS);
                        output.accept(ModItems.HEMP_STICK);
                        output.accept(ModItems.HEMP_FABRIC);
                        output.accept(ModItems.BIO_COMPOSITE);
                        output.accept(ModItems.HEMP_COAL);
                        output.accept(ModItems.HEMP_PLASTIC);
                        output.accept(ModItems.UNFINISHED_HEMP_CORE);
                        output.accept(ModItems.HEMP_CORE);

                        // Baja Hoodie armor (woven from Hemp Fabric) and its netherite-tier upgrade.
                        output.accept(ModItems.BAJA_HOODIE_HELMET);
                        output.accept(ModItems.BAJA_HOODIE_CHESTPLATE);
                        output.accept(ModItems.BAJA_HOODIE_LEGGINGS);
                        output.accept(ModItems.BAJA_HOODIE_BOOTS);
                        output.accept(ModItems.REINFORCED_BAJA_HOODIE_HELMET);
                        output.accept(ModItems.REINFORCED_BAJA_HOODIE_CHESTPLATE);
                        output.accept(ModItems.REINFORCED_BAJA_HOODIE_LEGGINGS);
                        output.accept(ModItems.REINFORCED_BAJA_HOODIE_BOOTS);

                        // Building blocks, one family at a time (base block, then its variants).
                        output.accept(ModBlocks.HEMP_STONE);
                        output.accept(ModBlocks.HEMP_STONE_SLAB);
                        output.accept(ModBlocks.HEMP_STONE_STAIRS);
                        output.accept(ModBlocks.HEMP_STONE_PRESSURE_PLATE);
                        output.accept(ModBlocks.HEMP_STONE_BUTTON);
                        output.accept(ModBlocks.HEMP_STONE_WALL);

                        output.accept(ModBlocks.HEMP_PLANKS);
                        output.accept(ModBlocks.HEMP_PLANK_SLAB);
                        output.accept(ModBlocks.HEMP_PLANK_STAIRS);
                        output.accept(ModBlocks.HEMP_PLANK_PRESSURE_PLATE);
                        output.accept(ModBlocks.HEMP_PLANK_BUTTON);
                        output.accept(ModBlocks.HEMP_PLANK_FENCE);
                        output.accept(ModBlocks.HEMP_PLANK_FENCE_GATE);
                        output.accept(ModBlocks.HEMP_PLANK_DOOR);
                        output.accept(ModBlocks.HEMP_PLANK_TRAPDOOR);

                        output.accept(ModBlocks.HEMP_BRICKS);
                        output.accept(ModBlocks.HEMP_BRICK_SLAB);
                        output.accept(ModBlocks.HEMP_BRICK_STAIRS);
                        output.accept(ModBlocks.HEMP_BRICK_WALL);

                        output.accept(ModBlocks.HEMP_CHISELED_STONE);
                        output.accept(ModBlocks.HEMP_CHISELED_STONE_SLAB);
                        output.accept(ModBlocks.HEMP_CHISELED_STONE_STAIRS);
                        output.accept(ModBlocks.HEMP_CHISELED_STONE_WALL);

                        for (DyeColor color : DyeColor.values()) {
                            output.accept(ModBlocks.HEMP_WOOL.get(color));
                        }

                        // Tobacco.
                        output.accept(ModItems.TOBACCO);
                        output.accept(ModItems.TOBACCO_LEAF);
                        output.accept(ModItems.DRIED_TOBACCO_LEAF);

                        // Lighting & grow equipment.
                        output.accept(ModBlocks.REFLECTOR);
                        output.accept(ModItems.HPS_LAMP);
                        output.accept(ModItems.DUAL_ARC_LAMP);
                        output.accept(ModBlocks.LED_LIGHT);
                        output.accept(ModBlocks.GROW_POT);

                        // Machines, in roughly the order they're used along the processing chain.
                        output.accept(ModBlocks.GENERATOR);
                        output.accept(ModBlocks.GRINDER);
                        output.accept(ModBlocks.EXTRACTOR);
                        output.accept(ModBlocks.LIQUIFIER);
                        output.accept(ModBlocks.MIXER);
                        output.accept(ModBlocks.MUTATOR);
                        output.accept(ModBlocks.STRAIN_MODIFIER);
                        output.accept(ModBlocks.SYNTHESIZER);
                        output.accept(ModBlocks.SEQUENCER);
                        output.accept(ModBlocks.DRYER);
                        output.accept(ModBlocks.DRYING_RACK);
                        output.accept(ModBlocks.GUMMY_MACHINE);

                        // Pipes & wiring tools.
                        output.accept(ModItems.ITEM_PIPE);
                        output.accept(ModItems.FLUID_PIPE);
                        output.accept(ModItems.ENERGY_PIPE);
                        output.accept(ModItems.PIPE_WRENCH);

                        // Hand tools & reference books.
                        output.accept(ModItems.HEMP_HAMMER);
                        output.accept(ModItems.MANUAL_GRINDER);
                        output.accept(ModItems.PLANT_ANALYZER);
                        output.accept(ModItems.STRAIN_BOOK);
                        output.accept(ModItems.SMOKELEAF_GUIDE);

                        // Crafting reagents.
                        output.accept(ModItems.BASE_EXTRACT);
                        output.accept(ModItems.DNA_STRAND);
                        output.accept(ModItems.GUMMY_MOLD);
                        output.accept(ModItems.GUMMY_WORM_MOLD);
                        output.accept(ModItems.EMPTY_BAG);
                        output.accept(ModItems.EMPTY_VIAL);

                        // Smoking accessories.
                        output.accept(ModItems.JOINT);
                        output.accept(ModItems.BLUNT);
                        output.accept(ModItems.BONG);
                        output.accept(ModItems.DAB_RIG);

                        // Food.
                        output.accept(ModItems.BUTTER);
                        output.accept(ModItems.INFUSED_BUTTER);
                        output.accept(ModItems.HERB_CAKE);
                        output.accept(ModItems.HASH_BROWNIE);
                        output.accept(ModItems.WEED_COOKIE);

                        // Fluids.
                        output.accept(ModFluids.HASH_OIL_BUCKET);
                        output.accept(ModFluids.HASH_OIL_SLUDGE_BUCKET);

                        // Nutrients & soil amendments.
                        output.accept(ModItems.WORM_CASTINGS);
                        output.accept(ModItems.COMPOST);
                        output.accept(ModItems.MYCORRHIZAE);
                        output.accept(ModItems.DOLOMITE_LIME);
                        output.accept(ModItems.BLOOD_MEAL);
                        output.accept(ModItems.PHOSPHORUS_POWDER);
                        output.accept(ModItems.BAT_GUANO);
                        output.accept(ModItems.KELP_MEAL);
                        output.accept(ModItems.WOOD_ASH);
                        output.accept(ModItems.FISH_EMULSION);
                        output.accept(ModItems.BLOOM_BOOSTER);
                        output.accept(ModItems.FRUIT_FINISHER);
                        output.accept(ModItems.NITROGEN_BOOST);
                        output.accept(ModItems.POTASH_BOOST);
                        output.accept(ModItems.BALANCED_BOOST);
                        output.accept(ModItems.PHOSPHORUS_REDUCER);
                        output.accept(ModItems.POTASSIUM_REDUCER);
                    }).build());

    public static final Supplier<CreativeModeTab> SMOKELEAF_HERB_TAB = CREATIVE_MODE_TAB.register("smokeleaf_herb_tab",
            () -> CreativeModeTab.builder().icon(() -> strainedStack(ModItems.GENERIC_BUD.get(), "amnesia_haze"))
                    .title(Component.translatable("creativetab.smokeleafindustries.smokeleaf_herb_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.TOBACCO_SEEDS);
                        output.accept(ModItems.HEMP_SEEDS);

                        // Unidentified items
//                        output.accept(ModItems.UNIDENTIFIED_SEEDS);
//                        output.accept(ModItems.UNIDENTIFIED_BUD);
//                        output.accept(ModItems.UNIDENTIFIED_WEED);

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


