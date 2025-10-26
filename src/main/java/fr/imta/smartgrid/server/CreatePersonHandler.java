package fr.imta.smartgrid.server;

import java.util.List;
import java.util.stream.Collectors;

import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class CreatePersonHandler implements Handler<RoutingContext> {

    private final EntityManager db;

    public CreatePersonHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        JsonObject body = context.body().asJsonObject();

        if (body == null || !body.containsKey("first_name") || !body.containsKey("last_name") || !body.containsKey("grid")) {
            context.response().setStatusCode(500).end("Missing JSON body or required fields.");
            return;
        }

        String firstName = body.getString("first_name");
        String lastName = body.getString("last_name");
        int gridId = body.getInteger("grid");

        try {
            db.getTransaction().begin();

            Grid grid = db.find(Grid.class, gridId);
            if (grid == null) {
                db.getTransaction().rollback();
                context.response().setStatusCode(500).end("Grid not found.");
                return;
            }

            Person person = new Person();
            person.setFirstName(firstName);
            person.setLastName(lastName);
            person.setGrid(grid);

            // S'il y a des capteurs à rattacher
            if (body.containsKey("owned_sensors")) {
                JsonArray sensorIds = body.getJsonArray("owned_sensors");
                List<Sensor> sensors = sensorIds.stream()
                        .map(Object::toString)
                        .map(Integer::parseInt)
                        .map(id -> db.find(Sensor.class, id))
                        .filter(s -> s != null)
                        .collect(Collectors.toList());
                person.setSensors(sensors);
            }

            db.persist(person);
            db.getTransaction().commit();

            JsonObject response = new JsonObject().put("id", person.getId());
            context.response()
                   .putHeader("Content-Type", "application/json")
                   .setStatusCode(201)
                   .end(response.encode());

        } catch (Exception e) {
            e.printStackTrace();
            db.getTransaction().rollback();
            context.response().setStatusCode(500).end("Error while creating person.");
        }
    }
}
