package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.DataPoint;
import fr.imta.smartgrid.model.Measurement;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetMeasurementValuesHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetMeasurementValuesHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        try {
            int id = Integer.parseInt(context.pathParam("id"));
            long from = 0;
            long to = 2147483646L;

            if (context.queryParam("from").size() > 0) {
                from = Long.parseLong(context.queryParam("from").get(0));
            }
            if (context.queryParam("to").size() > 0) {
                to = Long.parseLong(context.queryParam("to").get(0));
            }

            Measurement m = em.find(Measurement.class, id);

            if (m == null) {
                context.response().setStatusCode(404).end("Measurement not found");
                return;
            }

            JsonArray values = new JsonArray();
            for (DataPoint dp : m.getDatapoints()) {
                long ts = dp.getTimestamp();
                if (ts >= from && ts <= to) {
                    JsonObject val = new JsonObject();
                    val.put("timestamp", ts);
                    val.put("value", dp.getValue());
                    values.add(val);
                }
            }

            JsonObject result = new JsonObject();
            result.put("sensor_id", m.getSensor().getId());
            result.put("measurement_id", m.getId());
            result.put("values", values);

            context.response()
                   .putHeader("content-type", "application/json")
                   .end(result.encode());

        } catch (Exception e) {
            context.response().setStatusCode(500).end("Error: " + e.getMessage());
        }
    }
}
