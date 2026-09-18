package net.micaxs.smokeleaf.client.guide;

import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Every string the guide book shows is a translation key derived from the entry's own resource
 * location, never literal text embedded in the content JSON — this is what makes "add a
 * translation" purely a matter of adding keys to a lang file, with no bespoke i18n plumbing.
 */
public final class GuideText {
    private GuideText() {}

    /** True if a translation exists for this key in the active language — used to skip an optional title/caption that was never written. */
    public static boolean has(String key) {
        return Language.getInstance().has(key);
    }

    /** The translated component, or the raw path of {@code fallback} if no translation exists yet (keeps freshly-scaffolded entries visibly non-empty during authoring). */
    public static Component translatableOrPath(String key, String fallback) {
        return has(key) ? Component.translatable(key) : Component.literal(fallback);
    }

    public static Component translatable(String key) {
        return Component.translatable(key);
    }

    /**
     * Resolves the translated string for {@code key} and interprets its {@code $(bold)...$()} /
     * {@code $(br)} mini-markup — the same two codes used throughout this mod's authored guide
     * content — into a real styled, multi-line {@link Component}. Plain {@link Component#translatable}
     * would just print those literal characters, which is why body/caption text always goes
     * through this instead.
     */
    public static Component parseFormatted(String key) {
        String raw = Language.getInstance().getOrDefault(key);
        return parseMarkup(raw);
    }

    private static Component parseMarkup(String raw) {
        MutableComponent result = Component.empty();
        StringBuilder buf = new StringBuilder();
        boolean bold = false;
        int i = 0;
        int len = raw.length();
        while (i < len) {
            if (raw.startsWith("$(bold)", i)) {
                flush(result, buf, bold);
                bold = true;
                i += 7;
            } else if (raw.startsWith("$()", i)) {
                flush(result, buf, bold);
                bold = false;
                i += 3;
            } else if (raw.startsWith("$(br)", i)) {
                flush(result, buf, bold);
                result.append(Component.literal("\n"));
                i += 5;
            } else {
                buf.append(raw.charAt(i));
                i++;
            }
        }
        flush(result, buf, bold);
        return result;
    }

    private static void flush(MutableComponent result, StringBuilder buf, boolean bold) {
        if (!buf.isEmpty()) {
            MutableComponent piece = Component.literal(buf.toString());
            if (bold) piece = piece.withStyle(ChatFormatting.BOLD);
            result.append(piece);
            buf.setLength(0);
        }
    }
}
