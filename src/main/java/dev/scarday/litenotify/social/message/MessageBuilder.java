package dev.scarday.litenotify.social.message;

import dev.scarday.litenotify.social.discord.embed.EmbedBuilder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@lombok.Builder
public class MessageBuilder {
    String message;

    EmbedBuilder embed;
}
