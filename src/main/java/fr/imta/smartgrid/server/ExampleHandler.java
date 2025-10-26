package fr.imta.smartgrid.server;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class ExampleHandler implements Handler<RoutingContext> {

    private EntityManager db;

    public ExampleHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        JsonObject json = new JsonObject();

        try {
            Long count = (Long) db.createNativeQuery("SELECT count(*) FROM sensor").getSingleResult();
            json.put("nb_sensors", count);
            context.response()
                   .putHeader("content-type", "application/json")
                   .end(json.encode());
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Error: " + e.getMessage());
        }
    }
}
