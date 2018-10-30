package io.mateu.kmlshop;

import io.mateu.mdd.core.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
