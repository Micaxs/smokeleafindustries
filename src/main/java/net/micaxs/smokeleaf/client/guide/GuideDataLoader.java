package net.micaxs.smokeleaf.client.guide;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Client-side reload listener that loads the guide book's content from
 * {@code assets/<namespace>/guide/{categories,entries}/**}.json} (registered against
 * {@code RegisterClientReloadListenersEvent} in {@code SmokeleafIndustriesClient}, so it also gets
 * a free F3+T reload during development). Not a Patchouli concept — a small custom format built
 * for exactly the page types this mod's guide content needs; see {@link GuidePage}.
 */
public class GuideDataLoader extends SimplePreparableReloadListener<GuideDataLoader.Loaded> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();

    private static Loaded current = Loaded.EMPTY;

    public record Loaded(Map<ResourceLocation, GuideCategory> categories, Map<ResourceLocation, GuideEntry> entries) {
        static final Loaded EMPTY = new Loaded(Map.of(), Map.of());
    }

    @Override
    protected Loaded prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, JsonElement> categoryJson = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(resourceManager, "guide/categories", GSON, categoryJson);
        Map<ResourceLocation, JsonElement> entryJson = new HashMap<>();
        SimpleJsonResourceReloadListener.scanDirectory(resourceManager, "guide/entries", GSON, entryJson);

        Map<ResourceLocation, GuideCategory> categories = new LinkedHashMap<>();
        categoryJson.forEach((id, element) -> {
            try {
                categories.put(id, GuideCategory.parse(id, element.getAsJsonObject()));
            } catch (Exception e) {
                LOGGER.error("Failed to parse guide category {}", id, e);
            }
        });

        Map<ResourceLocation, GuideEntry> entries = new LinkedHashMap<>();
        entryJson.forEach((id, element) -> {
            try {
                entries.put(id, GuideEntry.parse(id, element.getAsJsonObject()));
            } catch (Exception e) {
                LOGGER.error("Failed to parse guide entry {}", id, e);
            }
        });

        return new Loaded(categories, entries);
    }

    @Override
    protected void apply(Loaded loaded, ResourceManager resourceManager, ProfilerFiller profiler) {
        current = loaded;
        LOGGER.info("Guide book: loaded {} categories, {} entries", loaded.categories().size(), loaded.entries().size());
    }

    public static List<GuideCategory> categories() {
        return current.categories().values().stream()
                .sorted(Comparator.comparingInt(GuideCategory::sortnum))
                .toList();
    }

    public static List<GuideEntry> entriesFor(ResourceLocation categoryId) {
        return current.entries().values().stream()
                .filter(e -> e.category().equals(categoryId))
                .sorted(Comparator.comparingInt(GuideEntry::sortnum))
                .toList();
    }
}
