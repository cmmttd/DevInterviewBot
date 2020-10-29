package com.belogrudov.javabot.controller;

import com.belogrudov.javabot.data.Question;
import com.belogrudov.javabot.data.QuestionsTable;
import com.belogrudov.javabot.data.User;
import com.belogrudov.javabot.data.UsersTable;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.belogrudov.javabot.enums.Constants.*;

@Slf4j
public class MessageDispatcher {

    final UsersTable usersTable;
    final QuestionsTable questionsTable;

    static int maxQNumber;

    public MessageDispatcher(UsersTable usersTable, QuestionsTable questionsTable) {
        this.usersTable = usersTable;
        this.questionsTable = questionsTable;
    }

    @PostConstruct
    void init() {
        maxQNumber = questionsTable.findAll().size();
    }

    public SendMessage getResponseByCallback(Long chatId, String messageText) {
        SendMessage outMessage = new SendMessage().setChatId(chatId).setReplyMarkup(getDefaultKeyboard());
        User user = usersTable.findByChatId(chatId);
        Question question = questionsTable.findById(user.getCurrentQId()).get();
        String text = null;

        if ("Show description".equals(messageText)) {
            text = question.getDescription();
        } else {
            text = BAD_REQUEST.getValue();
        }
        outMessage.setText(text);
        return outMessage;
    }

    public SendMessage getResponseByRegularMessage(Long chatId, String messageText) {
        SendMessage outMessage = new SendMessage().setChatId(chatId);
        User user = usersTable.findByChatId(chatId);
        Long currentQId = user.getCurrentQId();
        String historyArray = usersTable.findByChatId(chatId).getHistory();
        List<Integer> historyQs = Arrays.stream(historyArray.split(";"))
                .filter(x -> x.matches(".+;.+"))
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        String text = null;
        ReplyKeyboard keyboard;

        switch (messageText) {
            case "/next":
                Set<Long> set = IntStream
                        .rangeClosed(1, maxQNumber)
                        .filter(x -> !historyQs.contains(x))
                        .asLongStream()
                        .boxed()
                        .collect(Collectors.toSet());
                currentQId = set.parallelStream().findAny().get();
                Question question = questionsTable.findById(currentQId).get();
                historyArray += historyArray.isEmpty() ? currentQId : ";" + currentQId;
                text = question.getQuestion();
                keyboard = getInlineKeyboard(new String[][]{{"Show description"}});
                break;
            case "/statistics":
                int currQNumber = historyQs
                        .stream()
                        .reduce(Integer::sum)
                        .orElse(0);
                text = String.format("Current progress: %.1f%%", currQNumber * 100.0 / maxQNumber);
                keyboard = getDefaultKeyboard();
                break;
            case "/reset":
                historyArray = "";
                text = RESET_SUCCESSFUL.getValue();
                keyboard = getDefaultKeyboard();
                break;
            case "/info":
                text = INFO_MESSAGE.getValue();
                keyboard = getDefaultKeyboard();
                break;
            default:
                text = BAD_REQUEST.getValue();
                keyboard = getDefaultKeyboard();
        }

        user.setCurrentQId(currentQId);
        user.setHistory(historyArray);
        usersTable.save(user);
        outMessage.setReplyMarkup(keyboard);
        outMessage.setText(text);
        return outMessage;
    }

    private InlineKeyboardMarkup getInlineKeyboard(String[][] strings) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (String[] ss : strings) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (String s : ss) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(s).setCallbackData(s);
                keyboardButtonsRow.add(inlineKeyboardButton);
            }
            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    private ReplyKeyboardMarkup getDefaultKeyboard() {
        return getKeyboard(new String[][]{
                {"/next"},
                {"/statistics"},
                {"/reset"},
                {"/info"}
        });
    }

    private ReplyKeyboardMarkup getKeyboard(String[][] strings) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();

        for (String[] ss : strings) {
            KeyboardRow row = new KeyboardRow();
            row.addAll(Arrays.asList(ss));
            rowList.add(row);
        }

        keyboardMarkup.setKeyboard(rowList);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        return keyboardMarkup;
    }

    public SendMessage getWelcomeMessage(Long chatId, String name) {
        return new SendMessage(chatId, WELCOME_MESSAGE.getValue().replaceAll("\\{name}", name))
                .setReplyMarkup(getDefaultKeyboard());
    }

    public void registerIfAbsent(Long chatId, String userName) {
        if (usersTable.existsByChatId(chatId)) return;
        else usersTable.save(new User(chatId, userName, 0L, ""));
    }
}
