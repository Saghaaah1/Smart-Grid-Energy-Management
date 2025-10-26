package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Producer;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetGridProductionHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetGridProductionHandler(EntityManager em) {
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

            double totalProduction = 0.0;
            // On parcourt tous les capteurs de la grid
            for (Sensor sensor : grid.getSensors()) {
                if (sensor instanceof Producer) {
                    Producer producer = (Producer) sensor;
                    // Calcule la production en ajoutant les DataPoint
                    for (var measurement : producer.getMeasurements()) {
                        for (var dataPoint : measurement.getDatapoints()) {
                            totalProduction += dataPoint.getValue();  // Additionner les valeurs
                        }
                    }
                }
            }

            context.response()
                   .putHeader("content-type", "application/json")
                   .end(Double.toString(totalProduction));

        } catch (Exception e) {
            context.response().setStatusCode(500).end("Error: " + e.getMessage());
        }
    }
}
