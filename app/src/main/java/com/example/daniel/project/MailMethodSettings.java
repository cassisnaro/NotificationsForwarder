package com.example.daniel.project;

/**
 * Created by daniel on 22/02/15.
 */
public class MailMethodSettings {
    String mailTo;
    String subject;

    public MailMethodSettings(String mailTo, String subject) {
        this.mailTo = mailTo;
        this.subject = subject;
    }

    public String getMailTo() {
        return mailTo;
    }

    public String getSubject() {
        return subject;
    }
}
