package com.belogrudov.javabot.controller;

import com.belogrudov.javabot.data.Question;
import com.belogrudov.javabot.data.QuestionsRepo;
import com.belogrudov.javabot.data.User;
import com.belogrudov.javabot.data.UsersRepo;
import com.belogrudov.javabot.utils.RandomUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
 * MessageDispatcher works with update's content
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageDispatcher {

    private final UsersRepo usersRepo;
    private final QuestionsRepo questionsRepo;

    public static int maxQNumber;

    // TODO: 14/05/2023 Need to fix workaround
    @PostConstruct
    void init() {
        maxQNumber = questionsRepo.findAll().size();
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
        User user = usersRepo.findByChatId(chatId);
        Integer currentQId = user.getCurrentQId();
        List<Integer> historyArray = new ArrayList<>(usersRepo.findByChatId(chatId).getHistoryArray());
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
                    Question question = questionsRepo.findById(currentQId).get();
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
        usersRepo.save(user);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(keyboard)
                .text(text)
                .build();
        sendMessage.enableMarkdown(true);
        sendMessage.disableWebPagePreview();
        return sendMessage;
    }

    /**
     * Callback always work after showing the question
     *
     * @param chatId
     * @param messageText
     * @return
     */
    public SendMessage getResponseByCallback(Long chatId, String messageText) {
        User user = usersRepo.findByChatId(chatId);
        Question question = questionsRepo.findById(user.getCurrentQId()).get();
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
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(keyboard)
                .text(text)
                .build();
        sendMessage.enableMarkdown(true);
        return sendMessage;
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
                InlineKeyboardButton inlineKeyboardButton = InlineKeyboardButton.builder()
                        .text(s)
                        .callbackData(s)
                        .build();
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
        if (usersRepo.existsByChatId(chatId)) return;
        else usersRepo.save(new User(chatId, userName, 0, new ArrayList<>()));
    }

    /**
     * @param chatId
     * @param name
     * @return
     */
    public SendMessage getWelcomeMessage(Long chatId, String name) {
        return SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(getDefaultKeyboard())
                .text(WELCOME_MESSAGE.toString().replaceAll("\\{name}", name))
                .build();
    }
}
