package net.micaxs.smokeleaf.item;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.block.ModBlocks;
import net.micaxs.smokeleaf.block.entity.pipe.PipeType;
import net.micaxs.smokeleaf.item.custom.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SmokeleafIndustries.MODID);

    // Tobacco
    public static final DeferredItem<Item> TOBACCO = ITEMS.registerItem("tobacco",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item>  TOBACCO_SEEDS = ITEMS.registerItem("tobacco_seeds",
            props -> new BaseSeedItem(ModBlocks.TOBACCO_CROP.get(), props, "tooltip.smokeleafindustries.seeds.tall_grass"), () -> new Item.Properties());
    public static final DeferredItem<Item> TOBACCO_LEAF = ITEMS.registerItem("tobacco_leaves",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> DRIED_TOBACCO_LEAF = ITEMS.registerItem("dried_tobacco_leaves",  props -> new Item(props), () -> new Item.Properties());



    // Generic strain items (Option C / full unification skeleton)
    public static final DeferredItem<Item> GENERIC_SEEDS = ITEMS.registerItem("seeds",
            props -> new UnidentifiedSeedsItem(props), () -> new Item.Properties());
    public static final DeferredItem<Item> GENERIC_BUD = ITEMS.registerItem("bud",
            props -> new BaseBudItem(props, 0, 1200), () -> new Item.Properties());
    public static final DeferredItem<Item> GENERIC_WEED = ITEMS.registerItem("weed",
            props -> new BaseWeedItem(props, MobEffects.WIND_CHARGED.value(), 200, 1, 15, 10, true), () -> new Item.Properties());
    public static final DeferredItem<Item> GENERIC_EXTRACT = ITEMS.registerItem("extract",
            props -> new BaseWeedItem(props, MobEffects.WIND_CHARGED.value(), 400, 2, 15, 10, false).withNameSuffix(" Extract"), () -> new Item.Properties());
    public static final DeferredItem<Item> GENERIC_BAG = ITEMS.registerItem("bag",
            props -> new GenericBagItem(props), () -> new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> GENERIC_GUMMY = ITEMS.registerItem("gummy",
            props -> new GenericGummyItem(props), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> GUMMY_MOLD = ITEMS.registerItem("gummy_mold",
            props -> new Item(props), () -> new Item.Properties().stacksTo(1));
    public static final DeferredItem<Item> GENERIC_GUMMY_WORM = ITEMS.registerItem("gummy_worm",
            props -> new GenericGummyWormItem(props), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> GUMMY_WORM_MOLD = ITEMS.registerItem("gummy_worm_mold",
            props -> new Item(props), () -> new Item.Properties().stacksTo(1));

    // Pipes — all 3 place/merge into the same ModBlocks.PIPE block position (up to one of each).
    public static final DeferredItem<Item> ITEM_PIPE = ITEMS.registerItem("item_pipe",
            props -> new PipeItem(ModBlocks.PIPE.get(), PipeType.ITEM, props), () -> new Item.Properties());
    public static final DeferredItem<Item> FLUID_PIPE = ITEMS.registerItem("fluid_pipe",
            props -> new PipeItem(ModBlocks.PIPE.get(), PipeType.FLUID, props), () -> new Item.Properties());
    public static final DeferredItem<Item> ENERGY_PIPE = ITEMS.registerItem("energy_pipe",
            props -> new PipeItem(ModBlocks.PIPE.get(), PipeType.ENERGY, props), () -> new Item.Properties());
    public static final DeferredItem<Item> PIPE_WRENCH = ITEMS.registerItem("pipe_wrench",
            props -> new PipeWrenchItem(props), () -> new Item.Properties().stacksTo(1).durability(1561));

    // Non-strain-specific seeds
    public static final DeferredItem<Item>  HEMP_SEEDS = ITEMS.registerItem("hemp_seeds",
            props -> new BaseSeedItem(ModBlocks.HEMP_CROP.get(), props, "tooltip.smokeleafindustries.seeds.tall_grass"), () -> new Item.Properties());

    public static final DeferredItem<Item> BASE_EXTRACT = ITEMS.registerItem("base_extract",  props -> new Item(props), () -> new Item.Properties());


    // Bags
    public static final DeferredItem<Item> EMPTY_BAG = ITEMS.registerItem("empty_bag",
            props -> new BaseBagItem(props, "tooltip.smokeleafindustries.empty_bag"), () -> new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> WHITE_WIDOW_BAG = ITEMS.registerItem("white_widow_bag",
            props -> new BaseBagItem(props, "tooltip.smokeleafindustries.white_widow_bag"), () -> new Item.Properties().stacksTo(64));


    // Gummies
    public static final DeferredItem<Item> WHITE_WIDOW_GUMMY = ITEMS.registerItem("white_widow_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> BUBBLE_KUSH_GUMMY = ITEMS.registerItem("bubble_kush_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> LEMON_HAZE_GUMMY = ITEMS.registerItem("lemon_haze_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> SOUR_DIESEL_GUMMY = ITEMS.registerItem("sour_diesel_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> BLUE_ICE_GUMMY = ITEMS.registerItem("blue_ice_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> BUBBLEGUM_GUMMY = ITEMS.registerItem("bubblegum_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> PURPLE_HAZE_GUMMY = ITEMS.registerItem("purple_haze_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> OG_KUSH_GUMMY = ITEMS.registerItem("og_kush_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> JACK_HERER_GUMMY = ITEMS.registerItem("jack_herer_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> GARY_PEYTON_GUMMY = ITEMS.registerItem("gary_peyton_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> AMNESIA_HAZE_GUMMY = ITEMS.registerItem("amnesia_haze_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> AK47_GUMMY = ITEMS.registerItem("ak47_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> GHOST_TRAIN_GUMMY = ITEMS.registerItem("ghost_train_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> GRAPE_APE_GUMMY = ITEMS.registerItem("grape_ape_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> COTTON_CANDY_GUMMY = ITEMS.registerItem("cotton_candy_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> BANANA_KUSH_GUMMY = ITEMS.registerItem("banana_kush_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> CARBON_FIBER_GUMMY = ITEMS.registerItem("carbon_fiber_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> BIRTHDAY_CAKE_GUMMY = ITEMS.registerItem("birthday_cake_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> BLUE_COOKIES_GUMMY = ITEMS.registerItem("blue_cookies_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> AFGHANI_GUMMY = ITEMS.registerItem("afghani_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> MOONBOW_GUMMY = ITEMS.registerItem("moonbow_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> LAVA_CAKE_GUMMY = ITEMS.registerItem("lava_cake_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> JELLY_RANCHER_GUMMY = ITEMS.registerItem("jelly_rancher_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> STRAWBERRY_SHORTCAKE_GUMMY = ITEMS.registerItem("strawberry_shortcake_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));
    public static final DeferredItem<Item> PINK_KUSH_GUMMY = ITEMS.registerItem("pink_kush_gummy",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 40, 2), () -> new Item.Properties().food(ModFoods.WEED_GUMMY));


    // --- FERTILIZER ITEMS ---
    public static final DeferredItem<Item> WORM_CASTINGS = ITEMS.registerItem("worm_castings", props -> new FertilizerItem(2, 2, 2, props), () -> new Item.Properties());
    public static final DeferredItem<Item> COMPOST = ITEMS.registerItem("compost", props -> new FertilizerItem(1, 1, 1, props), () -> new Item.Properties());
    public static final DeferredItem<Item> MYCORRHIZAE = ITEMS.registerItem("mycorrhizae", props -> new FertilizerItem(0, 3, 1, props), () -> new Item.Properties());
    public static final DeferredItem<Item> DOLOMITE_LIME = ITEMS.registerItem("dolomite_lime", props -> new FertilizerItem(-2, 4, -2, props), () -> new Item.Properties());
    public static final DeferredItem<Item> BLOOD_MEAL = ITEMS.registerItem("blood_meal", props -> new FertilizerItem(4, 0, 0, props), () -> new Item.Properties());
    public static final DeferredItem<Item> PHOSPHORUS_POWDER = ITEMS.registerItem("phosphorus_feed", props -> new FertilizerItem(0, 5, 0, props), () -> new Item.Properties());
    public static final DeferredItem<Item> BAT_GUANO = ITEMS.registerItem("bat_guano", props -> new FertilizerItem(-1, 4, 1, props), () -> new Item.Properties());
    public static final DeferredItem<Item> KELP_MEAL = ITEMS.registerItem("kelp_meal", props -> new FertilizerItem(0, 0, 4, props), () -> new Item.Properties());
    public static final DeferredItem<Item> WOOD_ASH = ITEMS.registerItem("wood_ash", props -> new FertilizerItem(-1, -1, 5, props), () -> new Item.Properties());
    public static final DeferredItem<Item> BLOOM_BOOSTER = ITEMS.registerItem("bloom_booster", props -> new FertilizerItem(-2, 4, 2, props), () -> new Item.Properties());
    public static final DeferredItem<Item> FRUIT_FINISHER = ITEMS.registerItem("fruit_finisher", props -> new FertilizerItem(0, -2, -2, props), () -> new Item.Properties());
    public static final DeferredItem<Item> NITROGEN_BOOST = ITEMS.registerItem("nitrogen_boost", props -> new FertilizerItem(3, -1, 0, props), () -> new Item.Properties());
    public static final DeferredItem<Item> POTASH_BOOST = ITEMS.registerItem("potash_boost", props -> new FertilizerItem(0, -1, 3, props), () -> new Item.Properties());
    public static final DeferredItem<Item> BALANCED_BOOST = ITEMS.registerItem("balanced_boost", props -> new FertilizerItem(1, 1, 1, props), () -> new Item.Properties());
    public static final DeferredItem<Item> PHOSPHORUS_REDUCER = ITEMS.registerItem("phosphorus_reducer", props -> new FertilizerItem(0, -3, 0, props), () -> new Item.Properties());
    public static final DeferredItem<Item> POTASSIUM_REDUCER = ITEMS.registerItem("potassium_reducer", props -> new FertilizerItem(0, 0, -3, props), () -> new Item.Properties());
    public static final DeferredItem<Item> FISH_EMULSION = ITEMS.registerItem("fish_emulsion", props -> new FertilizerItem(2, 2, -1, props), () -> new Item.Properties());


    // Consumables
    public static final DeferredItem<Item> BLUNT = ITEMS.registerItem("blunt",
            props -> new BluntItem(props), () -> new Item.Properties().stacksTo(64));
    public static final DeferredItem<Item> JOINT = ITEMS.registerItem("joint",
            props -> new JointItem(props), () -> new Item.Properties().stacksTo(64));

    public static final DeferredItem<Item> HERB_CAKE = ITEMS.registerItem("herb_cake",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT, 50), () -> new Item.Properties().food(ModFoods.HERB_CAKE));
    public static final DeferredItem<Item> HASH_BROWNIE = ITEMS.registerItem("hash_brownie",
            props -> new WeedDerivedItem(props, 1.5f, 1f, ItemUseAnimation.EAT), () -> new Item.Properties().food(ModFoods.HASH_BROWNIE));
    public static final DeferredItem<Item> WEED_COOKIE = ITEMS.registerItem("weed_cookie",
            props -> new WeedDerivedItem(props, 0.125f, 0.25f, ItemUseAnimation.EAT, 15), () -> new Item.Properties().food(ModFoods.WEED_COOKIE).stacksTo(16));


    // Tinctures
    public static final DeferredItem<Item> EMPTY_TINCTURE = ITEMS.registerItem("empty_tincture",
            props -> new EmptyTinctureItem(props), () -> new Item.Properties().stacksTo(16).fireResistant());
    public static final DeferredItem<Item> HASH_OIL_TINCTURE = ITEMS.registerItem("hash_oil_tincture",
            props -> new HashOilTinctureItem(props), () -> new Item.Properties()
                    .craftRemainder(ModItems.EMPTY_TINCTURE.get()).stacksTo(1)
                    .durability(3).fireResistant());


    // Other Items
    public static final DeferredItem<Item> HEMP_HAMMER = ITEMS.registerItem("hemp_hammer", props -> new HempHammer(props), () -> new Item.Properties().stacksTo(1).durability(64).enchantable(12));

    public static final DeferredItem<Item> MANUAL_GRINDER = ITEMS.registerItem("manual_grinder", props -> new ManualGrinderItem(props), () -> new Item.Properties().stacksTo(1).fireResistant());

    public static final DeferredItem<Item> BONG = ITEMS.registerItem("bong", props -> new BongItem(props), () -> new Item.Properties().stacksTo(1).fireResistant());
    public static final DeferredItem<Item> DAB_RIG = ITEMS.registerItem("dab_rig", props -> new DabRigItem(props), () -> new Item.Properties().stacksTo(1).fireResistant());

    public static final DeferredItem<Item> HEMP_CORE = ITEMS.registerItem("hemp_core",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> UNFINISHED_HEMP_CORE = ITEMS.registerItem("unfinished_hemp_core",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> HEMP_LEAF = ITEMS.registerItem("hemp_leaf",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> HEMP_FIBERS = ITEMS.registerItem("hemp_fibers",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> HEMP_FABRIC = ITEMS.registerItem("hemp_fabric",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> HEMP_STICK = ITEMS.registerItem("hemp_stick",  props -> new Item(props), () -> new Item.Properties());

    // Baja Hoodie — cloth armor (leather-tier protection), full set negates the Stoned effect.
    public static final DeferredItem<Item> BAJA_HOODIE_HELMET = ITEMS.registerItem("baja_hoodie_helmet",
            BajaHoodieArmorItem::new, () -> new Item.Properties().humanoidArmor(ModArmorMaterials.BAJA_HOODIE, ArmorType.HELMET));
    public static final DeferredItem<Item> BAJA_HOODIE_CHESTPLATE = ITEMS.registerItem("baja_hoodie_chestplate",
            BajaHoodieArmorItem::new, () -> new Item.Properties().humanoidArmor(ModArmorMaterials.BAJA_HOODIE, ArmorType.CHESTPLATE));
    public static final DeferredItem<Item> BAJA_HOODIE_LEGGINGS = ITEMS.registerItem("baja_hoodie_leggings",
            BajaHoodieArmorItem::new, () -> new Item.Properties().humanoidArmor(ModArmorMaterials.BAJA_HOODIE, ArmorType.LEGGINGS));
    public static final DeferredItem<Item> BAJA_HOODIE_BOOTS = ITEMS.registerItem("baja_hoodie_boots",
            BajaHoodieArmorItem::new, () -> new Item.Properties().humanoidArmor(ModArmorMaterials.BAJA_HOODIE, ArmorType.BOOTS));

    // Reinforced Baja Hoodie — netherite-tier upgrade (Hemp Plastic + Netherite Ingot per piece),
    // +1 defense over plain netherite armor and also negates the Stoned effect.
    public static final DeferredItem<Item> REINFORCED_BAJA_HOODIE_HELMET = ITEMS.registerItem("reinforced_baja_hoodie_helmet",
            BajaHoodieArmorItem::new, () -> new Item.Properties().humanoidArmor(ModArmorMaterials.REINFORCED_BAJA_HOODIE, ArmorType.HELMET).fireResistant());
    public static final DeferredItem<Item> REINFORCED_BAJA_HOODIE_CHESTPLATE = ITEMS.registerItem("reinforced_baja_hoodie_chestplate",
            BajaHoodieArmorItem::new, () -> new Item.Properties().humanoidArmor(ModArmorMaterials.REINFORCED_BAJA_HOODIE, ArmorType.CHESTPLATE).fireResistant());
    public static final DeferredItem<Item> REINFORCED_BAJA_HOODIE_LEGGINGS = ITEMS.registerItem("reinforced_baja_hoodie_leggings",
            BajaHoodieArmorItem::new, () -> new Item.Properties().humanoidArmor(ModArmorMaterials.REINFORCED_BAJA_HOODIE, ArmorType.LEGGINGS).fireResistant());
    public static final DeferredItem<Item> REINFORCED_BAJA_HOODIE_BOOTS = ITEMS.registerItem("reinforced_baja_hoodie_boots",
            BajaHoodieArmorItem::new, () -> new Item.Properties().humanoidArmor(ModArmorMaterials.REINFORCED_BAJA_HOODIE, ArmorType.BOOTS).fireResistant());

    public static final DeferredItem<Item> DNA_STRAND = ITEMS.registerItem("dna_strand",  props -> new DNAStrandItem(props), () -> new Item.Properties());

    public static final DeferredItem<Item> BIO_COMPOSITE = ITEMS.registerItem("bio_composite",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> HEMP_PLASTIC = ITEMS.registerItem("hemp_plastic",  props -> new Item(props), () -> new Item.Properties());


    public static final DeferredItem<Item> CAT_URINE_BOTTLE = ITEMS.registerItem("cat_urine_bottle",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> BUTTER = ITEMS.registerItem("butter",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> INFUSED_BUTTER = ITEMS.registerItem("infused_butter",  props -> new Item(props), () -> new Item.Properties());
    public static final DeferredItem<Item> HEMP_COAL = ITEMS.registerItem("hemp_coal",  props -> new Item(props), () -> new Item.Properties());

    public static final DeferredItem<Item> EMPTY_VIAL = ITEMS.registerItem("empty_vial",  props -> new Item(props), () -> new Item.Properties());



    public static final DeferredItem<Item> HPS_LAMP = ITEMS.registerItem("hps_lamp",  props -> new BaseLampItem(props), () -> new Item.Properties().stacksTo(1).durability(18000)); // 15mins
    public static final DeferredItem<Item> DUAL_ARC_LAMP = ITEMS.registerItem("dual_arc_lamp",  props -> new BaseLampItem(props), () -> new Item.Properties().stacksTo(1).durability(54000)); // 45mins

    public static final DeferredItem<Item> PLANT_ANALYZER = ITEMS.registerItem("plant_analyzer",  props -> new PlantAnalyzerItem(props), () -> new Item.Properties().stacksTo(1).durability(512));



    public static final DeferredItem<Item> STRAIN_BOOK = ITEMS.registerItem("strain_book",
            props -> new StrainBookItem(props), () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<Item> SMOKELEAF_GUIDE = ITEMS.registerItem("smokeleaf_guide", props -> new SmokeleafGuideItem(props), () -> new Item.Properties().stacksTo(1));



    // Custom Strains (Player-created) — use the generic items above; these are kept for world compatibility only.
    // public static final DeferredItem<Item> UNIDENTIFIED_SEEDS = ...
    // public static final DeferredItem<Item> UNIDENTIFIED_BUD = ...
    // public static final DeferredItem<Item> UNIDENTIFIED_WEED = ...


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
