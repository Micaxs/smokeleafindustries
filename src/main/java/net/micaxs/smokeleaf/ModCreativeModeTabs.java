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
        return stack;
    }

    /** Adds seeds, bud, weed, and extract for the given strain to the output. */
    private static void addStrain(CreativeModeTab.Output output, String strainId) {
        output.accept(strainedStack(ModItems.GENERIC_SEEDS.get(), strainId));
        output.accept(strainedStack(ModItems.GENERIC_BUD.get(), strainId));
        output.accept(strainedStack(ModItems.GENERIC_WEED.get(), strainId));
        output.accept(strainedStack(ModItems.GENERIC_EXTRACT.get(), strainId));
    }


    public static final Supplier<CreativeModeTab> SMOKELEAF_ITEMS_TAB = CREATIVE_MODE_TAB.register("smokeleaf_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.HEMP_CORE.get()))
                    .title(Component.translatable("creativetab.smokeleafindustries.smokeleaf_items_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.UNFINISHED_HEMP_CORE);
                        output.accept(ModItems.HEMP_CORE);

                        output.accept(ModItems.HEMP_LEAF);
                        output.accept(ModItems.HEMP_FIBERS);
                        output.accept(ModItems.HEMP_STICK);
                        output.accept(ModItems.HEMP_FABRIC);
                        output.accept(ModItems.BIO_COMPOSITE);
                        output.accept(ModItems.HEMP_COAL);
                        output.accept(ModItems.HEMP_PLASTIC);

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

                        output.accept(ModItems.TOBACCO);
                        output.accept(ModItems.TOBACCO_LEAF);
                        output.accept(ModItems.DRIED_TOBACCO_LEAF);

                        output.accept(ModBlocks.REFLECTOR);
                        output.accept(ModItems.HPS_LAMP);
                        output.accept(ModItems.DUAL_ARC_LAMP);
                        output.accept(ModBlocks.LED_LIGHT);
                        output.accept(ModBlocks.GROW_POT);

                        output.accept(ModItems.HEMP_HAMMER);
                        output.accept(ModItems.PLANT_ANALYZER);
                        output.accept(ModItems.MANUAL_GRINDER);
                        output.accept(ModItems.EMPTY_BAG);
                        output.accept(ModItems.JOINT);
                        output.accept(ModItems.BLUNT);
                        output.accept(ModItems.BONG);
                        output.accept(ModItems.DAB_RIG);
                        output.accept(ModItems.BASE_EXTRACT);
                        output.accept(ModItems.DNA_STRAND);

                        output.accept(ModItems.BUTTER);
                        output.accept(ModItems.INFUSED_BUTTER);
                        output.accept(ModItems.HERB_CAKE);
                        output.accept(ModItems.HASH_BROWNIE);
                        output.accept(ModItems.WEED_COOKIE);

                        output.accept(ModFluids.HEMP_OIL_BUCKET);
                        output.accept(ModFluids.HASH_OIL_BUCKET);
                        output.accept(ModFluids.HASH_OIL_SLUDGE_BUCKET);

                        output.accept(ModBlocks.GENERATOR);
                        output.accept(ModBlocks.GRINDER);
                        output.accept(ModBlocks.EXTRACTOR);
                        output.accept(ModBlocks.LIQUIFIER);
                        output.accept(ModBlocks.DRYER);
                        output.accept(ModBlocks.MUTATOR);
                        output.accept(ModBlocks.SYNTHESIZER);
                        output.accept(ModBlocks.SEQUENCER);
                        output.accept(ModBlocks.DRYING_RACK);

                        output.accept(ModItems.WORM_CASTINGS);
                        output.accept(ModItems.COMPOST);
                        output.accept(ModItems.MYCORRHIZAE);
                        output.accept(ModItems.DOLOMITE_LIME);
                        output.accept(ModItems.BLOOD_MEAL);
                        output.accept(ModItems.PHOSPHORUS_POWDER);
                        output.accept(ModItems.BAT_GUANO);
                        output.accept(ModItems.KELP_MEAL);
                        output.accept(ModItems.WOOD_ASH);
                        output.accept(ModItems.EMPTY_VIAL);
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
                        output.accept(ModItems.UNIDENTIFIED_SEEDS);
                        output.accept(ModItems.UNIDENTIFIED_BUD);
                        output.accept(ModItems.UNIDENTIFIED_WEED);

                        // All 25 named strains — seeds, bud, weed, extract grouped by strain
                        addStrain(output, "white_widow");
                        addStrain(output, "bubble_kush");
                        addStrain(output, "lemon_haze");
                        addStrain(output, "sour_diesel");
                        addStrain(output, "blue_ice");
                        addStrain(output, "bubblegum");
                        addStrain(output, "purple_haze");
                        addStrain(output, "og_kush");
                        addStrain(output, "jack_herer");
                        addStrain(output, "gary_peyton");
                        addStrain(output, "amnesia_haze");
                        addStrain(output, "ak47");
                        addStrain(output, "ghost_train");
                        addStrain(output, "grape_ape");
                        addStrain(output, "cotton_candy");
                        addStrain(output, "banana_kush");
                        addStrain(output, "carbon_fiber");
                        addStrain(output, "birthday_cake");
                        addStrain(output, "blue_cookies");
                        addStrain(output, "afghani");
                        addStrain(output, "moonbow");
                        addStrain(output, "lava_cake");
                        addStrain(output, "jelly_rancher");
                        addStrain(output, "strawberry_shortcake");
                        addStrain(output, "pink_kush");

                        // Bags and gummies retain per-strain items (own textures)
                        output.accept(ModItems.WHITE_WIDOW_BAG);
                        output.accept(ModItems.BUBBLE_KUSH_BAG);
                        output.accept(ModItems.LEMON_HAZE_BAG);
                        output.accept(ModItems.SOUR_DIESEL_BAG);
                        output.accept(ModItems.BLUE_ICE_BAG);
                        output.accept(ModItems.BUBBLEGUM_BAG);
                        output.accept(ModItems.PURPLE_HAZE_BAG);
                        output.accept(ModItems.OG_KUSH_BAG);
                        output.accept(ModItems.JACK_HERER_BAG);
                        output.accept(ModItems.GARY_PEYTON_BAG);
                        output.accept(ModItems.AMNESIA_HAZE_BAG);
                        output.accept(ModItems.AK47_BAG);
                        output.accept(ModItems.GHOST_TRAIN_BAG);
                        output.accept(ModItems.GRAPE_APE_BAG);
                        output.accept(ModItems.COTTON_CANDY_BAG);
                        output.accept(ModItems.BANANA_KUSH_BAG);
                        output.accept(ModItems.CARBON_FIBER_BAG);
                        output.accept(ModItems.BIRTHDAY_CAKE_BAG);
                        output.accept(ModItems.BLUE_COOKIES_BAG);
                        output.accept(ModItems.AFGHANI_BAG);
                        output.accept(ModItems.MOONBOW_BAG);
                        output.accept(ModItems.LAVA_CAKE_BAG);
                        output.accept(ModItems.JELLY_RANCHER_BAG);
                        output.accept(ModItems.STRAWBERRY_SHORTCAKE_BAG);
                        output.accept(ModItems.PINK_KUSH_BAG);

                        output.accept(ModItems.WHITE_WIDOW_GUMMY);
                        output.accept(ModItems.BUBBLE_KUSH_GUMMY);
                        output.accept(ModItems.SOUR_DIESEL_GUMMY);
                        output.accept(ModItems.PURPLE_HAZE_GUMMY);
                        output.accept(ModItems.LEMON_HAZE_GUMMY);
                        output.accept(ModItems.BLUE_ICE_GUMMY);
                        output.accept(ModItems.BUBBLEGUM_GUMMY);
                        output.accept(ModItems.OG_KUSH_GUMMY);
                        output.accept(ModItems.JACK_HERER_GUMMY);
                        output.accept(ModItems.GARY_PEYTON_GUMMY);
                        output.accept(ModItems.AMNESIA_HAZE_GUMMY);
                        output.accept(ModItems.AK47_GUMMY);
                        output.accept(ModItems.GHOST_TRAIN_GUMMY);
                        output.accept(ModItems.GRAPE_APE_GUMMY);
                        output.accept(ModItems.COTTON_CANDY_GUMMY);
                        output.accept(ModItems.BANANA_KUSH_GUMMY);
                        output.accept(ModItems.CARBON_FIBER_GUMMY);
                        output.accept(ModItems.BIRTHDAY_CAKE_GUMMY);
                        output.accept(ModItems.BLUE_COOKIES_GUMMY);
                        output.accept(ModItems.AFGHANI_GUMMY);
                        output.accept(ModItems.MOONBOW_GUMMY);
                        output.accept(ModItems.LAVA_CAKE_GUMMY);
                        output.accept(ModItems.JELLY_RANCHER_GUMMY);
                        output.accept(ModItems.STRAWBERRY_SHORTCAKE_GUMMY);
                        output.accept(ModItems.PINK_KUSH_GUMMY);
                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

}


