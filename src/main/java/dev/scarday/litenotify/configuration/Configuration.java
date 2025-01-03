package dev.scarday.litenotify.configuration;

import eu.okaeri.configs.OkaeriConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Configuration extends OkaeriConfig {

    String patternTime = "HH:mm:ss dd-MM-YYYY";
    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VK extends OkaeriConfig {
        boolean enable = false;
        String token = "";
        List<Integer> ids = new ArrayList<>();
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Telegram extends OkaeriConfig {
        boolean enable = false;
        String token = "";
        List<Integer> ids = new ArrayList<>();
    }

    @Setter
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Messages extends OkaeriConfig {
        String nonePlayer = "❓ Неизвестно";
        String noneTime = "⏳ Бессрочно";

        List<String> kick = List.of(
                "🦶 <b>Игрок был кикнут</b>",
                "<b>🔹 Нарушитель:</b> %player%",
                "<b>🕒 Выдано:</b> %time%",
                "<b>❗ Причина:</b> %reason%",
                "<b>🌐 На сервере:</b> %server%, действует на %servers%",
                "<b>👤 Выдал кик:</b> %exec_player%"
        );

        List<String> ban = List.of(
                "🚫 <b>Игрок был наказан</b>",
                "<b>🔹 Нарушитель:</b> %player%",
                "<b>🕒 Выдано:</b> %time%",
                "<b>⏰ Действует до:</b> %time_end%",
                "<b>❗ Причина:</b> %reason%",
                "<b>🔧 Тип наказания:</b> %type%",
                "<b>🌐 На сервере:</b> %server%, действует на %servers%",
                "<b>🔒 Скрыто:</b> %isSilent%",
                "<b>🌍 По IP:</b> %isIp%",
                "<b>👤 Выдал наказание:</b> %exec_player%"
        );

        List<String> mute = List.of(
                "🔇 <b>Игрок был заглушен</b>",
                "<b>🔹 Нарушитель:</b> %player%",
                "<b>🕒 Выдано:</b> %time%",
                "<b>⏰ Действует до:</b> %time_end%",
                "<b>❗ Причина:</b> %reason%",
                "<b>🔧 Тип наказания:</b> %type%",
                "<b>🌐 На сервере:</b> %server%, действует на %servers%",
                "<b>🔒 Скрыто:</b> %isSilent%",
                "<b>🌍 По IP:</b> %isIp%",
                "<b>👤 Выдал наказание:</b> %exec_player%"
        );

        List<String> warn = List.of(
                "⚠️ <b>Игрок получил предупреждение</b>",
                "<b>🔹 Нарушитель:</b> %player%",
                "<b>🕒 Выдано:</b> %time%",
                "<b>⏰ Действует до:</b> %time_end%",
                "<b>❗ Причина:</b> %reason%",
                "<b>🔧 Тип предупреждения:</b> %type%",
                "<b>🌐 На сервере:</b> %server%, действует на %servers%",
                "<b>🔒 Скрыто:</b> %isSilent%",
                "<b>🌍 По IP:</b> %isIp%",
                "<b>👤 Выдал предупреждение:</b> %exec_player%"
        );

        List<String> unban = List.of(
                "✅ <b>У игрока сняли бан</b>",
                "<b>🔹 Нарушитель:</b> %player%",
                "<b>🕒 Снято:</b> %time%",
                "<b>❗ Причина:</b> %reason%",
                "<b>🔧 Тип наказания:</b> %type%",
                "<b>🌐 На сервере:</b> %server%, действует на %servers%",
                "<b>🔒 Скрыто:</b> %isSilent%",
                "<b>🌍 По IP:</b> %isIp%",
                "<b>👤 Снял наказание:</b> %exec_player%"
        );
        List<String> unmute = List.of(
                "🔊 <b>У игрока сняли заглушение</b>",
                "<b>🔹 Нарушитель:</b> %player%",
                "<b>🕒 Снято:</b> %time%",
                "<b>❗ Причина:</b> %reason%",
                "<b>🔧 Тип наказания:</b> %type%",
                "<b>🌐 На сервере:</b> %server%, действует на %servers%",
                "<b>🔒 Скрыто:</b> %isSilent%",
                "<b>🌍 По IP:</b> %isIp%",
                "<b>👤 Снял наказание:</b> %exec_player%");
        List<String> unwarn = List.of(
                "✅ <b>У игрока сняли предупреждение</b>",
                "<b>🔹 Нарушитель:</b> %player%",
                "<b>🕒 Снято:</b> %time%",
                "<b>❗ Причина:</b> %reason%",
                "<b>🔧 Тип предупреждения:</b> %type%",
                "<b>🌐 На сервере:</b> %server%, действует на %servers%",
                "<b>🔒 Скрыто:</b> %isSilent%",
                "<b>🌍 По IP:</b> %isIp%",
                "<b>👤 Снял предупреждение:</b> %exec_player%"
        );
    }

    private VK vk = new VK();
    private Telegram telegram = new Telegram();

    private Messages messages = new Messages();
}
