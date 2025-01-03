package dev.scarday.litenotify.handler;

import dev.scarday.litenotify.Main;
import dev.scarday.litenotify.configuration.Configuration;
import litebans.api.Entry;
import litebans.api.Events;
import dev.scarday.litenotify.social.Builder;
import dev.scarday.litenotify.social.impl.TelegramImpl;
import dev.scarday.litenotify.social.impl.VkImpl;
import lombok.val;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class LiteBansListener implements Listener {

    private final Main plugin;
    private final TelegramImpl tg;
    private final VkImpl vk;
    private final Configuration config;

    public LiteBansListener(Main plugin) {
        this.plugin = plugin;
        this.tg = plugin.getTg();
        this.vk = plugin.getVk();
        this.config = plugin.getConfiguration();
    }

    public void register() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                sendNotification(entry, false);
            }
            @Override
            public void entryRemoved(Entry entry) {
                sendNotification(entry, true);
            }
        });
    }

    private void sendNotification(Entry entry, boolean isRemove) {
        val key = getMessageKey(entry.getType(), isRemove);
        if (key == null) return;

        String message = key.stream()
                .map(line -> replacePlaceholders(line, entry))
                .collect(Collectors.joining("\n"));

        if (tg != null) {
            tg.sendMessage(Builder.builder()
                    .message(message)
                    .build()
            );
        }

        if (vk != null) {
            vk.sendMessage(Builder.builder()
                    .message(message)
                    .build()
            );
        }
    }

    private List<String> getMessageKey(String type, boolean isRemove) {
        return switch (type.toLowerCase()) {
            case "ban" -> isRemove ? config.getMessages().getUnban() : config.getMessages().getBan();
            case "mute" -> isRemove ? config.getMessages().getUnmute() : config.getMessages().getMute();
            case "warn" -> isRemove ? config.getMessages().getUnwarn() : config.getMessages().getWarn();
            case "kick" -> config.getMessages().getKick();
            default -> null;
        };
    }

    private String replacePlaceholders(String line, Entry entry) {
        String user = getUserName(entry.getUuid());
        String timeStart = formatDate(entry.getDateStart());
        String timeEnd = (entry.getDateEnd() == 0)
                ? config.getMessages().getNoneTime()
                : formatDate(entry.getDateEnd());

        return line
                .replace("%player%",      user)
                .replace("%time%",        timeStart)
                .replace("%time_end%",    timeEnd)
                .replace("%reason%",      entry.getReason() == null || entry.getReason().isEmpty() ? "Не указана" : entry.getReason())
                .replace("%type%",        entry.getType())
                .replace("%server%",      entry.getServerOrigin())
                .replace("%servers%",     entry.getServerScope())
                .replace("%isSilent%",    entry.isSilent() ? "Да" : "Нет")
                .replace("%isIp%",        entry.isIpban()  ? "Да" : "Нет")
                .replace("%exec_player%", Objects.requireNonNull(entry.getExecutorName()));
    }

    private String getUserName(String uuid) {
        if (uuid == null || uuid.isEmpty()) {
            return config.getMessages().getNonePlayer();
        }
        OfflinePlayer p = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));
        if (p != null && p.getName() != null && !p.getName().isEmpty()) {
            return p.getName();
        }
        return config.getMessages().getNonePlayer();
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat(config.getPatternTime())
                .format(new Date(millis));
    }
}
