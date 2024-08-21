package project.server;

import project.utilities.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.*;

public class ServerModel {

    /** A {@link HashSet} used to maintain all the usable accounts as strings.
     * Initialized by the constructor through {@link ServerModel#readAccountsFile}. */
    private HashSet<String> accountSet;
    /** A {@link HashMap} containing a series of locks, one for each file of the persistence.
     * Used to correctly update files with Threads.*/
    private final HashMap<String, ReentrantReadWriteLock> fileLocks;

    public ServerModel() {
        try {
            HashSet<String> starterSet = readAccountsFile();
            if (starterSet == null)
                throw new IOException("Unable to read accounts file");
            accountSet = new HashSet<>(starterSet);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            fileLocks = new HashMap<>();
        }
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

    /**
     * This method returns the List of MailHeader of a user, reading it by a file in the directory "/persistence/headers"
     * @param targetUser : the name of the user, the same of his file in "/persistence/headers"
     * @return An ArrayList containing all the MailHeaders related to his received emails
     */
    public ArrayList<MailHeader> readHeaderFile(String targetUser) throws Exception {
        ReentrantReadWriteLock rwl = getFileLock("persistence/headers/" + targetUser + ".txt");
        rwl.readLock().lock();
        try (FileInputStream fileInput = new FileInputStream("persistence/headers/" + targetUser + ".txt")) {
            ObjectInputStream input = new ObjectInputStream(fileInput);
            Object inObject = input.readObject();

            ArrayList<MailHeader> mailHeaders = Utilities.castToMailHeadersList(inObject);
            if (mailHeaders == null)
                throw new IOException("Erroneous content of headers fIle");
            return mailHeaders;
        } catch (FileNotFoundException fileNotFoundException) {
            return new ArrayList<MailHeader>();
        } finally {
            rwl.readLock().unlock();
        }
    }

    /** Function used to overwrite headers file using locks on {@link ServerModel#fileLocks}.<br>
     * todo -> could be generalized to all the files, passing a function as argument!
     * @param targetUser The string representing the name of the file to update.
     *                   It must be only the final name without its file extension.
     * @throws IOException If the content of the file passed is unparsable as a list of {@link project.utilities.MailHeader}.
     * @throws Exception Or any other of its extenders is launched if something goes wrong.
     * These have to be handled by its caller! */
    public void updateHeaderFile(String targetUser, Collection<MailHeader> headers, boolean delete) throws Exception {
        ReentrantReadWriteLock rwl = getFileLock("persistence/headers/" + targetUser + ".txt");
        ArrayList<MailHeader> mailHeaders;

        // Reading...
        rwl.writeLock().lock();
        try {
            try (FileInputStream fileInput = new FileInputStream("persistence/headers/" + targetUser + ".txt")) {
                ObjectInputStream input = new ObjectInputStream(fileInput);
                Object inObject = input.readObject();

                mailHeaders = Utilities.castToMailHeadersList(inObject);
            } catch (FileNotFoundException fileNotFoundException) {
                mailHeaders = new ArrayList<MailHeader>();
            } catch (ClassCastException classCastException)  {
                throw new IOException("Erroneous content of headers fIle");
            }

            //assert mailHeaders != null
            if(delete) {
                for(MailHeader mailHeader : headers) {
                    mailHeaders.remove(mailHeader);
                    decreaseCounter(mailHeader);
                }
            } else {
                //@todo: make ordered the add method
                mailHeaders.addAll(headers);
            }

            FileOutputStream fileOutput = null;
            try {
                fileOutput = new FileOutputStream("persistence/headers/" + targetUser + ".txt");
                ObjectOutputStream output = new ObjectOutputStream(fileOutput);
                output.writeObject(mailHeaders);
                output.flush();
            } finally {
                if (fileOutput != null && fileOutput.getChannel().isOpen())
                    fileOutput.close();
            }
        } finally {
            rwl.writeLock().unlock();
        }

    }

    /**
     * This method returns an eMail, reading it by a file in the directory "/persistence/emails"
     * @param header : the header, of the requested eMail in "/persistence/mails"
     * @return an Email obj
     */
    public Email readEmailFile(MailHeader header) throws Exception {
        ReentrantReadWriteLock rwl = getFileLock("persistence/mails/" + header.hashCode() + ".txt");
        rwl.readLock().lock();
        try (FileInputStream fileInput = new FileInputStream("persistence/mails/" + header.hashCode() + ".txt")) {
            ObjectInputStream input = new ObjectInputStream(fileInput);
            Object inObject = input.readObject();
            rwl.readLock().unlock();

            return Utilities.castToEmail(inObject);
        }
    }

    /**
     * This method writes the Email object creating the corresponding file
     * @param email The Email to write
     *
     * @throws IOException – if an I/ O error occurs while writing stream header
     * @throws SecurityException – if untrusted subclass illegally overrides security-sensitive methods
     * @throws NullPointerException – if out is null
     * @throws FileNotFoundException – if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
     */
    public void writeEmailFile(Email email) throws Exception {
        ReentrantReadWriteLock rwl = getFileLock("persistence/mails/" + email.getHeader().hashCode() + ".txt");
        email.setReferencesCounter(email.getReceivers().size());//@todo: questa operazione la può fare il client?

        FileOutputStream fileOutput = null;
        rwl.writeLock().lock();
        try {
            fileOutput = new FileOutputStream("persistence/mails/" + email.getHeader().hashCode() + ".txt");
            ObjectOutputStream output = new ObjectOutputStream(fileOutput);
            output.writeObject(email);
            output.flush();
        } finally {
            if (fileOutput != null && fileOutput.getChannel().isOpen())
                fileOutput.close();
        }
        rwl.writeLock().unlock();
    }
    
    /** Function called to retrieve a lock about a persistence file.
     * @param filePath The String representing a file of the persistence.
     * @return the associated {@link ReentrantReadWriteLock} to a String, if it exists.
     * If the String passed is not contained in the {@link HashMap} {@link #fileLocks}, then this function creates
     * the instance and returns it back. */
    private ReentrantReadWriteLock getFileLock(String filePath) {
        ReentrantReadWriteLock rwl;
        synchronized (fileLocks) {
            rwl = fileLocks.get(filePath);
            if (rwl == null) {
                fileLocks.put(filePath , new ReentrantReadWriteLock(true));
                rwl = fileLocks.get(filePath);
            }
        }
        return rwl;
    }

    /**
     *  Updates the referenceCounter of an Email decreasing it by 1. If the referenceCounter become 0, also deletes the mailFile
     * @param header is the MailHeader corresponding to the target Email
     * @throws Exception @todo:
     */
    private void decreaseCounter(MailHeader header) throws Exception {
        ReentrantReadWriteLock rwl = getFileLock("persistence/mails/" + header.hashCode() + ".txt");
        Email email;
        rwl.writeLock().lock();
        try (FileInputStream fileInput = new FileInputStream("persistence/mails/" + header.hashCode() + ".txt")) {
            ObjectInputStream input = new ObjectInputStream(fileInput);
            Object inObject = input.readObject();

            email = Utilities.castToEmail(inObject);
            email.decreaseReferencesCounter();
        }
        if (email.getReferencesCounter() <= 0){
            File emailFile = new File("persistence/mails/" + header.hashCode() + ".txt"); //todo: needs a removeEmail method?
            if (!emailFile.delete())
                throw new SecurityException("Can't delete " + emailFile.getAbsolutePath());
        } else {
            FileOutputStream fileOutput = null;
            try {
                fileOutput = new FileOutputStream("persistence/mails/" + header.hashCode() + ".txt");
                ObjectOutputStream output = new ObjectOutputStream(fileOutput);

                output.writeObject(email);
                output.flush();
            } finally {
                if (fileOutput != null && fileOutput.getChannel().isOpen())
                    fileOutput.close();
            }
        }
        rwl.writeLock().unlock();
    }
}
