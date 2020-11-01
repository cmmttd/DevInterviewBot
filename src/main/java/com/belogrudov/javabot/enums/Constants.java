package com.belogrudov.javabot.enums;

public enum Constants {
    WELCOME_MESSAGE("Hello, {name}!\nWelcome!"),
    BAD_REQUEST("Bad request"),
    INFO_MESSAGE("Hi, {name}!\n\nThis is a demo bot.\n\nReport bugs to @belogrudovw\n\nBot source: https://github.com/cmmttd/JavaTeacherBot\n\nContent source: https://github.com/enhorse/java-interview"),
    RESET_SUCCESSFUL("Progress reset"),
    THATS_ALL("Thanks, that's all.\nSend feedback to @belogrudovw");

    private final String value;

    Constants(String s) {
        this.value = s;
    }

    public String getValue() {
        return this.value;
    }
}
