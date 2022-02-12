package net.runelite.client.plugins.freeworldvorkath;

import lombok.Getter;

@Getter
public enum Potions {
    Prayer_potion("Prayer potion"),
    Super_restore("Super restore");

    private final String name;

    Potions(String name) {
        this.name = name;
    }
}
