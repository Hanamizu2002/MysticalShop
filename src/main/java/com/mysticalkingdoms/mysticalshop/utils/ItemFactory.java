package com.mysticalkingdoms.mysticalshop.utils;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemFactory {

    public static ItemStack createItem(Section section, Map<String, String> placeholders) {
        if (section == null) {
            return null;
        }
        Material material = Material.getMaterial(section.getString("material"));

        String nameString = section.getString("name");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            nameString = nameString.replace(entry.getKey(), entry.getValue());
        }

        Component name = MiniMessage.miniMessage().deserialize(nameString)
                .applyFallbackStyle(TextDecoration.ITALIC.withState(false));

        List<String> loreString = section.getStringList("lore");
        loreString.replaceAll(text -> {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                text = text.replace(entry.getKey(), entry.getValue());
            }
            return text;
        });
        List<Component> lore = loreString.stream()
                .map(text -> MiniMessage.miniMessage().deserialize(text)
                        .applyFallbackStyle(TextDecoration.ITALIC.withState(false)))
                .collect(Collectors.toList());

        String texture = section.getString("texture");
        boolean glow = section.getBoolean("glow");
        int modelData = section.getInt("modelData");

        if (texture != null) {
            return ItemBuilder.skull()
                    .texture(texture)
                    .name(name)
                    .lore(lore)
                    .glow(glow)
                    .model(modelData)
                    .build();
        }

        return ItemBuilder.from(material)
                .name(name)
                .lore(lore)
                .glow(glow)
                .model(modelData)
                .build();
    }

}
