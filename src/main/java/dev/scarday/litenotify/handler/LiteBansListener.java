package dev.scarday.litenotify.handler;

import dev.scarday.litenotify.Main;
import dev.scarday.litenotify.configuration.Configuration;
import dev.scarday.litenotify.social.discord.embed.EmbedBuilder;
import dev.scarday.litenotify.social.message.MessageBuilder;
import lombok.val;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.*;

import litebans.api.*;
import dev.scarday.litenotify.social.impl.*;

public class LiteBansListener implements Listener {

    private final Main plugin;
    private final TelegramImpl telegram;
    private final DiscordImpl discord;
    private final Configuration config;

    public LiteBansListener(Main plugin) {
        this.plugin = plugin;
        this.telegram = plugin.getTg();
        this.discord = plugin.getDiscord();
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

        if (telegram != null) {
            telegram.sendMessage(MessageBuilder.builder()
                    .message(message)
                    .build()
            );
        }

        if (discord != null) {
            val messageBuilder = MessageBuilder.builder()
                    .embed(EmbedBuilder.builder()
                            .title(plugin.getName())
                            .description(message)
                            .color(16777215)
                            .build()).build();

            discord.sendMessage(messageBuilder);
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
                .replace("{player}",      user)
                .replace("{time}",        timeStart)
                .replace("{time_end}",    timeEnd)
                .replace("{reason}",      entry.getReason() == null || entry.getReason().isEmpty() ? "Не указана" : entry.getReason())
                .replace("{type}",        entry.getType())
                .replace("{server}",      entry.getServerOrigin())
                .replace("{servers}",     entry.getServerScope())
                .replace("{isSilent}",    entry.isSilent() ? "Да" : "Нет")
                .replace("{isIp}",        entry.isIpban()  ? "Да" : "Нет")
                .replace("{exec_player}", Objects.requireNonNull(entry.getExecutorName()));
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
