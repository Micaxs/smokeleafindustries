package net.micaxs.smokeleaf.client.guide;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import com.google.gson.JsonObject;

/**
 * One top-level tab in the guide book's left-side category rail. Loaded from
 * {@code assets/<namespace>/guide/categories/<name>.json} by {@link GuideDataLoader}.
 */
public record GuideCategory(ResourceLocation id, ResourceLocation icon, int sortnum) {

    private String keyBase() {
        return "guide." + id.getNamespace() + ".category." + id.getPath().replace('/', '.');
    }

    public Component name() {
        return GuideText.translatableOrPath(keyBase() + ".name", id.getPath());
    }

    public Component description() {
        return GuideText.translatableOrPath(keyBase() + ".description", "");
    }

    public static GuideCategory parse(ResourceLocation id, JsonObject json) {
        ResourceLocation icon = ResourceLocation.parse(GsonHelper.getAsString(json, "icon"));
        int sortnum = GsonHelper.getAsInt(json, "sortnum", 0);
        return new GuideCategory(id, icon, sortnum);
    }
}
