package org.simonscode.telegrambots.framework;

import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by simon on 02.05.17.
 */
public class Utils {

    public static Message getMessageFromUpdate(Update update) {
        return getMessageFromUpdate(update, true);
    }

    public static Message getMessageFromUpdate(Update update, boolean evaluateEdited) {
        if (update.hasMessage())
            return update.getMessage();
        else if (evaluateEdited && update.hasEditedMessage())
            return update.getEditedMessage();
        return null;
    }

    public static Message checkForCommand(Update update, String command) {
        return checkForCommand(update, command, false);
    }

    public static Message checkForCommand(Update update, String command, boolean evaluateEdited) {
        Message message = getMessageFromUpdate(update, evaluateEdited);
        if (message != null) {
            if (message.hasText() && message.getText().toLowerCase().startsWith(command + " ")) {
                return message;
            }
        }
        return null;
    }

    public static String parseUserName(User user) {
        if (user.getFirstName() != null && !user.getFirstName().isEmpty() && user.getLastName() != null && !user.getLastName().isEmpty())
            return user.getFirstName() + " " + user.getLastName();
        else if (user.getUserName() != null && !user.getUserName().isEmpty())
            return user.getUserName();
        else
            return String.valueOf(user.getId());
    }

    public static void sendFailableMessage(AbsSender bot, Chat chat, String text) {
        try {
            sendMessage(bot, chat, false, 0, text);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendFailableMessage(AbsSender bot, Chat chat, int replyId, String text) {
        try {
            sendMessage(bot, chat, true, replyId, text);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(AbsSender bot, Chat chat, String text) throws TelegramApiException {
        sendMessage(bot, chat, false, 0, text);
    }

    public static void sendMessage(AbsSender bot, Chat chat, int replyId, String text) throws TelegramApiException {
        sendMessage(bot, chat, true, replyId, text);
    }

    private static void sendMessage(AbsSender bot, Chat chat, boolean doReply, int replyId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        if (doReply)
            message.setReplyToMessageId(replyId);
        message.setChatId(chat.getId());
        message.setText(text);
        bot.execute(message);
    }

    public static void logUpdate(Update update) {
        StringBuilder sb = new StringBuilder();
        Message message = null;
        if (update.hasMessage()) {
            message = update.getMessage();
        } else if (update.hasEditedMessage()) {
            sb.append("Edited ");
            message = update.getEditedMessage();
        }
        if (message != null) {
            sb.append("Message: ");
            sb.append(Utils.parseUserName(message.getFrom()));
            if (message.getChat().getTitle() != null) {
                sb.append('@');
                sb.append(message.getChat().getTitle());
            }
            sb.append(": ");
            if (message.hasText())
                sb.append(message.getText());
            else if (message.hasPhoto()) {
                sb.append(message.getCaption());
                sb.append("  [Photo]");
            } else if (message.hasEntities())
                sb.append(message.getEntities());
            else if (message.hasDocument())
                sb.append("[Document]");
            else
                sb.append("[Unknown content]");
        }

        if (update.hasCallbackQuery()) {
            sb.append("CallbackQuery: ");
            sb.append(Utils.parseUserName(update.getCallbackQuery().getFrom()));
            sb.append('@');
            sb.append(update.getCallbackQuery().getMessage().getChat().getTitle());
            sb.append(": ");
            sb.append(update.getCallbackQuery().getData());
        } else if (update.hasInlineQuery()) {
            sb.append("InlineQuery: ");
            sb.append(Utils.parseUserName(update.getInlineQuery().getFrom()));
            sb.append(": ");
            sb.append(update.getInlineQuery().getQuery());
        } else if (update.hasChosenInlineQuery()) {
            sb.append("ChosenInlineQuery: ");
            sb.append(Utils.parseUserName(update.getChosenInlineQuery().getFrom()));
            sb.append(": ");
            sb.append(update.getChosenInlineQuery().getQuery());
        } else if (update.hasChannelPost()) {
            sb.append("ChannelPost: ");
            sb.append(Utils.parseUserName(update.getChannelPost().getFrom()));
            sb.append('@');
            sb.append(update.getChannelPost().getChat().getTitle());
            sb.append(": ");
            sb.append(update.getChannelPost().getText());
        } else if (update.hasEditedChannelPost()) {
            sb.append("Edited ChannelPost: ");
            sb.append(Utils.parseUserName(update.getEditedChannelPost().getFrom()));
            sb.append('@');
            sb.append(update.getEditedChannelPost().getChat().getTitle());
            sb.append(": ");
            sb.append(update.getEditedChannelPost().getText());
        } else if (update.hasPreCheckoutQuery()) {
            sb.append("PreCheckoutQuery: ");
            sb.append(Utils.parseUserName(update.getPreCheckoutQuery().getFrom()));
            sb.append(": ");
            sb.append(update.getPreCheckoutQuery().getOrderInfo());
            sb.append(" Total: ");
            sb.append(update.getPreCheckoutQuery().getTotalAmount());
            sb.append(update.getPreCheckoutQuery().getCurrency());
        } else if (update.hasShippingQuery()) {
            sb.append("PreCheckoutQuery: ");
            sb.append(Utils.parseUserName(update.getShippingQuery().getFrom()));
            sb.append(": ");
            sb.append(update.getShippingQuery().getId());
            sb.append(" Total: ");
            sb.append(update.getShippingQuery().getInvoicePayload());
        }
        System.out.println(sb.toString());
    }

    public static void deleteMessageFailable(AbsSender sender, String chatId, int messageId) {
        try {
            sender.execute(new DeleteMessage().setChatId(chatId).setMessageId(messageId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static File getFile(Bot bot, String fileId) throws IOException, TelegramApiException {
        return getFile(bot, fileId, fileId);
    }

    public static File getFile(Bot bot, String fileId, String fileName) throws IOException, TelegramApiException {
        File localFile = new File("/tmp/" + fileName + ".png");
        URL url = new URL(bot.execute(new GetFile().setFileId(fileId)).getFileUrl(bot.getBotToken()));
        if (localFile.exists()) localFile.delete();
        FileUtils.copyURLToFile(url, localFile);
        return localFile;
    }
}
