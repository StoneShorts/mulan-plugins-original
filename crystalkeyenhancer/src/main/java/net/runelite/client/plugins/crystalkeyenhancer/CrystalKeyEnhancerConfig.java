package net.runelite.client.plugins.crystalkeyenhancer;

import net.runelite.client.config.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("CrystalKeyEnhancer")
public interface CrystalKeyEnhancerConfig extends Config
{

    @ConfigSection(
        keyName = "delayConfig",
        name = "Sleep Delay Configuration",
        description = "Configure how the bot handles sleep delays",
        closedByDefault = true,
        position = 2
    )
    String delayConfig = "delayConfig";

    @Range(
        min = 0,
        max = 550
    )
    @ConfigItem(
        keyName = "sleepMin",
        name = "Sleep Min",
        description = "",
        position = 3,
        section = "delayConfig"
    )
    default int sleepMin()
    {
        return 60;
    }

    @Range(
        min = 0,
        max = 550
    )
    @ConfigItem(
        keyName = "sleepMax",
        name = "Sleep Max",
        description = "",
        position = 4,
        section = "delayConfig"
    )
    default int sleepMax()
    {
        return 350;
    }

    @Range(
        min = 0,
        max = 550
    )
    @ConfigItem(
        keyName = "sleepTarget",
        name = "Sleep Target",
        description = "",
        position = 5,
        section = "delayConfig"
    )
    default int sleepTarget()
    {
        return 100;
    }

    @Range(
        min = 0,
        max = 550
    )
    @ConfigItem(
        keyName = "sleepDeviation",
        name = "Sleep Deviation",
        description = "",
        position = 6,
        section = "delayConfig"
    )
    default int sleepDeviation()
    {
        return 10;
    }

    @ConfigItem(
        keyName = "sleepWeightedDistribution",
        name = "Sleep Weighted Distribution",
        description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
        position = 7,
        section = "delayConfig"
    )
    default boolean sleepWeightedDistribution()
    {
        return false;
    }

    @ConfigSection(
        keyName = "delayTickConfig",
        name = "Game Tick Configuration",
        description = "Configure how the bot handles game tick delays, 1 game tick equates to roughly 600ms",
        closedByDefault = true,
        position = 10
    )
    String delayTickConfig = "delayTickConfig";

    @Range(
        min = 0,
        max = 10
    )
    @ConfigItem(
        keyName = "tickDelayMin",
        name = "Game Tick Min",
        description = "",
        position = 11,
        section = "delayTickConfig"
    )
    default int tickDelayMin()
    {
        return 1;
    }

    @Range(
        min = 0,
        max = 10
    )
    @ConfigItem(
        keyName = "tickDelayMax",
        name = "Game Tick Max",
        description = "",
        position = 12,
        section = "delayTickConfig"
    )
    default int tickDelayMax()
    {
        return 3;
    }

    @Range(
        min = 0,
        max = 10
    )
    @ConfigItem(
        keyName = "tickDelayTarget",
        name = "Game Tick Target",
        description = "",
        position = 13,
        section = "delayTickConfig"
    )
    default int tickDelayTarget()
    {
        return 2;
    }

    @Range(
        min = 0,
        max = 10
    )
    @ConfigItem(
        keyName = "tickDelayDeviation",
        name = "Game Tick Deviation",
        description = "",
        position = 14,
        section = "delayTickConfig"
    )
    default int tickDelayDeviation()
    {
        return 1;
    }

    @ConfigItem(
        keyName = "tickDelayWeightedDistribution",
        name = "Game Tick Weighted Distribution",
        description = "Shifts the random distribution towards the lower end at the target, otherwise it will be an even distribution",
        position = 15,
        section = "delayTickConfig"
    )
    default boolean tickDelayWeightedDistribution()
    {
        return false;
    }


    @ConfigSection(
        keyName = "instructionsTitle",
        name = "Instructions",
        description = "Start in the GE with nothing in your inventory. Required items in bank: Eternal teleport crystal, Varrock teletabs (tele to GE), crystal shards (minimum 10), crystal keys. ",
        position = 0
    )
    String instructionsTitle = "instructionsTitle";

    @ConfigItem(
            keyName = "Type",
            name = "Type",
            description = "Select mode of auto fighter.",
            position = 41,
            title = "AFighter"
    )
    default CrystalKeyEnhancerType Type() {
        return CrystalKeyEnhancerType.MAKE_KEYS;
    }


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

    @ConfigItem(
        keyName = "startButton",
        name = "Start/Stop",
        description = "Test button that changes variable value",
        position = 100
    )
    default Button startButton()
    {
        return new Button();
    }
}
