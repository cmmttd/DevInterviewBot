package com.belogrudov.javabot.enums;

public enum Constants {
    WELCOME_MESSAGE("Hello, {name}!\nWelcome!"),
    BAD_REQUEST("Bad request. Try to use bot's keyboard. Please."),
    INFO_MESSAGE("Hi, {name}!\n\nThis is a demo bot.\n\nPlease report bugs to @belogrudovw\n\nBot source: https://github.com/cmmttd/JavaTeacherBot\n\nContent source: https://github.com/enhorse/java-interview"),
    RESET_SUCCESSFUL("Progress has been reset"),
    THATS_ALL("Thanks, champion, that's all.\nPlease send feedback to @belogrudovw\n");

    private final String value;

    Constants(String s) {
        this.value = s;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
