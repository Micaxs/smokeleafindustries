package net.micaxs.smokeleaf.item;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.effect.ModEffects;
import net.micaxs.smokeleaf.item.custom.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.UseAnim;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SmokeleafIndustries.MODID);

    // Tobacco
    public static final DeferredItem<Item> TOBACCO = ITEMS.register("tobacco",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item>  TOBACCO_SEEDS = ITEMS.register("tobacco_seeds",
            () -> new ItemNameBlockItem(ModBlocks.TOBACCO_CROP.get(), new Item.Properties()));
    public static final DeferredItem<Item> TOBACCO_LEAF = ITEMS.register("tobacco_leaves",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DRIED_TOBACCO_LEAF = ITEMS.register("dried_tobacco_leaves",  () -> new Item(new Item.Properties()));



    // Generic strain items (Option C / full unification skeleton)
    public static final DeferredItem<Item> GENERIC_SEEDS = ITEMS.register("seeds",
            () -> new UnidentifiedSeedsItem(new Item.Properties()));
    public static final DeferredItem<Item> GENERIC_BUD = ITEMS.register("bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1200));
    public static final DeferredItem<Item> GENERIC_WEED = ITEMS.register("weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.WIND_CHARGED.value(), 200, 1, 15, 10, true));
    public static final DeferredItem<Item> GENERIC_EXTRACT = ITEMS.register("extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.WIND_CHARGED.value(), 400, 2, 15, 10, false));
    public static final DeferredItem<Item> GENERIC_BAG = ITEMS.register("bag",
            () -> new GenericBagItem(new Item.Properties().stacksTo(64)));

    // Deprecated named strain seeds. Superseded by GENERIC_SEEDS + STRAIN_DATA and kept for world compatibility.
    public static final DeferredItem<Item>  HEMP_SEEDS = ITEMS.register("hemp_seeds",
            () -> new ItemNameBlockItem(ModBlocks.HEMP_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item>  WHITE_WIDOW_SEEDS = ITEMS.register("white_widow_seeds",
            () -> new ItemNameBlockItem(ModBlocks.WHITE_WIDOW_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item>  BUBBLE_KUSH_SEEDS = ITEMS.register("bubble_kush_seeds",
            () -> new ItemNameBlockItem(ModBlocks.BUBBLE_KUSH_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> LEMON_HAZE_SEEDS = ITEMS.register("lemon_haze_seeds",
            () -> new ItemNameBlockItem(ModBlocks.LEMON_HAZE_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> SOUR_DIESEL_SEEDS = ITEMS.register("sour_diesel_seeds",
            () -> new ItemNameBlockItem(ModBlocks.SOUR_DIESEL_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> BLUE_ICE_SEEDS = ITEMS.register("blue_ice_seeds",
            () -> new ItemNameBlockItem(ModBlocks.BLUE_ICE_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> BUBBLEGUM_SEEDS = ITEMS.register("bubblegum_seeds",
            () -> new ItemNameBlockItem(ModBlocks.BUBBLEGUM_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> PURPLE_HAZE_SEEDS = ITEMS.register("purple_haze_seeds",
            () -> new ItemNameBlockItem(ModBlocks.PURPLE_HAZE_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> OG_KUSH_SEEDS = ITEMS.register("og_kush_seeds",
            () -> new ItemNameBlockItem(ModBlocks.OG_KUSH_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> JACK_HERER_SEEDS = ITEMS.register("jack_herer_seeds",
            () -> new ItemNameBlockItem(ModBlocks.JACK_HERER_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> GARY_PEYTON_SEEDS = ITEMS.register("gary_peyton_seeds",
            () -> new ItemNameBlockItem(ModBlocks.GARY_PEYTON_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> AMNESIA_HAZE_SEEDS = ITEMS.register("amnesia_haze_seeds",
            () -> new ItemNameBlockItem(ModBlocks.AMNESIA_HAZE_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> AK47_SEEDS = ITEMS.register("ak47_seeds",
            () -> new ItemNameBlockItem(ModBlocks.AK47_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> GHOST_TRAIN_SEEDS = ITEMS.register("ghost_train_seeds",
            () -> new ItemNameBlockItem(ModBlocks.GHOST_TRAIN_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> GRAPE_APE_SEEDS = ITEMS.register("grape_ape_seeds",
            () -> new ItemNameBlockItem(ModBlocks.GRAPE_APE_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> COTTON_CANDY_SEEDS = ITEMS.register("cotton_candy_seeds",
            () -> new ItemNameBlockItem(ModBlocks.COTTON_CANDY_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> BANANA_KUSH_SEEDS = ITEMS.register("banana_kush_seeds",
            () -> new ItemNameBlockItem(ModBlocks.BANANA_KUSH_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> CARBON_FIBER_SEEDS = ITEMS.register("carbon_fiber_seeds",
            () -> new ItemNameBlockItem(ModBlocks.CARBON_FIBER_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> BIRTHDAY_CAKE_SEEDS = ITEMS.register("birthday_cake_seeds",
            () -> new ItemNameBlockItem(ModBlocks.BIRTHDAY_CAKE_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> BLUE_COOKIES_SEEDS = ITEMS.register("blue_cookies_seeds",
            () -> new ItemNameBlockItem(ModBlocks.BLUE_COOKIES_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> AFGHANI_SEEDS = ITEMS.register("afghani_seeds",
            () -> new ItemNameBlockItem(ModBlocks.AFGHANI_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> MOONBOW_SEEDS = ITEMS.register("moonbow_seeds",
            () -> new ItemNameBlockItem(ModBlocks.MOONBOW_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> LAVA_CAKE_SEEDS = ITEMS.register("lava_cake_seeds",
            () -> new ItemNameBlockItem(ModBlocks.LAVA_CAKE_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> JELLY_RANCHER_SEEDS = ITEMS.register("jelly_rancher_seeds",
            () -> new ItemNameBlockItem(ModBlocks.JELLY_RANCHER_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> STRAWBERRY_SHORTCAKE_SEEDS = ITEMS.register("strawberry_shortcake_seeds",
            () -> new ItemNameBlockItem(ModBlocks.STRAWBERRY_SHORTCAKE_CROP.get(), new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> PINK_KUSH_SEEDS = ITEMS.register("pink_kush_seeds",
            () -> new ItemNameBlockItem(ModBlocks.PINK_KUSH_CROP.get(), new Item.Properties()));


    // Deprecated named strain buds. Superseded by GENERIC_BUD + STRAIN_DATA and kept for world compatibility.
    @Deprecated
    public static final DeferredItem<Item> WHITE_WIDOW_BUD = ITEMS.register("white_widow_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1200));
    @Deprecated
    public static final DeferredItem<Item> BUBBLE_KUSH_BUD = ITEMS.register("bubble_kush_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1000));
    @Deprecated
    public static final DeferredItem<Item> LEMON_HAZE_BUD = ITEMS.register("lemon_haze_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 800));
    @Deprecated
    public static final DeferredItem<Item> SOUR_DIESEL_BUD = ITEMS.register("sour_diesel_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 900));
    @Deprecated
    public static final DeferredItem<Item> BLUE_ICE_BUD = ITEMS.register("blue_ice_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1100));
    @Deprecated
    public static final DeferredItem<Item> BUBBLEGUM_BUD = ITEMS.register("bubblegum_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 700));
    @Deprecated
    public static final DeferredItem<Item> PURPLE_HAZE_BUD = ITEMS.register("purple_haze_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 600));
    @Deprecated
    public static final DeferredItem<Item> OG_KUSH_BUD = ITEMS.register("og_kush_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1300));
    @Deprecated
    public static final DeferredItem<Item> JACK_HERER_BUD = ITEMS.register("jack_herer_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1250));
    @Deprecated
    public static final DeferredItem<Item> GARY_PEYTON_BUD = ITEMS.register("gary_peyton_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1150));
    @Deprecated
    public static final DeferredItem<Item> AMNESIA_HAZE_BUD = ITEMS.register("amnesia_haze_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1050));
    @Deprecated
    public static final DeferredItem<Item> AK47_BUD = ITEMS.register("ak47_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1100));
    @Deprecated
    public static final DeferredItem<Item> GHOST_TRAIN_BUD = ITEMS.register("ghost_train_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1400));
    @Deprecated
    public static final DeferredItem<Item> GRAPE_APE_BUD = ITEMS.register("grape_ape_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 950));
    @Deprecated
    public static final DeferredItem<Item> COTTON_CANDY_BUD = ITEMS.register("cotton_candy_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 850));
    @Deprecated
    public static final DeferredItem<Item> BANANA_KUSH_BUD = ITEMS.register("banana_kush_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1200));
    @Deprecated
    public static final DeferredItem<Item> CARBON_FIBER_BUD = ITEMS.register("carbon_fiber_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1500));
    @Deprecated
    public static final DeferredItem<Item> BIRTHDAY_CAKE_BUD = ITEMS.register("birthday_cake_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 900));
    @Deprecated
    public static final DeferredItem<Item> BLUE_COOKIES_BUD = ITEMS.register("blue_cookies_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1000));
    @Deprecated
    public static final DeferredItem<Item> AFGHANI_BUD = ITEMS.register("afghani_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1600));
    @Deprecated
    public static final DeferredItem<Item> MOONBOW_BUD = ITEMS.register("moonbow_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1350));
    @Deprecated
    public static final DeferredItem<Item> LAVA_CAKE_BUD = ITEMS.register("lava_cake_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 950));
    @Deprecated
    public static final DeferredItem<Item> JELLY_RANCHER_BUD = ITEMS.register("jelly_rancher_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 850));
    @Deprecated
    public static final DeferredItem<Item> STRAWBERRY_SHORTCAKE_BUD = ITEMS.register("strawberry_shortcake_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 800));
    @Deprecated
    public static final DeferredItem<Item> PINK_KUSH_BUD = ITEMS.register("pink_kush_bud",
            () -> new BaseBudItem(new Item.Properties(), 0, 1250));




    // Deprecated named strain weeds. Superseded by GENERIC_WEED + STRAIN_DATA and kept for world compatibility.
    @Deprecated
    public static final DeferredItem<Item> WHITE_WIDOW_WEED = ITEMS.register("white_widow_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.WIND_CHARGED.value(), 200, 1, 15, 10,
                    "White", "Widow"));
    @Deprecated
    public static final DeferredItem<Item> BUBBLE_KUSH_WEED = ITEMS.register("bubble_kush_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.DAMAGE_BOOST.value(), 180, 1, 20, 5,
                    "Bubble", "Kush"));
    @Deprecated
    public static final DeferredItem<Item> LEMON_HAZE_WEED = ITEMS.register("lemon_haze_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.MOVEMENT_SPEED.value(), 160, 1, 19, 6,
                    "Lemon", "Haze"));
    @Deprecated
    public static final DeferredItem<Item> SOUR_DIESEL_WEED = ITEMS.register("sour_diesel_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.DIG_SPEED.value(), 170, 1, 19, 6,
                    "Sour", "Diesel"));
    @Deprecated
    public static final DeferredItem<Item> BLUE_ICE_WEED = ITEMS.register("blue_ice_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.NIGHT_VISION.value(), 190, 1, 20, 5,
                    "Blue", "Ice"));
    @Deprecated
    public static final DeferredItem<Item> BUBBLEGUM_WEED = ITEMS.register("bubblegum_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.HEALTH_BOOST.value(), 150, 1, 17, 8,
                    "Bubble", "Gum"));
    @Deprecated
    public static final DeferredItem<Item> PURPLE_HAZE_WEED = ITEMS.register("purple_haze_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.LUCK.value(), 140, 1, 16, 9,
                    "Purple", "Haze"));
    @Deprecated
    public static final DeferredItem<Item> OG_KUSH_WEED = ITEMS.register("og_kush_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.DAMAGE_RESISTANCE.value(), 210, 1, 25, 10,
                    "OG", "Kush"));
    @Deprecated
    public static final DeferredItem<Item> JACK_HERER_WEED = ITEMS.register("jack_herer_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.R_TREES.value(), 205, 1, 18, 7,
                    "Jack", "Herer"));
    @Deprecated
    public static final DeferredItem<Item> GARY_PEYTON_WEED = ITEMS.register("gary_peyton_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.UPLIFTED.value(), 195, 1, 22, 3,
                    "Gary", "Peyton"));
    @Deprecated
    public static final DeferredItem<Item> AMNESIA_HAZE_WEED = ITEMS.register("amnesia_haze_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.ZOMBIFIED.value(), 185, 1, 19, 6,
                    "Amnesia", "Haze"));
    @Deprecated
    public static final DeferredItem<Item> AK47_WEED = ITEMS.register("ak47_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.RELAXED.value(), 190, 1, 19, 6,
                    "AK", "47"));
    @Deprecated
    public static final DeferredItem<Item> GHOST_TRAIN_WEED = ITEMS.register("ghost_train_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.SHY.value(), 220, 1, 19, 6,
                    "Ghost", "Train"));
    @Deprecated
    public static final DeferredItem<Item> GRAPE_APE_WEED = ITEMS.register("grape_ape_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.AROUSED.value(), 175, 1, 18, 7,
                    "Grape", "Ape"));
    @Deprecated
    public static final DeferredItem<Item> COTTON_CANDY_WEED = ITEMS.register("cotton_candy_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.CHILLOUT.value(), 165, 1, 19, 6,
                    "Cotton", "Candy"));
    @Deprecated
    public static final DeferredItem<Item> BANANA_KUSH_WEED = ITEMS.register("banana_kush_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.STICKY_ICKY.value(), 200, 1, 21, 4,
                    "Banana", "Kush"));
    @Deprecated
    public static final DeferredItem<Item> CARBON_FIBER_WEED = ITEMS.register("carbon_fiber_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.VEIN_HIGH.value(), 230, 1, 24, 1,
                    "Carbon", "Fiber"));
    @Deprecated
    public static final DeferredItem<Item> BIRTHDAY_CAKE_WEED = ITEMS.register("birthday_cake_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.OOZING.value(), 170, 1, 23, 2,
                    "Birthday", "Cake"));
    @Deprecated
    public static final DeferredItem<Item> BLUE_COOKIES_WEED = ITEMS.register("blue_cookies_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.LINGUISTS_HIGH.value(), 180, 1, 17, 8,
                    "Blue", "Cookies"));
    @Deprecated
    public static final DeferredItem<Item> AFGHANI_WEED = ITEMS.register("afghani_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.BAD_OMEN.value(), 240, 1, 18, 7,
                    "Afghani", "Kush"));
    @Deprecated
    public static final DeferredItem<Item> MOONBOW_WEED = ITEMS.register("moonbow_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.NIGHT_VISION.value(), 215, 1, 30, 13,
                    "Moonbow", "Kush"));
    @Deprecated
    public static final DeferredItem<Item> LAVA_CAKE_WEED = ITEMS.register("lava_cake_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.GLOWING.value(), 175, 1, 22, 3,
                    "Lava", "Cake"));
    @Deprecated
    public static final DeferredItem<Item> JELLY_RANCHER_WEED = ITEMS.register("jelly_rancher_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.DOLPHINS_GRACE.value(), 165, 1, 20, 5,
                    "Jelly", "Rancher"));
    @Deprecated
    public static final DeferredItem<Item> STRAWBERRY_SHORTCAKE_WEED = ITEMS.register("strawberry_shortcake_weed",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.HIGH_FLYER.value(), 160, 1, 16, 9,
                    "Strawberry", "Shortcake"));
    @Deprecated
    public static final DeferredItem<Item> PINK_KUSH_WEED = ITEMS.register("pink_kush_weed",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.REGENERATION.value(), 205, 1, 19, 6,
                    "Pink", "Kush"));


    // Deprecated named strain extracts. Superseded by GENERIC_EXTRACT + STRAIN_DATA and kept for world compatibility.
    public static final DeferredItem<Item> BASE_EXTRACT = ITEMS.register("base_extract",  () -> new Item(new Item.Properties()));
    @Deprecated
    public static final DeferredItem<Item> WHITE_WIDOW_EXTRACT = ITEMS.register("white_widow_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.WIND_CHARGED.value(), 400, 2, 15, 10, false));
    @Deprecated
    public static final DeferredItem<Item> BUBBLE_KUSH_EXTRACT = ITEMS.register("bubble_kush_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.DAMAGE_BOOST.value(), 360, 2, 20, 5, false));
    @Deprecated
    public static final DeferredItem<Item> LEMON_HAZE_EXTRACT = ITEMS.register("lemon_haze_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.MOVEMENT_SPEED.value(), 320, 2, 19, 6, false));
    @Deprecated
    public static final DeferredItem<Item> SOUR_DIESEL_EXTRACT = ITEMS.register("sour_diesel_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.DIG_SPEED.value(), 340, 2, 19, 6, false));
    @Deprecated
    public static final DeferredItem<Item> BLUE_ICE_EXTRACT = ITEMS.register("blue_ice_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.NIGHT_VISION.value(), 380, 2, 20, 5, false));
    @Deprecated
    public static final DeferredItem<Item> BUBBLEGUM_EXTRACT = ITEMS.register("bubblegum_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.HEALTH_BOOST.value(), 300, 2, 17, 8, false));
    @Deprecated
    public static final DeferredItem<Item> PURPLE_HAZE_EXTRACT = ITEMS.register("purple_haze_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.LUCK.value(), 280, 2, 16, 9, false));
    @Deprecated
    public static final DeferredItem<Item> OG_KUSH_EXTRACT = ITEMS.register("og_kush_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.DAMAGE_RESISTANCE.value(), 420, 2, 25, 0, false));
    @Deprecated
    public static final DeferredItem<Item> JACK_HERER_EXTRACT = ITEMS.register("jack_herer_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.R_TREES.value(), 410, 2, 18, 7, false));
    @Deprecated
    public static final DeferredItem<Item> GARY_PEYTON_EXTRACT = ITEMS.register("gary_peyton_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.UPLIFTED.value(), 390, 2, 22, 3, false));
    @Deprecated
    public static final DeferredItem<Item> AMNESIA_HAZE_EXTRACT = ITEMS.register("amnesia_haze_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.ZOMBIFIED.value(), 370, 2, 19, 6, false));
    @Deprecated
    public static final DeferredItem<Item> AK47_EXTRACT = ITEMS.register("ak47_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.RELAXED.value(), 380, 2, 19, 6, false));
    @Deprecated
    public static final DeferredItem<Item> GHOST_TRAIN_EXTRACT = ITEMS.register("ghost_train_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.SHY.value(), 440, 2, 19, 6, false));
    @Deprecated
    public static final DeferredItem<Item> GRAPE_APE_EXTRACT = ITEMS.register("grape_ape_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.AROUSED.value(), 350, 2, 18, 7, false));
    @Deprecated
    public static final DeferredItem<Item> COTTON_CANDY_EXTRACT = ITEMS.register("cotton_candy_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.CHILLOUT.value(), 330, 2, 19, 6, false));
    @Deprecated
    public static final DeferredItem<Item> BANANA_KUSH_EXTRACT = ITEMS.register("banana_kush_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.STICKY_ICKY.value(), 400, 2, 21, 4, false));
    @Deprecated
    public static final DeferredItem<Item> CARBON_FIBER_EXTRACT = ITEMS.register("carbon_fiber_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.VEIN_HIGH.value(), 460, 2, 24, 1, false));
    @Deprecated
    public static final DeferredItem<Item> BIRTHDAY_CAKE_EXTRACT = ITEMS.register("birthday_cake_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.OOZING.value(), 340, 2, 23, 2, false));
    @Deprecated
    public static final DeferredItem<Item> BLUE_COOKIES_EXTRACT = ITEMS.register("blue_cookies_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.LINGUISTS_HIGH.value(), 360, 2, 17, 8, false));
    @Deprecated
    public static final DeferredItem<Item> AFGHANI_EXTRACT = ITEMS.register("afghani_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.BAD_OMEN.value(), 480, 2, 18, 7, false));
    @Deprecated
    public static final DeferredItem<Item> MOONBOW_EXTRACT = ITEMS.register("moonbow_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.NIGHT_VISION.value(), 430, 2, 30, 13, false));
    @Deprecated
    public static final DeferredItem<Item> LAVA_CAKE_EXTRACT = ITEMS.register("lava_cake_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.GLOWING.value(), 350, 2, 22, 3, false));
    @Deprecated
    public static final DeferredItem<Item> JELLY_RANCHER_EXTRACT = ITEMS.register("jelly_rancher_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.DOLPHINS_GRACE.value(), 330, 2, 20, 5, false));
    @Deprecated
    public static final DeferredItem<Item> STRAWBERRY_SHORTCAKE_EXTRACT = ITEMS.register("strawberry_shortcake_extract",
            () -> new BaseWeedItem(new Item.Properties(), ModEffects.HIGH_FLYER.value(), 320, 2, 16, 9, false));
    @Deprecated
    public static final DeferredItem<Item> PINK_KUSH_EXTRACT = ITEMS.register("pink_kush_extract",
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.REGENERATION.value(), 410, 2, 19, 6, false));


    // Bags
    public static final DeferredItem<Item> EMPTY_BAG = ITEMS.register("empty_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.empty_bag"));
    public static final DeferredItem<Item> WHITE_WIDOW_BAG = ITEMS.register("white_widow_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.white_widow_bag"));
    public static final DeferredItem<Item> BUBBLE_KUSH_BAG = ITEMS.register("bubble_kush_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.bubble_kush_bag"));
    public static final DeferredItem<Item> LEMON_HAZE_BAG = ITEMS.register("lemon_haze_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.lemon_haze_bag"));
    public static final DeferredItem<Item> SOUR_DIESEL_BAG = ITEMS.register("sour_diesel_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.sour_diesel_bag"));
    public static final DeferredItem<Item> BLUE_ICE_BAG = ITEMS.register("blue_ice_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.blue_ice_bag"));
    public static final DeferredItem<Item> BUBBLEGUM_BAG = ITEMS.register("bubblegum_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.bubblegum_bag"));
    public static final DeferredItem<Item> PURPLE_HAZE_BAG = ITEMS.register("purple_haze_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.purple_haze_bag"));
    public static final DeferredItem<Item> OG_KUSH_BAG = ITEMS.register("og_kush_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.og_kush_bag"));
    public static final DeferredItem<Item> JACK_HERER_BAG = ITEMS.register("jack_herer_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.jack_herer_bag"));
    public static final DeferredItem<Item> GARY_PEYTON_BAG = ITEMS.register("gary_peyton_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.gary_peyton_bag"));
    public static final DeferredItem<Item> AMNESIA_HAZE_BAG = ITEMS.register("amnesia_haze_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.amnesia_haze_bag"));
    public static final DeferredItem<Item> AK47_BAG = ITEMS.register("ak47_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.ak47_bag"));
    public static final DeferredItem<Item> GHOST_TRAIN_BAG = ITEMS.register("ghost_train_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.ghost_train_bag"));
    public static final DeferredItem<Item> GRAPE_APE_BAG = ITEMS.register("grape_ape_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.grape_ape_bag"));
    public static final DeferredItem<Item> COTTON_CANDY_BAG = ITEMS.register("cotton_candy_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.cotton_candy_bag"));
    public static final DeferredItem<Item> BANANA_KUSH_BAG = ITEMS.register("banana_kush_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.banana_kush_bag"));
    public static final DeferredItem<Item> CARBON_FIBER_BAG = ITEMS.register("carbon_fiber_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.carbon_fiber_bag"));
    public static final DeferredItem<Item> BIRTHDAY_CAKE_BAG = ITEMS.register("birthday_cake_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.birthday_cake_bag"));
    public static final DeferredItem<Item> BLUE_COOKIES_BAG = ITEMS.register("blue_cookies_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.blue_cookies_bag"));
    public static final DeferredItem<Item> AFGHANI_BAG = ITEMS.register("afghani_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.afghani_bag"));
    public static final DeferredItem<Item> MOONBOW_BAG = ITEMS.register("moonbow_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.moonbow_bag"));
    public static final DeferredItem<Item> LAVA_CAKE_BAG = ITEMS.register("lava_cake_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.lava_cake_bag"));
    public static final DeferredItem<Item> JELLY_RANCHER_BAG = ITEMS.register("jelly_rancher_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.jelly_rancher_bag"));
    public static final DeferredItem<Item> STRAWBERRY_SHORTCAKE_BAG = ITEMS.register("strawberry_shortcake_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.strawberry_shortcake_bag"));
    public static final DeferredItem<Item> PINK_KUSH_BAG = ITEMS.register("pink_kush_bag",
            () -> new BaseBagItem(new Item.Properties().stacksTo(64), "tooltip.smokeleafindustries.pink_kush_bag"));


    // Gummies
    public static final DeferredItem<Item> WHITE_WIDOW_GUMMY = ITEMS.register("white_widow_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> BUBBLE_KUSH_GUMMY = ITEMS.register("bubble_kush_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> LEMON_HAZE_GUMMY = ITEMS.register("lemon_haze_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> SOUR_DIESEL_GUMMY = ITEMS.register("sour_diesel_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> BLUE_ICE_GUMMY = ITEMS.register("blue_ice_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> BUBBLEGUM_GUMMY = ITEMS.register("bubblegum_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> PURPLE_HAZE_GUMMY = ITEMS.register("purple_haze_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> OG_KUSH_GUMMY = ITEMS.register("og_kush_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> JACK_HERER_GUMMY = ITEMS.register("jack_herer_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> GARY_PEYTON_GUMMY = ITEMS.register("gary_peyton_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> AMNESIA_HAZE_GUMMY = ITEMS.register("amnesia_haze_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> AK47_GUMMY = ITEMS.register("ak47_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> GHOST_TRAIN_GUMMY = ITEMS.register("ghost_train_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> GRAPE_APE_GUMMY = ITEMS.register("grape_ape_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> COTTON_CANDY_GUMMY = ITEMS.register("cotton_candy_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> BANANA_KUSH_GUMMY = ITEMS.register("banana_kush_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> CARBON_FIBER_GUMMY = ITEMS.register("carbon_fiber_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> BIRTHDAY_CAKE_GUMMY = ITEMS.register("birthday_cake_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> BLUE_COOKIES_GUMMY = ITEMS.register("blue_cookies_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> AFGHANI_GUMMY = ITEMS.register("afghani_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> MOONBOW_GUMMY = ITEMS.register("moonbow_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> LAVA_CAKE_GUMMY = ITEMS.register("lava_cake_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> JELLY_RANCHER_GUMMY = ITEMS.register("jelly_rancher_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> STRAWBERRY_SHORTCAKE_GUMMY = ITEMS.register("strawberry_shortcake_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));
    public static final DeferredItem<Item> PINK_KUSH_GUMMY = ITEMS.register("pink_kush_gummy",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_GUMMY), 1.5f, 1f, UseAnim.EAT, 40));


    // --- FERTILIZER ITEMS ---
    public static final DeferredItem<Item> WORM_CASTINGS = ITEMS.register("worm_castings", () -> new FertilizerItem(2, 2, 2, new Item.Properties()));
    public static final DeferredItem<Item> COMPOST = ITEMS.register("compost", () -> new FertilizerItem(1, 1, 1, new Item.Properties()));
    public static final DeferredItem<Item> MYCORRHIZAE = ITEMS.register("mycorrhizae", () -> new FertilizerItem(0, 3, 1, new Item.Properties()));
    public static final DeferredItem<Item> DOLOMITE_LIME = ITEMS.register("dolomite_lime", () -> new FertilizerItem(-2, 4, -2, new Item.Properties()));
    public static final DeferredItem<Item> BLOOD_MEAL = ITEMS.register("blood_meal", () -> new FertilizerItem(4, 0, 0, new Item.Properties()));
    public static final DeferredItem<Item> PHOSPHORUS_POWDER = ITEMS.register("phosphorus_feed", () -> new FertilizerItem(0, 5, 0, new Item.Properties()));
    public static final DeferredItem<Item> BAT_GUANO = ITEMS.register("bat_guano", () -> new FertilizerItem(-1, 4, 1, new Item.Properties()));
    public static final DeferredItem<Item> KELP_MEAL = ITEMS.register("kelp_meal", () -> new FertilizerItem(0, 0, 4, new Item.Properties()));
    public static final DeferredItem<Item> WOOD_ASH = ITEMS.register("wood_ash", () -> new FertilizerItem(-1, -1, 5, new Item.Properties()));
    public static final DeferredItem<Item> BLOOM_BOOSTER = ITEMS.register("bloom_booster", () -> new FertilizerItem(-2, 4, 2, new Item.Properties()));
    public static final DeferredItem<Item> FRUIT_FINISHER = ITEMS.register("fruit_finisher", () -> new FertilizerItem(0, -2, -2, new Item.Properties()));
    public static final DeferredItem<Item> NITROGEN_BOOST = ITEMS.register("nitrogen_boost", () -> new FertilizerItem(3, -1, 0, new Item.Properties()));
    public static final DeferredItem<Item> POTASH_BOOST = ITEMS.register("potash_boost", () -> new FertilizerItem(0, -1, 3, new Item.Properties()));
    public static final DeferredItem<Item> BALANCED_BOOST = ITEMS.register("balanced_boost", () -> new FertilizerItem(1, 1, 1, new Item.Properties()));
    public static final DeferredItem<Item> PHOSPHORUS_REDUCER = ITEMS.register("phosphorus_reducer", () -> new FertilizerItem(0, -3, 0, new Item.Properties()));
    public static final DeferredItem<Item> POTASSIUM_REDUCER = ITEMS.register("potassium_reducer", () -> new FertilizerItem(0, 0, -3, new Item.Properties()));
    public static final DeferredItem<Item> FISH_EMULSION = ITEMS.register("fish_emulsion", () -> new FertilizerItem(2, 2, -1, new Item.Properties()));


    // Consumables
    public static final DeferredItem<Item> BLUNT = ITEMS.register("blunt",
            () -> new BluntItem(new Item.Properties().stacksTo(64)));
    public static final DeferredItem<Item> JOINT = ITEMS.register("joint",
            () -> new JointItem(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> HERB_CAKE = ITEMS.register("herb_cake",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.HERB_CAKE), 1.5f, 1f, UseAnim.EAT, 50));
    public static final DeferredItem<Item> HASH_BROWNIE = ITEMS.register("hash_brownie",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.HASH_BROWNIE), 1.5f, 1f, UseAnim.EAT));
    public static final DeferredItem<Item> WEED_COOKIE = ITEMS.register("weed_cookie",
            () -> new WeedDerivedItem(new Item.Properties().food(ModFoods.WEED_COOKIE).stacksTo(16), 0.125f, 0.25f, UseAnim.EAT, 15));


    // Tinctures
    public static final DeferredItem<Item> EMPTY_TINCTURE = ITEMS.register("empty_tincture",
            () -> new EmptyTinctureItem(new Item.Properties().stacksTo(16).fireResistant()));
    public static final DeferredItem<Item> HASH_OIL_TINCTURE = ITEMS.register("hash_oil_tincture",
            () -> new HashOilTinctureItem(new Item.Properties()
                    .craftRemainder(ModItems.EMPTY_TINCTURE.get()).stacksTo(1)
                    .durability(3).fireResistant()));


    // Other Items
    public static final DeferredItem<Item> HEMP_HAMMER = ITEMS.register("hemp_hammer", () -> new HempHammer(new Item.Properties().stacksTo(1).durability(12)));

    public static final DeferredItem<Item> MANUAL_GRINDER = ITEMS.register("manual_grinder", () -> new ManualGrinderItem(new Item.Properties().stacksTo(1).fireResistant()));

    public static final DeferredItem<Item> BONG = ITEMS.register("bong", () -> new BongItem(new Item.Properties().stacksTo(1).fireResistant()));
    public static final DeferredItem<Item> DAB_RIG = ITEMS.register("dab_rig", () -> new DabRigItem(new Item.Properties().stacksTo(1).fireResistant()));

    public static final DeferredItem<Item> HEMP_CORE = ITEMS.register("hemp_core",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> UNFINISHED_HEMP_CORE = ITEMS.register("unfinished_hemp_core",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEMP_LEAF = ITEMS.register("hemp_leaf",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEMP_FIBERS = ITEMS.register("hemp_fibers",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEMP_FABRIC = ITEMS.register("hemp_fabric",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEMP_STICK = ITEMS.register("hemp_stick",  () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DNA_STRAND = ITEMS.register("dna_strand",  () -> new DNAStrandItem(new Item.Properties()));

    public static final DeferredItem<Item> BIO_COMPOSITE = ITEMS.register("bio_composite",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEMP_PLASTIC = ITEMS.register("hemp_plastic",  () -> new Item(new Item.Properties()));


    public static final DeferredItem<Item> CAT_URINE_BOTTLE = ITEMS.register("cat_urine_bottle",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUTTER = ITEMS.register("butter",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> INFUSED_BUTTER = ITEMS.register("infused_butter",  () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HEMP_COAL = ITEMS.register("hemp_coal",  () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> EMPTY_VIAL = ITEMS.register("empty_vial",  () -> new Item(new Item.Properties()));



    public static final DeferredItem<Item> HPS_LAMP = ITEMS.register("hps_lamp",  () -> new BaseLampItem(new Item.Properties().stacksTo(1).durability(18000))); // 15mins
    public static final DeferredItem<Item> DUAL_ARC_LAMP = ITEMS.register("dual_arc_lamp",  () -> new BaseLampItem(new Item.Properties().stacksTo(1).durability(54000))); // 45mins

    public static final DeferredItem<Item> PLANT_ANALYZER = ITEMS.register("plant_analyzer",  () -> new PlantAnalyzerItem(new Item.Properties().stacksTo(1).durability(512)));



//    public static final DeferredItem<Item> SMOKELEAF_GUIDE = ITEMS.register("smokeleaf_guide", () -> new SmokeleafGuideItem(new Item.Properties().stacksTo(1)));



    // Custom Strains (Player-created)
    public static final DeferredItem<Item> UNIDENTIFIED_SEEDS = ITEMS.register("unidentified_seeds",
            () -> new UnidentifiedSeedsItem(new Item.Properties()));
    public static final DeferredItem<Item> UNIDENTIFIED_BUD = ITEMS.register("unidentified_bud",
            () -> new UnidentifiedBudItem(new Item.Properties(), 0, 1000));
    public static final DeferredItem<Item> UNIDENTIFIED_WEED = ITEMS.register("unidentified_weed",
            () -> new UnidentifiedStrainItem(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
