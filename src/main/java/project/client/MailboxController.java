package project.client;

public class MailboxController {
    /* The listView here will use a complex class to have:
     * - a CheckBox,
     * - a String (to show),
     * - an ID associated (hidden) */


    private final MailboxModel model;

    public MailboxController() {
        this.model = new MailboxModel();
    }
}
