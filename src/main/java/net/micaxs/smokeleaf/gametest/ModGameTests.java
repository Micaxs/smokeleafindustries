package net.micaxs.smokeleaf.gametest;

import net.micaxs.smokeleaf.SmokeleafIndustries;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Game tests are registry entries since 1.21.5: each test is a test function plus a test instance
 * (structure, environment, timeout). All of ours run in the empty 3x3x3 structure.
 */
@EventBusSubscriber(modid = SmokeleafIndustries.MODID)
public final class ModGameTests {
    private ModGameTests() {}

    public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
            DeferredRegister.create(Registries.TEST_FUNCTION, SmokeleafIndustries.MODID);

    private static final Map<String, DeferredHolder<Consumer<GameTestHelper>, Consumer<GameTestHelper>>> TESTS = new LinkedHashMap<>();

    static {
        test("no_conflicts_with_vanilla_crafting", RecipeConflictGameTests::noConflictsWithVanillaCrafting);
        test("short_grass_drops_only_seeds", LootModifierGameTests::shortGrassDropsOnlySeeds);
        test("tall_grass_drops_only_seeds", LootModifierGameTests::tallGrassDropsOnlySeeds);
        test("dual_arc_lamp_still_in_intended_loot", LootModifierGameTests::dualArcLampStillInIntendedLoot);
    }

    private static void test(String name, Consumer<GameTestHelper> function) {
        TESTS.put(name, TEST_FUNCTIONS.register(name, () -> function));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(SmokeleafIndustries.MODID, path);
    }

    public static void register(IEventBus modEventBus) {
        TEST_FUNCTIONS.register(modEventBus);
    }

    @SubscribeEvent
    public static void onRegisterGameTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(id("default"));
        TESTS.forEach((name, function) -> event.registerTest(id(name), new FunctionGameTestInstance(
                ResourceKey.create(Registries.TEST_FUNCTION, function.getId()),
                new TestData<>(environment, id("empty_3x3x3"), 100, 0, true, Rotation.NONE, false, 1, 1, false, 0))));
    }
}
