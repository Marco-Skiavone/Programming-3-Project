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

}
