package net.micaxs.smokeleaf.client.guide;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * One topic in the guide book (e.g. "Growing Hemp", "The Dryer", "OG Kush"). Loaded from
 * {@code assets/<namespace>/guide/entries/<category>/<name>.json} by {@link GuideDataLoader}; its
 * id (e.g. {@code smokeleafindustries:getting_started/hemp_plant}) is what every one of its pages'
 * translation keys is derived from.
 */
public record GuideEntry(ResourceLocation id, ResourceLocation icon, ResourceLocation category, int sortnum, List<GuidePage> pages, String strainId) {

    public String keyBase() {
        return "guide." + id.getNamespace() + "." + id.getPath().replace('/', '.');
    }

    public Component name() {
        return GuideText.translatableOrPath(keyBase() + ".name", id.getPath());
    }

    public static GuideEntry parse(ResourceLocation id, JsonObject json) {
        ResourceLocation icon = ResourceLocation.parse(GsonHelper.getAsString(json, "icon"));
        ResourceLocation category = ResourceLocation.parse(GsonHelper.getAsString(json, "category"));
        int sortnum = GsonHelper.getAsInt(json, "sortnum", 0);

        String keyBase = "guide." + id.getNamespace() + "." + id.getPath().replace('/', '.');
        List<GuidePage> pages = new ArrayList<>();
        JsonArray pagesJson = GsonHelper.getAsJsonArray(json, "pages");
        for (int i = 0; i < pagesJson.size(); i++) {
            pages.add(GuidePage.parse(pagesJson.get(i).getAsJsonObject(), keyBase, i));
        }

        // The entry's own icon (and its list-row icon) are colored using whichever strain its first
        // spotlight page names, so e.g. each preset strain's row in the list shows that strain's own
        // colors instead of always falling back to the same default (see GuideIcons).
        String strainId = "";
        for (GuidePage page : pages) {
            if (page instanceof GuidePage.Spotlight spotlight && spotlight.strainId() != null && !spotlight.strainId().isBlank()) {
                strainId = spotlight.strainId();
                break;
            }
        }

        return new GuideEntry(id, icon, category, sortnum, List.copyOf(pages), strainId);
    }
}
