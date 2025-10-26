package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetGridByIdHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetGridByIdHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        try {
            int id = Integer.parseInt(context.pathParam("id"));

            Grid grid = em.find(Grid.class, id);

            if (grid == null) {
                context.response().setStatusCode(404).end("Grid not found");
                return;
            }

            JsonObject json = new JsonObject();
            json.put("id", grid.getId());
            json.put("name", grid.getName());
            json.put("description", grid.getDescription());

            JsonArray users = new JsonArray();
            for (Person p : grid.getPersons()) {
                users.add(p.getId());
            }
            json.put("users", users);

            JsonArray sensors = new JsonArray();
            for (Sensor s : grid.getSensors()) {
                sensors.add(s.getId());
            }
            json.put("sensors", sensors);

            context.response()
                   .putHeader("content-type", "application/json")
                   .end(json.encode());

        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal error: " + e.getMessage());
        }
    }
}
