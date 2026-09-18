package net.micaxs.smokeleaf.item;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, SmokeleafIndustries.MODID);

    // Baja Hoodie — cloth armor, protection on par with leather.
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BAJA_HOODIE = ARMOR_MATERIALS.register(
            "baja_hoodie",
            () -> new ArmorMaterial(
                    defense(1, 2, 3, 1, 3),
                    15,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(ModItems.HEMP_FABRIC),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "baja_hoodie"))),
                    0.0F,
                    0.0F
            ));

    // Reinforced Baja Hoodie — netherite-tier, +1 defense per slot over plain netherite.
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> REINFORCED_BAJA_HOODIE = ARMOR_MATERIALS.register(
            "reinforced_baja_hoodie",
            () -> new ArmorMaterial(
                    defense(4, 7, 9, 4, 12),
                    15,
                    SoundEvents.ARMOR_EQUIP_NETHERITE,
                    () -> Ingredient.of(Items.NETHERITE_INGOT),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "reinforced_baja_hoodie"))),
                    3.0F,
                    0.1F
            ));

    private static Map<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet, int body) {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.CHESTPLATE, chestplate);
        map.put(ArmorItem.Type.HELMET, helmet);
        map.put(ArmorItem.Type.BODY, body);
        return map;
    }

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
