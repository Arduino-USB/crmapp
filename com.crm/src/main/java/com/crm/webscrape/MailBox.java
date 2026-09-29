package com.crm.webscrape;

import jakarta.mail.internet.*;
import jakarta.mail.*;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MailBox {

    private Store store;
    private final String address;
    private final String port;
    private final String username;
    private final String password;
    
    private final Map<String, Integer> folderBaselines = new ConcurrentHashMap<>();
    private final List<String> cachedFolders;
    private Thread backgroundTracker;
    private volatile boolean isListening = false;

    /**
     * Constructor blocks until connection is established, folders are opened,
     * and baselines are fully initialized.
     */

		
	public static class MailBoxConfig {
		
		public MailBoxConfig(String address, String port, String username, String password, List<String> targetFolders) {
		    this.address = address;
		    this.port = port;
		    this.username = username;
		    this.password = password;
		}	

		public String username;
		public String address;
		public String port;
		public String password;
		public List<String> targetFolders;
				
	}


    public MailBox(MailBoxConfig mailBoxConfig) {
        this.address = mailBoxConfig.address;
        this.port = mailBoxConfig.port;
        this.username = mailBoxConfig.username;
        this.password = mailBoxConfig.password;
        this.cachedFolders = (mailBoxConfig.targetFolders != null && !mailBoxConfig.targetFolders.isEmpty()) ? mailBoxConfig.targetFolders : List.of("INBOX");
        
        // Fully initialize connection and baselines synchronously before returning
        connectStore();
        initializeAndStartListening();
    }

	public String getUsername() {
		return this.username;
	}

    private synchronized void connectStore() {
        try {
            if (store != null) {
                try { store.close(); } catch (Exception ignored) {}
            }
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", address);
            props.put("mail.imaps.port", port);
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.connectiontimeout", "10000");
            props.put("mail.imaps.timeout", "10000");

            Session session = Session.getInstance(props);
            store = session.getStore("imaps");
            store.connect(address, username, password);
        } catch (Exception e) {
            throw new RuntimeException("[MailBox] Failed to connect to mail store", e);
        }
    }

    private synchronized void ensureConnected() {
        try {
            if (store == null || !store.isConnected()) {
                System.out.println("[MailBox] Store disconnected. Reconnecting...");
                connectStore();
            }
        } catch (Exception e) {
            System.out.println("[MailBox] Reconnection attempt failed: " + e.getMessage());
        }
    }

    private synchronized void initializeAndStartListening() {
        if (isListening) {
            return;
        }

        try {
            ensureConnected();
            
            // Open specified folders and fetch initial baselines immediately (blocking)
            for (String folderName : cachedFolders) {
                Folder folder = store.getFolder(folderName);
                if (!folder.isOpen()) {
                    folder.open(Folder.READ_ONLY);
                }
                folderBaselines.put(folderName, folder.getMessageCount());
            }
            
            System.out.println("[MailPolling] Listening initialized synchronously across " + cachedFolders.size() + " folder(s). Baselines loaded.");
            isListening = true;

            // Start background thread for continuous polling
            backgroundTracker = new Thread(() -> {
                int consecutiveErrors = 0;
                while (isListening) {
                    try {
                        ensureConnected();
                        for (String folderName : cachedFolders) {
                            Folder folder = store.getFolder(folderName);
                            if (!folder.isOpen()) {
                                folder.open(Folder.READ_ONLY);
                            }
                            
                            int currentCount = folder.getMessageCount();
                            int previousCount = folderBaselines.getOrDefault(folderName, currentCount);

                            if (currentCount > previousCount) {
                                if (folderBaselines.replace(folderName, previousCount, currentCount)) {
                                    int newEmails = currentCount - previousCount;
                                    System.out.println("[MailPolling] New email received in folder '" + folderName + "'! (" + newEmails + " new message(s). Total: " + currentCount + ")");
                                }
                            }
                        }
                        consecutiveErrors = 0;
                    } catch (MessagingException e) {
                        consecutiveErrors++;
                        System.out.println("[MailPolling] Background tracker error: " + e.getMessage());
                        try { if (store != null) store.close(); } catch (Exception ignored) {}
                    }

                    try {
                        long sleepTime = Math.min(2000L * (1L << Math.min(consecutiveErrors, 4)), 30000L);
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });

            backgroundTracker.setDaemon(true);
            backgroundTracker.start();

        } catch (MessagingException e) {
            throw new RuntimeException("[MailPolling] Failed to initialize folder listeners: " + e.getMessage(), e);
        }
    }

    public MessageResult getLatestMessageAcrossAllFolders(String fromFilter) {
        ensureConnected();
        Map<String, Integer> localBaselines = new HashMap<>();
        for (String fName : cachedFolders) {
            try {
                Folder folder = store.getFolder(fName);
                if (!folder.isOpen()) {
                    folder.open(Folder.READ_ONLY);
                }
                localBaselines.put(fName, folder.getMessageCount());
            } catch (MessagingException e) {
                localBaselines.put(fName, 0);
            }
        }

        MessageResult result = null;
        int attemptCount = 0;

        while (result == null && isListening) {
            attemptCount++;

            try {
                ensureConnected();
                for (String folderName : cachedFolders) {
                    Folder folder = store.getFolder(folderName);
                    if (!folder.isOpen()) {
                        folder.open(Folder.READ_ONLY);
                    }

                    int currentCount = folder.getMessageCount();
                    int baselineCount = localBaselines.getOrDefault(folderName, currentCount);

                    if (currentCount > baselineCount) {
                        for (int i = currentCount; i > baselineCount; i--) {
                            Message msg = folder.getMessage(i);
                            if (matchesSenderFilter(msg, fromFilter)) {
                                System.out.println("[MailPolling] Found matching email in folder '" + folderName + "' from '" + fromFilter + "' at index " + i);
                                result = new MessageResult(folderName, msg);
                                break;
                            }
                        }
                    }
                    if (result != null) {
                        break;
                    }
                }
            } catch (MessagingException e) {
                System.out.println("[MailPolling] [Attempt " + attemptCount + "] Error querying folders: " + e.getMessage());
                try { if (store != null) store.close(); } catch (Exception ignored) {}
            }

            if (result == null) {
                if (attemptCount % 5 == 0) {
                    System.out.println("[MailPolling] Waiting for email matching '" + fromFilter + "' across specified folders (Attempt " + attemptCount + ")...");
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("[MailPolling] Polling interrupted.");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        return result;
    }

    private boolean matchesSenderFilter(Message message, String fromFilter) throws MessagingException {
        if (fromFilter == null || fromFilter.isEmpty()) {
            return true;
        }

        Address[] senders = message.getFrom();
        if (senders == null) {
            return false;
        }

        for (Address sender : senders) {
            if (sender.toString().toLowerCase().contains(fromFilter.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private String getContent(Part p, List<String> mimeTypes) {
        try {
            if (p.isMimeType("text/plain") || p.isMimeType("text/html")) {
                Object content = p.getContent();
                if (content instanceof String) {
                    for (String mimeType : mimeTypes) {
                        if (p.isMimeType(mimeType)) {
                            return (String) content;
                        }
                    }
                }
            }
            if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    String subContent = getContent(mp.getBodyPart(i), mimeTypes);
                    if (subContent != null) {
                        return subContent;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public String getMessageData(String fromSender, String selector, int nth) {
        try {
            MessageResult messageResult = getLatestMessageAcrossAllFolders(fromSender);
            if (messageResult == null || messageResult.getMessage() == null) {
                return null;
            }

            System.out.println("[MailBox] Processing message from dynamically resolved folder: " + messageResult.getFolderName());

            String html = getContent(messageResult.getMessage(), List.of("text/html"));
            if (html == null) {
                return null;
            }

            Document messageDoc = Jsoup.parse(html);
            return messageDoc.select(selector).eq(nth).text();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stopListening() {
        isListening = false;
        if (backgroundTracker != null) {
            backgroundTracker.interrupt();
        }
    }

    public static class MessageResult {
        private final String folderName;
        private final Message message;

        public MessageResult(String folderName, Message message) {
            this.folderName = folderName;
            this.message = message;
        }

        public String getFolderName() {
            return folderName;
        }

        public Message getMessage() {
            return message;
        }
    }
}
