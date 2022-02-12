package net.runelite.client.plugins.freeworldvorkath;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Food {

    Anglerfish("Anglerfish",13441),
    Cooked_karambwan("Cooked karambwan",3144),
    Dark_crab("Dark crab",11936),
    Manta_ray("Manta ray",391),
    Mushroom_potato("Mushroom potato",7058),
    Saradomin_brew("Saradomin brew",6685),
    Sea_turtle("Sea turtle",397),
    Shark("Shark",385);

    private String name;
    Food(String name)
    {
        this.name = name;
    }

    private int id;
    Food(int id) { this.id = id;}



}
