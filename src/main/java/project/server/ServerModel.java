package project.server;

import project.utilities.*;
import project.utilities.requests.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ServerModel {
    private static final int PORT = 42069;

    /** A {@link HashSet} used to maintain all the usable accounts as strings.
     * Initialized by the constructor through {@link ServerModel#readAccountsFile}. */
    private HashSet<String> accountSet;

    public ServerModel() {
        try {
            HashSet<String> starterSet = readAccountsFile();
            if (starterSet == null)
                throw new IOException("Unable to read accounts file");
            accountSet = new HashSet<>(starterSet);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        // @todo stuff to initialize
    }

    /** Function that has to retrieve from "accountList.txt" file the list of the current accounts for the server. */
    private HashSet<String> readAccountsFile() {
        try (FileInputStream input = new FileInputStream("persistence/accountList.txt")) {
            byte[] allBytes = new byte[input.available()];
            if (input.read(allBytes) != allBytes.length)    // Read all bytes from file
                throw new Exception("Could not read the entire file.");
            // Converting the array into a String
            String content = new String(allBytes, StandardCharsets.UTF_8);
            ArrayList<String> stringList = new ArrayList<>(Arrays.asList(content.split("\\r?\\n")));
            return new HashSet<>(stringList);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } return null;
    }

    /** Check for the existence of the address passed inside the accounts list.
     * @param address The string representing the email address to check. */
    public boolean checkAddress(String address) {
        return address != null && accountSet.contains(address);
    }

    public static int getPORT() {
        return PORT;
    }

    /**
     * This method returns the List of MailHeader of a user, reading it by a file in the directory "/persistence/headers"
     * @param targetUser : the name of the user, the same of his file in "/persistence/headers"
     * @return An ArrayList containing all the MailHeaders related to his received emails
     */
    public static ArrayList<MailHeader> getMailHeaders(String targetUser) throws Exception { //@todo: da eseguire in sezione critica
        try (FileInputStream fileInput = new FileInputStream("persistence/headers/" + targetUser)) {
            ObjectInputStream input = new ObjectInputStream(fileInput);
            Object inObject = input.readObject();

            ArrayList<MailHeader> mailHeaders = Utilities.castToMailHeadersList(inObject);
            if (mailHeaders == null)
                throw new IOException("Erroneous content of headers fIle");
            return mailHeaders;
        } catch (FileNotFoundException fileNotFoundException) {
            return new ArrayList<MailHeader>();
        }
    }

    public static void writeHeaderFile(String targetUser, ArrayList<MailHeader> mailHeaders) throws Exception { //@todo: eseguire in sezione critica
        try (FileOutputStream fileOutput = new FileOutputStream("persistence/headers/" + targetUser)) {
            ObjectOutputStream output = new ObjectOutputStream(fileOutput);
            output.writeObject(mailHeaders);
        }
    }
}
