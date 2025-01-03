package dev.scarday.litenotify;

import dev.scarday.litenotify.configuration.Configuration;
import dev.scarday.litenotify.handler.LiteBansListener;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import lombok.Getter;
import dev.scarday.litenotify.social.impl.TelegramImpl;
import dev.scarday.litenotify.social.impl.VkImpl;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public class Main extends JavaPlugin {

    private VkImpl vk;
    private TelegramImpl tg;

    private Configuration configuration;

    @Override
    public void onEnable() {
        loadConfiguration();

        if (getConfiguration().getTelegram().isEnable()) {
            tg = new TelegramImpl(this);
            getLogger().info("Интеграция с Telegram включена!");
        }
        if (getConfiguration().getVk().isEnable()) {
            vk = new VkImpl(this);
            getLogger().info("Интеграция с VK включена!");
        }
        new LiteBansListener(this).register();
    }

    public void loadConfiguration() {
        try {
            configuration = ConfigManager.create(Configuration.class, (it) -> {
                it.withConfigurer(new YamlBukkitConfigurer(), new SerdesBukkit());
                it.withBindFile(new File(this.getDataFolder(), "config.yml"));
                it.saveDefaults();
                it.load(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {}
}
