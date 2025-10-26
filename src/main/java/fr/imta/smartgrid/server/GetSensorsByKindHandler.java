package fr.imta.smartgrid.server;

import java.util.List;
import java.util.stream.Collectors;

import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetSensorsByKindHandler implements Handler<RoutingContext> {

    private final EntityManager db;

    public GetSensorsByKindHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        String kind = context.pathParam("kind");

        try {
            //  On sélectionne tous les capteurs (aucun champ custom)
            List<Sensor> allSensors = db.createQuery("SELECT s FROM Sensor s", Sensor.class).getResultList();

            // On filtre par type de classe en Java uniquement
            List<Integer> matchingIds = allSensors.stream()
                    .filter(s -> s.getClass().getSimpleName().equals(kind))
                    .map(Sensor::getId)
                    .collect(Collectors.toList());

            JsonArray response = new JsonArray(matchingIds);

            context.response()
                   .putHeader("Content-Type", "application/json")
                   .setStatusCode(200)
                   .end(response.encode());

        } catch (Exception e) {
            e.printStackTrace();
            context.response().setStatusCode(500).end("Internal Server Error");
        }
    }
}
