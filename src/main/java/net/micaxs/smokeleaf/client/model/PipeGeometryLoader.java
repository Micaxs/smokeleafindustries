package net.micaxs.smokeleaf.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

/**
 * Reads the pipe block's custom-loader model. The model JSON carries no data of its own (the
 * loader is the only key it needs) — all real geometry comes from the {@code PipeBlockEntity}'s
 * {@code ModelData} at render time, since it depends on per-position state that can't live in
 * {@code BlockState} (see the pipe system plan). One stateless singleton is enough.
 */
public class PipeGeometryLoader implements IGeometryLoader<PipeUnbakedGeometry> {
    public static final PipeGeometryLoader INSTANCE = new PipeGeometryLoader();

    private PipeGeometryLoader() {}

    @Override
    public PipeUnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return PipeUnbakedGeometry.INSTANCE;
    }
}
