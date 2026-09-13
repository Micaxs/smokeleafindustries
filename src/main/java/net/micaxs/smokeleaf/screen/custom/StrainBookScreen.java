package net.micaxs.smokeleaf.screen.custom;

import net.micaxs.smokeleaf.network.StrainBookDataPayload;
import net.micaxs.smokeleaf.strain.StrainData;
import net.micaxs.smokeleaf.strain.StrainRegistry;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StrainBookScreen extends BookViewScreen {

    public StrainBookScreen(Map<String, String> myStrains, Map<String, StrainBookDataPayload.ServerStrain> globalDiscoveries) {
        super(new BookAccess(buildPages(myStrains, globalDiscoveries)));
    }

    private static List<Component> buildPages(Map<String, String> myStrains, Map<String, StrainBookDataPayload.ServerStrain> globalDiscoveries) {
        List<Component> pages = new ArrayList<>();

        List<Map.Entry<String, String>> mySorted = myStrains.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        int idx = 0;
        while (idx < mySorted.size()) {
            StringBuilder sb = new StringBuilder();
            if (idx == 0) sb.append("My Discoveries\n\n");
            int end = Math.min(idx + 6, mySorted.size());
            for (int i = idx; i < end; i++) {
                var e = mySorted.get(i);
                sb.append(e.getValue()).append('\n');
                String info = baseStrainInfo(e.getKey());
                sb.append(info).append('\n');
            }
            pages.add(Component.literal(sb.toString()));
            idx = end;
        }

        List<Map.Entry<String, StrainBookDataPayload.ServerStrain>> globalSorted = globalDiscoveries.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((a, b) -> a.displayName().compareToIgnoreCase(b.displayName())))
                .toList();

        idx = 0;
        while (idx < globalSorted.size()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Server Strains\n\n");
            int end = Math.min(idx + 6, globalSorted.size());
            for (int i = idx; i < end; i++) {
                var e = globalSorted.get(i);
                sb.append(e.getValue().displayName()).append('\n');
                String discoverers = String.join(", ", e.getValue().discoverers());
                sb.append("  by ").append(discoverers).append('\n');
            }
            pages.add(Component.literal(sb.toString()));
            idx = end;
        }

        if (pages.isEmpty()) {
            pages.add(Component.translatable("gui.smokeleafindustries.strain_book.no_strains"));
        }

        return pages;
    }

    private static String baseStrainInfo(String strainId) {
        Optional<StrainData> d = StrainRegistry.get(strainId);
        if (d.isPresent()) {
            String b1 = d.get().baseStrain1();
            String b2 = d.get().baseStrain2();
            if (!b1.isBlank() && !b2.isBlank()) {
                return "  " + Component.translatable("gui.smokeleafindustries.strain_book.cross", b1, b2).getString();
            }
        }
        return "";
    }
}
