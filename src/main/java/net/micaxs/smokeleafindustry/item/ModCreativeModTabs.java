package net.micaxs.smokeleafindustry.item;

import net.micaxs.smokeleafindustry.SmokeleafIndustryMod;
import net.micaxs.smokeleafindustry.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SmokeleafIndustryMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SMOKELEAF_INDUSTRY_TAB = CREATIVE_MODE_TABS.register("smokeleaf_industry_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WHITE_WIDOW_BUD.get()))
                    .title(Component.translatable("creativetab.smokeleaf_industry_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        // Buds
                        pOutput.accept(ModItems.WHITE_WIDOW_BUD.get());
                        pOutput.accept(ModItems.BUBBLE_KUSH_BUD.get());
                        pOutput.accept(ModItems.LEMON_HAZE_BUD.get());
                        pOutput.accept(ModItems.SOUR_DIESEL_BUD.get());
                        pOutput.accept(ModItems.BLUE_ICE_BUD.get());
                        pOutput.accept(ModItems.BUBBLEGUM_BUD.get());
                        pOutput.accept(ModItems.PURPLE_HAZE_BUD.get());

                        // Weed
                        pOutput.accept(ModItems.WHITE_WIDOW_WEED.get());
                        pOutput.accept(ModItems.BUBBLE_KUSH_WEED.get());
                        pOutput.accept(ModItems.LEMON_HAZE_WEED.get());
                        pOutput.accept(ModItems.SOUR_DIESEL_WEED.get());
                        pOutput.accept(ModItems.BLUE_ICE_WEED.get());
                        pOutput.accept(ModItems.BUBBLEGUM_WEED.get());
                        pOutput.accept(ModItems.PURPLE_HAZE_WEED.get());

                        // Seeds
                        pOutput.accept(ModItems.WHITE_WIDOW_SEEDS.get());
                        pOutput.accept(ModItems.BUBBLE_KUSH_SEEDS.get());
                        pOutput.accept(ModItems.LEMON_HAZE_SEEDS.get());
                        pOutput.accept(ModItems.SOUR_DIESEL_SEEDS.get());
                        pOutput.accept(ModItems.BLUE_ICE_SEEDS.get());
                        pOutput.accept(ModItems.BUBBLEGUM_SEEDS.get());
                        pOutput.accept(ModItems.PURPLE_HAZE_SEEDS.get());

                        // Extracts
                        pOutput.accept(ModItems.PURPLE_HAZE_EXTRACT.get());
                        pOutput.accept(ModItems.BLUE_ICE_EXTRACT.get());
                        pOutput.accept(ModItems.BUBBLE_KUSH_EXTRACT.get());
                        pOutput.accept(ModItems.BUBBLEGUM_EXTRACT.get());
                        pOutput.accept(ModItems.LEMON_HAZE_EXTRACT.get());
                        pOutput.accept(ModItems.SOUR_DIESEL_EXTRACT.get());
                        pOutput.accept(ModItems.WHITE_WIDOW_EXTRACT.get());

                        // Baggies
                        pOutput.accept(ModItems.EMPTY_BAG.get());
                        pOutput.accept(ModItems.WHITE_WIDOW_BAG.get());
                        pOutput.accept(ModItems.BUBBLE_KUSH_BAG.get());
                        pOutput.accept(ModItems.LEMON_HAZE_BAG.get());
                        pOutput.accept(ModItems.SOUR_DIESEL_BAG.get());
                        pOutput.accept(ModItems.BLUE_ICE_BAG.get());
                        pOutput.accept(ModItems.BUBBLEGUM_BAG.get());
                        pOutput.accept(ModItems.PURPLE_HAZE_BAG.get());

                        // Other Items
                        pOutput.accept(ModItems.HEMP_LEAF.get());
                        pOutput.accept(ModItems.HEMP_FIBERS.get());
                        pOutput.accept(ModItems.HEMP_FABRIC.get());

                        pOutput.accept(ModItems.HEMP_HELMET_RED.get());
                        pOutput.accept(ModItems.HEMP_CHESTPLATE_RED.get());
                        pOutput.accept(ModItems.HEMP_LEGGINGS_RED.get());
                        pOutput.accept(ModItems.HEMP_BOOTS_RED.get());

                        pOutput.accept(ModItems.HEMP_HELMET_GREEN.get());
                        pOutput.accept(ModItems.HEMP_CHESTPLATE_GREEN.get());
                        pOutput.accept(ModItems.HEMP_LEGGINGS_GREEN.get());
                        pOutput.accept(ModItems.HEMP_BOOTS_GREEN.get());

                        pOutput.accept(ModItems.HEMP_HELMET_YELLOW.get());
                        pOutput.accept(ModItems.HEMP_CHESTPLATE_YELLOW.get());
                        pOutput.accept(ModItems.HEMP_LEGGINGS_YELLOW.get());
                        pOutput.accept(ModItems.HEMP_BOOTS_YELLOW.get());

                        pOutput.accept(ModItems.HASH_OIL_BUCKET.get());
                        pOutput.accept(ModItems.BONG.get());
                        pOutput.accept(ModItems.DAB_RIG.get());
                        pOutput.accept(ModItems.BLUNT.get());
                        pOutput.accept(ModItems.JOINT.get());
                        pOutput.accept(ModItems.GRINDER.get());
                        pOutput.accept(ModItems.HERB_CAKE.get());
                        pOutput.accept(ModItems.HASH_BROWNIE.get());
                        pOutput.accept(ModItems.WEED_COOKIE.get());

                        pOutput.accept(ModItems.WHITE_WIDOW_GUMMY.get());
                        pOutput.accept(ModItems.SOUR_DIESEL_GUMMY.get());
                        pOutput.accept(ModItems.PURPLE_HAZE_GUMMY.get());
                        pOutput.accept(ModItems.LEMON_HAZE_GUMMY.get());
                        pOutput.accept(ModItems.BUBBLE_KUSH_GUMMY.get());
                        pOutput.accept(ModItems.BLUE_ICE_GUMMY.get());
                        pOutput.accept(ModItems.BUBBLEGUM_GUMMY.get());

                        pOutput.accept(ModBlocks.HEMP_STONE.get());
                        pOutput.accept(ModBlocks.HEMP_PLANKS.get());
                        pOutput.accept(ModBlocks.HEMP_WOOL.get());

                        pOutput.accept(ModItems.HEMP_STICK.get());
                        pOutput.accept(ModItems.HEMP_CORE.get());

                        pOutput.accept(ModBlocks.HEMP_MACHINE_BLOCK.get());
                        pOutput.accept(ModBlocks.HERB_GENERATOR.get());
                        pOutput.accept(ModBlocks.HERB_GRINDER_STATION.get());
                        pOutput.accept(ModBlocks.HERB_EXTRACTOR.get());
                        pOutput.accept(ModBlocks.HERB_MUTATION.get());
                        pOutput.accept(ModBlocks.HERB_EVAPORATOR.get());
                        pOutput.accept(ModBlocks.HEMP_SPINNER.get());
                        pOutput.accept(ModBlocks.HEMP_WEAVER.get());

                        pOutput.accept(ModBlocks.GROW_LIGHT.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
