package io.mateu.kmlshop.model;

import io.mateu.mdd.core.annotations.NotInList;
import io.mateu.mdd.core.annotations.TextArea;
import io.mateu.mdd.core.model.common.Resource;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity@Getter@Setter
public class Route {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private boolean active;

    @TextArea
    private String description;

    @ManyToOne
    @NotInList
    private Resource image;

    @ManyToOne
    @NotInList
    private Resource kml;

    private double valueInEuros;

}
