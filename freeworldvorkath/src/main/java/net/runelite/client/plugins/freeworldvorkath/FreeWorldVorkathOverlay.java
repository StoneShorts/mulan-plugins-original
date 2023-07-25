package net.runelite.client.plugins.freeworldvorkath;

import com.openosrs.client.ui.overlay.components.table.TableAlignment;
import com.openosrs.client.ui.overlay.components.table.TableComponent;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

@Singleton
class FreeWorldVorkathOverlay extends OverlayPanel {

    private final FreeWorldVorkathPlugin plugin;

    private final FreeWorldVorkathConfig config;

    private static final Logger log = LoggerFactory.getLogger(FreeWorldVorkathOverlay.class);
    String timeFormat;
    private String statusInfo;

    @Inject
    private FreeWorldVorkathOverlay(final Client client, final FreeWorldVorkathPlugin plugin, final FreeWorldVorkathConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        statusInfo = "Starting...";
        setPosition(OverlayPosition.BOTTOM_LEFT);
        getMenuEntries().add(new OverlayMenuEntry(
                MenuAction.RUNELITE_OVERLAY_CONFIG,
                "Configure",
                "Vorkath overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if (plugin.botTimer == null || !config.enableUI()) {
            log.debug("Overlay conditions not met, not starting overlay");
            return null;
        }
        TableComponent tableComponent;
        (tableComponent = new TableComponent()).setColumnAlignments(
                TableAlignment.LEFT,
                TableAlignment.RIGHT);

        Duration between = Duration.between(plugin.botTimer, Instant.now());

        timeFormat = (between.toHours() < 1L) ? "mm:ss" : "HH:mm:ss";

        tableComponent.addRow("Time running:", DurationFormatUtils.formatDuration(between.toMillis(), timeFormat));

        if (plugin.currentTask != null && !plugin.currentTask.name().equals("TIMEOUT"))
            statusInfo = plugin.currentTask.name();
        tableComponent.addRow("Status:", statusInfo);
        tableComponent.addRow("KC until teleport",String.valueOf(FreeWorldVorkathPlugin.killCount),"/" , String.valueOf(config.killCountTele()));
        tableComponent.addRow("Total killcount:", String.valueOf(FreeWorldVorkathPlugin.killCountTotal));

        TableComponent tableComponent2;

        (tableComponent2 = new TableComponent()).setColumnAlignments(
                TableAlignment.LEFT,
                TableAlignment.RIGHT);

        if (!tableComponent.isEmpty()) {
            panelComponent.setBackgroundColor(new Color(102, 255, 255, 70));
            panelComponent.setPreferredSize(new Dimension(200, 200));
            panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
            panelComponent.getChildren().add(TitleComponent.builder().text("Free World Vorkath").color(ColorUtil.fromHex("#ffffff")).build());
            panelComponent.getChildren().add(tableComponent);
            panelComponent.getChildren().add(tableComponent2);
        }
        return super.render(graphics);
    }


}
