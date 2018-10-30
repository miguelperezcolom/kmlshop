package io.mateu.kmlshop.model;

import io.mateu.mdd.core.CSS;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.annotations.NotInList;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.common.Resource;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

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

    @ManyToOne
    @NotInList
    private Resource image;

    @ManyToOne
    @NotInList
    private Resource kml;

    private double valueInEuros;

    @Override
    public String toString() {
        return getName();
    }

    @Action(style = "danger")
    public static void publish() {

    }
}
