package net.runelite.client.plugins.freeworldvorkath;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("avork")
public interface FreeWorldVorkathConfig extends Config {
    @ConfigSection(
            keyName = "delayConfig",
            name = "Sleep Delay Configuration",
            description = "Configure how the plugin handles sleep delays.",
            closedByDefault = true,
            position = 0)
    public static String delayConfig = "delayConfig";

    @ConfigSection(
            keyName = "fightConfig",
            name = "Melee or Range",
            description = "Use range or melee in the settings below.",
            closedByDefault = true,
            position = 1)
    public static String fightConfig = "fightConfig";

    @ConfigSection(
            keyName = "eatConfig",
            name = "Food and potions",
            description = "Set everything from food to potions.",
            closedByDefault = true,
            position = 2)
    public static String eatConfig = "eatConfig";

    @ConfigSection(
            keyName = "teleConfig",
            name = "Teleportations and such",
            description = "Set teleport in and out options.",
            closedByDefault = true,
            position = 3)
    public static String teleConfig = "teleConfig";

    @ConfigSection(
            keyName = "miscConfig",
            name = "Miscellanous",
            description = "Stuff like: POH pool, alching, looting.",
            closedByDefault = true,
            position = 4)
    public static String miscConfig = "miscConfig";

    @Range(min = 0, max = 550)
    @ConfigItem(
            keyName = "sleepMin",
            name = "Sleep Min",
            description = "",
            position = 2,
            section = "delayConfig")
    default int sleepMin() {
        return 60;
    }

    @Range(min = 0, max = 550)
    @ConfigItem(
            keyName = "sleepMax",
            name = "Sleep Max",
            description = "",
            position = 3,
            section = "delayConfig")
    default int sleepMax() {
        return 350;
    }

    @Range(min = 0, max = 550)
    @ConfigItem(
            keyName = "sleepTarget",
            name = "Sleep Target",
            description = "",
            position = 4,
            section = "delayConfig")
    default int sleepTarget() {
        return 100;
    }

    @Range(min = 0, max = 550)
    @ConfigItem(
            keyName = "sleepDeviation",
            name = "Sleep Deviation",
            description = "",
            position = 5,
            section = "delayConfig")
    default int sleepDeviation() {
        return 10;
    }

    @ConfigItem(
            keyName = "sleepWeightedDistribution",
            name = "Sleep Weighted Distribution",
            description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
            position = 6,
            section = "delayConfig")
    default boolean sleepWeightedDistribution() {
        return false;
    }

    @Range(min = 0, max = 10)
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 8,
            section = "delayConfig")
    default int tickDelayMin() {
        return 1;
    }

    @Range(min = 0, max = 10)
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 9,
            section = "delayConfig")
    default int tickDelayMax() {
        return 3;
    }

    @Range(min = 0, max = 10)
    @ConfigItem(
            keyName = "tickDelayTarget",
            name = "Game Tick Target",
            description = "",
            position = 10,
            section = "delayConfig")
    default int tickDelayTarget() {
        return 2;
    }

    @Range(min = 0, max = 10)
    @ConfigItem(
            keyName = "tickDelayDeviation",
            name = "Game Tick Deviation",
            description = "",
            position = 11,

            section = "delayConfig")
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribution",
            description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
            position = 12,
            section = "delayConfig")
    default boolean tickDelayWeightedDistribution() {
        return false;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @ConfigItem(
            keyName = "useRanged",
            name = "Ranged Mode",
            description = "If disabled, uses melee",
            position = 1,
            section = "fightConfig")
    default boolean useRanged() {
        return true;
    }

    @ConfigItem(
            keyName = "useSerp",
            name = "Use Serp Helmet",
            description = "Enable to not withdraw anti venom/anti dote",
            position = 2,
            section = "fightConfig")
    default boolean useSerp() {
        return false;
    }

    @ConfigItem(
            keyName = "useSpec",
            name = "Use Spec Weapon",
            description = "Enable to use a special attack.",
            position = 2,
            section = "fightConfig")
    default boolean useSpec() {
        return false;
    }

    @ConfigItem(
            keyName = "specWeapons",
            name = "Spec Weapon",
            description = "Select your special attack weapon",
            position = 86,
            hidden = true,
            unhide = "useSpec",
            section = "fightConfig")
    default SpecWeapons specWeapons() {
        return SpecWeapons.D_Claws;
    }

    @ConfigItem(
            keyName = "meleeWeapon",
            name = "Main Weapon",
            description = "ID of regular weapon",
            position = 87,
            hidden = true,
            unhide = "useSpec",
            section = "fightConfig")
    default MainWeapons mainWeapons() {
        return MainWeapons.D_H_Lance;
    }

    @ConfigItem(
            keyName = "offhand",
            name = "Regular Offhand",
            description = "Choose your offhand weapon",
            position = 88,
            hidden = true,
            unhide = "useSpec",
            section = "fightConfig")
    default OffHandWeapons offHand() {
        return OffHandWeapons.Dragon_Def;
    }

    @ConfigItem(
            keyName = "specHP",
            name = "Spec HP",
            description = "Minimum health Vorkath must have before spec",
            position = 89,
            hidden = true,
            unhide = "useSpec",
            section = "fightConfig")
    default int specHP() {
        return 200;
    }
    @ConfigItem(
            keyName = "specThreshold",
            name = "Spec Energy",
            description = "Amount of special attack energy required to spec",
            position = 90,
            hidden = true,
            unhide = "useSpec",
            section = "fightConfig")
    default int specThreshold() {
        return 50;
    }

    @ConfigItem(
            keyName = "useBlowpipe",
            name = "Blowpipe",
            description = "If disabled, will attempt to use Bolts",
            position = 5,
            hidden = true,
            unhide = "useRanged",
            section = "fightConfig")
    default boolean useBlowpipe() {
        return false;
    }

    @ConfigItem(
            keyName = "potThreshold",
            name = "Boost level",
            description = "Enter level to drink combat related potions, e.g set at 99, it will drink at or below 99",
            position = 6,
            section = "fightConfig")
    default int potThreshold() {
        return 99;
    }

    @ConfigItem(
            keyName = "fightPotions",
            name = "Fight potions",
            description = "Supported potions to fight.",
            position = 5,
            title = "Selecting potion",
            section = "fightConfig")
    default FightPotions fightPotions() {return FightPotions.Ranging_potion;}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ConfigItem(
            keyName = "superantifire",
            name = "Ext Super Antifire",
            description = "Enable to use Extended Super Antifire. Disable to use regular antifire.",
            position = 66,
            section = "eatConfig")
    default boolean superantifire() {
        return true;
    }

    @ConfigItem(
            keyName = "antivenomplus",
            name = "Anti Venom+",
            description = "Enable to use Anti-venom+. Disable to use Antidote++",
            position = 67,
            section = "eatConfig")
    default boolean antivenomplus() {
        return true;
    }

    @ConfigItem(
            keyName = "antipoisonamount",
            name = "Anti Venom Amount",
            description = "Amount of (4) dose Antivenom+, or Antidote++ to take",
            position = 68,
            section = "eatConfig")
    default int antipoisonamount() {
        return 1;
    }




    @ConfigItem(
            keyName = "prayDrinker",
            name = "Drink Prayer Potions",
            description = "Automatically drinks prayer pots/super restores",
            position = 70,
            section = "eatConfig")
    default boolean prayerDrink()
    {
        return false;
    }

    @ConfigItem(
            keyName = "potions",
            name = "Potions",
            description = "Supported prayer refilling potions",
            position = 71,
            title = "Selecting potions for prayer refill",
            section = "eatConfig")
    default Potions potions() {return Potions.Prayer_potion;}

    @ConfigItem(
            keyName = "praypotAmount",
            name = "Prayer refill",
            description = "Amount of super restores to withdraw from the bank",
            position = 72,
            section = "eatConfig")
    default int praypotAmount() {
        return 2;
    }

    @ConfigItem(
            keyName = "minPrayerLevel",
            name = "Minimum level to drink at",
            description = "",
            position = 73,
            section = "eatConfig"
    )
    default int minPrayerLevel() { return 1; }

    @ConfigItem(
            keyName = "maxPrayerLevel",
            name = "Maximum level to drink at",
            description = "",
            position = 74,
            section = "eatConfig"
    )
    default int maxPrayerLevel() { return 30; }

    @ConfigItem(
            keyName = "eatfood",
            name = "Eat food",
            description = "Enable to eat food",
            position = 75,
            section = "eatConfig")
    default boolean eatFood() {return true;}


    @ConfigItem(
            keyName = "minimumHealthSingle",
            name = "Health to eat at",
            description = "Health to single eat food at",
            position = 77,
            section = "eatConfig"
    )
    default int minimumHealthSingle()
    {
        return 70 ;
    }

    @ConfigItem(
            keyName = "foodID",
            name = "Food",
            description = "Supported food",
            title = "Selecting food",
            position = 78,
            section = "eatConfig")
    default Food foodID() {return Food.Shark;}

    @ConfigItem(
            keyName = "foodAmount",
            name = "First type of food",
            description = "Amount of food to withdraw",
            position = 79,
            section = "eatConfig")
    default int foodAmount() {
        return 17;
    }

    @ConfigItem(
            keyName = "usefood2",
            name = "Second type of food",
            description = "Enable to take a second type of food with you",
            position = 80,
            section = "eatConfig")
    default boolean useFood2() {
        return false;
    }

    @ConfigItem(
            keyName = "foodID2",
            name = "Food",
            description = "Supported food",
            title = "Selecting food",
            position = 82,
            hidden = true,
            unhide = "usefood2",
            section = "eatConfig")
    default Food foodID2() {return Food.Cooked_karambwan;}

    @ConfigItem(
            keyName = "foodAmount2",
            name = "Amount of food 2",
            description = "Amount of food to withdraw",
            position = 83,
            hidden = true,
            unhide = "usefood2",
            section = "eatConfig")
    default int foodAmount2() {
        return 4;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @ConfigItem(
            keyName = "enablekcTele",
            name = "Enable to use teleport after killcount",
            description = "",
            position = 73,
            section = "teleConfig")
    default boolean enablekcTele() {return false;
}

    @ConfigItem(
            keyName = "killCountTele",
            name = "Teleport killcount",
            description = "Kills before teleporting out",
            position = 74,
            hidden = true,
            unhide = "enablekcTele",
            hideValue = "true",
            section = teleConfig)
    default int killCountTele()
{
    return 2;
}


    @ConfigItem(
            keyName = "teleportations",
            name = "Tele to Rellekka options",
            description = "Choose a way to get to Rellekka",
            position = 75,
            section = "teleConfig")
    default Teleportations teleportations() {return Teleportations.House_tele;}

    @ConfigItem(
            keyName = "useteletab",
            name = "Tele with tele house tabs",
            description = "Enable to teleport out with tele house tabs to Rellekka/home.",
            position = 76,
            section = "teleConfig")
    default boolean teleTab() {
        return false;
    }

    @ConfigItem(
            keyName = "onlytelenofood",
            name = "Tele when out of food/prayer",
            description = "Enable to only teleport out when you have 0 food and / or 0 restore pots. Disable to teleport out after every kill.",
            position = 77,
            section = "teleConfig")
    default boolean onlytelenofood() {
        return false;
    }

    @ConfigItem(
            keyName = "healthTP",
            name = "Tele out health",
            description = "Minimum health to allow before teleporting (after running out of food)",
            position = 83,
            section = "teleConfig")
    default int healthTP() {
        return 40;
    }

    @ConfigItem(
            keyName = "prayTP",
            name = "Tele out prayer",
            description = "Minimum prayer to allow before teleporting (after running out of potions)",
            position = 84,
            section = "teleConfig")
    default int prayTP() {
        return 1;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ConfigItem(
            keyName = "prayIce",
            name = "Zombie Spawn pray",
            description = "Enable to not turn off prayer at zombie spawn",
            position = 1,
            section = "miscConfig")
    default boolean prayIce()
    {
        return false;
    }


    @ConfigItem(
            keyName = "bankAreas",
            name = "Bank Areas",
            description = "Supported banking areas",
            position = 2,
            title = "Selecting bank area",
            section = "miscConfig")
    default BankAreas bankAreas() {return BankAreas.Draynor;}

    @ConfigItem(
            keyName = "usepohpool",
            name = "Drink POH Pool",
            description = "Enable to drink from POH pool to restore HP / Prayer.",
            position = 3,
            section = "miscConfig")
    default boolean usePOHpool() {
        return true;
    }

    @ConfigItem(
            keyName = "pools",
            name = "Pools",
            description = "Supported pools in POH",
            position = 4,
            title = "Selecting pool",
            hidden = true,
            unhide = "usepohpool",
            hideValue = "true",
            section = "miscConfig")
    default Pools pools() {return Pools.Restoration_pool;}

    @ConfigItem(
            keyName = "acidFreePathMinLength",
            name = "Acid walk length",
            description = "Minimum length of acid walk",
            position = 5,
            section = "miscConfig")
    default int acidFreePathLength()
    {
        return 3;
    }

    @ConfigItem(
            keyName = "lootNames",
            name = "Items to loot (separate with comma)",
            description = "Provide partial or full names of items you'd like to loot.",
            position = 6,
            section = "miscConfig")
    default String lootNames() {
        return "head,key,visage,hide,bone,rune,diamond,ore,onyx,diamond,dragonstone,snapdragon,torstol,ranarr,maple,yew,magic,palm,spirit,dragonfruit,celastrus,redwood,bar,uncut,jar,arrow,logs,dragon,manta,coins,battlestaff,left";
    }

    @ConfigItem(
            keyName = "alchNames",
            name = "Items to high alch (separate with comma)",
            description = "Provide partial or full names of items you'd like to alch.",
            position = 7,
            section = "miscConfig")
    default String alchNames() {
        return "kiteshield,sword,spear,legs,skirt,sq,med,battleaxe";
    }

    @ConfigItem(
            keyName = "dontlootHide",
            name = "Don't loot the blue D'hide",
            description = "",
            position = 8,
            section = "miscConfig")
    default boolean dontlootHide() {
        return true;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @ConfigItem(
            keyName = "enableUI",
            name = "Enable UI",
            description = "Enable to turn on in game UI",
            position = 95
    )
    default boolean enableUI()
    {
        return true;
    }

    @ConfigItem(keyName = "startButton", name = "Start vork", description = "", position = 150)
    default Button startButton() {
        return new Button();
    }

    @ConfigItem(keyName = "stopButton", name = "Stop vork", description = "", position = 150)
    default Button stopButton() {
        return new Button();
    }
}
