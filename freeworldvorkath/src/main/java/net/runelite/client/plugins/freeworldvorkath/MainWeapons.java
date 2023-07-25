package net.runelite.client.plugins.freeworldvorkath;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MainWeapons {
    D_H_Lance("D. H. lance",22978), //
    D_H_Crossbow("Dragon hunter crossbow", 21012),
    G_Rapier("G. rapier",22324),//
    Z_Hasta("Z. hasta",11889),
    A_Crossbow("Armadyl crossbow",11785),
    D_Crossbow("Dragon crossbow",21902),//
    A_Dagger("A. dagger",13265),
    T_Bpipe("Toxic blowpipe",12926),
    R_Crossbow("Rune crossbow",9185),
    L_B_Sword("L-B sword",11902);


    private String name;
    MainWeapons(String name)
    {
        this.name = name;
    }

    private int id;
    MainWeapons(int id) { this.id = id;}


}
