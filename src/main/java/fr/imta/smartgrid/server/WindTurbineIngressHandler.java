package fr.imta.smartgrid.server;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class WindTurbineIngressHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext context) {
        // Récupérer les données envoyées dans la requête POST
        JsonObject body = context.body().asJsonObject();

        
        System.out.println("Request Body: " + body);

        if (body == null) {
            context.response().setStatusCode(400).end("Bad Request: Missing JSON body");
            return;
        }

        
        Integer windTurbineId = body.getInteger("windturbine");
        Long timestamp = body.getLong("timestamp");

        
        JsonObject data = body.getJsonObject("data");
        if (data == null) {
            context.response().setStatusCode(400).end("Bad Request: Missing 'data' object");
            return;
        }

        Double speed = data.getDouble("speed");
        Double power = data.getDouble("power");

    
        if (windTurbineId == null || timestamp == null || speed == null || power == null) {
            context.response().setStatusCode(400).end("Bad Request: Missing required parameters");
            return;
        }

        
        System.out.println("Received data from wind turbine:");
        System.out.println("ID: " + windTurbineId);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("Speed: " + speed);
        System.out.println("Power: " + power);

        // Réponse
        context.response().putHeader("Content-Type", "application/json").end("{\"status\": \"success\"}");
    }
}


