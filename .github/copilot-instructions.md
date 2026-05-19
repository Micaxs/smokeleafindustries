# Smokeleaf Industries — Copilot Instructions

NeoForge 1.21.1 Minecraft mod (Java 21). mod_id: `smokeleafindustries`, base package: `net.micaxs.smokeleaf`.

## Build & Run Commands

```bash
./gradlew build              # Compile and package the mod jar
./gradlew runClient          # Launch Minecraft client in dev environment
./gradlew runServer          # Launch dedicated server (no GUI)
./gradlew runData            # Run data generators (outputs to src/generated/resources/)
./gradlew runGameTestServer  # Run game tests and exit
```

There are no unit tests. The `gameTestServer` run config runs NeoForge game tests in-world.

## Architecture

### Entry Points
- `SmokeleafIndustries.java` — main mod class (`@Mod("smokeleafindustries")`). All registry `.register(modEventBus)` calls happen here in the constructor.
- `SmokeleafIndustriesClient.java` — client-only setup (renderers, screen registration, particle factories).

### Registry Pattern
Every content category has a `Mod*.java` registry class (e.g., `ModBlocks`, `ModItems`, `ModEffects`) using NeoForge `DeferredRegister`. Each exposes a static `register(IEventBus)` method called from the main mod constructor. Blocks and items use `DeferredBlock<T>` / `DeferredItem<T>`.

Blocks that need a `BlockItem` use the `registerBlock(name, supplier)` helper in `ModBlocks`, which auto-registers both the block and its item.

### Machine / Block Entity Pattern
Each machine (Grinder, Dryer, Extractor, Liquifier, Mixer, Mutator, Sequencer, Synthesizer, Generator) follows this consistent structure:
- `XxxBlock` — block with `POWERED` blockstate, opens menu on use
- `XxxBlockEntity` — implements `MenuProvider`, holds `ItemStackHandler`, `ModEnergyStorage` (RF/FE), `ContainerData` for sync, and a `tick()` method registered in `ModBlockEntityTypesExtra`
- `XxxRecipe` + `XxxRecipeInput` — custom recipe type
- `XxxMenu` + `XxxScreen` — container menu and client screen
- `XxxRecipeCategory` in `compat/jei/` — JEI display

### Strain / Weed Data System
`StrainData` is a Codec-serialized `record` (stored as a NeoForge data component via `ModDataComponentTypes`) that travels with item stacks and fluid stacks:
- Fields: `colorArgb`, `thc`, `cbd`, `nitrogen`, `phosphorus`, `potassium`, `effects` (list of `ResourceLocation`), `amplifier`, `durationTicks`, `identified`, `displayName`
- `StrainUtil` handles creating/modifying strain data
- `WeedDataUtil` / `WeedEffectHelper` apply strain effects to players

All 25 weed strain crops extend `BaseWeedCropBlock`; their items extend `BaseWeedItem` (handles right-click consume and effect application). Seeds are `ItemNameBlockItem` pointing to the crop block.

### Effects
Custom `MobEffect` subclasses are grouped under `effect/beneficial/`, `effect/harmful/`, and `effect/neutral/`. All extend `BeneficialEffectBase` or implement tick logic directly. Registered in `ModEffects` via `DeferredRegister<MobEffect>`.

### Custom Fluids
Hemp Oil, Hash Oil, and the dynamic "unidentified mixture" fluid live in the `fluid/` package. The mixture fluid carries `StrainData` via `WeedFluidData`. `ModFluids` / `ModFluidTypes` register the fluid and fluid type.

### Data Generation
`DataGenerators.java` wires all providers. After adding new blocks/items, run `./gradlew runData` to regenerate block states, item models, loot tables, tags, and recipes into `src/generated/resources/` (this directory is committed and included in the build via `sourceSets.main.resources`).

### Mod Compat
- `compat/jei/` — JEI recipe categories (one per custom machine recipe type)
- `compat/jade/` — Jade tooltip providers for crop info, grow pot, drying rack, reflector
- Entry points: `JEISmokeleafIndustriesPlugin`, `JadeSmokeleafIndustriesPlugin`

## Key Conventions

- **Resource location namespace** is always `smokeleafindustries` (not `smokeleaf`). Use `SmokeleafIndustries.MODID` constant.
- **Datagen is the source of truth** for block states, item models, loot tables, and standard recipes. Do not hand-edit files under `src/generated/resources/`; regenerate them instead. Custom machine recipes are JSON files in `src/main/resources/data/smokeleafindustries/recipes/`.
- **Energy machines** use `ModEnergyStorage` (a thin wrapper over `EnergyStorage`) and expose it via `IEnergyStorage` capability on the `Direction` sides.
- **Screens** use `FluidTankRenderer` and `EnergyDisplayTooltipArea` helpers from `screen/renderer/` for consistent fluid/energy HUD rendering.
- **Client-server split**: client-only code lives in `client/` or is guarded by `level.isClientSide()`. Network payloads are in `network/` and registered in `ModPayloads`.
- **Patchouli guide book** entries are under `src/main/resources/assets/smokeleafindustries/patchouli_books/smokeleaf_guide/`. The book is currently unfinished.
- **Mod config** (`Config.java`) is a NeoForge `ModConfigSpec` of type `COMMON`, loaded automatically.
- **`src/main/templates/`** contains `neoforge.mods.toml` and `pack.mcmeta` with `${property}` placeholders expanded by the `generateModMetadata` Gradle task from `gradle.properties`.
