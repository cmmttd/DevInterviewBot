package com.belogrudov.javabot.controller;

import com.belogrudov.javabot.data.Question;
import com.belogrudov.javabot.data.QuestionsTable;
import com.belogrudov.javabot.data.User;
import com.belogrudov.javabot.data.UsersTable;
import com.belogrudov.javabot.utils.RandomUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.belogrudov.javabot.enums.Constants.*;

/**
 * MessageDispatcher worked with update's content
 */
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageDispatcher {

    final UsersTable usersTable;
    final QuestionsTable questionsTable;

    public static int maxQNumber;

    public MessageDispatcher(UsersTable usersTable, QuestionsTable questionsTable) {
        this.usersTable = usersTable;
        this.questionsTable = questionsTable;
    }

    /**
     * Main method produce message for four ways:
     * next
     * statistics
     * reset
     * info
     *
     * @param chatId
     * @param messageText
     * @return Message instance for answer
     */
    public SendMessage getResponseByRegularMessage(Long chatId, String messageText) {
        SendMessage outMessage = new SendMessage().setChatId(chatId);
        User user = usersTable.findByChatId(chatId);
        Integer currentQId = user.getCurrentQId();
        List<Integer> historyArray = new ArrayList<>(usersTable.findByChatId(chatId).getHistoryArray());
        String text = null;
        String statistic = String.format("Current progress: %.1f%% (%d/%d)",
                historyArray.size() * 100.0 / maxQNumber,
                historyArray.size(),
                maxQNumber);
        ReplyKeyboard keyboard;

        switch (messageText) {
            case "Next":
            case "/next":
                currentQId = RandomUtil.inRangeExcludeList(1, maxQNumber, historyArray);
                if (currentQId == 0) {
                    text = THATS_ALL.toString();
                    text += statistic;
                    keyboard = getDefaultKeyboard();
                } else {
                    Question question = questionsTable.findById(currentQId).get();
                    historyArray.add(currentQId);
                    text = question.getQuestion();
                    keyboard = getInlineKeyboard(new String[][]{{"Skip", "Expand description"}});
                }
                break;
            case "Statistics":
            case "/statistics":
                text = statistic;
                keyboard = getDefaultKeyboard();
                break;
            case "Reset":
            case "/reset":
                historyArray = Collections.emptyList();
                text = RESET_SUCCESSFUL.toString();
                keyboard = getDefaultKeyboard();
                break;
            case "Info":
            case "/info":
                text = INFO_MESSAGE.toString().replaceAll("\\{name}", user.getName());
                keyboard = getDefaultKeyboard();
                break;
            default:
                text = BAD_REQUEST.toString();
                keyboard = getDefaultKeyboard();
        }

        user.setCurrentQId(currentQId);
        user.setHistoryArray(historyArray);
        usersTable.save(user);
        outMessage.setReplyMarkup(keyboard);
        outMessage.setText(text);
        outMessage.enableMarkdown(true);
        outMessage.disableWebPagePreview();
        return outMessage;
    }

    /**
     * Callback always work after showing the question
     *
     * @param chatId
     * @param messageText
     * @return
     */
    public SendMessage getResponseByCallback(Long chatId, String messageText) {
        SendMessage outMessage = new SendMessage().setChatId(chatId);
        User user = usersTable.findByChatId(chatId);
        Question question = questionsTable.findById(user.getCurrentQId()).get();
        String text = null;
        ReplyKeyboard keyboard = getDefaultKeyboard();

        switch (messageText) {
            case "Expand description":
                text = question.getDescription();
                break;
            case "Skip":
                SendMessage response = getResponseByRegularMessage(chatId, "/next");
                text = response.getText();
                keyboard = response.getReplyMarkup();
                break;
            default:
                text = BAD_REQUEST.toString();
        }
        outMessage.setText(text);
        outMessage.setReplyMarkup(keyboard);
        outMessage.enableMarkdown(true);
        return outMessage;
    }

    /**
     * Inline keyboard - buttons below questions
     *
     * @param strings
     * @return
     */
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

    /**
     * Standart keyboard, instead regular qwerty
     *
     * @param strings
     * @return
     */
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

    /**
     * Default regular keyboard
     *
     * @return
     */
    private ReplyKeyboardMarkup getDefaultKeyboard() {
        return getKeyboard(new String[][]{
                {"Next"},
                {"Statistics"},
                {"Reset"},
                {"Info"}
        });
    }

    /**
     * Register in db through spring data
     *
     * @param chatId
     * @param userName
     */
    public void registerIfAbsent(Long chatId, String userName) {
        if (usersTable.existsByChatId(chatId)) return;
        else usersTable.save(new User(chatId, userName, 0, new ArrayList<>()));
    }

    /**
     * @param chatId
     * @param name
     * @return
     */
    public SendMessage getWelcomeMessage(Long chatId, String name) {
        return new SendMessage(chatId, WELCOME_MESSAGE.toString().replaceAll("\\{name}", name))
                .setReplyMarkup(getDefaultKeyboard());
    }
}
