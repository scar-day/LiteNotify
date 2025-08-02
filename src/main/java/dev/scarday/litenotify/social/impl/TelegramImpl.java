package dev.scarday.litenotify.social.impl;

import dev.scarday.litenotify.Main;
import dev.scarday.litenotify.social.message.MessageBuilder;
import dev.scarday.litenotify.social.Social;
import lombok.val;
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

    private final List<Long> ids;
    private final String token;

    public TelegramImpl(Main plugin) {
        val configuration = plugin.getConfiguration();
        this.ids = configuration.getTelegram().getIds();
        this.token = configuration.getTelegram().getToken();
    }

    @Override
    public void sendMessage(MessageBuilder builder) {
        CompletableFuture.runAsync(() -> {
            for (Long chatId : ids) {
                try {
                    String urlText = URLEncoder.encode(builder.getMessage(), "UTF-8");
                    String urlStr = "https://api.telegram.org/bot" + token
                            + "/sendMessage?chat_id=" + chatId
                            + "&text=" + urlText
                            + "&parse_mode=HTML";

                    HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
                    conn.setRequestMethod("GET");

                    int responseCode = conn.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) {
                                sb.append(line);
                            }
                            Bukkit.getLogger().warning("[LiteNotify] Ошибка Telegram: " + sb);
                        }
                    }

                    conn.disconnect();
                } catch (IOException e) {
                    Bukkit.getLogger().severe("[LiteNotify] Ошибка Telegram: " + e.getMessage());
                }
            }
        });
    }
}
