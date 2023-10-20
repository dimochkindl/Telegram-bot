package org.example.service;

import lombok.SneakyThrows;
import org.example.PictureProcessor;
import org.example.entities.Currency;
import org.example.enums.Heroes;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FirstBot extends TelegramLongPollingBot {

    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final CurrencyConversionService currencyConversionService = CurrencyConversionService.getInstance();

    private final PictureProcessor pictureProcessor = new PictureProcessor();

    @Override
    public String getBotUsername() {
        return "@dimonchikFirstBot";
    }

    @Override
    public String getBotToken() {
        return "6133154161:AAEDdig49H-ZlKlNKu_5cuD-KzScVdBaks8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallBack(update.getCallbackQuery());
        }
        if (update.hasMessage()) {
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleMessage(Message message) throws TelegramApiException {
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/set_currency":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
                        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
                        for (Currency currency : Currency.values()) {
                            buttons.add(Arrays.asList(
                                    InlineKeyboardButton.builder().
                                            text(getCurrencyButton(originalCurrency, currency)).
                                            callbackData("ORIGINAL:" + currency).build(),
                                    InlineKeyboardButton.builder().
                                            text(getCurrencyButton(targetCurrency, currency)).
                                            callbackData("TARGET:" + currency).build()));
                        }
                        execute(SendMessage.builder().text("Please choose Original and Target currencies").
                                chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build()).build());
                        return;
                    case "/get_picture":
                        List<List<InlineKeyboardButton>> button = new ArrayList<>();
                        for (Heroes heroes : Heroes.values()) {
                            button.add(Arrays.asList(InlineKeyboardButton.builder()
                                    .text(heroes.name())
                                    .callbackData("Picture:" + heroes.name()).build()
                            ));
                        }
                        execute(SendMessage.builder().text("Please choose who's picture u wanna get")
                                .chatId(message.getChatId())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(button).build()).build());
                        return;
                }
            }
        }
        if (message.hasText()) {
            String messageText = message.getText();
            Optional<Double> value = parseToDouble(messageText);
            Currency original = currencyModeService.getOriginalCurrency(message.getChatId());
            Currency target = currencyModeService.getTargetCurrency(message.getChatId());
            double ratio = currencyConversionService.getConversionRatio(original, target);
            if (value.isPresent()) {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString()).text(String.format("%4.2f %s is %4.2f %s", value.get(), original, value.get() * ratio, target))
                        .build());
                return;
            }
        }
    }

    private Optional<Double> parseToDouble(String messageText) {
        try {
            return Optional.of(Double.parseDouble(messageText));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getCurrencyButton(Currency saved, Currency current) {
        return saved == current ? current + "☑️" : current.name();
    }

    @SneakyThrows
    private void handleCallBack(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        switch (action) {
            case "ORIGINAL":
                Currency newCurrency = Currency.valueOf(param[1]);
                currencyModeService.setOriginalCurrency(message.getChatId(), newCurrency);
                break;
            case "TARGET":
                Currency newCurrenc = Currency.valueOf(param[1]);
                currencyModeService.setTargetCurrency(message.getChatId(), newCurrenc);
                break;
            case "Picture":
                execute(pictureProcessor.getPhoto(param[1], message.getChatId().toString()));
                return;
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
        for (Currency currency : Currency.values()) {
            buttons.add(Arrays.asList(
                    InlineKeyboardButton.builder().
                            text(getCurrencyButton(originalCurrency, currency)).
                            callbackData("ORIGINAL:" + currency).build(),
                    InlineKeyboardButton.builder().
                            text(getCurrencyButton(targetCurrency, currency)).
                            callbackData("TARGET:" + currency).build()));
        }
        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());

    }
}
