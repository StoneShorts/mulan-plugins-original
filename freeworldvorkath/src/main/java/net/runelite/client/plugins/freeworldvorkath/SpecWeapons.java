package net.runelite.client.plugins.freeworldvorkath;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SpecWeapons {
    D_Claws("D. claws",13652), //
    B_Godsword("B. godsword",11804),//
    D_Warhammer("D. warhammer",13576),//
    S_Godsword("S. godsword",11806),
    A_Godsword("A. godsword",11802);


    private String name;
    SpecWeapons(String name)
    {
        this.name = name;
    }

    private int id;
    SpecWeapons(int id) { this.id = id;}


}
