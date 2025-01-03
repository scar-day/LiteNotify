package me.scarday.litenotify.social.impl;

import me.scarday.litenotify.Main;
import me.scarday.litenotify.social.Builder;
import me.scarday.litenotify.social.Social;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TelegramImpl implements Social {
    private final Main instance;

    private final List<String> ids;

    private final String token;

    public TelegramImpl(Main instance) {
        this.instance = instance;
        ids = instance.getConfig().getStringList("settings.tg.ids");
        token = instance.getConfig().getString("settings.tg.token");
    }

    public void sendMessage(Builder builder) {
        CompletableFuture.runAsync(() -> {
            for (String chatId : ids) {
                try {
                    String urlString = "https://api.telegram.org/bot" + token + "/sendMessage?text=" + URLEncoder.encode(builder.getMessage(), "UTF-8") +
                            "&chat_id=" + chatId;
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        StringBuilder jsonResponse = new StringBuilder();
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            jsonResponse.append(inputLine);
                        }
                        in.close();

                        instance.getLogger().info("Произошла ошибка при отправке сообщения: " + jsonResponse);
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    instance.getLogger().info("Произошла ошибка при отправке сообщения: " + e);
                }
            }
        });
    }
}
