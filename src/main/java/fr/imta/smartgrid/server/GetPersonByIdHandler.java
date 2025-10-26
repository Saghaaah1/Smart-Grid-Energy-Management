package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Person;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetPersonByIdHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetPersonByIdHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        String idParam = context.pathParam("id");
        int id = Integer.parseInt(idParam);

        Person p = em.find(Person.class, id);

        if (p == null) {
            context.response().setStatusCode(404).end("Person not found");
        } else {
            JsonObject json = new JsonObject();
            json.put("id", p.getId());
            json.put("first_name", p.getFirstName());
            json.put("last_name", p.getLastName());
            json.put("grid", p.getGrid() != null ? p.getGrid().getId() : null);

            JsonArray sensorIds = new JsonArray();
            for (var s : p.getSensors()) {
                sensorIds.add(s.getId());
            }
            json.put("owned_sensors", sensorIds);

            context.response()
                   .putHeader("content-type", "application/json")
                   .end(json.encode());
        }
    }
}
