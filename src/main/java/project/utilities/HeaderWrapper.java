package project.utilities;

import java.io.*;
import java.util.*;

/** Class used by the client "Mailbox" section to add a boolean to the MailHeader ObservableList.
 * That is to make easier updating the client listView. */
public class HeaderWrapper implements Serializable, Comparable<HeaderWrapper> {
    private final MailHeader header;
    private boolean selected;

    public HeaderWrapper(MailHeader header) {
        this.header = header;
        this.selected = false;
    }

    public MailHeader getHeader() {
        return header;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int compareTo(HeaderWrapper o) {
        return header.compareTo(o.header);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof HeaderWrapper headerWrapper)
            return header.equals(headerWrapper.header) && selected == headerWrapper.selected;
        if (o instanceof MailHeader h)
            return header.equals(h);
        return false;
    }

    @Override
    public int hashCode() {
        return header.hashCode();
    }

    /** Function used to wrap {@link MailHeader}s into objects paired with a boolean, to easily handle "view" selection.
     * @param headers The List of MailHeaders to wrap in this record.
     * @return A {@link List<>} Where every element is wrapped in the record with {@code selected} == false. */
    public static List<HeaderWrapper> toWrappedList(List<MailHeader> headers) {
        List<HeaderWrapper> wrappedList = new ArrayList<>();
        for (MailHeader header : headers)
            wrappedList.add(new HeaderWrapper(header));
        return wrappedList;
    }


}
