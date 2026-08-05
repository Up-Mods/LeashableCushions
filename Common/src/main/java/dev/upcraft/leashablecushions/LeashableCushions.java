package dev.upcraft.leashablecushions;

import net.minecraft.resources.Identifier;

public class LeashableCushions {
    public static final String MOD_ID = "leashablecushions";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
