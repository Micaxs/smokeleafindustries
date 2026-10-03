package net.micaxs.smokeleaf.client.guide;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * One page of a {@link GuideEntry}. Every page type covers exactly what the mod's real content
 * needs (confirmed by surveying every page the old Patchouli book ever used): plain text, a single
 * spotlighted item (optionally strain-tinted), a vanilla crafting/smelting recipe rendered as a
 * grid, or a full-width image. Title/caption text is never stored here as a literal string — only
 * the translation key it resolves to (see {@link GuideText}), computed once at parse time from the
 * owning entry's own id plus this page's index.
 */
public sealed interface GuidePage {

    record Text(String titleKey, String textKey) implements GuidePage {}

    /** {@code strainId} is one of the 25 preset strain ids ({@code StrainRegistry}); when present the preview item is tinted with that strain's data before rendering. */
    record Spotlight(ResourceLocation item, String strainId, String textKey) implements GuidePage {}

    record Crafting(ResourceLocation recipeId, String textKey) implements GuidePage {}

    record Smelting(ResourceLocation recipeId, String textKey) implements GuidePage {}

    record Image(ResourceLocation image, String textKey) implements GuidePage {}

    /**
     * A static "these items go in, this comes out" display, for machines like the Synthesizer
     * whose real recipe accepts arbitrary items (so there's no one fixed {@link Crafting} recipe
     * to point at) — {@code inputs} is shown as an example combination, not the only valid one.
     */
    record Combine(List<ResourceLocation> inputs, ResourceLocation output, String textKey) implements GuidePage {}

    static GuidePage parse(JsonObject json, String keyBase, int pageIndex) {
        String type = GsonHelper.getAsString(json, "type");
        String titleKey = keyBase + ".page" + pageIndex + ".title";
        String textKey = keyBase + ".page" + pageIndex + ".text";
        return switch (type) {
            case "text" -> new Text(titleKey, textKey);
            case "spotlight" -> new Spotlight(
                    ResourceLocation.parse(GsonHelper.getAsString(json, "item")),
                    GsonHelper.getAsString(json, "strain", ""),
                    textKey);
            case "crafting" -> new Crafting(ResourceLocation.parse(GsonHelper.getAsString(json, "recipe")), textKey);
            case "smelting" -> new Smelting(ResourceLocation.parse(GsonHelper.getAsString(json, "recipe")), textKey);
            case "image" -> new Image(ResourceLocation.parse(GsonHelper.getAsString(json, "image")), textKey);
            case "combine" -> {
                JsonArray arr = GsonHelper.getAsJsonArray(json, "inputs");
                List<ResourceLocation> inputs = new ArrayList<>();
                for (var element : arr) inputs.add(ResourceLocation.parse(element.getAsString()));
                yield new Combine(inputs, ResourceLocation.parse(GsonHelper.getAsString(json, "output")), textKey);
            }
            default -> throw new IllegalArgumentException("Unknown guide page type: " + type);
        };
    }
}
