package net.runelite.client.plugins.shayzienagility;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.autils.AUtils;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.time.Instant;

import static net.runelite.client.plugins.shayzienagility.ShayzienAgilityState.*;


@Extension
@PluginDependency(AUtils.class)
@PluginDescriptor(
    name = "Mulan Shayzien Agility Course",
    enabledByDefault = false,
    description = "Shayzien Agility runner",
    tags = {"shayzien", "agility", "course", "mulan"}
)
@Slf4j
public class ShayzienAgilityPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    public ReflectBreakHandler chinBreakHandler;

    @Inject
    private ShayzienAgilityConfig config;

    @Inject
    private AUtils utils;

    @Inject
    private ConfigManager configManager;

    @Inject
    ClientThread clientThread;

    @Inject
    OverlayManager overlayManager;

    @Inject
    private ShayzienAgilityOverlay overlay;

    @Inject
    private ItemManager itemManager;

    int count = 0;
    ShayzienAgilityState state;
    MenuEntry targetMenu;
    WorldPoint skillLocation;
    Instant botTimer;
    LocalPoint beforeLoc;
    Player player;

    int timeout = 0;
    long sleepLength;
    boolean startRun;

    @Provides
    ShayzienAgilityConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ShayzienAgilityConfig.class);
    }

    @Override
    protected void startUp() {
        resetVals();
        chinBreakHandler.registerPlugin(this);
        if (startRun) {
            startRun = false;
            state = null;
            targetMenu = null;
            botTimer = Instant.now();
            setLocation();
            overlayManager.add(overlay);
            chinBreakHandler.startPlugin(this);
        } else {
            startRun = false;
            chinBreakHandler.stopPlugin(this);
            resetVals();
        }
    }

    @Override
    protected void shutDown() {
        resetVals();
        chinBreakHandler.unregisterPlugin(this);
        overlayManager.remove(overlay);
        log.info("Plugin stopped");
        startRun = false;
    }

    private void resetVals() {
        overlayManager.remove(overlay);
        state = null;
        timeout = 0;
        botTimer = null;
        skillLocation = null;
        startRun = false;
        chinBreakHandler.unregisterPlugin(this);
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked) {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("ShayzienAgility")) {
            return;
        }
        log.info("button {} pressed!", configButtonClicked.getKey());
        if (configButtonClicked.getKey().equals("startButton")) {
            startRun = true;
            state = null;
            targetMenu = null;
            botTimer = Instant.now();
            setLocation();
            overlayManager.add(overlay);
            chinBreakHandler.startPlugin(this);
        } else if (configButtonClicked.getKey().equals("stopButton")) {
            startRun = false;
            chinBreakHandler.stopPlugin(this);
            resetVals();
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("ShayzienAgility")) {
            return;
        }
        startRun = false;
        resetVals();
    }

    public void setLocation() {
        if (client != null
                && client.getLocalPlayer() != null
                && client.getGameState().equals(GameState.LOGGED_IN)) {
            skillLocation = client.getLocalPlayer().getWorldLocation();
            beforeLoc = client.getLocalPlayer().getLocalLocation();
        } else {
            log.debug("Tried to start bot before being logged in");
            skillLocation = null;
            resetVals();
        }
    }

    private long sleepDelay() {
        sleepLength = utils.randomDelay(
                config.sleepWeightedDistribution(),
                config.sleepMin(),
                config.sleepMax(),
                config.sleepDeviation(),
                config.sleepTarget());
        return sleepLength;
    }

    private int tickDelay() {
        int tickLength = (int) utils.randomDelay(config.tickDelayWeightedDistribution(),
                config.tickDelayMin(),
                config.tickDelayMax(),
                config.tickDelayDeviation(),
                config.tickDelayTarget());

        log.debug("tick delay for {} ticks", tickLength);
        return tickLength;
    }


    private ShayzienAgilityState getBankState() {
        if (config.Type() == ShayzienAgilityType.BASIC) {
            // banking is unnecessary
            return UNHANDLED_STATE;
        }
        if (config.Type() == ShayzienAgilityType.ADVANCED) {
            // banking is unnecessary
            return UNHANDLED_STATE;
        }
        return TIMEOUT;
    }

    public ShayzienAgilityState getState() {
        if (timeout > 0) {
            return TIMEOUT;
        } else if (utils.isMoving(beforeLoc)) {
            timeout = 2 + tickDelay();
            return MOVING;
        } else if (utils.isBankOpen()) {
            return getBankState();
        } else if (client.getLocalPlayer().getAnimation() != -1) {
            return ANIMATING;
        } else if (client.getLocalPlayer().getAnimation() != -1
                && client.getLocalPlayer().getAnimation() != 7202) {
            return ANIMATING;
        } else {
            return AgilityState();
        }


    }



    /*
    private void openBank()
    {
        NPC geNPC = new NPCQuery()
                .idEquals(1634,3089,1633,1613)
                .result(client)
                .nearestTo(client.getLocalPlayer());
        if (geNPC != null) {
            clientThread.invoke(() -> client.invokeMenuAction("", "", geNPC.getIndex(), MenuAction.NPC_THIRD_OPTION.getId(), geNPC.getLocalLocation().getX(), geNPC.getLocalLocation().getY()));
        }
            return;
        }
     */


    private void useGameObject(int id, int opcode) {
        GameObject targetObject = utils.findNearestGameObject(id);
        if (targetObject != null) {
            clientThread.invoke(() -> client.invokeMenuAction("", "", targetObject.getId(), opcode, targetObject.getSceneMinLocation().getX(), targetObject.getSceneMinLocation().getY()));
        }
    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        if (!startRun) {
            return;
        }
        if (chinBreakHandler.isBreakActive(this)) {
            return;
        }
        if (chinBreakHandler.shouldBreak(this)) {
            chinBreakHandler.startBreak(this);
        }
        player = client.getLocalPlayer();
        if (client != null && player != null && skillLocation != null) {
            if (!client.isResized()) {
                utils.sendGameMessage("client must be set to resizable");
                startRun = true;
                return;
            }
            state = getState();
            beforeLoc = player.getLocalLocation();
            utils.setMenuEntry(null);
            switch (state) {
                case TIMEOUT:
                    utils.handleRun(30, 20);
                    timeout--;
                    break;
                case ANIMATING:
                case MOVING:
                    utils.handleRun(30, 20);
                    timeout = tickDelay();
                    break;
                /*

                case RUN:
                    utils.walk(new WorldPoint(3249, 6065, 0));
                    timeout = tickDelay();
                    break;

                */
                case OBST1:
                    utils.useGameObject(42209, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST2:
                    utils.useGameObject(42211, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST3:
                    utils.useGroundObject(42212,3, sleepDelay());
                    //utils.useGameObject(42212, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST4:
                    utils.useGameObject(42213, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST5:
                    utils.useGroundObject(42214, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST6:
                    utils.useGroundObject(42215, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST7:
                    utils.useGameObject(42216, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST8:
                    utils.useGameObject(42217, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST9:
                    utils.useGameObject(42218, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST10:
                    utils.useGameObject(42219, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST11:
                    utils.useGameObject(42220, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case OBST12:
                    utils.useGameObject(42221, 3, sleepDelay());
                    timeout = tickDelay();

            }
        }
    }


    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN && startRun) {
            state = TIMEOUT;
            timeout = 2;
        }
    }


    private ShayzienAgilityState AgilityState() {
        if (config.Type() == ShayzienAgilityType.BASIC) {
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1508, 3613, 0), new WorldPoint(1556, 3642, 0)))) {
                return OBST1;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1553, 3631, 3), new WorldPoint(1555, 3634, 3)))) {
                return OBST2;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1541, 3632, 2), new WorldPoint(1541, 3634, 2)))) {
                return OBST3;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1527, 3633, 2), new WorldPoint(1529, 3633, 2)))) {
                return OBST4;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1522, 3643, 3), new WorldPoint(1524, 3645, 3)))) {
                return OBST5;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1538, 3643, 2), new WorldPoint(1540, 3645, 2)))) {
                return OBST6;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1551, 3644, 2), new WorldPoint(1553, 3644, 2)))) {
                return OBST7;
            }


            return UNHANDLED_STATE;
        }
        if (config.Type() == ShayzienAgilityType.ADVANCED) {
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1508, 3613, 0), new WorldPoint(1556, 3642, 0)))) {
                return OBST1;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1553, 3631, 3), new WorldPoint(1555, 3634, 3)))) {
                return OBST2;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1541, 3632, 2), new WorldPoint(1541, 3634, 2)))) {
                return OBST3;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1527, 3633, 2), new WorldPoint(1529, 3633, 2)))) {
                return OBST8;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1510, 3637, 2), new WorldPoint(1512, 3637, 2)))) {
                return OBST9;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1510, 3629, 2), new WorldPoint(1510, 3631, 2)))) {
                return OBST10;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1509, 3620, 2), new WorldPoint(1511, 3620, 2)))) {
                return OBST11;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(1520, 3619, 2), new WorldPoint(1522, 3619, 2)))) {
                return OBST12;
            }


            return UNHANDLED_STATE;
        }


        return UNHANDLED_STATE;
    }
}


