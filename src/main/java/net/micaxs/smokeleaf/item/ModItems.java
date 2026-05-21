package net.micaxs.smokeleaf.item;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
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
            () -> new BaseWeedItem(new Item.Properties(), MobEffects.WIND_CHARGED.value(), 400, 2, 15, 10, false).withNameSuffix(" Extract"));
    public static final DeferredItem<Item> GENERIC_BAG = ITEMS.register("bag",
            () -> new GenericBagItem(new Item.Properties().stacksTo(64)));

    // Non-strain-specific seeds
    public static final DeferredItem<Item>  HEMP_SEEDS = ITEMS.register("hemp_seeds",
            () -> new ItemNameBlockItem(ModBlocks.HEMP_CROP.get(), new Item.Properties()));

    public static final DeferredItem<Item> BASE_EXTRACT = ITEMS.register("base_extract",  () -> new Item(new Item.Properties()));


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



    // Custom Strains (Player-created) — use the generic items above; these are kept for world compatibility only.
    // public static final DeferredItem<Item> UNIDENTIFIED_SEEDS = ...
    // public static final DeferredItem<Item> UNIDENTIFIED_BUD = ...
    // public static final DeferredItem<Item> UNIDENTIFIED_WEED = ...


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
