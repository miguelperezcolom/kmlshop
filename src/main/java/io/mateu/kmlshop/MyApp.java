package io.mateu.kmlshop;

import io.mateu.kmlshop.model.Route;
import io.mateu.kmlshop.model.Sale;
import io.mateu.kmlshop.model.ShopAppConfig;
import io.mateu.mdd.core.annotations.Action;
import io.mateu.mdd.core.app.AbstractAction;
import io.mateu.mdd.core.app.MDDOpenCRUDAction;
import io.mateu.mdd.core.app.MDDOpenEditorAction;
import io.mateu.mdd.core.app.SimpleMDDApplication;
import io.mateu.mdd.core.model.authentication.User;
import io.mateu.mdd.core.model.config.AppConfig;
import io.mateu.mdd.core.util.Helper;
import java.io.IOException;

public class MyApp extends SimpleMDDApplication {

    @Action(order = 1)
    public AbstractAction routes() {
        return new MDDOpenCRUDAction(Route.class);
    }

    @Action(order = 2)
    public AbstractAction sales() {
        return new MDDOpenCRUDAction(Sale.class);
    }

    @Action(order = 3)
    public AbstractAction users() {
        return new MDDOpenCRUDAction(User.class);
    }

    @Action(order = 4)
    public AbstractAction config() {
        return new MDDOpenEditorAction("", ShopAppConfig.class, 1l);
    }

    @Override
    public boolean isAuthenticationNeeded() {
        return true;
    }

    @Override
    public Class<? extends AppConfig> getAppConfigClass() {
        return ShopAppConfig.class;
    }

    @Override
    public String getName() {
        return "KML Shop";
    }
}