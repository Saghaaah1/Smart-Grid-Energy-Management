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

public class UpdatePersonHandler implements Handler<RoutingContext> {

    private final EntityManager db;

    public UpdatePersonHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        String idParam = context.pathParam("id");
        int personId;

        try {
            personId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            context.response().setStatusCode(400).end("Invalid ID format.");
            return;
        }

        Person person = db.find(Person.class, personId);

        if (person == null) {
            context.response().setStatusCode(404).end("Person not found.");
            return;
        }

        JsonObject body = context.body().asJsonObject();

        try {
            db.getTransaction().begin();

            // Mise à jour du prénom / nom si présents
            if (body.containsKey("first_name")) {
                person.setFirstName(body.getString("first_name"));
            }
            if (body.containsKey("last_name")) {
                person.setLastName(body.getString("last_name"));
            }

            // Mise à jour de la grille si "grid" est présent ET non nul
            if (body.containsKey("grid") && body.getInteger("grid") != null) {
                int gridId = body.getInteger("grid");
                Grid grid = db.find(Grid.class, gridId);
                if (grid != null) {
                    person.setGrid(grid);
                }
            }

            // Mise à jour des capteurs si "owned_sensors" présent ET non nul
            if (body.containsKey("owned_sensors") && body.getJsonArray("owned_sensors") != null) {
                JsonArray sensorsArray = body.getJsonArray("owned_sensors");
                List<Sensor> sensors = sensorsArray.stream()
                        .map(Object::toString)
                        .map(Integer::parseInt)
                        .map(id -> db.find(Sensor.class, id))
                        .filter(s -> s != null)
                        .collect(Collectors.toList());

                person.setSensors(sensors);
            }

            db.merge(person);
            db.getTransaction().commit();

            context.response()
                   .putHeader("Content-Type", "application/json")
                   .setStatusCode(200)
                   .end(person.toJSON().encode());

        } catch (Exception e) {
            e.printStackTrace();
            db.getTransaction().rollback();
            context.response().setStatusCode(500).end("Error while updating person.");
        }
    }
}
