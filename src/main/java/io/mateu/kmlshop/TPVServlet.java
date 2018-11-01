package io.mateu.kmlshop;

import com.google.common.base.Strings;
import io.mateu.kmlshop.model.Route;
import io.mateu.kmlshop.model.Sale;
import io.mateu.kmlshop.model.ShopAppConfig;
import io.mateu.mdd.core.MDD;
import io.mateu.mdd.core.model.config.AppConfig;
import io.mateu.mdd.core.util.Helper;
import io.mateu.mdd.core.util.Utils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/paypal/*"})
public class TPVServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.out.println("uri:" + req.getRequestURI());
        System.out.println("url:" + req.getRequestURL());

        resp.addHeader("Access-Control-Allow-Origin", "*");

        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");

        String uri = req.getRequestURI();
        try {

            if (uri.endsWith("lanzadera")) {
                resp.getWriter().print("<html>\n" +
                        "<head>\n" +
                        "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                        "    <title>Payment</title>\n" +
                        "</head>\n" +
                        "<body onload=\"document.f.submit();\">");
                //resp.getWriter().print(TPVTransaction.getForm(Long.parseLong(req.getParameter("idtransaccion"))));
                resp.getWriter().print("</body>\n" +
                        "</html>");
            } else if (uri.endsWith("notify")) {
                System.out.println("****TPVNOTIFICACION****");
                for (String n : req.getParameterMap().keySet()) {
                    for (String v : req.getParameterValues(n)) {
                        System.out.println("" + n + ":" + v);
                    }
                }
                System.out.println("****TPVNOTIFICACION****");

                String payer_email = req.getParameter("payer_email");
                String item_number = req.getParameter("item_number");
                String mc_gross = req.getParameter("mc_gross");
                String first_name = req.getParameter("first_name");
                String last_name = req.getParameter("last_name");


                Sale s = new Sale();
                Helper.transact(em -> {
                    s.setConfirmed(LocalDateTime.now());
                    s.setCreated(LocalDateTime.now());
                    s.setEmail(payer_email);
                    s.setEuros(Double.parseDouble(mc_gross));
                    s.setRoute(em.find(Route.class, Long.parseLong(item_number)));
                    em.persist(s);
                });

                Helper.transact(em -> {

                    ShopAppConfig appconfig = ShopAppConfig.get(em);

                    // Create the attachment
//                EmailAttachment attachment = new EmailAttachment();
//                attachment.setPath("mypictures/john.jpg");
//                attachment.setDisposition(EmailAttachment.ATTACHMENT);
//                attachment.setDescription("Picture of John");
//                attachment.setName("John");

                    // Create the email message
                    HtmlEmail email = new HtmlEmail();
                    //Email email = new HtmlEmail();
                    email.setHostName(appconfig.getAdminEmailSmtpHost());
                    email.setSmtpPort(appconfig.getAdminEmailSmtpPort());
                    email.setSSLOnConnect(appconfig.isAdminEmailSSLOnConnect());
                    email.setAuthentication(appconfig.getAdminEmailUser(), appconfig.getAdminEmailPassword());
                    email.setFrom(appconfig.getAdminEmailFrom());
                    if (!Strings.isNullOrEmpty(appconfig.getAdminEmailCC())) email.getCcAddresses().add(new InternetAddress(appconfig.getAdminEmailCC()));

                    //email.setSubject("Your KML file from " + MDD.getApp().getName());
                    email.setSubject("Your KML file");
                    Map<String, Object> data = new HashMap<>();
                    data.put("payer_email", payer_email);
                    data.put("item_number", item_number);
                    data.put("mc_gross", mc_gross);
                    data.put("first_name", first_name);
                    data.put("last_name", last_name);
                    data.put("item_name", s.getRoute().getName());

                    email.setMsg(Helper.freemark(appconfig.get(em).getSaleEmailTemplate(), data));

                    email.addTo((!Strings.isNullOrEmpty(System.getProperty("allemailsto")))?System.getProperty("allemailsto"):s.getEmail());


                    if (s.getRoute().getKml() != null) try {
                        File attachment = new File(s.getRoute().getKml().toFileLocator().getTmpPath());
                        if (attachment != null) email.attach(attachment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (s.getRoute().getArchivo1() != null) try {
                        File attachment = new File(s.getRoute().getArchivo1().toFileLocator().getTmpPath());
                        if (attachment != null) email.attach(attachment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (s.getRoute().getArchivo2() != null) try {
                        File attachment = new File(s.getRoute().getArchivo2().toFileLocator().getTmpPath());
                        if (attachment != null) email.attach(attachment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //email.attach(new ByteArrayDataSource(new XMLOutputter(Format.getPrettyFormat()).outputString(IslandbusHelper.toPrivateXml(getPurchaseOrders())).getBytes(), "text/xml"), "private.xml", "xml for privates");
                    //email.attach(new ByteArrayDataSource(new XMLOutputter(Format.getPrettyFormat()).outputString(IslandbusHelper.toShuttleXml(getPurchaseOrders())).getBytes(), "text/xml"), "shuttle.xml", "xml for shuttle");

                    email.send();

                    s.setSent(LocalDateTime.now());
                    em.merge(s);
                });


            } else if (uri.endsWith("ok")) {
                resp.getWriter().print(Utils.read(this.getClass().getResourceAsStream("/ok")));
            } else if (uri.endsWith("ko")) {
                resp.getWriter().print(Utils.read(this.getClass().getResourceAsStream("/ko")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }


    }

}
