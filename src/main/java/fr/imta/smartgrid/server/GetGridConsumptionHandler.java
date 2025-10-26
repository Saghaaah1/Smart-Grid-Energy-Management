package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Consumer;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Sensor;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetGridConsumptionHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetGridConsumptionHandler(EntityManager em) {
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

            double totalConsumption = 0.0;
            // On parcourt tous les capteurs de la grid
            for (Sensor sensor : grid.getSensors()) {
                if (sensor instanceof Consumer) {
                    Consumer consumer = (Consumer) sensor;
                    // Calcule la consommation en ajoutant les DataPoint
                    for (var measurement : consumer.getMeasurements()) {
                        for (var dataPoint : measurement.getDatapoints()) {
                            totalConsumption += dataPoint.getValue();  
                        }
                    }
                }
            }

            context.response()
                   .putHeader("content-type", "application/json")
                   .end(Double.toString(totalConsumption));

        } catch (Exception e) {
            context.response().setStatusCode(500).end("Error: " + e.getMessage());
        }
    }
}
