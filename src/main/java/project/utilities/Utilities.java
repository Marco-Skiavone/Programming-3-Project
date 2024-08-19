package project.utilities;

import java.util.*;

public interface Utilities {

    int PORT = 42069;

    static ArrayList<MailHeader> castToMailHeadersList(Object obj) throws ClassCastException {
        if (obj instanceof ArrayList<?> headerList) {
            for(Object o : headerList)
                if (!(o instanceof MailHeader))
                    throw new ClassCastException("Erroneous type in obj");
            //noinspection unchecked
            return (ArrayList<MailHeader>) headerList;
        }
        return null;
    }

}
