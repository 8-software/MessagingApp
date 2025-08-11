package com.mycompany.lastpoe;

import javax.swing.*;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class Messageapp {
    static int maxMessages = 0;
    static int totalMessages = 0;
    static final String STORED_MESSAGES_FILE = "stored_messages.json";

    static void startMessaging() {
        String input = JOptionPane.showInputDialog("Enter the number of messages you want to send:");
        try {
            maxMessages = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number entered. Exiting program.");
            return;
        }

        int choice = 0;
        do {
            String menu = """
                Menu:
                1) Send Message
                2) Show Recently Stored Messages
                3) Display Sender & Recipient of All Sent Messages
                4) Display Longest Sent Message
                5) Search by Message ID
                6) Search by Recipient Number
                7) Delete Message by Hash
                8) Display Full Report
                9) Quit""";

            String inputChoice = JOptionPane.showInputDialog(menu);
            if (inputChoice == null) return;

            try {
                choice = Integer.parseInt(inputChoice);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input.");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    if (totalMessages >= maxMessages) {
                        JOptionPane.showMessageDialog(null, "You have reached the maximum number of messages.");
                        break;
                    }

                    String msg = JOptionPane.showInputDialog("Enter your message (less than 50 characters):");
                    if (msg == null || msg.length() > 50) {
                        JOptionPane.showMessageDialog(null, "Message is too long.");
                        break;
                    }

                    String recipientNumber;
                    while (true) {
                        recipientNumber = JOptionPane.showInputDialog("Enter recipient phone number (must start with +27):");
                        if (recipientNumber == null) return;
                        if (recipientNumber.matches("\\+27\\d{9}")) break;
                        JOptionPane.showMessageDialog(null, "Invalid phone number.");
                    }

                    Message message = new Message(msg, recipientNumber);
                    String result = message.sendMessage();
                    JOptionPane.showMessageDialog(null, result);
                    totalMessages = message.getTotalMessages();

                    saveStoredMessagesToJson();
                }

                case 2 -> {
                    List<String> stored = JsonUtils.readMessagesFromJson(STORED_MESSAGES_FILE);
                    if (stored.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No stored messages.");
                    } else {
                        StringBuilder sb = new StringBuilder("Stored Messages:\n");
                        for (String m : stored) {
                            sb.append("- ").append(m).append("\n\n");
                        }
                        JOptionPane.showMessageDialog(null, sb.toString());
                    }
                }

                case 3 -> {
                    StringBuilder sb = new StringBuilder("Sent Messages:\n");
                    List<String> sent = Message.getSentMessages();
                    List<String> ids = Message.getMessageIds();
                    for (int i = 0; i < sent.size(); i++) {
                        sb.append("ID: ").append(ids.get(i)).append(" | Content: ").append(sent.get(i)).append("\n");
                    }
                    JOptionPane.showMessageDialog(null, sb.toString());
                }

                case 4 -> {
                    String longest = "";
                    for (String msg : Message.getSentMessages()) {
                        if (msg.length() > longest.length()) longest = msg;
                    }
                    JOptionPane.showMessageDialog(null, "Longest Sent Message:\n" + longest);
                }

                case 5 -> {
                    String inputId = JOptionPane.showInputDialog("Enter Message ID:");
                    List<String> ids = Message.getMessageIds();
                    List<String> contents = Message.getSentMessages();
                    boolean found = false;
                    for (int i = 0; i < ids.size(); i++) {
                        if (ids.get(i).equals(inputId)) {
                            JOptionPane.showMessageDialog(null, "Content: " + contents.get(i));
                            found = true;
                            break;
                        }
                    }
                    if (!found) JOptionPane.showMessageDialog(null, "Message ID not found.");
                }

                case 6 -> {
                    String recipient = JOptionPane.showInputDialog("Enter recipient phone number to search:");

                    StringBuilder sb = new StringBuilder("Messages to " + recipient + ":\n");

                    // Search in stored messages
                    List<String> stored = JsonUtils.readMessagesFromJson(STORED_MESSAGES_FILE);
                    boolean found = false;
                    for (String msg : stored) {
                        if (msg.contains(recipient)) {
                            sb.append("[STORED] ").append(msg).append("\n\n");
                            found = true;
                        }
                    }

                    // Search in sent messages
                    List<String> sent = Message.getSentMessages();
                    List<String> ids = Message.getMessageIds();

                    for (int i = 0; i < sent.size(); i++) {
                        String content = sent.get(i);
                        String id = ids.get(i);

                        if (content.contains(recipient)) {
                            sb.append("[SENT] ID: ").append(id).append(" | Content: ").append(content).append("\n\n");
                            found = true;
                        }
                    }

                    if (!found) {
                        sb.append("No messages found for this recipient.");
                    }

                    JOptionPane.showMessageDialog(null, sb.toString());
                }

                case 7 -> {
                    String inputHash = JOptionPane.showInputDialog("Enter message hash to delete:");
                    List<String> hashes = Message.getMessageHashes();
                    List<String> sent = Message.getSentMessages();
                    List<String> ids = Message.getMessageIds();

                    boolean deleted = false;

                    for (int i = 0; i < hashes.size(); i++) {
                        if (hashes.get(i).equalsIgnoreCase(inputHash)) {
                            hashes.remove(i);
                            sent.remove(i);
                            ids.remove(i);
                            JOptionPane.showMessageDialog(null, "Message deleted.");
                            deleted = true;
                            break;
                        }
                    }
                    if (!deleted) {
                        JOptionPane.showMessageDialog(null, "Hash not found.");
                    }
                }

                case 8 -> {
                    StringBuilder report = new StringBuilder("Sent Messages Report:\n");
                    List<String> sent = Message.getSentMessages();
                    List<String> ids = Message.getMessageIds();
                    List<String> hashes = Message.getMessageHashes();
                    for (int i = 0; i < sent.size(); i++) {
                        report.append("ID: ").append(ids.get(i))
                              .append("\nHash: ").append(hashes.get(i))
                              .append("\nContent: ").append(sent.get(i))
                              .append("\n\n");
                    }
                    JOptionPane.showMessageDialog(null, report.toString());
                }

                case 9 -> JOptionPane.showMessageDialog(null, "Goodbye!");

                default -> JOptionPane.showMessageDialog(null, "Invalid option.");
            }
        } while (choice != 9);
    }

    private static void saveStoredMessagesToJson() {
        List<String> stored = Message.getStoredMessages();
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        try (FileWriter writer = new FileWriter(STORED_MESSAGES_FILE)) {
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer, stored);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save stored messages.");
        }
    }
}
