package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.network.StrainBookDataPayload;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StrainBookItem extends Item {

    private static final String TAG_DISCOVERIES = "smokeleafindustries:strain_discoveries";

    public StrainBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        ServerPlayer sp = (ServerPlayer) player;
        List<String> myIds = loadDiscoveries(sp);

        Map<String, String> myStrains = new HashMap<>();
        for (String id : myIds) {
            String name = resolveDisplayName(sp.server, id);
            if (name != null && !name.isBlank()) {
                myStrains.put(id, name);
            }
        }

        Map<String, StrainBookDataPayload.ServerStrain> global = new HashMap<>();
        for (ServerPlayer other : sp.server.getPlayerList().getPlayers()) {
            if (other.getUUID().equals(sp.getUUID())) continue;
            List<String> otherIds = loadDiscoveries(other);
            String otherName = other.getName().getString();
            for (String id : otherIds) {
                if (myStrains.containsKey(id)) continue;
                String name = resolveDisplayName(sp.server, id);
                if (name == null || name.isBlank()) continue;
                global.computeIfAbsent(id, k -> new StrainBookDataPayload.ServerStrain(name, new ArrayList<>()))
                        .discoverers().add(otherName);
            }
        }

        sp.connection.send(new StrainBookDataPayload(myStrains, global));
        return InteractionResultHolder.success(stack);
    }

    private static String resolveDisplayName(MinecraftServer server, String strainId) {
        Optional<StrainData> preset = StrainRegistry.get(strainId);
        if (preset.isPresent() && !preset.get().displayName().isBlank()) {
            return preset.get().displayName();
        }
        String name = StrainRegistrySavedData.get(server).lookupName(strainId);
        return (name != null && !name.isBlank()) ? name : null;
    }

    public static void addDiscovery(Player player, String strainId) {
        if (player == null || strainId == null || strainId.isBlank()) return;
        var tag = player.getPersistentData().getList(TAG_DISCOVERIES, Tag.TAG_STRING);
        for (Tag t : tag) {
            if (t.getAsString().equals(strainId)) return;
        }
        tag.add(StringTag.valueOf(strainId));
        player.getPersistentData().put(TAG_DISCOVERIES, tag);
    }

    public static List<String> loadDiscoveries(Player player) {
        List<String> result = new ArrayList<>();
        var tag = player.getPersistentData().getList(TAG_DISCOVERIES, Tag.TAG_STRING);
        for (Tag t : tag) {
            result.add(t.getAsString());
        }
        return result;
    }
}
