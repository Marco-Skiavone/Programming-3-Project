package project.utilities;

import java.util.*;

public interface Utilities {

    int PORT = 42069;

    static ArrayList<MailHeader> castToMailHeadersList(Object obj) throws ClassCastException {
        if (obj instanceof ArrayList<?> headerList) {
            for(Object o : headerList)
                if (!(o instanceof MailHeader))
                    throw new ClassCastException("Erroneous type in obj: it doesn't contains a MailHeader");
            //noinspection unchecked
            return (ArrayList<MailHeader>) headerList;
        }
        throw new ClassCastException("Erroneous type obj: it is not a ArrayList");
    }

    static Email castToEmail(Object obj) throws ClassCastException {
        if (obj instanceof Email)
            return (Email) obj;

        throw new ClassCastException("Erroneous obj is not an Email");
    }

    /** It requires lowercase letters and trimmed string, to ensure a simpler input commitment.
     * @param input a string that represents the e-mail to check.
     * @return true if input matches the regex, false otherwise.
     * @Note: It'll let you insert 24 chars before '@', '-' and '_' (but not strictly before '@'), needs at least 2 letters after domain. */
    static boolean checkSyntax(String input) {
        if (input == null || input.length() < 9)     // e.g: at least ma@dom.it
            return false;
        return input.matches("^(?=.{2,32}@)[a-z0-9_-]+(\\.[a-z0-9_-]+)*" +
                "@[^-][a-z0-9-]+(\\.[a-z-]+)*(\\.[a-z]{2,})$");
    }
}
