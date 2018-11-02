package io.mateu.kmlshop.model;

import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.NotInList;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.common.Resource;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;

@Entity@Getter@Setter
public class Route {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty
    private String name;

    private boolean active;

    private boolean bestSeller;

    @TextArea
    private String description;

    @TextArea
    private String content;

    @ManyToOne
    @NotInList
    @NotNull
    private Resource image;

    @ManyToOne
    @NotInList
    private Resource kml;

    @ManyToOne
    @NotInList
    private Resource archivo1;

    @ManyToOne
    @NotInList
    private Resource archivo2;


    private double valueInEuros;

    @Override
    public String toString() {
        return getName();
    }

    @Action(style = "friendly")
    public static void publish() throws Throwable {

        Helper.notransact(em -> {

            ShopAppConfig appconfig = ShopAppConfig.get(em);

            String dp = System.getProperty("java.io.tmpdir") + "/github/" + Helper.camelcasize(appconfig.getBusinessName());

            System.out.println("*******************************************");
            System.out.println("PUBLICANDO WEB");
            System.out.println("*******************************************");


            File dir = new File(dp);

            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println(Helper.runCommand("cd " + dir.getAbsolutePath() + "; git clone " + appconfig.getGithubUrl().replaceAll("github.com", appconfig.getGithubUser() + ":" + appconfig.getGithubPassword() + "@github.com") + " ."));
            } else {
                System.out.println(Helper.runCommand("cd " + dir.getAbsolutePath() + "; git pull"));
            }

            for (String d : new String[] {"web/static/imagesfromdatabase", "web/content/rutas", "web/content/bestsellers"}) {
                File dd = new File(dir.getAbsolutePath() + "/" + d);
                if (!dd.exists()) dd.mkdirs();
                System.out.println(Helper.runCommand("cd " + dir.getAbsolutePath() +  ";git rm " + dd.getAbsolutePath() + "/*"));
            }

            for (String d : new String[] {"web/static/imagesfromdatabase", "web/content/rutas", "web/content/bestsellers"}) {
                File dd = new File(dir.getAbsolutePath() + "/" + d);
                if (!dd.exists()) dd.mkdirs();
            }

            System.out.println(Helper.runCommand("touch " + dir.getAbsolutePath() + "/web/content/bestsellers/_index.md"));

            try {
                Helper.escribirFichero(dir.getAbsolutePath() + "/web/content/bestsellers/_index.md", "---\n" +
                        "title: \"Best sellers\"\n" +
                        "---\n" +
                        "\n" +
                        "Lista de rutas");
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(Helper.runCommand("touch " + dir.getAbsolutePath() + "/web/content/rutas/_index.md"));

            try {
                Helper.escribirFichero(dir.getAbsolutePath() + "/web/content/rutas/_index.md", "---\n" +
                        "title: \"Routes\"\n" +
                        "---\n" +
                        "\n" +
                        "Lista de rutas");
            } catch (Exception e) {
                e.printStackTrace();
            }


            for (Route r : (List<Route>) em.createQuery("select x from " + Route.class.getName() + " x where x.active = true").getResultList()) {

                Helper.escribirFichero(dir.getAbsolutePath() + "/web/content/rutas/" + Helper.urlize(r.getName()) + ".md", r.toMD());
                if (r.isBestSeller()) Helper.escribirFichero(dir.getAbsolutePath() + "/web/content/bestsellers/" + Helper.urlize(r.getName()) + ".md", r.toMD());

                if (r.getImage() != null) try {
                    Helper.escribirFichero(dir.getAbsolutePath() + "/web/static/imagesfromdatabase/" + r.getImage().getName(), r.getImage().getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println(Helper.runCommand("cd " + dir.getAbsolutePath() + "; git add .;git commit -m 'publish';git push"));


            System.out.println("*******************************************");
            System.out.println("PUBLICACIÓN WEB FINALIZADA");
            System.out.println("*******************************************");

        });




    }

    public String toMD() {
        String md = "---\n" +
                "title: \"" + getName() + "\"\n" +
                "date: 2018-10-27T11:59:33+02:00\n";

        if (getImage() != null) md += "mainimage: \"imagesfromdatabase/" + getImage().getName() + "\"\n";

        md += "description: \"" + ((getDescription() != null)?getDescription().replaceAll("\"", "\\\""):"") + "\"\n" +
                "item_name: \"" + getName().replaceAll("\"", "\\\"") + "\"\n" +
                "item_number: \"" + getId() + "\"\n" +
                "item_value: \"" + getValueInEuros() + "\"\n" +
                "notify_url: \"http://kmlshop.mateu.io/paypal/notify\"\n" +
                "---\n" +
                ((getContent() != null)?getContent():"");
        return md;
    }
}
