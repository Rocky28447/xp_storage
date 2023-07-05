package com.github.charlyb01.xpstorage;

import com.github.charlyb01.xpstorage.cardinal.MyComponents;
import com.github.charlyb01.xpstorage.config.ModConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.github.charlyb01.xpstorage.Utils.*;

import java.util.List;

public class XpBook extends Item {
    private final int maxLevel;
    private final int maxExperience;
    private final float xpPenalty;

    public XpBook(int maxLevel, boolean isFireproof, Rarity rarity, float penalty) {
        super(isFireproof ? new Item.Settings().maxCount(1).rarity(rarity).fireproof()
                : new Item.Settings().maxCount(1).rarity(rarity));

        this.maxLevel = maxLevel;
        this.maxExperience = getTotalXpForLevel(maxLevel);
        this.xpPenalty = penalty;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int bookExperience = MyComponents.XP_COMPONENT.get(stack).getAmount();
        tooltip.add(Text.translatable("item.xp_storage.xp_books.advanced_tooltip", bookExperience, maxExperience)
                .formatted(Formatting.GRAY));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        int bookExperience = MyComponents.XP_COMPONENT.get(stack).getAmount();
        return (bookExperience / (float) this.maxExperience) * 100 >= ModConfig.get().cosmetic.glint;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x7AAC52;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int bookExperience = MyComponents.XP_COMPONENT.get(stack).getAmount();
        return Math.round((bookExperience * 13) / (float)this.maxExperience);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return MyComponents.XP_COMPONENT.get(stack).getAmount() > 0;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        int bookXp = MyComponents.XP_COMPONENT.get(stack).getAmount();
        int playerXp = getPlayerXp(player);
        int playerLevel = player.experienceLevel;

        if (world.isClient) {
            if (player.isSneaking() && bookXp > 0) {
                player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }
        } else {
            int addBookXp = 0;
            int addPlayerXp = 0;

            if (player.isSneaking()) { // Withdrawal
                int desiredXpWithdrawal = getXpBetweenLevels(playerLevel, playerLevel + 1) - getExtraPlayerXp(player);
                float actualXpWithdrawal = Math.min(desiredXpWithdrawal / xpPenalty, bookXp);
                addBookXp = -Math.round(actualXpWithdrawal);
                addPlayerXp = Math.round(actualXpWithdrawal * xpPenalty);
            } else { // Deposit
                if (getExtraPlayerXp(player) > 0) {
                    addBookXp = Math.min(getExtraPlayerXp(player), maxExperience - bookXp);
                    addPlayerXp = -addBookXp;
                } else if (player.experienceLevel > 0) {
                    addBookXp = Math.min(getXpBetweenLevels(player.experienceLevel - 1, player.experienceLevel), maxExperience - bookXp);
                    addPlayerXp = -addBookXp;
                }
            }

            player.addExperience(addPlayerXp);
            MyComponents.XP_COMPONENT.get(stack).setAmount(bookXp + addBookXp);
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
    }
}
