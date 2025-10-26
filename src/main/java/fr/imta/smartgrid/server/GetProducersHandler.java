package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Producer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetProducersHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetProducersHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        var producers = em.createQuery("SELECT p FROM Producer p", Producer.class).getResultList();
        JsonArray result = new JsonArray();

        for (Producer p : producers) {
            result.add(p.toJSON());
        }

        context.response()
               .putHeader("content-type", "application/json")
               .end(result.encode());
    }
}
