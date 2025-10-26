package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Measurement;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetMeasurementByIdHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetMeasurementByIdHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        try {
            int id = Integer.parseInt(context.pathParam("id"));
            Measurement m = em.find(Measurement.class, id);

            if (m == null) {
                context.response().setStatusCode(404).end("Measurement not found");
                return;
            }

            JsonObject json = new JsonObject();
            json.put("id", m.getId());
            json.put("sensor", m.getSensor().getId());
            json.put("name", m.getName());
            json.put("unit", m.getUnit());

            context.response()
                   .putHeader("content-type", "application/json")
                   .end(json.encode());

        } catch (Exception e) {
            context.response().setStatusCode(500).end("Error: " + e.getMessage());
        }
    }
}
