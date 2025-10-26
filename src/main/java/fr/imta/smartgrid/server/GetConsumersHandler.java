package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Consumer;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetConsumersHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetConsumersHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        var consumers = em.createQuery("SELECT c FROM Consumer c", Consumer.class).getResultList();
        JsonArray result = new JsonArray();

        for (Consumer c : consumers) {
            result.add(c.toJSON());
        }

        context.response()
               .putHeader("content-type", "application/json")
               .end(result.encode());
    }
}
