package io.mateu.kmlshop.model;

import io.mateu.mdd.core.model.config.AppConfig;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

@Entity@Getter@Setter
public class ShopAppConfig extends AppConfig {


    private String paypalEmail;


    public static ShopAppConfig get(EntityManager em) {
        return em.find(ShopAppConfig.class, 1l);
    }

}
