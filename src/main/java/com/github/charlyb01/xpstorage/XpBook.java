package com.github.charlyb01.xpstorage;

import com.github.charlyb01.xpstorage.config.ModConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class XpBook extends Item {
    private final int maxLevel;
    private final int maxExperience;

    public XpBook(final int maxLevel, final int maxExperience) {
        super(new Item.Settings()
                .group(ItemGroup.MISC)
                .maxDamage(maxExperience));

        this.maxLevel = maxLevel;
        this.maxExperience = maxExperience;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("item.xp_storage.xp_books.tooltip", maxLevel));
        tooltip.add(new TranslatableText("item.xp_storage.xp_books.tooltip2", stack.getDamage(), maxExperience)
                .formatted(Formatting.GRAY).formatted(Formatting.ITALIC));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.getDamage() > 0;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        float f = Math.max(0.0F, (float)stack.getDamage() / (float)this.maxExperience);
        return MathHelper.packRgb((f < 0.67F) ? (int) (((0.67F - f) / 0.67F) * 222) : 0,
                255,
                (f > 0.67F) ? (int) (((f - 0.67F) / 0.67F) * 111) : 0);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round((float)stack.getDamage() * 13.0F / (float)this.maxExperience);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = (user.getMainHandStack().getItem() instanceof XpBook) ? user.getMainHandStack() : user.getOffHandStack();
        int bookExperience = stack.getDamage();
        int playerExperience = Utils.getExperienceToLevel(user.experienceLevel);
        playerExperience += user.experienceProgress * Utils.getLevelExperience(user.experienceLevel);

        if (world.isClient) {
            // Play sound when emptying
            if (!user.isSneaking() && playerExperience > 0 && bookExperience < maxExperience) {
                user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }
        } else {
            // Empty / Fill
            if (user.isSneaking()) {
                int retrievedExperience = (int) (bookExperience * (ModConfig.get().XP_FROM_BOOK_USE / 100.0F));
                ExperienceOrbEntity.spawn((ServerWorld) world, user.getPos(), retrievedExperience);
                stack.setDamage(0);
            } else {
                // Check max value
                if (maxExperience - bookExperience < playerExperience) {
                    user.addExperience(bookExperience - maxExperience);
                    stack.setDamage(maxExperience);
                } else {
                    stack.setDamage(bookExperience + playerExperience);
                    user.addExperience(-playerExperience);
                    user.experienceProgress = 0.0F;
                }
            }
        }

        return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
    }
}
