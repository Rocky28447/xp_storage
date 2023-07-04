package com.github.charlyb01.xpstorage;

import com.github.charlyb01.xpstorage.config.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class XpStorage implements ModInitializer {
    public final String MOD_ID = "xp_storage";

    public static final Item CRYSTALLIZED_LAPIS = new Item(new Item.Settings());
    public static XpBook xp_book1;
    public static XpBook xp_book2;
    public static XpBook xp_book3;

    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, PartitioningSerializer.wrap(GsonConfigSerializer::new));

        xp_book1 = new XpBook(ModConfig.get().books.book1.levelCapacity, false, Rarity.COMMON, ModConfig.get().books.book1.xpReturnPercent);
        xp_book2 = new XpBook(ModConfig.get().books.book2.levelCapacity, true, Rarity.UNCOMMON, ModConfig.get().books.book2.xpReturnPercent);
        xp_book3 = new XpBook(ModConfig.get().books.book3.levelCapacity, true, Rarity.RARE, ModConfig.get().books.book3.xpReturnPercent);

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "crystallized_lapis"), CRYSTALLIZED_LAPIS);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "xp_book"), xp_book1);
        if (ModConfig.get().books.nbBooks > 1)
            Registry.register(Registry.ITEM, new Identifier(MOD_ID, "xp_book2"), xp_book2);
        if (ModConfig.get().books.nbBooks > 2)
            Registry.register(Registry.ITEM, new Identifier(MOD_ID, "xp_book3"), xp_book3);

//        ItemGroupEvents.modifyEntriesEvent(ItemGroup.MATERIALS).register(entries -> entries.add(CRYSTALLIZED_LAPIS));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroup.TOOLS).register(entries -> {
//            entries.add(xp_book1);
//            if (ModConfig.get().books.nbBooks > 1)
//                entries.add(xp_book2);
//            if (ModConfig.get().books.nbBooks > 2)
//                entries.add(xp_book3);
//        });
    }
}
