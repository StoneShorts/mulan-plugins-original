package net.runelite.client.plugins.freeworldvorkath;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RangeWeapons {
    D_H_Crossbow("Dragon hunter crossbow", 21012), //
    A_Crossbow("Armadyl crossbow",11785),//
    D_Crossbow("Dragon crossbow",21902),
    T_Bpipe("Toxic blowpipe",12926),//
    R_Crossbow("Rune crossbow",9185);


    private String name;
    RangeWeapons(String name)
    {
        this.name = name;
    }

    private int id;
    RangeWeapons(int id) { this.id = id;}


}
