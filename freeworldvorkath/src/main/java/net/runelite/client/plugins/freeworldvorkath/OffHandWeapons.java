package net.runelite.client.plugins.freeworldvorkath;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OffHandWeapons {
    Avernic_Def("Avernic def",22322), //
    Dragon_Def("Dragon def",12954),//
    Dfire_Shield("Dfire shield",11283),//
    Toktz_K_Xil("Toktz-k-xil",6524),
    Rune_Def("Rune def",8850),
    Anti_D_Shield("Anti-D Shield",1540);


    private String name;
    OffHandWeapons(String name)
    {
        this.name = name;
    }

    private int id;
    OffHandWeapons(int id) { this.id = id;}


}
