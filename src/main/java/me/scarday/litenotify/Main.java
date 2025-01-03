package me.scarday.litenotify;

import litebans.api.Entry;
import litebans.api.Events;
import lombok.Getter;
import me.scarday.litenotify.social.Builder;
import me.scarday.litenotify.social.impl.TelegramImpl;
import me.scarday.litenotify.social.impl.VkImpl;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
public class Main extends JavaPlugin {

    VkImpl vk;
    TelegramImpl tg;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (getConfig().getBoolean("settings.tg.enable")) {
            tg = new TelegramImpl(this);

            getLogger().info("Интеграция с Telegram включена!");
        }

        if (getConfig().getBoolean("settings.vk.enable")) {
            vk = new VkImpl(this);

            getLogger().info("Интеграция с VK включена!");
        }

        registerEvents();
    }

    @Override
    public void onDisable() {}

    public void registerEvents() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                sendMessage(entry, false);
            }

            @Override
            public void entryRemoved(Entry entry) {
                sendMessage(entry, true);
            }
        });
    }

    private void sendMessage(Entry entry, boolean isRemove) {
        String user = getUserName(entry.getUuid());
        String pattern = !getConfig().getString("settings.time.format").isEmpty() ? getConfig().getString("settings.time.format") : "HH:mm:ss dd-MM-YYYY";
        String time = new SimpleDateFormat(pattern).format(new Date(entry.getDateStart()));
        String timeEnd = new SimpleDateFormat(getConfig().getString("settings.time.format")).format(new Date(entry.getDateEnd()));
        String normalFormat = entry.getDateEnd() == 0 ? getConfig().getString("messages.none-time") : timeEnd;

        String messageKey;
        switch (entry.getType()) {
            case "ban":
                messageKey = isRemove ? "unban" : "ban";
                break;
            case "mute":
                messageKey = isRemove ? "unmute" : "mute";
                break;
            case "warn":
                messageKey = isRemove ? "unwarn" : "warn";
                break;
            case "kick":
                messageKey = "kick";
                break;
            default:
                return;
        }

        if (getTg() != null) {
            getTg().sendMessage(Builder.builder()
                    .message(joinToString(getConfig().getStringList("messages." + messageKey))
                    .replace("%player%", user)
                    .replace("%ip%", String.valueOf(entry.getId()))
                    .replace("%type%", entry.getType())
                    .replace("%time%", time)
                    .replace("%time_end%", normalFormat)
                    .replace("%server%", entry.getServerOrigin())
                    .replace("%servers%", entry.getServerScope())
                    .replace("%reason%", entry.getReason())
                    .replace("%isIp%", String.valueOf(entry.isIpban()))
                    .replace("%isSilent%", String.valueOf(entry.isSilent()))
                    .replace("%exec_player%", entry.getExecutorName()))
                    .build()
            );
        }

        if (getVk() != null) {
            getVk().sendMessage(Builder.builder()
                    .message(joinToString(getConfig().getStringList("messages." + messageKey))
                            .replace("%player%", user)
                            .replace("%ip%", String.valueOf(entry.getId()))
                            .replace("%type%", entry.getType())
                            .replace("%time%", time)
                            .replace("%time_end%", normalFormat)
                            .replace("%server%", entry.getServerOrigin())
                            .replace("%servers%", entry.getServerScope())
                            .replace("%reason%", entry.getReason())
                            .replace("%isIp%", String.valueOf(entry.isIpban()))
                            .replace("%isSilent%", String.valueOf(entry.isSilent()))
                            .replace("%exec_player%", entry.getExecutorName()))
                    .build()
            );
        }
    }

    private String getUserName(String uuid) {
        OfflinePlayer offlinePlayer = getServer().getPlayer(UUID.fromString(uuid));
        if (offlinePlayer != null) {
            return offlinePlayer.getName() != null && !offlinePlayer.getName().isEmpty() ?
                    offlinePlayer.getName() :
                    getConfig().getString("messages.none-player");
        } else {
            return getConfig().getString("messages.none-player");
        }
    }

    private String joinToString(List<String> list) {
        return String.join("\n", list);
    }
}
