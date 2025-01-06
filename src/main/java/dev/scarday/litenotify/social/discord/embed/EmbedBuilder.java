package dev.scarday.litenotify.social.discord.embed;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class EmbedBuilder {
    String title;
    String description;
    Integer color;
}
