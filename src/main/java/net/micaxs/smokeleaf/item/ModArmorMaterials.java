package net.micaxs.smokeleaf.item;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.utils.ModTags;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.Map;

public class ModArmorMaterials {
    public static final ResourceKey<EquipmentAsset> BAJA_HOODIE_ASSET = asset("baja_hoodie");
    public static final ResourceKey<EquipmentAsset> REINFORCED_BAJA_HOODIE_ASSET = asset("reinforced_baja_hoodie");

    // Baja Hoodie — cloth armor, protection on par with leather.
    public static final ArmorMaterial BAJA_HOODIE = new ArmorMaterial(
            5,
            defense(1, 2, 3, 1, 3),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            ModTags.REPAIRS_BAJA_HOODIE,
            BAJA_HOODIE_ASSET
    );

    // Reinforced Baja Hoodie — netherite-tier, +1 defense per slot over plain netherite.
    public static final ArmorMaterial REINFORCED_BAJA_HOODIE = new ArmorMaterial(
            37,
            defense(4, 7, 9, 4, 12),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.0F,
            0.1F,
            ItemTags.REPAIRS_NETHERITE_ARMOR,
            REINFORCED_BAJA_HOODIE_ASSET
    );

    private static ResourceKey<EquipmentAsset> asset(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, name));
    }

    private static Map<ArmorType, Integer> defense(int boots, int leggings, int chestplate, int helmet, int body) {
        Map<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, boots);
        map.put(ArmorType.LEGGINGS, leggings);
        map.put(ArmorType.CHESTPLATE, chestplate);
        map.put(ArmorType.HELMET, helmet);
        map.put(ArmorType.BODY, body);
        return map;
    }
}
