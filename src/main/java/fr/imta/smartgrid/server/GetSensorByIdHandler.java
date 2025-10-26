package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetSensorByIdHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetSensorByIdHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        try {
            String idParam = context.pathParam("id");
            int id = Integer.parseInt(idParam);

            Sensor sensor = em.find(Sensor.class, id);

            if (sensor == null) {
                context.response().setStatusCode(404).end("Sensor not found");
            } else {
                context.response()
                       .putHeader("content-type", "application/json")
                       .end(sensor.toJSON().encode());
            }

        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal error: " + e.getMessage());
        }
    }
}
