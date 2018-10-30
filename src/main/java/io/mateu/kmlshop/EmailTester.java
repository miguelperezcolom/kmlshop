package io.mateu.kmlshop;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.time.LocalDateTime;

public class EmailTester {

    public static void main(String[] args) throws EmailException {
        testGmail();
    }

    private static void testGmail() throws EmailException {

        // Create the email message
        HtmlEmail email = new HtmlEmail();
        //Email email = new HtmlEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(465);
        email.setSSLOnConnect(true);
        email.setAuthentication("fweewwe", "gifgiweugfew");
        //email.setSSLOnConnect(true);
        email.setFrom("miguel@mateu.io");

        email.setSubject("Prueba gmail");
        email.setMsg("Hola! " + LocalDateTime.now());

        email.addTo("miguelperezcolom@gmail.com");

        email.send();

    }
}
