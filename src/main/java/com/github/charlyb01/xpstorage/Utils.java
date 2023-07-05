package com.github.charlyb01.xpstorage;

import net.minecraft.entity.player.PlayerEntity;

public class Utils {

    public static int getXpOfLevel(final int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else {
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
    }

    public static int getXpBetweenLevels(final int lower, final int upper) {
        return getTotalXpForLevel(upper) - getTotalXpForLevel(lower);
    }

    public static int getLevelFromXp(final int experience) {
        if (experience <= 0)
            return 0;

        int level = 0;
        int xp = 0;
        while (xp < experience) {
            xp += getXpOfLevel(level++);
        }

        return xp == experience ? level : level - 1;
    }

    // Lines 38 - 48 from XpHelper.java, Team CoFH 2023
    // https://github.com/CoFH/CoFHCore/blob/1.19.x/src/main/java/cofh/core/util/helpers/XpHelper.java
    public static int getTotalXpForLevel(int level) {
        return level >= 32 ? (9 * level * level - 325 * level + 4440) / 2 : level >= 17 ? (5 * level * level - 81 * level + 720) / 2 : (level * level + 6 * level);
    }

    public static int getPlayerXp(PlayerEntity player) {
        return getTotalXpForLevel(player.experienceLevel) + getExtraPlayerXp(player);
    }

    public static int getExtraPlayerXp(PlayerEntity player) {
        return Math.round(player.experienceProgress * getXpOfLevel(player.experienceLevel));
    }
}
