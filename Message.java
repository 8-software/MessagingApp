
package com.mycompany.lastpoe;

import javax.swing.*;
import java.util.*;

public class Message {
    private static Set<String> usedMessageIds = new HashSet<>();
    private static List<String> sentMessages = new ArrayList<>();
    private static List<String> disregardedMessages = new ArrayList<>();
    private static List<String> storedMessages = new ArrayList<>();
    private static List<String> messageHashes = new ArrayList<>();
    private static List<String> messageIds = new ArrayList<>();
    private static int totalMessages = 0;

    private final String messageId;
    private final String messageContent;
    private final String recipientPhoneNumber;

    public Message(String messageContent, String recipientPhoneNumber) {
        this.messageContent = messageContent;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.messageId = generateMessageID();
    }

    private String generateMessageID() {
        String id;
        Random rand = new Random();
        do {
            long number = Math.abs(rand.nextLong() % 1_000_000_0000L);
            id = String.format("%010d", number);
        } while (usedMessageIds.contains(id));
        return id;
    }

    public String createMessageHash() {
        String firstTwoDigits = messageId.substring(0, 2);
        String[] words = messageContent.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;

        firstWord = firstWord.replaceAll("[^a-zA-Z0-9]", "");
        lastWord = lastWord.replaceAll("[^a-zA-Z0-9]", "");

        return (firstTwoDigits + ":" + totalMessages + ":" + firstWord + lastWord).toUpperCase();
    }

    public String sendMessage() {
        String[] options = {"Send Now", "Disregard", "Store for Later"};
        int choice = JOptionPane.showOptionDialog(null, "What would you like to do with this message?",
                "Message Options", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0:
                usedMessageIds.add(messageId);
                totalMessages++;
                sentMessages.add(this.messageContent);
                messageIds.add(messageId);
                String hash = createMessageHash();
                messageHashes.add(hash);
                return printMessage();
            case 1:
                disregardedMessages.add(this.messageContent);
                return "Message disregarded.";
            case 2:
                storeMessage(this.messageContent);
                return "Message stored to send later.";
            default:
                return "Action canceled.";
        }
    }

    public String printMessage() {
        String log = String.format("Message ID: %s\nHash: %s\nRecipient Phone: %s\nContent: %s",
                messageId, createMessageHash(), recipientPhoneNumber, messageContent);
        storeMessage(log);
        return log;
    }

    public void storeMessage(String fullMessage) {
        storedMessages.add(fullMessage);
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public static List<String> getSentMessages() {
        return sentMessages;
    }

    public static List<String> getDisregardedMessages() {
        return disregardedMessages;
    }

    public static List<String> getStoredMessages() {
        return storedMessages;
    }

    public static List<String> getMessageHashes() {
        return messageHashes;
    }

    public static List<String> getMessageIds() {
        return messageIds;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getRecipientPhoneNumber() {
        return recipientPhoneNumber;
    }
}


    
