package com.belogrudov.javabot.enums;

public enum Constants {
    WELCOME_MESSAGE("Hello, {name}!\nWelcome!"),
    BAD_REQUEST("Bad request"),
    INFO_MESSAGE("Hi, {name}!\nThis is a demo bot. Report bugs to @belogrudovw"),
    RESET_SUCCESSFUL("You are dumped"),
    THATS_ALL("Thanks, that's all");

    private final String value;

    Constants(String s) {
        this.value = s;
    }

    public String getValue() {
        return this.value;
    }
}
