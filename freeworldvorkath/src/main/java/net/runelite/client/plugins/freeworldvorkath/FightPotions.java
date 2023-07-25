package net.runelite.client.plugins.freeworldvorkath;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FightPotions {
    Divine_s_combat("Divine super combat", 23685), //
    Super_combat("Super combat",12695),//
    Combat_potion("Combat potion",9739),//
    Divine_ranging("Divine ranging",23733),
    Divine_bastion("Divine bastion",24635),//
    Bastion_potion("Bastion potion",22461),//
    Ranging_potion("Ranging potion",2444),//
    //Divine_magic_potion("Divine magic potion"),
    //Divine_battlemage_potion("Divine battlemage potion"),
    ;


    private String name;
    FightPotions(String name)
    {
        this.name = name;
    }

    private int id;
    FightPotions(int id) { this.id = id;}


}
