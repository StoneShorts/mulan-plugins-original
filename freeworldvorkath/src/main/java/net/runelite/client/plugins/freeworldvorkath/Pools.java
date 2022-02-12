package net.runelite.client.plugins.freeworldvorkath;

import lombok.Getter;

@Getter
public enum Pools {
    Restoration_pool("Restoration pool"),
    Revitalisation_pool("Revitalisation pool"),
    Pool_of_Rejuvination("Pool of Rejuvination"),
    Fancy_pool("Fancy rejuvenation pool"),
    Ornate_pool("Ornate rejuvenation pool");

    private final String name;

    Pools(String name) {
        this.name = name;
    }
}
