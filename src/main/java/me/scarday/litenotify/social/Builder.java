package me.scarday.litenotify.social;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@lombok.Builder
public class Builder {
    String message;
    int color; // for discord
}
