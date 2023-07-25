package net.runelite.client.plugins.freeworldvorkath;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Teleportations {

    Frem_sea_boots("Frem sea boots",13131),//
    Ench_lyre("Ench lyre",23458),
    House_tele("House tele",8013),//
    Return_Orb("Return Orb",8013),//
    NPC_kick("NPC kick",3843);

    private String name;
    Teleportations(String name)
    {
        this.name = name;
    }

    private int id;
    Teleportations(int id) { this.id = id;}



}
