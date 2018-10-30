package io.mateu.kmlshop.model;


import io.mateu.mdd.core.annotations.Indelible;
import io.mateu.mdd.core.annotations.NewNotAllowed;
import io.mateu.mdd.core.annotations.Output;
import io.mateu.mdd.core.model.authentication.Audit;
import io.mateu.mdd.core.model.authentication.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter@Setter
@NewNotAllowed@Indelible
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime created;

    private String email;

    private double euros;

    private LocalDateTime confirmed;

    private LocalDateTime sent;

    @ManyToOne@NotNull
    private Route route;


}
