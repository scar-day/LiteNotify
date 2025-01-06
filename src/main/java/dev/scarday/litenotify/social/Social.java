package dev.scarday.litenotify.social;

import dev.scarday.litenotify.social.message.MessageBuilder;

public interface Social {
    void sendMessage(MessageBuilder builder);
}
