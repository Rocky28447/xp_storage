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
        int experience = 0;
        for (int i = lower; i < upper; i++) {
            experience += getXpOfLevel(i);
        }
        return experience;
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

    public static int getXpNeededForNextLevel(PlayerEntity player) {
        return getXpOfLevel(player.experienceLevel + 1) - getPlayerXp(player);
    }

    // Lines 42 - 90 adapted from XpHelper.java, Team CoFH 2023
    // https://github.com/CoFH/CoFHCore/blob/1.19.x/src/main/java/cofh/core/util/helpers/XpHelper.java
    public static void setPlayerLevel(PlayerEntity player, int level) {
        player.experienceLevel = level;
        player.experienceProgress = 0.0F;
    }

    public static int getTotalXpForLevel(int level) {
        return level >= 32 ? (9 * level * level - 325 * level + 4440) / 2 : level >= 17 ? (5 * level * level - 81 * level + 720) / 2 : (level * level + 6 * level);
    }

    public static int getPlayerXp(PlayerEntity player) {
        return getTotalXpForLevel(player.experienceLevel) + getExtraPlayerXp(player);
    }

    public static void setPlayerXp(PlayerEntity player, int exp) {
        player.experienceLevel = 0;
        player.experienceProgress = 0.0F;
        player.totalExperience = 0;

        addXpToPlayer(player, exp);
    }

    public static void addXpToPlayer(PlayerEntity player, int exp) {
        int i = Integer.MAX_VALUE - player.totalExperience;

        if (exp > i) {
            exp = i;
        }

        player.experienceProgress += (float) exp / (float) getXpOfLevel(player.experienceLevel);

        for (player.totalExperience += exp; player.experienceProgress >= 1.0F; player.experienceProgress /= (float) getXpOfLevel(player.experienceLevel)) {
            player.experienceProgress = (player.experienceProgress - 1.0F) * (float) getXpOfLevel(player.experienceLevel);
            addXpLevelToPlayer(player, 1);
        }
    }

    public static void addXpLevelToPlayer(PlayerEntity player, int levels) {
        player.experienceLevel += levels;

        if (player.experienceLevel < 0) {
            player.experienceLevel = 0;
            player.experienceProgress = 0.0F;
            player.totalExperience = 0;
        }
    }

    public static int getExtraPlayerXp(PlayerEntity player) {
        return Math.round(player.experienceProgress * getXpOfLevel(player.experienceLevel));
    }
}
