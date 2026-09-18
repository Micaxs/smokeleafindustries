// File: src/main/java/net/micaxs/smokeleaf/datagen/ModAdvancementProvider.java
package net.micaxs.smokeleaf.datagen;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {

    public ModAdvancementProvider(PackOutput output,
                                  CompletableFuture<HolderLookup.Provider> registries,
                                  ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper,
                List.of(new SmokeleafIndustriesAdvancements()));
    }

    public static class SmokeleafIndustriesAdvancements implements AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider provider,
                             Consumer<AdvancementHolder> consumer,
                             ExistingFileHelper existingFileHelper) {

            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            Items.BOOK,
                            Component.translatable("advancement.smokeleafindustries.root.title"),
                            Component.translatable("advancement.smokeleafindustries.root.desc"),
                            ResourceLocation.parse("textures/gui/advancements/backgrounds/stone.png"),
                            AdvancementType.TASK,
                            false,
                            false,
                            false
                    )
                    .addCriterion("has_book",
                            InventoryChangeTrigger.TriggerInstance.hasItems(Items.BOOK))
                    .save(consumer,
                            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "root"),
                            existingFileHelper);

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModItems.CAT_URINE_BOTTLE.get(),
                            Component.translatable("advancement.smokeleafindustries.cat_urine.title"),
                            Component.translatable("advancement.smokeleafindustries.cat_urine.desc"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    // Unlocks when the custom item is obtained
                    .addCriterion("has_cat_urine_bottle",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CAT_URINE_BOTTLE.get()))
                    .rewards(AdvancementRewards.Builder.experience(25))
                    .save(consumer,
                            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "cat_urine_bottle"),
                            existingFileHelper);


            Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModItems.HEMP_SEEDS.get(),
                            Component.translatable("advancement.smokeleafindustries.hemp_seeds.title"),
                            Component.translatable("advancement.smokeleafindustries.hemp_seeds.desc"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    // Unlocks when the custom item is obtained
                    .addCriterion("has_hemp_seeds",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.HEMP_SEEDS.get()))
                    .rewards(AdvancementRewards.Builder.experience(5))
                    .save(consumer,
                            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "hemp_seeds"),
                            existingFileHelper);


            Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModItems.JOINT.get(),
                            Component.translatable("advancement.smokeleafindustries.joint.title"),
                            Component.translatable("advancement.smokeleafindustries.joint.desc"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    // Unlocks when the custom item is obtained
                    .addCriterion("has_joint",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.JOINT.get()))
                    .rewards(AdvancementRewards.Builder.experience(5))
                    .save(consumer,
                            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "joint"),
                            existingFileHelper);


            Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModItems.HEMP_PLASTIC.get(),
                            Component.translatable("advancement.smokeleafindustries.hemp_plastic.title"),
                            Component.translatable("advancement.smokeleafindustries.hemp_plastic.desc"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    // Unlocks when Hemp Plastic is smelted in a furnace (bio composite -> hemp plastic)
                    .addCriterion("smelted_hemp_plastic",
                            RecipeCraftedTrigger.TriggerInstance.craftedItem(
                                    ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID,
                                            "hemp_plastic_from_bio_composite")))
                    .rewards(AdvancementRewards.Builder.experience(10))
                    .save(consumer,
                            ResourceLocation.fromNamespaceAndPath(SmokeleafIndustries.MODID, "hemp_plastic"),
                            existingFileHelper);

        }
    }
}