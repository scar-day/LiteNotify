package dev.scarday.litenotify.social.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.scarday.litenotify.Main;
import dev.scarday.litenotify.social.discord.embed.EmbedBuilder;
import dev.scarday.litenotify.social.message.MessageBuilder;
import dev.scarday.litenotify.social.Social;
import lombok.val;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class DiscordImpl implements Social {
    private final String webHookUrl;

    public DiscordImpl(Main plugin) {
        val configuration = plugin.getConfiguration();
        this.webHookUrl = configuration.getDiscord().getWebHook();
    }

    @Override
    public void sendMessage(MessageBuilder builder) {
        CompletableFuture.runAsync(() -> {
            try {
                val messageJson = getJsonObject(builder.getEmbed());

                val conn = (HttpURLConnection) new URL(webHookUrl).openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                try (val outputStream = conn.getOutputStream()) {
                    outputStream.write(messageJson.toString().getBytes());
                    outputStream.flush();
                }

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        Bukkit.getLogger().warning("[LiteNotify] Ошибка Discord: " + sb);
                    }
                }

                conn.disconnect();
            } catch (Exception e) {
                Bukkit.getLogger().severe("[LiteNotify] Ошибка при отправке сообщения: " + e.getMessage());
            }
        });
    }

    private @NotNull JsonObject getJsonObject(EmbedBuilder embedBuilder) {
        val messageJson = new JsonObject();

        val embed = new JsonObject();
        if (embedBuilder.getTitle() != null) {
            embed.addProperty("title", embedBuilder.getTitle());
        }
        if (embedBuilder.getDescription() != null) {
            embed.addProperty("description", convertHtmlToMarkdown(embedBuilder.getDescription()));
        }
        if (embedBuilder.getColor() != null) {
            embed.addProperty("color", embedBuilder.getColor());
        }

        val embedsArray = new JsonArray();
        embedsArray.add(embed);

        messageJson.add("embeds", embedsArray);
        return messageJson;
    }

    private String convertHtmlToMarkdown(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        html = html.replaceAll("(?i)<b>(.*?)</b>", "**$1**");
        html = html.replaceAll("(?i)<strong>(.*?)</strong>", "**$1**");
        html = html.replaceAll("(?i)<i>(.*?)</i>", "*$1*");
        html = html.replaceAll("(?i)<em>(.*?)</em>", "*$1*");
        html = html.replaceAll("(?i)<a\\s+href=['\"](.*?)['\"]>(.*?)</a>", "[$2]($1)");
        html = html.replaceAll("(?i)<br\\s*/?>", "\n");
        html = html.replaceAll("(?i)<.*?>", "");
        return html.trim();
    }
}
