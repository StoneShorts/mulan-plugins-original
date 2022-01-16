package net.runelite.client.plugins.crystalkeyenhancer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.WidgetInfo;
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
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.autils.Banks.BANK_SET;
import static net.runelite.client.plugins.crystalkeyenhancer.CrystalKeyEnhancerState.*;


@Extension
@PluginDependency(AUtils.class)
@PluginDescriptor(
    name = "Mulan Crystal Key Enhancer",
    enabledByDefault = false,
    description = "Crystal Key Enhancer",
    tags = {"crystal", "key", "enhancer", "mulan92"}
)
@Slf4j
public class CrystalKeyEnhancerPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    public ReflectBreakHandler chinBreakHandler;

    @Inject
    private CrystalKeyEnhancerConfig config;

    @Inject
    private AUtils utils;

    @Inject
    private ConfigManager configManager;

    @Inject
    ClientThread clientThread;

    @Inject
    OverlayManager overlayManager;

    @Inject
    private CrystalKeyEnhancerOverlay overlay;

    @Inject
    private ItemManager itemManager;

    int count = 0;
    CrystalKeyEnhancerState state;
    MenuEntry targetMenu;
    WorldPoint skillLocation;
    Instant botTimer;
    LocalPoint beforeLoc;
    Player player;

    int timeout = 0;
    long sleepLength;
    boolean startRun;
    List<Integer> GE = new ArrayList<>();
    List<Integer> SB = new ArrayList<>();
    List<Integer> REQ = new ArrayList<>();

    @Provides
    CrystalKeyEnhancerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(CrystalKeyEnhancerConfig.class);
    }

    @Override
    protected void startUp()
    {
        resetVals();
        chinBreakHandler.registerPlugin(this);
        if (startRun)
        {
            startRun = false;
            state = null;
            targetMenu = null;
            botTimer = Instant.now();
            setLocation();
            overlayManager.add(overlay);
            chinBreakHandler.startPlugin(this);
        }
        else
        {
            startRun=false;
            chinBreakHandler.stopPlugin(this);
            resetVals();
        }
    }

    @Override
    protected void shutDown()
    {
        resetVals();
        chinBreakHandler.unregisterPlugin(this);
        overlayManager.remove(overlay);
        log.info("Plugin stopped");
        startRun=false;
    }

    private void resetVals()
    {
        overlayManager.remove(overlay);
        state = null;
        timeout = 0;
        botTimer = null;
        skillLocation = null;
        startRun=false;
        chinBreakHandler.unregisterPlugin(this);
    }

    @Subscribe
    private void onConfigButtonPressed(ConfigButtonClicked configButtonClicked)
    {
        if (!configButtonClicked.getGroup().equalsIgnoreCase("CrystalKeyEnhancer"))
        {
            return;
        }
        log.info("button {} pressed!", configButtonClicked.getKey());
        if (configButtonClicked.getKey().equals("startButton"))
        {
            startRun = true;
            state = null;
            targetMenu = null;
            botTimer = Instant.now();
            setLocation();
            overlayManager.add(overlay);
            chinBreakHandler.startPlugin(this);
            GE.addAll(BANK_SET);
            GE.add(10517);
            GE.add(31582);
            GE.add(26707);
            GE.add(26711);
            //GE.add(10583);
            SB.add(36552);
        }
        else
        {
            startRun=false;
            chinBreakHandler.stopPlugin(this);
            resetVals();
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("CrystalKeyEnhancer"))
        {
            return;
        }
        startRun = false;
        resetVals();
    }

    public void setLocation()
    {
        if (client != null
                && client.getLocalPlayer() != null
                && client.getGameState().equals(GameState.LOGGED_IN))
        {
            skillLocation = client.getLocalPlayer().getWorldLocation();
            beforeLoc = client.getLocalPlayer().getLocalLocation();
        }
        else
        {
            log.debug("Tried to start bot before being logged in");
            skillLocation = null;
            resetVals();
        }
    }

    private long sleepDelay()
    {
        sleepLength = utils.randomDelay(
                config.sleepWeightedDistribution(),
                config.sleepMin(),
                config.sleepMax(),
                config.sleepDeviation(),
                config.sleepTarget());
        return sleepLength;
    }

    private int tickDelay()
    {
        int tickLength = (int) utils.randomDelay(config.tickDelayWeightedDistribution(),
                config.tickDelayMin(),
                config.tickDelayMax(),
                config.tickDelayDeviation(),
                config.tickDelayTarget());

        log.debug("tick delay for {} ticks", tickLength);
        return tickLength;
    }

    private void singBowl() {
        GameObject singBowl = new GameObjectQuery()
                .idEquals(SB)
                .result(client)
                .nearestTo(client.getLocalPlayer());
        if (singBowl != null) {
            if (singBowl.getId() == 36552) {
                client.invokeMenuAction("", "",
                        singBowl.getId(), 3,
                        singBowl.getLocalLocation().getSceneX(),
                        singBowl.getLocalLocation().getSceneY() - 1);
            }
        }
    }



    private CrystalKeyEnhancerState getBankState() {
        if (config.Type() == CrystalKeyEnhancerType.MAKE_KEYS) {
            if (utils.inventoryContains(23951)
                    && (utils.inventoryContains(8007))
                    && (utils.inventoryContains(23946))) {
                return DEPOSIT_ALL_ITEMS;
                //8007 varrock teletab //23946 teleport crystal //23962 crystal shards //23951 crystal keys //23951 enhanced crystal key
            }

            if (utils.bankContains(23962, 10) && (utils.bankContains(989, 1)) && utils.inventoryEmpty()) {
                return WITHDRAW_TELE_PRIF;
            }

            if (utils.inventoryContains(23946)
                    && (!utils.inventoryContains(8007))) {
                return WITHDRAW_TELE_GE;
                //8007 varrock teletab //23946 tele crystal //23962 crys shards //23951 crys keys //23951 enh crystal key
            }
            if (utils.inventoryContains(23946)
                    && (utils.inventoryContains(8007))
                    && (!utils.inventoryContains(23962))) {
                return WITHDRAW_SHARDS;
                //8007 varrock teletab //23946 tele crystal //23962 crys shards //23951 crys keys //23951 enh crystal key
            }
            if (utils.inventoryContains(23946)
                    && (utils.inventoryContains(8007))
                    && (utils.inventoryContains(23962))
                    && (!utils.inventoryContains(989))) {
                return WITHDRAW_KEYS;
                //8007 varrock teletab //23946 tele crystal //23962 crys shards //23951 crys keys //23951 enh crystal key
            }

            if (utils.inventoryContains(989)
                    && utils.inventoryContains(23946)
                    && utils.inventoryContainsStack(23962, 10)
                    && utils.inventoryContains(8007)) {
                utils.closeBank();
                return TELEPORT_PRIF;
            }
            return UNHANDLED_STATE;
        }
        if (config.Type() == CrystalKeyEnhancerType.OPEN_KEYS) {
            if (utils.inventoryEmpty()
                    && utils.bankContains(23951, 1) //enh crys key
                    && utils.bankContains(8007, 1) // varrock tele
                    && utils.bankContains(23946, 1)) { //tele crystal
                return WITHDRAW_E_KEYS;
            }
            if (utils.inventoryContains(23951) && (!utils.inventoryContains(8007) && (!utils.inventoryContains(23946)))){
                return WITHDRAW_TELE_GE;
            }
            if (utils.inventoryContains(8007) && (utils.inventoryContains(23951) && (!utils.inventoryContains(23946)))){
                return WITHDRAW_TELE_PRIF;
            }
            if (utils.inventoryContains(23951)
                    && utils.inventoryContains(23946)
                    && utils.inventoryContains(8007)) {
                utils.closeBank();
                return TELEPORT_PRIF;
            }

            if (utils.inventoryContains(1631)) {
                utils.depositAll();
                return DEPOSIT_ALL_ITEMS;
            }
            return UNHANDLED_STATE;
        }
        return TIMEOUT;
    }

    public CrystalKeyEnhancerState getState()
    {
        if (timeout > 0)
        {
            return TIMEOUT;
        }
        else if (utils.isMoving(beforeLoc))
        {
            timeout = 2 + tickDelay();
            return MOVING;
        }
        else if(utils.isBankOpen()){
            return getBankState();
        }
        else if(client.getLocalPlayer().getAnimation()!=-1){
            return ANIMATING;
        }
        else if(client.getLocalPlayer().getAnimation()!=-1
                && client.getLocalPlayer().getAnimation() != 7202){
            return ANIMATING;
        }
        else {
            return CrystalKeyState();
        }


    }



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



    private void useGameObject(int id, int opcode)
    {
        GameObject targetObject = utils.findNearestGameObject(id);
        if(targetObject!=null){
            clientThread.invoke(() -> client.invokeMenuAction("", "", targetObject.getId(),opcode,targetObject.getSceneMinLocation().getX(),targetObject.getSceneMinLocation().getY()));
        }
    }

    @Subscribe
    private void onGameTick(GameTick tick)
    {
        if (!startRun)
        {
            return;
        }
        if (chinBreakHandler.isBreakActive(this)){
            return;
        }
        if (chinBreakHandler.shouldBreak(this))
        {
            chinBreakHandler.startBreak(this);
        }
        player = client.getLocalPlayer();
        if (client != null && player != null && skillLocation != null)
        {
            if (!client.isResized())
            {
                utils.sendGameMessage("client must be set to resizable");
                startRun = true;
                return;
            }
            state = getState();
            beforeLoc = player.getLocalLocation();
            utils.setMenuEntry(null);
            switch (state)
            {
                case TIMEOUT:
                    utils.handleRun(30, 20);
                    timeout--;
                    break;
                case ANIMATING:
                case MOVING:
                    utils.handleRun(30, 20);
                    timeout = tickDelay();
                    break;
                /*case RUN:
                    utils.walk(new WorldPoint(3249, 6065, 0));
                    timeout = tickDelay();
                    break;*/
                case FIND_BANK:
                    openBank();
                    timeout = tickDelay();
                    break;
                case DEPOSIT_ALL_ITEMS:
                    utils.depositAll();
                    timeout = tickDelay();
                    break;
                case WITHDRAW_TELE_PRIF:
                    utils.withdrawItem(23946);//teleport crystal
                    timeout = tickDelay();
                    break;
                case WITHDRAW_TELE_GE:
                    utils.withdrawItemAmount(8007, 5);//varrock teletab //teleport crystal //crystal shards //crystal keys
                    timeout = tickDelay();
                    break;
                case WITHDRAW_SHARDS:
                    utils.withdrawAllItem(23962);//crystal shards
                    timeout = tickDelay();
                    break;
                case WITHDRAW_KEYS:
                    utils.withdrawAllItem(989);//crystal keys
                    timeout = tickDelay();
                    break;
                case WITHDRAW_E_KEYS:
                    utils.withdrawItemAmount(23951,5); // 5 keys anders kunnen we loot verliezen - toekomst fix = loot oprapen
                    timeout = tickDelay();
                    break;
                case TELEPORT_PRIF:
                    clientThread.invoke(() -> client.invokeMenuAction("", "", 23946, MenuAction.ITEM_SECOND_OPTION.getId(), this.utils.getInventoryWidgetItem(23946).getIndex(), WidgetInfo.INVENTORY.getId()));
                    timeout = tickDelay();
                    break;
                case TELEPORT_GE:
                    clientThread.invoke(() -> client.invokeMenuAction("", "", 8007, MenuAction.ITEM_THIRD_OPTION.getId(), this.utils.getInventoryWidgetItem(8007).getIndex(), WidgetInfo.INVENTORY.getId()));
                    timeout = tickDelay();
                    break;
                case TAKE_STAIRS:
                    utils.useGameObject(36387, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case SING_BOWL:
                    utils.useGameObject(36552, 3, sleepDelay());
                    timeout = tickDelay();
                    break;
                case SINGING:
                    clientThread.invoke(() -> client.invokeMenuAction("", "",1, 57, -1, 17694734));
                    timeout = tickDelay();
                    break;
                case OPEN_CHEST:
                    utils.useGameObject(37342/* 36582 */, 3, sleepDelay());
                    timeout = tickDelay();

            }
        }
    }


    @Subscribe
    private void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN && startRun)
        {
            state = TIMEOUT;
            timeout = 2;
        }
    }



    private CrystalKeyEnhancerState CrystalKeyState()
    {
        if (config.Type() == CrystalKeyEnhancerType.MAKE_KEYS) {
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(3261, 6063, 0), new WorldPoint(3267, 6069, 0)))) {
                return SING_BOWL;
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(3150, 3474, 0), new WorldPoint(3175, 3501, 0)))) {
                return FIND_BANK;    //Find bank after teleporting to GE and player location is between these 2 tiles
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(2899, 3379, 0), new WorldPoint(2902, 3383, 0))) && utils.inventoryFull()) {
                return DEPOSIT_ALL_ITEMS;    //On way to bank
            }
            /*if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(3248, 6064, 0), new WorldPoint(3250, 6066, 0)))) {
                return SING_BOWL;
            }*/
            if (client.getWidget(270, 5) != null) {
                if (client.getWidget(270, 5).getText().equals("How many would you like to make?")) {
                    clientThread.invoke(() -> client.invokeMenuAction("", "", 1, 57, -1, 17694732));
                    return SINGING;
                }
            }
            if (utils.inventoryContains(23951) && (utils.inventoryContains(8007))){
                return TELEPORT_GE;
            }

            return UNHANDLED_STATE;
        }
        if (config.Type() == CrystalKeyEnhancerType.OPEN_KEYS) {
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(3150, 3474, 0), new WorldPoint(3175, 3501, 0)))) {
                return FIND_BANK;    //Find bank after teleporting to GE and player location is between these 2 tiles
            }
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(3261, 6063, 0), new WorldPoint(3267, 6069, 0)))) {
                return TAKE_STAIRS;
            }
            //(Objects.equals(player.getWorldLocation(), new WorldPoint(3268, 6082, 2) Oplossing misschien?
            if (player.getWorldArea().intersectsWith(new WorldArea(new WorldPoint(3267, 6080, 2), new WorldPoint(3274, 6086, 2))) && utils.inventoryContains(23951)) {
                return OPEN_CHEST;    //opent chest na trap omhoog te gaan
            }
            if (!utils.inventoryContains(23951)){
                return TELEPORT_GE;
            }


            return UNHANDLED_STATE;
        }


        return UNHANDLED_STATE;
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event){
        if(targetMenu!=null){
            menuAction(event,targetMenu.getOption(), targetMenu.getTarget(), targetMenu.getIdentifier(), targetMenu.getMenuAction(),
                    targetMenu.getParam0(), targetMenu.getParam1());
            targetMenu = null;
        }
    }

    public void menuAction(MenuOptionClicked menuOptionClicked, String option, String target, int identifier, MenuAction menuAction, int param0, int param1)
    {
        menuOptionClicked.setMenuOption(option);
        menuOptionClicked.setMenuTarget(target);
        menuOptionClicked.setId(identifier);
        menuOptionClicked.setMenuAction(menuAction);
        menuOptionClicked.setActionParam(param0);
        menuOptionClicked.setWidgetId(param1);
    }

    private Point getRandomNullPoint()
    {
        if(client.getWidget(161,34)!=null){
            Rectangle nullArea = client.getWidget(161,34).getBounds();
            return new Point ((int)nullArea.getX()+utils.getRandomIntBetweenRange(0,nullArea.width), (int)nullArea.getY()+utils.getRandomIntBetweenRange(0,nullArea.height));
        }

        return new Point(client.getCanvasWidth()-utils.getRandomIntBetweenRange(0,2),client.getCanvasHeight()-utils.getRandomIntBetweenRange(0,2));
    }
}