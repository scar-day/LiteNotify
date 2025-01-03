package dev.scarday.litenotify.social.impl;

import lombok.SneakyThrows;
import dev.scarday.litenotify.Main;
import dev.scarday.litenotify.social.Builder;
import dev.scarday.litenotify.social.Social;
import lombok.val;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VkImpl implements Social {

    private final Main plugin;
    private final String token;
    private final List<Integer> ids;

    public VkImpl(Main plugin) {
        this.plugin = plugin;
        this.token = plugin.getConfiguration().getVk().getToken();
        this.ids = plugin.getConfiguration().getVk().getIds();
    }

    @SneakyThrows
    @Override
    public void sendMessage(Builder builder) {
        CompletableFuture.runAsync(() -> {
            for (val peerId : ids) {
                try {
                    String urlString = "https://api.vk.com/method/messages.send"
                            + "?access_token=" + token
                            + "&message=" + URLEncoder.encode(builder.getMessage(), "UTF-8")
                            + "&peer_id=" + peerId
                            + "&random_id=0"
                            + "&v=5.199";

                    HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                            StringBuilder jsonResponse = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                jsonResponse.append(inputLine);
                            }
                            plugin.getLogger().warning("Ошибка при отправке сообщения в VK: " + jsonResponse);
                        }
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    plugin.getLogger().severe("Ошибка при отправке сообщения в VK: " + e.getMessage());
                }
            }
        });
    }
}
