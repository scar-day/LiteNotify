package me.scarday.litenotify.social.impl;

import lombok.SneakyThrows;
import me.scarday.litenotify.Main;
import me.scarday.litenotify.social.Builder;
import me.scarday.litenotify.social.Social;

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
        ids = plugin.getConfig().getIntegerList("settings.vk.ids");
        token = plugin.getConfig().getString("settings.vk.token");
    }

    @SneakyThrows
    @Override
    public void sendMessage(Builder builder) {
        CompletableFuture.runAsync(() -> {
            for (Integer peerId : ids) {
                try {
                    String urlString = "https://api.vk.com/method/messages.send?access_token=" + token +
                            "&message=" + URLEncoder.encode(builder.getMessage(), "UTF-8")  +
                            "&peer_id=" + peerId +
                            "&random_id=0" +
                            "&v=5.199";
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

                        plugin.getLogger().info("Произошла ошибка при отправке сообщения: " + jsonResponse);
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    plugin.getLogger().info("Произошла ошибка при отправке сообщения: " + e);
                }
            }
        });
    }

}
