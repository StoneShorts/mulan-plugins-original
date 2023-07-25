package net.runelite.client.plugins.freeworldvorkath;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;

import java.util.Arrays;

public enum PrayerRestoreType
{
    PRAYER_POTION(new int[] { 143, 141, 139, 2434 }),
    SUPER_RESTORE(new int[] { 3030, 3028, 3026, 3024, 24605, 24603, 24601, 24598 }),
    RANGED(new int[] { 24635, 24638, 24641, 24644, 23733, 23736, 23739, 23742, 22470, 22467, 22464, 22461, 173, 171, 169, 2444 }),
    ANTI_FIRE(new int[] { 11951, 11953, 11955, 11957, 22218, 22215, 22212, 22209 }),
    ANTI_VENOM(new int[] { 5958, 5956, 5954, 5952, 12919, 12917, 12915, 12913 }),
    SANFEW_SERUM(new int[] { 10931, 10929, 10927, 10925 }),
    COMBAT(new int[] { 23685, 23688, 23691, 23694, 12701, 12699, 12697, 12695, 9745, 9743, 9741, 9739 });
    
    public int[] ItemIDs;
    
    PrayerRestoreType(int[] itemIDs) {
        this.ItemIDs = itemIDs;
    }
    
    public boolean containsId( int n2) {
        return Arrays.stream(this.ItemIDs).anyMatch(n -> n == n2);
    }
    
    public WidgetItem getItemFromInventory( Client client) {
        Widget widget;
        if ((widget = client.getWidget(WidgetInfo.INVENTORY)) == null) {
            return null;
        }
        for (WidgetItem widgetItem : widget.getWidgetItems()) {
            if (Arrays.stream(this.ItemIDs).anyMatch(n -> n == widgetItem.getId())) {
                return widgetItem;
            }
        }
        return null;
    }
}
