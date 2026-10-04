package net.micaxs.smokeleaf.gametest;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.micaxs.smokeleaf.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Rolls real loot tables many times to check that our global loot modifiers only fire where intended.
 */
@GameTestHolder(SmokeleafIndustries.MODID)
@PrefixGameTestTemplate(false)
public class LootModifierGameTests {

    private static final String TEMPLATE = "empty_3x3x3";
    private static final int ROLLS = 5000;
    private static final double TOLERANCE = 0.03;

    @GameTest(template = TEMPLATE)
    public static void shortGrassDropsOnlySeeds(GameTestHelper helper) {
        BlockPos pos = new BlockPos(0, 1, 0);
        helper.setBlock(pos.below(), Blocks.GRASS_BLOCK);
        helper.setBlock(pos, Blocks.SHORT_GRASS);

        assertGrassDrops(helper, "short_grass", pos);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void tallGrassDropsOnlySeeds(GameTestHelper helper) {
        BlockPos lower = new BlockPos(2, 1, 2);
        BlockPos upper = lower.above();
        helper.setBlock(lower.below(), Blocks.GRASS_BLOCK);
        helper.setBlock(lower, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(upper, Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER));

        assertGrassDrops(helper, "tall_grass (lower)", lower);
        assertGrassDrops(helper, "tall_grass (upper)", upper);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void dualArcLampStillInIntendedLoot(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Vec3 origin = Vec3.atCenterOf(helper.absolutePos(BlockPos.ZERO));

        // Both modifiers use a 20% / 30% chance; anything above zero proves the loot_table_id condition matches.
        Supplier<LootParams> archaeology = () -> new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .create(LootContextParamSets.ARCHAEOLOGY);
        Supplier<LootParams> chest = () -> new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .create(LootContextParamSets.CHEST);

        assertTableCanDropLamp(helper, "archaeology/desert_well", archaeology, 0.2);
        assertTableCanDropLamp(helper, "chests/trial_chambers/reward_common", chest, 0.3);
        helper.succeed();
    }

    private static void assertGrassDrops(GameTestHelper helper, String name, BlockPos relativePos) {
        ServerLevel level = helper.getLevel();
        BlockPos absPos = helper.absolutePos(relativePos);
        BlockState state = level.getBlockState(absPos);
        Item hempSeeds = ModItems.HEMP_SEEDS.get();
        Item tobaccoSeeds = ModItems.TOBACCO_SEEDS.get();

        Map<Item, Integer> tally = new HashMap<>();
        for (int i = 0; i < ROLLS; i++) {
            LootParams.Builder params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(absPos))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
            for (ItemStack stack : state.getDrops(params)) {
                tally.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }

        SmokeleafIndustries.LOGGER.info("[LootModifierGameTests] {} drops over {} rolls: {}", name, ROLLS, tally);

        for (Item item : tally.keySet()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            boolean ours = id.getNamespace().equals(SmokeleafIndustries.MODID);
            helper.assertTrue(!ours || item == hempSeeds || item == tobaccoSeeds,
                    name + " dropped unexpected item " + id + " x" + tally.get(item));
        }

        // Each modifier adds exactly one seed per break, so the item count equals the number of breaks that hit.
        assertRate(helper, name, "hemp_seeds", tally.getOrDefault(hempSeeds, 0), 0.35);
        assertRate(helper, name, "tobacco_seeds", tally.getOrDefault(tobaccoSeeds, 0), 0.25);
    }

    private static void assertTableCanDropLamp(GameTestHelper helper, String path, Supplier<LootParams> params, double expected) {
        LootTable table = helper.getLevel().getServer().reloadableRegistries()
                .getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.withDefaultNamespace(path)));
        Item lamp = ModItems.DUAL_ARC_LAMP.get();

        int lamps = 0;
        for (int i = 0; i < ROLLS; i++) {
            for (ItemStack stack : table.getRandomItems(params.get())) {
                if (stack.is(lamp)) lamps += stack.getCount();
            }
        }

        SmokeleafIndustries.LOGGER.info("[LootModifierGameTests] {}: {} dual_arc_lamp over {} rolls", path, lamps, ROLLS);
        assertRate(helper, path, "dual_arc_lamp", lamps, expected);
    }

    private static void assertRate(GameTestHelper helper, String name, String item, int hits, double expected) {
        double rate = hits / (double) ROLLS;
        helper.assertTrue(Math.abs(rate - expected) <= TOLERANCE,
                String.format("%s: %s rate %.3f, expected %.2f +/- %.2f", name, item, rate, expected, TOLERANCE));
    }
}
