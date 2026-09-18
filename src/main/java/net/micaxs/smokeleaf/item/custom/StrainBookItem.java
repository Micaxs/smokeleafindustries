package net.micaxs.smokeleaf.item.custom;

import net.micaxs.smokeleaf.network.StrainDataPadPayload;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.micaxs.smokeleaf.strain.StrainRegistrySavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The "Strain Data Pad" — right-click in the main hand to open a screen listing every strain
 * you've personally picked up (with live THC/CBD/N-P-K stats and item previews) and every strain
 * registered server-wide (with its discoverer). See {@link StrainDataPadPayload}.
 */
public class StrainBookItem extends Item {

    private static final String TAG_DISCOVERIES = "smokeleafindustries:strain_discoveries";

    public StrainBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        ServerPlayer sp = (ServerPlayer) player;
        StrainRegistrySavedData registry = StrainRegistrySavedData.get(sp.server);
        String selfName = sp.getName().getString();

        java.util.Set<String> seen = new java.util.HashSet<>();
        List<StrainDataPadPayload.Entry> personal = new ArrayList<>();
        for (String id : loadDiscoveries(sp)) {
            StrainData data = resolveStrainData(registry, id);
            if (data == null) continue;
            StrainRegistrySavedData.StrainEntry regEntry = registry.lookup(id);
            String creator = (regEntry != null) ? regEntry.creatorName() : "";
            personal.add(new StrainDataPadPayload.Entry(id, data, creator));
            seen.add(id);
        }
        // Creating/naming a strain (Strain Identifier, Mutator) never fires the item-entity
        // pickup event that normally records a discovery, so also fold in any registry entry
        // this player is credited as the creator of — covers strains made before this behavior
        // was added, with no per-player migration needed.
        for (var e : registry.allEntries().entrySet()) {
            if (seen.contains(e.getKey())) continue;
            StrainRegistrySavedData.StrainEntry regEntry = e.getValue();
            if (regEntry.displayName() == null || regEntry.displayName().isBlank()) continue;
            if (!selfName.equalsIgnoreCase(regEntry.creatorName())) continue;
            personal.add(new StrainDataPadPayload.Entry(e.getKey(), strainDataFromEntry(regEntry), regEntry.creatorName()));
            seen.add(e.getKey());
        }
        personal.sort(Comparator.comparing(e -> e.data().displayName(), String.CASE_INSENSITIVE_ORDER));

        List<StrainDataPadPayload.Entry> serverStrains = new ArrayList<>();
        for (var e : registry.allEntries().entrySet()) {
            StrainRegistrySavedData.StrainEntry regEntry = e.getValue();
            if (regEntry.displayName() == null || regEntry.displayName().isBlank()) continue;
            serverStrains.add(new StrainDataPadPayload.Entry(e.getKey(), strainDataFromEntry(regEntry), regEntry.creatorName()));
        }
        serverStrains.sort(Comparator.comparing(e -> e.data().displayName(), String.CASE_INSENSITIVE_ORDER));

        sp.connection.send(new StrainDataPadPayload(personal, serverStrains));
        return InteractionResultHolder.success(stack);
    }

    @Nullable
    private static StrainData resolveStrainData(StrainRegistrySavedData registry, String strainId) {
        Optional<StrainData> preset = StrainRegistry.get(strainId);
        if (preset.isPresent()) return preset.get();
        StrainRegistrySavedData.StrainEntry entry = registry.lookup(strainId);
        if (entry != null && entry.displayName() != null && !entry.displayName().isBlank()) {
            return strainDataFromEntry(entry);
        }
        return null;
    }

    private static StrainData strainDataFromEntry(StrainRegistrySavedData.StrainEntry entry) {
        return new StrainData(
                entry.colorArgb() != null ? entry.colorArgb() : StrainData.EMPTY.colorArgb(),
                entry.leafColor() != null ? entry.leafColor() : StrainData.EMPTY.leafColor(),
                entry.thc() != null ? entry.thc() : 0,
                entry.cbd() != null ? entry.cbd() : 0,
                entry.nitrogen() != null ? entry.nitrogen() : 0,
                entry.phosphorus() != null ? entry.phosphorus() : 0,
                entry.potassium() != null ? entry.potassium() : 0,
                List.of(), 0, 0,
                true, entry.displayName(),
                StrainData.TypeColors.NONE,
                entry.baseStrain1(), entry.baseStrain2()
        );
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.smokeleafindustries.strain_book").withStyle(ChatFormatting.GRAY));
    }
}
