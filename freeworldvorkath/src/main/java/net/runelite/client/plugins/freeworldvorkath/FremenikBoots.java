package net.runelite.client.plugins.freeworldvorkath;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;

import java.util.Arrays;

@Getter
public enum FremenikBoots {
    Fremennik_sea_boots(new int[] {13131});


    public int[] ItemIDs;

    FremenikBoots(int[] itemIDs) {
        this.ItemIDs = itemIDs;
    }

    public boolean containsId( int n2) {
        return Arrays.stream(this.ItemIDs).anyMatch(n -> n == n2);
    }

    public WidgetItem getItemFromInventory(Client client) {
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
