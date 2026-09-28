package net.micaxs.smokeleaf.item;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    // Baja Hoodie — cloth armor, protection on par with leather.
    BAJA_HOODIE("baja_hoodie", 5, defense(1, 2, 3, 1), 15,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F,
            () -> Ingredient.of(ModItems.HEMP_FABRIC.get())),

    // Reinforced Baja Hoodie — netherite-tier, +1 defense per slot over plain netherite.
    REINFORCED_BAJA_HOODIE("reinforced_baja_hoodie", 37, defense(4, 7, 9, 4), 15,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F,
            () -> Ingredient.of(Items.NETHERITE_INGOT));

    private static final Map<ArmorItem.Type, Integer> BASE_DURABILITY = Map.of(
            ArmorItem.Type.BOOTS, 13,
            ArmorItem.Type.LEGGINGS, 15,
            ArmorItem.Type.CHESTPLATE, 16,
            ArmorItem.Type.HELMET, 11);

    private final String name;
    private final int durabilityMultiplier;
    private final Map<ArmorItem.Type, Integer> protection;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, Map<ArmorItem.Type, Integer> protection,
                      int enchantmentValue, SoundEvent equipSound, float toughness,
                      float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protection = protection;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
    }

    private static Map<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.LEGGINGS, leggings);
        map.put(ArmorItem.Type.CHESTPLATE, chestplate);
        map.put(ArmorItem.Type.HELMET, helmet);
        return map;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return BASE_DURABILITY.get(type) * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return protection.get(type);
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    // Namespaced so the armor layer textures resolve to textures/models/armor/<name>_layer_N.png in our namespace.
    @Override
    public String getName() {
        return SmokeleafIndustries.MODID + ":" + name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
