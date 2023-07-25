package net.runelite.client.plugins.freeworldvorkath;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.queries.NPCQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.autils.AUtils;
import net.runelite.client.plugins.autils.CalculationUtils;
import net.runelite.client.plugins.autils.NewMenuEntry;
import net.runelite.client.plugins.autils.Spells;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.apache.commons.lang3.ArrayUtils;
import org.pf4j.Extension;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static java.awt.event.KeyEvent.VK_SPACE;
import static net.runelite.api.AnimationID.IDLE;
import static net.runelite.client.plugins.freeworldvorkath.BankAreas.Crafting_Guild;
import static net.runelite.client.plugins.freeworldvorkath.FreeWorldVorkathState.*;

@Extension
@PluginDependency(AUtils.class)
@PluginDescriptor(
        name = "FreeWorldVorkath",
        description = "Mulans Free World Vorkath",
        tags = {"vorkath", "mulan", "mplugins"},
        enabledByDefault = false
)
@Slf4j
public class FreeWorldVorkathPlugin extends Plugin {
  @Inject
  private Client client;

  @Inject
  public ReflectBreakHandler chinBreakHandler;

  @Inject
  private FreeWorldVorkathConfig config;

  @Inject
  ClientThread clientThread;

  @Inject
  private AUtils utils;

  @Inject
  private ConfigManager configManager;

  @Inject
  private InfoBoxManager infoBoxManager;

  @Inject
  OverlayManager overlayManager;

  @Inject
  private FreeWorldVorkathOverlay overlay;

  @Inject
  private ItemManager itemManager;

  @Inject
  public static CalculationUtils calc;

  @Inject
  ExecutorService executorService;

  @Inject
  public CalculationUtils calcnonStatic; //calcnonStatic.getRandomIntBetweenRange(2, 4)

  private Rectangle clickBounds;

  private int H;

  private NPC vorkath;

  private long wooxWalkTimer = -1;

  private WorldPoint[] wooxWalkPath = new WorldPoint[2];

  private final List<WorldPoint> acidSpots;

  boolean a;

  private List<WorldPoint> acidFreePath;

  private int L;

  private final Set<Integer> diamondBoltIDs;
  private final Set<Integer> rubyBoltIDs;

  WorldArea drayArea;
  WorldArea craftArea;
  WorldArea feroxArea;
  WorldArea lunarArea;
  WorldArea portalArea;
  WorldArea lunarbankArea;
  WorldArea vorkArea;
  WorldArea c;

  WorldArea d;

  WorldArea e;

  WorldArea f;

  FreeWorldVorkathState currentTask;

  LocalPoint beforeLoc;

  Player player;

  MenuEntry targetMenu; /*j*/

  LocalPoint dodgeRight;

  LocalPoint dodgeLeft;

  Instant botTimer;

  private boolean O;

  private final List<String> lootNamesList;

  private final List<TileItem> itemsToLoot;

  private Prayer R;

  boolean startRun;

  boolean q;

  private boolean noBomb;

  private boolean zombieSpawnDead;

  private int T;

  private int U;

  private int V;

  private int X;

  String[] u;

  int v;

  int x;

  private final int Y;

  private static final List<Integer> vorkathRegions = Arrays.asList(7513, 7514, 7769, 7770);
  private final static List <Integer> HouseRegion = Arrays.asList(7513, 7514 );
  private final static List <Integer> RellekkaRegion = Arrays.asList(10552, 10553 );
  private final static List <Integer> vorkIsleRegion = Arrays.asList(9023);

  private final static List <Integer> LunarRegion = Arrays.asList(8253);
  private final static List <Integer> CraftRegion = Arrays.asList(11571);


  private Instant lastAnimating;
  private int lastAnimation = IDLE;
  private int lastCombatCountdown = 0;
  private static final int LOGOUT_WARNING_MILLIS = (3 * 60 + 30) * 1000; // 3 minutes and 30 seconds
  private static final int COMBAT_WARNING_MILLIS = 19 * 60 * 1000; // 19 minutes
  private static final int LOGOUT_WARNING_CLIENT_TICKS = LOGOUT_WARNING_MILLIS / Constants.CLIENT_TICK_LENGTH;
  private static final int COMBAT_WARNING_CLIENT_TICKS = COMBAT_WARNING_MILLIS / Constants.CLIENT_TICK_LENGTH;
  private boolean notifyIdleLogout = true;
  private Actor lastInteract;
  private Instant lastInteracting;


  private int tickDelay;



  boolean banked;
  boolean talkedNPC;
  boolean usedBoat;
  boolean didObstacle;


  static int killCount;
  static int killCountTotal;


  @Provides
  FreeWorldVorkathConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(FreeWorldVorkathConfig.class);
  }

  public FreeWorldVorkathPlugin() {
    acidSpots = new ArrayList<>();
    a = true;
    acidFreePath = new ArrayList<>();
    L = 0;
    diamondBoltIDs = Set.of(21946, 9243);
    craftArea = new WorldArea(new WorldPoint(2934, 3278, 0), new WorldPoint(2938, 3282, 0));
    drayArea = new WorldArea(new WorldPoint(3087, 3238, 0), new WorldPoint(3108, 3254, 0));
    feroxArea = new WorldArea(new WorldPoint(3145, 3630, 0), new WorldPoint(3154, 3639, 0));
    lunarArea = new WorldArea(new WorldPoint(2091, 3910, 0), new WorldPoint(2119, 3929, 0));
    portalArea = new WorldArea(new WorldPoint(3323, 4748, 0), new WorldPoint(3332, 4755, 0));
    lunarbankArea = new WorldArea(new WorldPoint(2098, 3918, 0), new WorldPoint(2100, 3920, 0));
    vorkArea = new WorldArea(new WorldPoint(8021, 2774, 0), new WorldPoint(8043, 2795, 0));

    c = new WorldArea(new WorldPoint(2664, 3625, 0), new WorldPoint(2678, 3638, 0));
    d = new WorldArea(new WorldPoint(2635, 3668, 0), new WorldPoint(2652, 3684, 0));
    e = new WorldArea(new WorldPoint(2262, 4032, 0), new WorldPoint(2286, 4053, 0));
    f = new WorldArea(new WorldPoint(2259, 4053, 0), new WorldPoint(2290, 4083, 0));
    rubyBoltIDs = Set.of(21944, 9242);
    Random q1 = new Random();
    lootNamesList = new ArrayList<>();
    itemsToLoot = new ArrayList<>();
    startRun = false;
    q = false;
    noBomb = true;
    zombieSpawnDead = true;
    T = 0;
    U = -1;
    V = -1;
    X = 0;
    v = 99999990;
    Y = 0;
    O = false;
    currentTask = null;
    banked = false;
    talkedNPC = false;
    usedBoat = false;
    didObstacle = false;
  }

  private void useItem(WidgetItem item) {
    if (item != null) {
      targetMenu = (MenuEntry) new NewMenuEntry("", "", item.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), item.getIndex(),
              WidgetInfo.INVENTORY.getId(), false);

    }
  }

  private void i() {
    itemsToLoot.clear();
    lootNamesList.clear();
    u = config.lootNames().toLowerCase().split("\\s*,\\s*");
    if (!config.lootNames().isBlank())
      lootNamesList.addAll(Arrays.asList(u));
    overlayManager.remove(overlay);
    startRun = false;
    T = 0;
    U = -1;
    currentTask = null;
    q = false;
    tickDelay = 3;
    V = -1;
    X = 0;
    O = false;
    Prayer p = null;
    noBomb = true;
    zombieSpawnDead = true;
    dodgeRight = null;
    dodgeLeft = null;
    botTimer = null;
    banked = false;
    talkedNPC = false;
    usedBoat = false;
    didObstacle = false;
    chinBreakHandler.stopPlugin(this);
  }

  @Subscribe
  protected void onConfigButtonPressed(ConfigButtonClicked ConfigButtonClicked) {
    if (!ConfigButtonClicked.getGroup().equalsIgnoreCase("avork")) {
      return;
    }
    if (ConfigButtonClicked.getKey().equals("startButton")) {
        startRun = true;
        overlayManager.add(overlay);
        itemsToLoot.clear();
        lootNamesList.clear();
        u = config.lootNames().toLowerCase().split("\\s*,\\s*");
        if (!config.lootNames().isBlank())
          lootNamesList.addAll(Arrays.asList(u));
        noBomb = true;
        zombieSpawnDead = true;
        botTimer = Instant.now();
        killCount = 0;
        killCountTotal = 0;

        chinBreakHandler.startPlugin(this);
        return;
    } else if (ConfigButtonClicked.getKey().equals("stopButton")) {
      i();
    }
  }

  public int getRandomIntBetweenRange(int min, int max)
  {
    //return (int) ((Math.random() * ((max - min) + 1)) + min); //This does not allow return of negative values
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  public long getSleepDelay() {
    return utils.randomDelay(
            config.sleepWeightedDistribution(),
            config.sleepMin(),
            config.sleepMax(),
            config.sleepDeviation(),
            config.sleepTarget());
  }

  public int getTickDelay() {
    return (int)utils.randomDelay(
            config.tickDelayWeightedDistribution(),
            config.tickDelayMin(),
            config.tickDelayMax(),
            config.tickDelayDeviation(),
            config.tickDelayTarget());
  }

  protected void startUp() throws Exception {
    chinBreakHandler.registerPlugin(this);
    i();
  }

  protected void shutDown() throws Exception {
    chinBreakHandler.unregisterPlugin(this);
    i();
  }

  public static boolean isInPOH(final Client client) {
    final IntStream stream = Arrays.stream(client.getMapRegions());
    final List<Integer> regions = FreeWorldVorkathPlugin.vorkathRegions;
    Objects.requireNonNull(regions);
    return stream.anyMatch(regions::contains);
  }

  private void setSpell(WidgetInfo widgetInfo) {
    Widget w;
    if ((w = client.getWidget(widgetInfo)) == null) {
      return;
    }
    client.setSelectedSpellName("<col=00ff00>" + w.getName() + "</col>");
    client.setSelectedSpellWidget(widgetInfo.getId());
    client.setSelectedSpellChildIndex(-1);
  }

  private void findBank() {
    if (CraftRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
      utils.useGameObject(14886,3,getSleepDelay());
      tickDelay = getTickDelay();
      }
    if (player.getWorldArea().intersectsWith(drayArea)){
      NPC geNPC = new NPCQuery()
              .idEquals(1613, 1618)
              .result(client)
              .nearestTo(client.getLocalPlayer());
      if (geNPC != null) {
        clientThread.invoke(() ->
                client.invokeMenuAction("", "",
                        geNPC.getIndex(),
                        MenuAction.NPC_THIRD_OPTION.getId(),
                        geNPC.getLocalLocation().getX(),
                        geNPC.getLocalLocation().getY()));
      }
    }
    if (player.getWorldArea().intersectsWith(feroxArea)){
      utils.useGameObjectDirect(
              utils.findNearestGameObject(26645),
              getSleepDelay(),
              MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
      tickDelay = getTickDelay();
      if (player.getWorldArea().intersectsWith(portalArea)){
        utils.useGameObject(26646,3,getSleepDelay());
        tickDelay = getTickDelay();
        if (player.getWorldLocation()
                .equals(new WorldPoint(3128,3628,1))) {
          utils.useGameObject(26711,3,getSleepDelay());
          tickDelay = getTickDelay();
        }
      }
    }

    if (LunarRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
      utils.useGameObject(16700, 4, getSleepDelay());
      tickDelay = getTickDelay();
    }
  }



  private void lootItem(List<TileItem> list) {
    TileItem tileItem;
    if ((tileItem = getNearestTileItem(list)) != null) {
      TileItem tileItem1 = null;
      clientThread.invoke(() ->
              client.invokeMenuAction("", "",
                      tileItem.getId(),
                      MenuAction.GROUND_ITEM_THIRD_OPTION.getId(),
                      tileItem.getTile().getSceneLocation().getX(),
                      tileItem.getTile().getSceneLocation().getY()));
    }
  }

  private TileItem getNearestTileItem(List<TileItem> list) {
    TileItem tileItem;
    int distanceTo = (tileItem = list.get(0)).getTile().getWorldLocation().distanceTo(player.getWorldLocation());
    Iterator<TileItem> iterator = list.iterator();
    while (iterator.hasNext()) {
      TileItem tileItem2;
      int distanceTo2;
      if ((distanceTo2 = (tileItem2 = iterator.next())
              .getTile()
              .getWorldLocation()
              .distanceTo(player.getWorldLocation())) < distanceTo) {
        tileItem = tileItem2;
        distanceTo = distanceTo2;
      }
    }
    return tileItem;
  }

  private Point getRandomNullPoint()
  {
    if(client.getWidget(161,34)!=null){
      Rectangle nullArea = client.getWidget(161,34).getBounds();
      return new Point ((int)nullArea.getX()+utils.getRandomIntBetweenRange(0,nullArea.width), (int)nullArea.getY()+utils.getRandomIntBetweenRange(0,nullArea.height));
    }

    return new Point(client.getCanvasWidth()-utils.getRandomIntBetweenRange(0,2),client.getCanvasHeight()-utils.getRandomIntBetweenRange(0,2));
  }

  public WidgetItem getDuelingRingWidget() {
    WidgetItem item = DuelingRing.Dueling_ring.getItemFromInventory(client);
    if (item != null) {
      return item;
    }
    return item;
  }

  public WidgetItem getFremBootsWidget() {
    WidgetItem item = FremenikBoots.Fremennik_sea_boots.getItemFromInventory(client);
    if (item != null) {
      return item;
    }
    return item;
  }

  public WidgetItem getCraftingCapeWidget() {
    WidgetItem item = CraftingCape.Crafting_cape.getItemFromInventory(client);
    if (item != null) {
      return item;
    }
    return item;
  }



  public WidgetItem getRestoreItem() {
    WidgetItem item;
    item = PrayerRestoreType.PRAYER_POTION.getItemFromInventory(client);
    if (item != null)
    {
      return item;
    }
    item = PrayerRestoreType.SANFEW_SERUM.getItemFromInventory(client);
    if (item != null)
    {
      return item;
    }
    item = PrayerRestoreType.SUPER_RESTORE.getItemFromInventory(client);
    return item;
  }



  public WidgetItem GetFoodItem() {
    WidgetItem item = utils.getInventoryWidgetItem(config.foodID().getId());
    if (item != null) {
      return item;
    }
    return item;
  }

  public WidgetItem GetRangedItem() {
    WidgetItem item = PrayerRestoreType.RANGED.getItemFromInventory(client);
    if (item != null) {
      return item;
    }
    return item;
  }

  public WidgetItem GetCombatItem() {
    WidgetItem item = PrayerRestoreType.COMBAT.getItemFromInventory(client);
    if (item != null) {
      return item;
    }
    return item;
  }

  public WidgetItem GetAntifireItem() {
    WidgetItem item = PrayerRestoreType.ANTI_FIRE.getItemFromInventory(client);
    if (item != null) {
      return item;
    }
    return item;
  }

  public WidgetItem GetAntiVenomItem() {
    WidgetItem item = PrayerRestoreType.ANTI_VENOM.getItemFromInventory(client);
    if (item != null) {
      return item;
    }
    return item;
  }

  private void keyEvent(int id, int key)
  {
    KeyEvent e = new KeyEvent(
            client.getCanvas(), id, System.currentTimeMillis(),
            0, key, KeyEvent.CHAR_UNDEFINED
    );
    client.getCanvas().dispatchEvent(e);
  }

  public void pressKey(int key)
  {
    keyEvent(401, key);
    keyEvent(402, key);
    //keyEvent(400, key);
  }


  public FreeWorldVorkathState calcTask() {
    if (tickDelay >= 2)
      return FreeWorldVorkathState.TIMEOUT;
    if (chinBreakHandler.shouldBreak(this) && !atVorky() && (player.getWorldArea().intersectsWith(drayArea) || player.getWorldArea().intersectsWith(lunarArea) || player.getWorldArea().intersectsWith(craftArea) || player.getWorldArea().intersectsWith(feroxArea)) )
    {
      return HANDLE_BREAK;
    }
    if (!utils.isBankOpen())
      return getTask();
    else
    return getBankTask();
  }




  @Nullable
  NPC vorkathLoc() {
    return utils.findNearestNpc(8059);
  }

  private boolean atVorky() {
    return ArrayUtils.contains(client.getMapRegions(), 9023);
  }

  public int getBoostAmount(WidgetItem restoreItem, int prayerLevel)
  {
    if (PrayerRestoreType.PRAYER_POTION.containsId(restoreItem.getId()))
    {
      return 7 + (int) Math.floor(prayerLevel * .25);
    }
    else if (PrayerRestoreType.SANFEW_SERUM.containsId(restoreItem.getId()))
    {
      return 4 + (int) Math.floor(prayerLevel * (double)(3 / 10));
    }
    else if (PrayerRestoreType.SUPER_RESTORE.containsId(restoreItem.getId()))
    {
      return 8 + (int) Math.floor(prayerLevel * .25);
    }

    return 0;
  }

  @Subscribe
  public void onActorDeath(ActorDeath actorDeath)
  {
    if (actorDeath.getActor() == client.getLocalPlayer())
    {
      utils.logout();
    }
  }


  private FreeWorldVorkathState getTask() {
///////////////////////////////////////////////////////////////////////////////////////////////
    if (!zombieSpawnDead
            && !atVorky())
      zombieSpawnDead = true;

    if (!noBomb
            && !atVorky())
      noBomb = true;
////////////////////////////////////////////////////////////////////////////////////////////
    if (utils.findNearestNpc(8059) != null
            && atVorky()) {
      acidFreePath.clear();
      acidSpots.clear();
      noBomb = true;
      zombieSpawnDead = true;
    }
    if (!noBomb
            && atVorky()) {
      return FreeWorldVorkathState.HANDLE_BOMB;
    }

    /*
    if (banked && client.getLocalPlayer().getWorldArea().intersectsWith(lunarbankArea) && !utils.isBankOpen()) {
      banked = false;
    }
    if (banked && client.getLocalPlayer().getWorldArea().intersectsWith(feroxArea) && !utils.isBankOpen()) {
      banked = false;
    }
    if (banked && client.getLocalPlayer().getWorldArea().intersectsWith(edgeArea) && !utils.isBankOpen()) {
      banked = false;
    }
    if (banked && CraftRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID()) && !utils.isBankOpen()) {
      banked = false;
    }
*/

    if (RellekkaRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
      banked = false;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    if (isInPOH(client)
            && utils.inventoryContains(rubyBoltIDs)
            && !utils.isItemEquipped(rubyBoltIDs)
            && config.useRanged()
            && !config.useBlowpipe()) {
      return FreeWorldVorkathState.EQUIP_RUBIES;
    }
    if (!atVorky()
            && utils.inventoryContains(rubyBoltIDs)
            && !utils.isItemEquipped(rubyBoltIDs)
            && config.useRanged()
            && !config.useBlowpipe()
            && !isInPOH(client)) {
      return FreeWorldVorkathState.EQUIP_RUBIES;
    }
    if (!atVorky()
            && utils.inventoryContains(rubyBoltIDs)
            && !utils.isItemEquipped(rubyBoltIDs)
            && config.useRanged()
            && !config.useBlowpipe()
            && !isInPOH(client)) {
      return FreeWorldVorkathState.EQUIP_RUBIES;
    }
    if (atVorky()
            && calchealth(vorkath, 750) > 265
            && calchealth(vorkath, 750) <= 750
            && utils.inventoryContains(rubyBoltIDs)
            && !utils.isItemEquipped(rubyBoltIDs)
            && acidSpots.isEmpty()
            && config.useRanged()
            && !config.useBlowpipe()
            && utils.findNearestNpc(8061) != null) {
      return FreeWorldVorkathState.EQUIP_RUBIES;
    }
    if (atVorky()
            && calchealth(vorkath, 750) < 265
            && utils.inventoryContains(diamondBoltIDs)
            && !utils.isItemEquipped(diamondBoltIDs)
            && acidSpots.isEmpty()
            && config.useRanged()
            && !config.useBlowpipe()
            && utils.findNearestNpc(8061) != null) {
      return FreeWorldVorkathState.EQUIP_DIAMONDS;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    if (client.getVar(Varbits.QUICK_PRAYER) == 1
            && !atVorky()) {
      return FreeWorldVorkathState.DEACTIVATE_PRAY;
    }

    if ((client.getVar(Varbits.QUICK_PRAYER) != 0
            && atVorky()
            && utils.findNearestNpc(8059) != null)) {
      return FreeWorldVorkathState.DEACTIVATE_PRAY;
    }

    int currentPrayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
    int prayerLevel = client.getRealSkillLevel(Skill.PRAYER);
    if (client.getVar(Varbits.QUICK_PRAYER) == 0
            && currentPrayerPoints > 1
            && atVorky()
            && acidSpots.isEmpty()
            && zombieSpawnDead
            && noBomb
            && utils.findNearestNpc(8061) != null) {
      return FreeWorldVorkathState.ACTIVATE_PRAY;
    }

    WidgetItem prayItem = getRestoreItem();
    if (config.prayerDrink()
            && prayItem != null
            && noBomb
            && zombieSpawnDead
            && acidSpots.isEmpty()
            && (currentPrayerPoints <= utils.getRandomIntBetweenRange(config.minPrayerLevel(), config.maxPrayerLevel()))     ) {
      return FreeWorldVorkathState.DRINK_PRAYER;
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    if (player.getWorldArea().intersectsWith(drayArea)
            && utils.inventoryContains(config.foodID().getId())
            && banked
            && !atVorky()
            && !utils.isBankOpen()) {
      return TELE_RELLEKKA;
    }

    if (player.getWorldArea().intersectsWith(feroxArea)
            && utils.inventoryContains(config.foodID().getId())
            && banked
            && !atVorky()
            && !utils.isBankOpen()) {
      return TELE_RELLEKKA;
    }

    if (player.getWorldArea().intersectsWith(craftArea)
            && utils.inventoryContains(config.foodID().getId())
            && banked
            && !atVorky()
            && !utils.isBankOpen()) {
      return TELE_RELLEKKA;
    }

    if (player.getWorldArea().intersectsWith(lunarArea)
            && utils.inventoryContains(config.foodID().getId())
            && banked
            && !atVorky()
            && config.teleportations().getName().equals("NPC kick")
            && !utils.isBankOpen()
            && !RellekkaRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())
            && !talkedNPC) { // meh
      return TALK_NPC;
    }

    if (player.getWorldArea().intersectsWith(lunarArea)
            && utils.inventoryContains(config.foodID().getId())
            && banked
            && !atVorky()
            && config.teleportations().getName().equals("Return Orb")
            && !utils.isBankOpen()
            && !RellekkaRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())) {
      return TELE_RELLEKKA;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    if (player.getWorldArea().intersectsWith(drayArea)
            && !banked
            && !utils.isBankOpen()
            || (!utils.inventoryItemContainsAmount(config.foodID().getId(), config.foodAmount(), false, true ))
            && player.getWorldArea().intersectsWith(drayArea)
            && !utils.isBankOpen() ) {
      return FreeWorldVorkathState.FIND_BANK;
    }
    if (CraftRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())
            && !banked
            && !utils.isBankOpen()
            || (!utils.inventoryItemContainsAmount(config.foodID().getId(), config.foodAmount(), false, true ))
            && CraftRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())
            && !utils.isBankOpen() ) {
      return FreeWorldVorkathState.FIND_BANK;
    }
    if (player.getWorldArea().intersectsWith(feroxArea)
            && !utils.isBankOpen()
            && !banked
            || (!utils.inventoryItemContainsAmount(config.foodID().getId(), config.foodAmount(), false, true ))
            && player.getWorldArea().intersectsWith(feroxArea)
            && !utils.isBankOpen()) {
      return FreeWorldVorkathState.FIND_BANK;
    }
    if (player.getWorldArea().intersectsWith(lunarArea)
            && !utils.isBankOpen()
            && !banked
            || (!utils.inventoryItemContainsAmount(config.foodID().getId(), config.foodAmount(), false, true ))
            && player.getWorldArea().intersectsWith(lunarArea)
            && !utils.isBankOpen()) {
      return FreeWorldVorkathState.FIND_BANK;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////
    if (Objects.equals(player.getLocalLocation(), new LocalPoint(5696, 8000))
            && atVorky()) {
      utils.walk(new LocalPoint(6208, 7872));
    }

    if (Objects.equals(player.getLocalLocation(), new LocalPoint(6720, 8000))
            && atVorky()) {
      utils.walk(new LocalPoint(6208, 7872));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    if (utils.isItemEquipped(Collections.singleton(config.specWeapons().getId()))
            && client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) < config.specThreshold() * 10) {
      WidgetItem inventoryWidgetItem = utils.getInventoryWidgetItem(config.mainWeapons().getId());
      WidgetItem inventoryWidgetItem2 = utils.getInventoryWidgetItem(config.offHand().getId());

      if (inventoryWidgetItem != null) {
        clientThread.invoke(() -> client.invokeMenuAction("", "",
                inventoryWidgetItem.getId(),
                MenuAction.ITEM_SECOND_OPTION.getId(),
                inventoryWidgetItem.getIndex(),
                WidgetInfo.INVENTORY.getId()));
      }

      if (inventoryWidgetItem2 != null) {
        clientThread.invoke(() -> client.invokeMenuAction("", "",
                inventoryWidgetItem2.getId(),
                MenuAction.ITEM_SECOND_OPTION.getId(),
                inventoryWidgetItem2.getIndex(),
                WidgetInfo.INVENTORY.getId()));
      }
    }

    if (utils.isItemEquipped(Collections.singleton(config.specWeapons().getId()))
            && !atVorky()) {
      WidgetItem inventoryWidgetItem3 = utils.getInventoryWidgetItem(config.mainWeapons().getId());
      WidgetItem inventoryWidgetItem4 = utils.getInventoryWidgetItem(config.offHand().getId());

      if (inventoryWidgetItem3 != null) {
        clientThread.invoke(() -> client.invokeMenuAction("", "",
                inventoryWidgetItem3.getId(),
                MenuAction.ITEM_SECOND_OPTION.getId(),
                inventoryWidgetItem3.getIndex(),
                WidgetInfo.INVENTORY.getId()));
      }
      if (inventoryWidgetItem4 != null) {
        clientThread.invoke(() -> client.invokeMenuAction("", "",
                inventoryWidgetItem4.getId(),
                MenuAction.ITEM_SECOND_OPTION.getId(),
                inventoryWidgetItem4.getIndex(),
                WidgetInfo.INVENTORY.getId()));
      }
    }

    if (!itemsToLoot.isEmpty()
            && !utils.inventoryFull()
            && atVorky()) {
      return FreeWorldVorkathState.LOOT_ITEMS;
    }

    if (utils.inventoryContains(22124) // dragon bones superior in invent !
            && itemsToLoot.isEmpty() // no items to loot
            && !isInPOH(client) // is not in poh
            && atVorky() // is at vorky
            && !config.onlytelenofood()) { // IF DISABLED TELEPORT AFTER KILL
      return FreeWorldVorkathState.TELE_OUT; /////////////////////TELEPORT OUT AFTER EVERY KILL
    }

    int health = client.getBoostedSkillLevel(Skill.HITPOINTS);
    if (!utils.inventoryContains(config.foodID().name())
            && health <= config.healthTP()
            && itemsToLoot.isEmpty()
            && !isInPOH(client)
            && atVorky()) { // teleporting only after all is depleted and no pots or food + health and pray are below threshhold
      return FreeWorldVorkathState.TELE_OUT;
    }

    if (client.getBoostedSkillLevel(Skill.PRAYER) < config.prayTP()
            && prayItem != null
            && atVorky()) {
      return FreeWorldVorkathState.TELE_OUT;
    }

    if (config.enablekcTele()
            && config.killCountTele() == killCount
            && atVorky()
            && itemsToLoot.isEmpty()){
      return FreeWorldVorkathState.TELE_OUT;
    }


    if (isInPOH(client)
            && currentPrayerPoints < prayerLevel
            && config.usePOHpool()) {
      return FreeWorldVorkathState.DRINK_POOL;
    }

    if (isInPOH(client)
            && !atVorky()) {
      return FreeWorldVorkathState.TELE_BANK;
    }

    if (RellekkaRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())
            && (utils.findNearestGameObject(29917) == null)) {
      return FreeWorldVorkathState.WALK_BOAT;
    }

    if (RellekkaRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())
            && !(utils.findNearestGameObject(29917) == null)) {
      return FreeWorldVorkathState.USE_BOAT;
    }

    if (vorkIsleRegion.contains(client.getLocalPlayer().getWorldLocation().getRegionID())
            && !(utils.findNearestGameObject(31989) == null)) {
      return FreeWorldVorkathState.JUMP_OBSTACLE;
    }

    if (!acidSpots.isEmpty()
            && atVorky())
    //&& config.acidWalk())
    {
      return FreeWorldVorkathState.ACID_WALK;
    }



    if (!noBomb
            && atVorky()) {
      return FreeWorldVorkathState.HANDLE_BOMB;
    }

    if (!zombieSpawnDead
            && atVorky()) {
      return FreeWorldVorkathState.HANDLE_ICE;
    }

    if (config.antivenomplus()
            && client.getVar(VarPlayer.IS_POISONED) > 0
            && atVorky()) {
      return FreeWorldVorkathState.DRINK_ANTIVENOM;
    }

    if (client.getBoostedSkillLevel(Skill.RANGED) <= config.potThreshold()
            && atVorky()
            && config.useRanged()) {
      return FreeWorldVorkathState.DRINK_RANGE;
    }

    if (client.getBoostedSkillLevel(Skill.STRENGTH) <= config.potThreshold()
            && atVorky()
            && !config.useRanged()) {
      return FreeWorldVorkathState.DRINK_COMBAT;
    }

    if (config.superantifire()
            && client.getVarbitValue(6101) == 0
            && atVorky()) {
      return FreeWorldVorkathState.DRINK_ANTIFIRE;
    }

    if (!config.superantifire()
            && client.getVarbitValue(3981) == 0
            && atVorky()) {
      return FreeWorldVorkathState.DRINK_ANTIFIRE;
    }

    if (utils.findNearestNpc(8059) != null
            && atVorky()
            && itemsToLoot.isEmpty()) {
      return FreeWorldVorkathState.WAKE_VORKATH;
    }

    if (utils.findNearestNpc(8059) != null
            && atVorky()
            && itemsToLoot.isEmpty()
            && config.killCountTele() != killCount ) {
      return FreeWorldVorkathState.WAKE_VORKATH;
    }

    if (player.getWorldLocation().distanceTo(vorkath.getWorldArea()) <= 2
            && config.useRanged()) {
      return FreeWorldVorkathState.MOVE_AWAY;
    }

    if (!utils.isItemEquipped(Collections.singleton(config.specWeapons().getId()))
            && utils.inventoryFull()
            && utils.inventoryContains(config.foodID().getId())
            && config.offHand().getId() != 0
            && calchealth(vorkath, 750) >= config.specHP()
            && client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 0
            && client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) >= config.specThreshold() * 10
            && config.useSpec()
            && noBomb
            && zombieSpawnDead
            && utils.findNearestNpc(8061) != null
            && acidSpots.isEmpty()
            && atVorky()) {
      return FreeWorldVorkathState.EAT_FOOD;
    }

    if (utils.inventoryContains(
            config.foodID().getId())
            && utils.inventoryFull()
            && !itemsToLoot.isEmpty()
            && !isInPOH(client)
            && atVorky()) {
      return FreeWorldVorkathState.EAT_FOOD;
    }
    if (utils.inventoryContains(
            config.foodID().getId())
            && utils.inventoryFull()
            && !itemsToLoot.isEmpty()
            && isInPOH(client)) {
      return FreeWorldVorkathState.EAT_FOOD;
    }

    if (utils.inventoryContains(config.foodID().getId())
            && noBomb
            && zombieSpawnDead
            && acidSpots.isEmpty()
            && health < config.minimumHealthSingle()
            && atVorky()) {
      return FreeWorldVorkathState.EAT_FOOD;
    }

    if (!utils.isItemEquipped(Collections.singleton(config.specWeapons().getId()))
            && calchealth(vorkath, 750) >= config.specHP()
            && client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 0
            && client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) >= config.specThreshold() * 10
            && config.useSpec()
            && noBomb
            && zombieSpawnDead
            && utils.findNearestNpc(8061) != null
            && acidSpots.isEmpty()
            && atVorky()) {
      return FreeWorldVorkathState.EQUIP_SPEC;
    }

    if (utils.isItemEquipped(Collections.singleton(config.specWeapons().getId()))
            && calchealth(vorkath, 750) >= config.specHP()
            && client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 0
            && client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) >= config.specThreshold() * 10
            && config.useSpec()
            && noBomb
            && zombieSpawnDead
            && utils.findNearestNpc(8061) != null
            && acidSpots.isEmpty()
            && atVorky()) {
      return FreeWorldVorkathState.SPECIAL_ATTACK;
    }

    if (noBomb
            && zombieSpawnDead
            && utils.findNearestNpc(8061) != null
            && acidSpots.isEmpty()
            && client.getLocalPlayer().getInteracting() != vorkath
            && atVorky()) {
      return FreeWorldVorkathState.ATTACK_VORKATH;
    }
    return FreeWorldVorkathState.TIMEOUT;
  }

  private FreeWorldVorkathState getBankTask() {
    if (!banked) {
      if (utils.inventoryEmpty()) {
        banked = true;
        killCount = 0;
        return FreeWorldVorkathState.DEPOSIT_ITEMS;
      } else {
        utils.depositAll();
        banked = true;
        killCount = 0;
        return FreeWorldVorkathState.DEPOSIT_ITEMS;
      }
    }

    if (config.useSpec()) {
      utils.withdrawItem(config.specWeapons().getId());
    }

    if (!utils.inventoryContains(2444)
            && config.useRanged()
            && config.fightPotions().name().equals("Ranging_potion")){
      return FreeWorldVorkathState.WITHDRAW_RANGED;
    }
    if (!utils.inventoryContains(23733)
            && config.useRanged()
            && config.fightPotions().name().equals("Divine_ranging")){
      return FreeWorldVorkathState.WITHDRAW_DIVINE_RANGE;
    }

    if (!utils.inventoryContains(24635)
            && config.useRanged()
            && config.fightPotions().name().equals("Divine_bastion")){
      return FreeWorldVorkathState.WITHDRAW_DIVINE_BASTION;
    }

    if (!utils.inventoryContains(22461)
            && config.useRanged()
            && config.fightPotions().name().equals("Bastion_potion")){
      return FreeWorldVorkathState.WITHDRAW_BASTION;
      }

    if (!utils.inventoryContains(23685)
            && !config.useRanged()
            && config.fightPotions().name().equals("Divine_s_combat")){
      return FreeWorldVorkathState.WITHDRAW_DIVINE_COMBAT;
    }

    if (!utils.inventoryContains(12695)
            && !config.useRanged()
            && config.fightPotions().name().equals("Super_combat")){
      return FreeWorldVorkathState.WITHDRAW_SUPER_COMBAT;
    }

    if (!utils.inventoryContains(9739)
            &&!config.useRanged()
            && config.fightPotions().name().equals("Combat_potion")){
      return FreeWorldVorkathState.WITHDRAW_COMBAT;
    }

    if (config.antivenomplus()
            && !utils.inventoryContains(12913)
            && !config.useSerp()){
      return FreeWorldVorkathState.WITHDRAW_VENOM_PLUS;
    }

    if (!config.antivenomplus()
            && !utils.inventoryContains(5952)
            && !config.useSerp()){
      return FreeWorldVorkathState.WITHDRAW_VENOM;
    }

    if (config.superantifire()
            && !utils.inventoryContains(22209)){
      return FreeWorldVorkathState.WITHDRAW_EX_ANTIFIRE;
    }
    if (!config.superantifire()
            && !utils.inventoryContains(11951)){
      return FreeWorldVorkathState.WITHDRAW_ANTIFIRE;
    }

    if (!utils.inventoryContains(12791)){
      return FreeWorldVorkathState.WITHDRAW_POUCH;
    }

    if (!utils.inventoryContains(8013)
            && config.teleTab()){
      return FreeWorldVorkathState.WITHDRAW_TELES;
    }

    if ((!utils.inventoryContains(13131))
            && config.teleportations().name().equals("Frem_sea_boots")){
      return FreeWorldVorkathState.WITHDRAW_FREMBOOTS;
    }

    if (!utils.inventoryContains(23458)
            && config.teleportations().name().equals("Ench_lyre")){
      return FreeWorldVorkathState.WITHDRAW_ENCHLYRE;
    }

    if (config.bankAreas().equals(Crafting_Guild)
            && !utils.inventoryContains(9781)){
      return FreeWorldVorkathState.WITHDRAW_CAPE;
    }

    if (config.potions().name().equals("Super_restore")
            && !utils.inventoryContains(3024)){
      return FreeWorldVorkathState.WITHDRAW_SUPER_RESTORE;
    }

    if (config.potions().name().equals("Prayer_potion")
            && !utils.inventoryContains(2434)){
      return FreeWorldVorkathState.WITHDRAW_PRAYER;
    }

    if (!utils.inventoryContains(diamondBoltIDs)
            && config.useRanged()
            && !config.useBlowpipe()){
      return FreeWorldVorkathState.WITHDRAW_BOLTS;
    }
    //////////////////////////////////////////////////////////////////////////////// END RANDOMIZATION WITHDRAW

    if (!utils.inventoryItemContainsAmount(config.foodID().getId(),config.foodAmount(),false,true)){
      return FreeWorldVorkathState.WITHDRAW_FOOD1;
    }

    if (!utils.inventoryItemContainsAmount(config.foodID().getId(),config.foodAmount(),false,true)){
      return FreeWorldVorkathState.WITHDRAW_FOOD1;
    }


    if (config.useFood2()
            && !utils.inventoryItemContainsAmount(config.foodID2().getId(),config.foodAmount2(),false,true)){
      return FreeWorldVorkathState.WITHDRAW_FOOD2;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    if (!config.useFood2() && utils.inventoryItemContainsAmount(config.foodID().getId(),config.foodAmount(),false,true) && banked && utils.isBankOpen()){
      return CLOSE_BANK;
    }
    if (config.useFood2() && utils.inventoryItemContainsAmount(config.foodID2().getId(),config.foodAmount2(),false,true) && banked && utils.isBankOpen()){
      return CLOSE_BANK;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    return FreeWorldVorkathState.TIMEOUT;
  }

  public Widget getContinueWidget() {
    Widget widget = client.getWidget(WidgetInfo.LEVEL_UP_CONTINUE);
    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    widget = client.getWidget(WidgetInfo.MINIGAME_DIALOG_CONTINUE);
    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    widget = client.getWidget(WidgetInfo.LEVEL_UP);
    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    widget = client.getWidget(WidgetInfo.LEVEL_UP_SKILL);
    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    widget = client.getWidget(WidgetInfo.LEVEL_UP_LEVEL);
    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    //WidgetInfo.DIALOG_PLAYER_CONTINUE
    widget = client.getWidget(231, 4);
    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    widget = client.getWidget(217, 4);
    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    widget = client.getWidget(WidgetInfo.DIALOG2_SPRITE_CONTINUE);

    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    widget = client.getWidget(WidgetInfo.DIALOG_NOTIFICATION_CONTINUE);

    if (widget != null && !widget.isHidden()) {
      return widget;
    }
    return null;
  }


  @Subscribe
  private void onGameTick(GameTick tick) throws AWTException, InterruptedException {
    if (!startRun || chinBreakHandler.isBreakActive(this)) {
      return;
    }
    player = client.getLocalPlayer();
    Widget continuewidget = getContinueWidget();
    final Player local = client.getLocalPlayer();
    final Duration waitDuration = Duration.ofMillis(10000);
    lastCombatCountdown = Math.max(lastCombatCountdown - 1, 0);

    if (client != null && player != null) {
      WidgetItem inventoryWidgetItem,
              inventoryWidgetItem2,
              inventoryWidgetItem3;
      WidgetItem bolts;
      NPC npc;
      LocalPoint fromWorld;
      if (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null) {
        log.info("bank pin needed");
        utils.sendGameMessage("bank pin needed");
        return;
      }
      if (!startRun){
        return;
      }
      /*if (!client.isResizedResized()) {
        utils.sendGameMessage("client must be set to resizable mode");
        return;
      }*/
      currentTask = calcTask();
      beforeLoc = player.getLocalLocation();
      utils.setMenuEntry(null);
      int health = client.getBoostedSkillLevel(Skill.HITPOINTS);
      switch (currentTask) {
        case TIMEOUT: {
          utils.handleRun(30, 20);
          --tickDelay;
          break;
        }

        case SPECIAL_ATTACK: {
          if (utils.isItemEquipped(Collections.singleton(config.specWeapons().getId()))) {
            clientThread.invoke(() ->
                    client.invokeMenuAction(
                            "Use Special Attack",
                            "",
                            1,
                            MenuAction.CC_OP.getId(),
                            -1,
                            38862884));
            break;
          }
          break;
        }

        case MOVE_AWAY: {
          this.utils.walk(new WorldPoint(this.player.getWorldLocation().getX(), this.player.getWorldLocation().getY() - 3, this.player.getWorldLocation().getPlane()));
          break;
        }



/*
        case CRAFT_CAPE: {
          if (utils.inventoryContains(9781)) {
              List<WidgetItem> craftCape = utils.getInventoryItems("Crafting cape(t)");
              for (WidgetItem cape : craftCape) {
                if (cape != null) {
                  clientThread.invoke(() -> client.invokeMenuAction(
                          "",
                          "", cape.getId(),
                          MenuAction.ITEM_SECOND_OPTION.getId(),
                          cape.getIndex(),
                          WidgetInfo.INVENTORY.getId()));
                  tickDelay = getTickDelay();
                  break;
                }
              }
            }

          break;
        }
        case EDGE_TELE: {
            utils.useDecorativeObject(13523, MenuAction.GAME_OBJECT_FIRST_OPTION.getId(), getSleepDelay());
            tickDelay = getTickDelay();
          break;
        }
        case DUEL_RING:{
          break;
        }
        case LUNAR_PORTAL: {
          utils.useGameObject(29339, 3, getSleepDelay());
          tickDelay = getTickDelay();
          break;
        }
*/

        case TELE_BANK:
          if (config.bankAreas().name().equals("Crafting_Guild") && isInPOH(client)) {
            WidgetItem craftCape = getCraftingCapeWidget();
            if (craftCape != null) {
              clientThread.invoke(() -> client.invokeMenuAction(
                      "Teleport",
                      "",
                      craftCape.getId(),
                      MenuAction.ITEM_THIRD_OPTION.getId(),
                      craftCape.getIndex(),
                      WidgetInfo.INVENTORY.getId()));
              didObstacle = false;
              usedBoat = false;
              tickDelay = getTickDelay();
            }
          } else {
            if (config.bankAreas().name().equals("Draynor") && isInPOH(client) ) {
              utils.useDecorativeObject(13523, MenuAction.GAME_OBJECT_THIRD_OPTION.getId(), getSleepDelay());
              tickDelay = getTickDelay();
              didObstacle = false;
              usedBoat = false;
              break;
            }
            if (config.bankAreas().name().equals("Ferox_Enclave") && utils.inventoryContains(getDuelingRingWidget().getId()) && isInPOH(client)) {
              WidgetItem duelRing = getDuelingRingWidget();
              if (duelRing != null) {
                clientThread.invoke(() -> client.invokeMenuAction(
                        "Rub",
                        "",
                        duelRing.getId(),
                        MenuAction.ITEM_FOURTH_OPTION.getId(),
                        duelRing.getIndex(),
                        WidgetInfo.INVENTORY.getId()));
                didObstacle = false;
                usedBoat = false;
                tickDelay = getTickDelay();
                if (client.getWidget(WidgetInfo.DIALOG_OPTION_OPTION1) != null) {
                  utils.pressKey(KeyEvent.VK_NUMPAD3);
                  break;
                }
              }
            }
            if (config.bankAreas().name().equals("Lunar_Isle") && isInPOH(client)) {
              utils.useGameObject(29339, 3, getSleepDelay());
              tickDelay = getTickDelay();
              talkedNPC = false;
              didObstacle = false;
              usedBoat = false;
              break;
            }
          }


        case EQUIP_SPEC: {
          if ((inventoryWidgetItem =
                  utils.getInventoryWidgetItem(
                          config.specWeapons().getId())) != null) {
            clientThread.invoke(() ->
                    client.invokeMenuAction(
                            "",
                            "",
                            inventoryWidgetItem.getId(),
                            MenuAction.ITEM_SECOND_OPTION.getId(),
                            inventoryWidgetItem.getIndex(),
                            WidgetInfo.INVENTORY.getId()));
            break;
          }
          break;
        }

        case EQUIP_RUBIES: {
          if ((inventoryWidgetItem2 =
                  utils.getInventoryWidgetItem(rubyBoltIDs)) != null) {
            clientThread.invoke(() ->
                    client.invokeMenuAction(
                            "",
                            "",
                            inventoryWidgetItem2.getId(),
                            MenuAction.ITEM_SECOND_OPTION.getId(),
                            inventoryWidgetItem2.getIndex(),
                            WidgetInfo.INVENTORY.getId()));
            break;
          }
          break;
        }

        case DRINK_POOL: {
          if (config.pools().name().equals("Restoration_pool")) {
            utils.useGameObjectDirect(
                    utils.findNearestGameObject(29237),
                    getSleepDelay(),
                    MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
            tickDelay = getTickDelay();
            break;
          } else if (config.pools().name().equals("Revitalisation_pool")) {
            utils.useGameObjectDirect(
                    utils.findNearestGameObject(29238),
                    getSleepDelay(),
                    MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
            tickDelay = getTickDelay();
            break;
          } else if (config.pools().name().equals("Pool_of_Rejuvination")) {
            utils.useGameObjectDirect(
                    utils.findNearestGameObject(29239),
                    getSleepDelay(),
                    MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
            tickDelay = getTickDelay();
            break;
          } else if (config.pools().name().equals("Fancy_pool")) {
            utils.useGameObjectDirect(
                    utils.findNearestGameObject(29240),
                    getSleepDelay(),
                    MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
            tickDelay = getTickDelay();
            break;
          } else if (config.pools().name().equals("Ornate_pool")) {
            utils.useGameObjectDirect(
                    utils.findNearestGameObject(29241),
                    getSleepDelay(),
                    MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
            tickDelay = getTickDelay();
            break;
          }
          break;
        }


        case EQUIP_DIAMONDS:
          bolts = utils.getInventoryWidgetItem(diamondBoltIDs);
          if (bolts != null)
            this.clientThread.invoke(() -> this.client.invokeMenuAction("", "", bolts.getId(), MenuAction.ITEM_SECOND_OPTION.getId(), bolts.getIndex(), WidgetInfo.INVENTORY.getId()));
          break;

        /*case WOOX_WALK:{
          calculateWooxWalkPath();
          if()
        }*/
        case ACID_WALK: {
          if (client.getVar(Varbits.QUICK_PRAYER) != 0){
            clientThread.invoke(() ->
                    client.invokeMenuAction(
                            "Deactivate",
                            "Quick-prayers",
                            1,
                            MenuAction.CC_OP.getId(),
                            -1,
                            10485775));
          }

          calculateAcidFreePath();
          if (config.acidFreePathLength() < 3)
            break;
          if (a) {
            utils.walk(acidFreePath.get(1));
            a = false;
          }
          if (!a) {
            utils.walk(acidFreePath.get(config.acidFreePathLength() -1 ) );
            a = true;
            break;
          }
          break;
        }

        case ATTACK_VORKATH: {
          if (atVorky()) {
            utils.attackNPCDirect(vorkath);
            break;
          }
          break;
        }




        case HANDLE_ICE: {
          npc = utils.findNearestNpc("Zombified Spawn");
          WorldPoint worldPoint = client.getLocalPlayer().getWorldLocation();
          LocalPoint.fromWorld(client, worldPoint);
          setSpell(Spells.CRUMBLE_UNDEAD.getInfo());
          clientThread.invoke(() -> castSpellOnNPC(npc));
          tickDelay = getTickDelay();
          break;
        }

        case HANDLE_BOMB: {
          final WorldPoint loc = this.client.getLocalPlayer().getWorldLocation();
          final LocalPoint localLoc = LocalPoint.fromWorld(this.client, loc);
          this.dodgeRight = new LocalPoint(localLoc.getX() + 256, localLoc.getY());
          this.dodgeLeft = new LocalPoint(localLoc.getX() - 256, localLoc.getY());
          if (localLoc.distanceTo(this.dodgeLeft) <= 1) {
            this.noBomb = true;
            this.zombieSpawnDead = true;
          }
          if (localLoc.distanceTo(this.dodgeRight) <= 1) {
            this.noBomb = true;
            this.zombieSpawnDead = true;
          }
          if (localLoc.getX() < 6208) {
            this.utils.walk(this.dodgeRight);
            this.tickDelay = this.getTickDelay();
            this.noBomb = true;
            this.zombieSpawnDead = true;
            break;
          }
          this.utils.walk(this.dodgeLeft);
          this.tickDelay = this.getTickDelay();
          this.noBomb = true;
          this.zombieSpawnDead = true;
          break;
        }

        case DEACTIVATE_PRAY: {
          clientThread.invoke(() ->
                  client.invokeMenuAction(
                          "Deactivate",
                          "Quick-prayers",
                          1,
                          MenuAction.CC_OP.getId(),
                          -1,
                          10485775));
          tickDelay = getTickDelay();
          break;
        }

        case ACTIVATE_PRAY: {
          clientThread.invoke(() ->
                  client.invokeMenuAction(
                          "Activate", "Quick-prayers",
                          1, MenuAction.CC_OP.getId(),
                          -1, 10485775));
          tickDelay = getTickDelay();
          break;
        }

        case WITHDRAW_DIVINE_COMBAT: { //  (config.pools().name().equals("Restoration_pool"))
            utils.withdrawItem(23685);
            tickDelay = getTickDelay();
            break;
        }

        case WITHDRAW_DIVINE_RANGE: { //  (config.pools().name().equals("Restoration_pool"))
            utils.withdrawItem(	23733);
            tickDelay = getTickDelay();
            break;
        }

        case WITHDRAW_DIVINE_BASTION: { //  (config.pools().name().equals("Restoration_pool"))
            utils.withdrawItem(	24635);
            tickDelay = getTickDelay();
            break;
        }

        case WITHDRAW_SUPER_COMBAT: { //  (config.pools().name().equals("Restoration_pool"))
            utils.withdrawItem(12695);
            tickDelay = getTickDelay();
            break;
        }

        case WITHDRAW_COMBAT: { //
            utils.withdrawItem(9739);
            tickDelay = getTickDelay();
            break;
        }

        case WITHDRAW_BASTION: {
          utils.withdrawItem(22461);
          tickDelay = getTickDelay();
          break;
        }

        case WITHDRAW_RANGED: {
          utils.withdrawItem(2444);
          tickDelay = getTickDelay();
          break;
        }

        case WAKE_VORKATH: {
          clientThread.invoke(() ->
                  client.invokeMenuAction(
                          "",
                          "",
                          utils.findNearestNpc("Vorkath")
                                  .getIndex(),
                          MenuAction.NPC_FIRST_OPTION
                                  .getId(),
                          0,
                          0));

          break;
        }

        case WITHDRAW_VENOM: {
          if (!config.antivenomplus()) {
            utils.withdrawItemAmount(5952, config.antipoisonamount());
          }
          tickDelay = getTickDelay();
          break;
        }

        case WITHDRAW_VENOM_PLUS: {
          if (config.antivenomplus()) {
          utils.withdrawItemAmount(12913, config.antipoisonamount());
          }
          tickDelay = getTickDelay();
          break;
        }

        case WITHDRAW_EX_ANTIFIRE: {
          if (config.superantifire()) {
            utils.withdrawItem(22209);
          }
          tickDelay = getTickDelay();
          break;
        }

        case WITHDRAW_ANTIFIRE: {
          if (!config.superantifire()) {
          utils.withdrawItem(11951);
          tickDelay = getTickDelay();
          }
          break;
        }

        case WITHDRAW_POUCH: {
            utils.withdrawItem(12791);
            tickDelay = getTickDelay();
            break;
          }

          case WITHDRAW_SUPER_RESTORE: {
            utils.withdrawItemAmount(3024, config.praypotAmount());
            tickDelay = 2 + getTickDelay();
            break;
          }

          case WITHDRAW_PRAYER: {
            utils.withdrawItemAmount(2434, config.praypotAmount());
            tickDelay = 2 + getTickDelay();
            break;
        }

        case WITHDRAW_TELES: {
          utils.withdrawItemAmount(8013, 10);
          tickDelay= getTickDelay();
          break;
        }

        case WITHDRAW_FREMBOOTS: {
          /*if (!utils.inventoryContains(13132) && utils.bankContains(13132,1)){
            utils.withdrawItem(13132);
            tickDelay= getTickDelay();
            break;}*/
          if (!utils.inventoryContains(13131))
            utils.withdrawItem(13131);
            tickDelay= getTickDelay();
            break;
        }

        case WITHDRAW_ENCHLYRE: {
          utils.withdrawItem(23458);
          tickDelay= getTickDelay();
          break;
        }

        case WITHDRAW_CAPE: {
          utils.withdrawItem(9781);
          tickDelay= getTickDelay();
          break;
        }


        case WITHDRAW_BOLTS: {
          if (utils.bankContains(21946, 1))
            utils.withdrawAllItem(21946);
          tickDelay = getTickDelay();

          if (!utils.bankContains(21946, 1)
                  && utils.bankContains(9243, 1))
            utils.withdrawAllItem(9243);
          tickDelay = getTickDelay();

          tickDelay = getTickDelay();
          break;
        }

        case WITHDRAW_FOOD1: {
            utils.withdrawItemAmount(config.foodID().getId(),config.foodAmount());
            tickDelay = 2 + getTickDelay();
            break;
        }

        case WITHDRAW_FOOD2: {
            utils.withdrawItemAmount(config.foodID2().getId(),config.foodAmount2());
            tickDelay = 2 + getTickDelay();
            break;
        }

        case CLOSE_BANK:{
          clientThread.invoke(() -> client.invokeMenuAction("", "",1, 57, 11, 786434));
          targetMenu = new NewMenuEntry("Close", "", 1, 57, 11, 786434, false);
          utils.setMenuEntry(targetMenu);
          utils.delayMouseClick(getRandomNullPoint(),getSleepDelay());
          tickDelay = getTickDelay();
          break;
        }

        case MOVING: {
          utils.handleRun(30, 20);
          tickDelay = getTickDelay();
          break;
        }

        case DRINK_ANTIVENOM: {
          WidgetItem ven = GetAntiVenomItem();
          if (ven != null) {
            clientThread.invoke(() -> client.invokeMenuAction(
                    "Drink",
                    "<col=ff9040>Potion",
                    ven.getId(),
                    MenuAction.ITEM_FIRST_OPTION.getId(),
                    ven.getIndex(),
                    WidgetInfo.INVENTORY.getId()));
            break;
          }
          break;
        }

        case DRINK_COMBAT: {
          WidgetItem Cpot = GetCombatItem();
          if (Cpot != null) {
            clientThread.invoke(() -> client.invokeMenuAction("Drink", "<col=ff9040>Potion", Cpot.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), Cpot.getIndex(), WidgetInfo.INVENTORY.getId()));
            break;
          }
          break;
        }

        case EAT_FOOD: {
          WidgetItem eat = GetFoodItem();
          if (eat != null) {
            clientThread.invoke(() -> client.invokeMenuAction("Eat", config.foodID().getName(), eat.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), eat.getIndex(), WidgetInfo.INVENTORY.getId()));
            break;
          }
          break;
        }

        case DRINK_RANGE: {
          final WidgetItem Rpot = GetRangedItem();
          if (Rpot != null) {
            clientThread.invoke(() -> client.invokeMenuAction("Drink", "<col=ff9040>Potion", Rpot.getId(), MenuAction.ITEM_FIRST_OPTION.getId(), Rpot.getIndex(), WidgetInfo.INVENTORY.getId()));
            break;
          }
          break;
        }

        case DRINK_ANTIFIRE: {
          final WidgetItem Afire = GetAntifireItem();
          if (Afire != null) {
            clientThread.invoke(() -> client.invokeMenuAction(
                    "Drink",
                    "<col=ff9040>Potion",
                    Afire.getId(),
                    MenuAction.ITEM_FIRST_OPTION.getId(),
                    Afire.getIndex(),
                    WidgetInfo.INVENTORY.getId()));

            break;
          }
          break;
        }

        case DRINK_PRAYER: {
          final WidgetItem RestoreItem = getRestoreItem();
          if (RestoreItem != null) {
            clientThread.invoke(() ->
                    client.invokeMenuAction(
                            "Drink",
                            "<col=ff9040>Potion",
                            RestoreItem.getId(),
                            MenuAction.ITEM_FIRST_OPTION.getId(),
                            RestoreItem.getIndex(),
                            WidgetInfo.INVENTORY.getId()));
          }
        }

        case TELE_RELLEKKA: {
          if (!utils.inventoryContains(8013) && !config.teleTab() && !atVorky() && !isInPOH(client)) {
            tickDelay = getTickDelay();
            clientThread.invoke(() -> client.invokeMenuAction(
                    "",
                    "",
                    2,
                    57,
                    -1,
                    14286877));
            tickDelay = getTickDelay();
            break;
          }
          if (utils.inventoryContains(8013) && config.teleTab() && !atVorky() && !isInPOH(client)) {
            clientThread.invoke(() -> client.invokeMenuAction(
                    "",
                    "",
                    8013,
                    MenuAction.ITEM_THIRD_OPTION.getId(),
                    utils.getInventoryWidgetItem(8013).getIndex(),
                    WidgetInfo.INVENTORY.getId()));
            tickDelay = getTickDelay();
            break;
          }
          if (config.teleportations().getName().equals("Return Orb") && !atVorky() && !isInPOH(client)) {
            utils.useGameObjectDirect(
                    utils.findNearestGameObject(30160),
                    getSleepDelay(),
                    MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
            tickDelay = getTickDelay();
            break;
          }
        }



        case TALK_NPC: {
          NPC lunarNPC = new NPCQuery()
                  .idEquals(3843)
                  .result(client)
                  .nearestTo(client.getLocalPlayer());
          if (lunarNPC != null && continuewidget == null) { // fuck it
            tickDelay = getTickDelay();
            clientThread.invoke(() ->
                    client.invokeMenuAction("", "",
                            lunarNPC.getIndex(),
                            MenuAction.NPC_FIRST_OPTION.getId(),
                            lunarNPC.getLocalLocation().getX(),
                            lunarNPC.getLocalLocation().getY()));
            if (continuewidget != null && !continuewidget.isHidden()) {
              talkedNPC = true;
              executorService.submit(() -> {
                utils.sleep(getRandomIntBetweenRange(80, 250));
                pressKey(VK_SPACE);});
              return;
            }
            //this widget requires it's own menu entry as it doesn't align with the others
            continuewidget = client.getWidget(WidgetInfo.DIALOG_SPRITE);
            if (continuewidget != null && !continuewidget.isHidden()) {
              executorService.submit(() -> {
                utils.sleep(getRandomIntBetweenRange(80, 250));
                pressKey(VK_SPACE);});
            }
            break;
          }
          break;
          }


        case TELE_OUT: {
          if (!config.teleTab() && atVorky()) {
            clientThread.invoke(() ->
                    client.invokeMenuAction(
                            "",
                            "",
                            1,
                            57,
                            -1,
                            14286877));
            tickDelay = getTickDelay();
            break;
          } if(config.teleTab()
                  && atVorky()){
            clientThread.invoke(() -> client.invokeMenuAction(
                    "",
                    "",
                    8013,
                    MenuAction.ITEM_SECOND_OPTION.getId(),
                    utils.getInventoryWidgetItem(8013).getIndex(),
                    WidgetInfo.INVENTORY.getId()));
            tickDelay = getTickDelay();
            break;
          }
          break;
        }

        case WALK_BOAT: {
          if (RellekkaRegion.contains(
                  client.getLocalPlayer().getWorldLocation().getRegionID())){
            utils.walk(new WorldPoint(2642 + calcnonStatic.getRandomIntBetweenRange(-2, 2), 3673 + calcnonStatic.getRandomIntBetweenRange(-2, 2), 0));
            tickDelay = getTickDelay();
            break;
          } else if (config.bankAreas().equals(BankAreas.Lunar_Isle)) { //utils.findNearestGameObject(29917) == null)
            utils.useGameObjectDirect(
                    utils.findNearestGameObject(29917),
                    getSleepDelay(),
                    MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
            tickDelay = getTickDelay();
            break;
          }
          break;
        }

        case USE_BOAT: {
          utils.useGameObjectDirect(
                  utils.findNearestGameObject(29917),
                  getSleepDelay(),
                  MenuAction.GAME_OBJECT_FIRST_OPTION.getId());
          tickDelay = getTickDelay();
          break;
        }

        case FIND_BANK: {
            findBank();
            tickDelay = getTickDelay();
            break;
        }

        case DEPOSIT_ITEMS: {
          tickDelay = getTickDelay();
          break;
        }

        case WITHDRAW_ITEMS: {
          tickDelay = getTickDelay();
          break;
        }

        case LOOT_ITEMS:{
          lootItem(itemsToLoot);

          break;
        }

        case JUMP_OBSTACLE: {
          utils.useGameObject(31990, 3, getSleepDelay());
          didObstacle = true;
          tickDelay = getTickDelay();
          break;
        }

        case HANDLE_BREAK:
          chinBreakHandler.startBreak(this);
          tickDelay = 10;
          break;


      }
    }

  }

  private boolean checkIdleLogout()
  {
    // Check clientside AFK first, because this is required for the server to disconnect you for being first
    int idleClientTicks = client.getKeyboardIdleTicks();
    if (client.getMouseIdleTicks() < idleClientTicks)
    {
      idleClientTicks = client.getMouseIdleTicks();
    }

    if (idleClientTicks < LOGOUT_WARNING_CLIENT_TICKS)
    {
      notifyIdleLogout = true;
      return false;
    }

    // If we are not receiving hitsplats then we can be afk kicked
    if (lastCombatCountdown <= 0)
    {
      boolean warn = notifyIdleLogout;
      notifyIdleLogout = false;
      return warn;
    }

    // We are in combat, so now we have to check for the timer that knocks you out of combat
    // I think there are other conditions that I don't know about, because during testing I just didn't
    // get removed from combat sometimes.
    final long lastInteractionAgo = System.currentTimeMillis() - client.getMouseLastPressedMillis();
    if (lastInteractionAgo < COMBAT_WARNING_MILLIS || client.getKeyboardIdleTicks() < COMBAT_WARNING_CLIENT_TICKS)
    {
      notifyIdleLogout = true;
      return false;
    }

    boolean warn = notifyIdleLogout;
    notifyIdleLogout = false;
    return warn;
  }
  private void resetTimers()
  {
    final Player local = client.getLocalPlayer();

    // Reset animation idle timer
    lastAnimating = null;
    if (client.getGameState() == GameState.LOGIN_SCREEN || local == null || local.getAnimation() != lastAnimation)
    {
      lastAnimation = IDLE;
    }

    // Reset interaction idle timer
    lastInteracting = null;
    if (client.getGameState() == GameState.LOGIN_SCREEN || local == null || local.getInteracting() != lastInteract)
    {
      lastInteract = null;
    }
  }

  @Subscribe
  private void onItemSpawned(ItemSpawned itemSpawned1) {
    TileItem itemSpawned = itemSpawned1.getItem();
    String string = client.getItemDefinition(itemSpawned
                    .getId())
            .getName()
            .toLowerCase();

    Objects.requireNonNull(string.toLowerCase());

    if (!lootNamesList.stream().anyMatch(string.toLowerCase()::contains))
      return;
    if (config.dontlootHide() && itemSpawned.getId() == 1751)

      return;
    itemsToLoot.add(itemSpawned);
  }

  @Subscribe
  private void a(ItemDespawned itemDespawned) {
    itemsToLoot.remove(itemDespawned.getItem());
  }

  public int a(WidgetItem widgetItem, int lootNamesList) {
    if (PrayerRestoreType.PRAYER_POTION.containsId(widgetItem.getId()))
      return 7 + (int)Math.floor(lootNamesList * 0.25D);
    if (PrayerRestoreType.SANFEW_SERUM.containsId(widgetItem.getId()))
      return 4 + (int)Math.floor(lootNamesList * 0.0D);
    if (PrayerRestoreType.SUPER_RESTORE.containsId(widgetItem.getId()))
      return 8 + (int)Math.floor(lootNamesList * 0.25D);
    return 0;
  }

  private int calchealth(NPC npc, Integer vorkHealth) {
    if (npc == null || npc.getName() == null)
      return -1;
    int healthScale = npc.getHealthScale();
    int healthRatio;
    if ((healthRatio = npc.getHealthRatio()) < 0 || healthScale <= 0 || vorkHealth == null)
      return -1;
    return (int)((vorkHealth * healthRatio / healthScale) + 0.5F);
  }

  @Subscribe
  private void onClientTick(ClientTick clientTick) {
    if (acidSpots.size() != L) {
      if (acidSpots.size() == 0) {
        acidFreePath.clear();
      } else {
        calculateAcidFreePath();
      }
      L = acidSpots.size();
    }
  }


  private void castSpellOnNPC(NPC nPC) {
    client.invokeMenuAction("", "", nPC.getIndex(), MenuAction.SPELL_CAST_ON_NPC.getId(), nPC.getLocalLocation().getX(), nPC.getLocalLocation().getY());
  }

  @Subscribe
  private void onProjectileSpawned(final ProjectileSpawned event) {
    if (client.getGameState() == GameState.LOGGED_IN) {
      Projectile projectile = event.getProjectile();
      LocalPoint fromWorld = LocalPoint.fromWorld(
              client,
              client
                      .getLocalPlayer()
                      .getWorldLocation());

      if (projectile.getId() == 1481)
        noBomb = false;
      if (projectile.getId() == 395) {
        zombieSpawnDead = false;
        if (client.getLocalPlayer().getInteracting() != null)
          utils.walk(fromWorld);
      }
    }
  }

  @Subscribe
  private void onProjectileMoved(final ProjectileMoved event) {
    Projectile projectile = event.getProjectile();
    LocalPoint position = event.getPosition();
    WorldPoint.fromLocal(client, position);
    client.getLocalPlayer().getLocalLocation();
    LocalPoint fromWorld = LocalPoint.fromWorld(client, client.getLocalPlayer().getWorldLocation());
    if (projectile.getId() == 1483)
      addAcidSpot(WorldPoint.fromLocal(client, position));
    if (projectile.getId() == 395) {
      zombieSpawnDead = false;
      if (client.getLocalPlayer().getInteracting() != null)
        utils.walk(fromWorld);
    }
    if (projectile.getId() == 1481)
      noBomb = false;
  }

  @Subscribe
  private void onNpcSpawned(final NpcSpawned event) {
    NPC npc;
    if ((npc = event.getNpc()).getName() == null)
      return;
    if (npc.getName().equals("Vorkath"))
      vorkath = event.getNpc();
    if (npc.getName().equals("Zombified Spawn"))
      zombieSpawnDead = false;
  }


  @Subscribe
  private void onGameStateChanged(final GameStateChanged event) {
    itemsToLoot.clear();
    if (event.getGameState() == GameState.LOADING
            && O)
      i();
  }

  @Subscribe
  private void onChatMessage(final ChatMessage event) {
    if (event.getType() != ChatMessageType.GAMEMESSAGE)
      return;
    Widget widget;
    if ((widget = client.getWidget(10616888)) != null)
      clickBounds = widget.getBounds();
    String prayerMessage = "Your prayers have been disabled!";
    String poisonMessage = "You have been poisoned by venom!";
    String poisonMessageNV = "You have been poisoned!";
    String frozenMessage = "You have been frozen!";
    String spawnExplode = "The spawn violently explodes, unfreezing you as it does so.";
    String unfrozenMessage = "You become unfrozen as you kill the spawn.";
    String killMessage = "Your Vorkath kill count is:";


    if (event.getMessage().equals(killMessage)
            || event.getMessage().contains("Your Vorkath kill count is:")) {
      killCount++;
      killCountTotal++;
    }

    if (event.getMessage().equals(prayerMessage)
            || event.getMessage().contains("Your prayers have been disabled!")){
      utils.attackNPCDirect(vorkath);
    }

    if ((event.getMessage().equals(frozenMessage ) && !config.prayIce())
            || (event.getMessage().contains("You have been frozen!") && !config.prayIce()) ){
      clientThread.invoke(() ->
            client.invokeMenuAction(
                    "Deactivate",
                    "Quick-prayers",
                    1,
                    MenuAction.CC_OP.getId(),
                    -1,
                    10485775));}

    if ((event.getMessage().equals(unfrozenMessage ) && !config.prayIce())
            || (event.getMessage().contains("You become unfrozen as you kill the spawn.") && !config.prayIce()) ){
      clientThread.invoke(() ->
              client.invokeMenuAction(
                      "Activate",
                      "Quick-prayers",
                      1,
                      MenuAction.CC_OP.getId(),
                      -1,
                      10485775));}

    if (event.getMessage().equals(frozenMessage)) {
      noBomb = false;
      zombieSpawnDead = false;
    }

    if (event.getMessage().equals(poisonMessage)) {
      WidgetItem ven = GetAntiVenomItem();
      if (ven != null) {
        clientThread.invoke(() -> client.invokeMenuAction(
                "Drink",
                "<col=ff9040>Potion",
                ven.getId(),
                MenuAction.ITEM_FIRST_OPTION.getId(),
                ven.getIndex(),
                WidgetInfo.INVENTORY.getId()));
        tickDelay = 1;
        utils.attackNPCDirect(vorkath);
      }
    }

    if (event.getMessage().equals(poisonMessageNV)) {
      WidgetItem ven = GetAntiVenomItem();
      if (ven != null && config.antivenomplus()) {
        clientThread.invoke(() -> client.invokeMenuAction(
                "Drink",
                "<col=ff9040>Potion",
                ven.getId(),
                MenuAction.ITEM_FIRST_OPTION.getId(),
                ven.getIndex(),
                WidgetInfo.INVENTORY.getId()));
        tickDelay = 1;
        utils.attackNPCDirect(vorkath);
      }
    }

    if (event.getMessage().equals(spawnExplode)
            || event.getMessage().equals(unfrozenMessage)) {
      noBomb = true;
      zombieSpawnDead = true;
      if (atVorky())
        utils.attackNPCDirect(vorkath);
    }
  }

  private void addAcidSpot(WorldPoint worldPoint) {
    if (!acidSpots.contains(worldPoint))
      acidSpots.add(worldPoint);
  }

  private void calculateAcidFreePath() {
    acidFreePath.clear();
    if (vorkath == null)
      return;
    int[][][] array = { { { 0, 1 }, { 0, -1 } }, { { 1, 0 }, { -1, 0 } } };
    ArrayList<WorldPoint> bestPath = new ArrayList<>();
    double bestClicksRequired = 99.0D;
    WorldPoint worldLocation = client.getLocalPlayer().getWorldLocation();
    WorldPoint worldLocation2 = vorkath.getWorldLocation();
    int n2 = worldLocation2.getX() + 14;
    int n3 = worldLocation2.getX() - 8;
    int n4 = worldLocation2.getY() - 1;
    int n5 = worldLocation2.getY() - 8;
    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        WorldPoint worldPoint = new WorldPoint(
                worldLocation.getX() + i,
                worldLocation.getY() + j,
                worldLocation.getPlane());
        if (!acidSpots.contains(worldPoint)
                && worldPoint.getY() >= n5
                && worldPoint.getY() <= n4)
          for (int l = 0; l < 2; l++) {
            double clicksRequired;
            if ((clicksRequired = (Math.abs(i) + Math.abs(j))) < 2.0D)
              clicksRequired += (Math.abs(j * array[l][0][0]) + Math.abs(i * array[l][0][1]));
            if (l == 0)
              clicksRequired += 0.5D;
            ArrayList<WorldPoint> currentPath;
            (currentPath = new ArrayList<>()).add(worldPoint);
            for (int n7 = 1; n7 < 25; n7++) {
              WorldPoint worldPoint2 = new WorldPoint(
                      worldPoint.getX() + n7 * array[l][0][0],
                      worldPoint.getY() + n7 * array[l][0][1],
                      worldPoint.getPlane());

              if (acidSpots.contains(worldPoint2)
                      || worldPoint2.getY() < n5
                      || worldPoint2.getY() > n4
                      || worldPoint2.getX() < n3
                      || worldPoint2.getX() > n2)
                break;
              currentPath.add(worldPoint2);
            }
            for (int n8 = 1; n8 < 25; n8++) {
              WorldPoint worldPoint3 = new WorldPoint(
                      worldPoint.getX() + n8 * array[l][1][0],
                      worldPoint.getY() + n8 * array[l][1][1],
                      worldPoint.getPlane());

              if (acidSpots.contains(worldPoint3)
                      || worldPoint3.getY() < n5
                      || worldPoint3.getY() > n4
                      || worldPoint3.getX() < n3
                      || worldPoint3.getX() > n2)
                break;
              currentPath.add(worldPoint3);
            }

            if ((currentPath.size() >= config.acidFreePathLength() && clicksRequired < bestClicksRequired)
                    || (clicksRequired == bestClicksRequired && currentPath.size() > bestPath.size())) {
              bestPath = currentPath;
              bestClicksRequired = clicksRequired;
            }
          }
      }
    }
    if (bestClicksRequired != 99.0D)
      acidFreePath = bestPath;
  }

  private void calculateWooxWalkPath()
  {


    if (client.getLocalPlayer() == null || utils.findNearestNpc(8061) == null)
    {
      return;
    }

    final WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
    final WorldPoint vorkLoc = utils.findNearestNpc(8061).getWorldLocation();

    final int maxX = vorkLoc.getX() + 14;
    final int minX = vorkLoc.getX() - 8;
    final int baseX = playerLoc.getX();
    final int baseY = vorkLoc.getY() - 5;
    final int middleX = vorkLoc.getX() + 3;

    // Loop through the arena tiles in the x-direction and
    // alternate between positive and negative x direction
    for (int i = 0; i < 50; i++)
    {
      // Make sure we always choose the spot closest to
      // the middle of the arena
      int directionRemainder = 0;
      if (playerLoc.getX() < middleX)
      {
        directionRemainder = 1;
      }

      int deviation = (int) Math.floor(i / 2.0);
      if (i % 2 == directionRemainder)
      {
        deviation = -deviation;
      }

      final WorldPoint attackLocation = new WorldPoint(baseX + deviation, baseY, playerLoc.getPlane());
      final WorldPoint outOfRangeLocation = new WorldPoint(baseX + deviation, baseY - 1, playerLoc.getPlane());

      if (acidSpots.contains(attackLocation) || acidSpots.contains(outOfRangeLocation)
              || attackLocation.getX() < minX || attackLocation.getX() > maxX)
      {
        continue;
      }

      wooxWalkPath[0] = attackLocation;
      wooxWalkPath[1] = outOfRangeLocation;

      break;
    }
  }

  @Subscribe
  private void onGameObjectSpawned(GameObjectSpawned event) {
    GameObject gameObject;
    if ((gameObject = event.getGameObject()).getId() == 30032
            || gameObject.getId() == 32000)
      addAcidSpot(gameObject.getWorldLocation());
  }

  @Subscribe
  public void onGameObjectDespawned(GameObjectDespawned event) {
    GameObject gameObject;
    if ((gameObject = event.getGameObject()).getId() == 30032
            || gameObject.getId() == 32000)
      acidSpots.remove(gameObject.getWorldLocation());
  }

  public void activatePrayer(WidgetInfo widgetInfo) {
    Widget w;
    if ((w = client.getWidget(widgetInfo)) == null)
      return;
    if (client.getBoostedSkillLevel(Skill.PRAYER) <= 0)
      return;
    clientThread.invoke(() ->
            client.invokeMenuAction(
                    "Activate",
                    w.getName(),
                    1,
                    MenuAction.CC_OP.getId(),
                    w.getItemId(),
                    w.getId()));
  }
}
