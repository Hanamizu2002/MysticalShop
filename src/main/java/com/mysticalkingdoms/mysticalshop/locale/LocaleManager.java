package com.mysticalkingdoms.mysticalshop.locale;

import com.mysticalkingdoms.mysticalshop.MysticalShop;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocaleManager {

    private final MysticalShop plugin;
    private final YamlDocument langFile;

    public LocaleManager(MysticalShop plugin) {
        this.plugin = plugin;
        this.langFile = plugin.createUpdatingConfig(new File(plugin.getDataFolder(), "lang.yml"));
    }

    public Message getMessage(String path) {
        return new Message(plugin.getAdventure(), MiniMessage.miniMessage().deserialize(langFile.getString(path)));
    }

    public Message getMessage(String path, Map<String, String> preformatPlaceholders) {
        String text = langFile.getString(path);
        for (Map.Entry<String, String> entry : preformatPlaceholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return new Message(plugin.getAdventure(), MiniMessage.miniMessage().deserialize(text));
    }
}

