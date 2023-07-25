package net.runelite.client.plugins.freeworldvorkath;

import lombok.Getter;

@Getter
public enum BankAreas {
    Crafting_Guild("Crafting Guild"),
    Draynor("Draynor"),
    Ferox_Enclave("Ferrox Enclave"),
    Lunar_Isle("Lunar Isle");

    private final String name;

    BankAreas(String name) {
        this.name = name;
    }
}
