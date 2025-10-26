package fr.imta.smartgrid.server;

import java.util.List;
import java.util.stream.Collectors;

import fr.imta.smartgrid.model.Consumer;
import fr.imta.smartgrid.model.EVCharger;
import fr.imta.smartgrid.model.Grid;
import fr.imta.smartgrid.model.Person;
import fr.imta.smartgrid.model.Producer;
import fr.imta.smartgrid.model.Sensor;
import fr.imta.smartgrid.model.SolarPanel;
import fr.imta.smartgrid.model.WindTurbine;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class UpdateSensorHandler implements Handler<RoutingContext> {

    private final EntityManager db;

    public UpdateSensorHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        // Récupération de l'ID depuis path param ou query param
        String idParam = context.pathParam("id");
        if (idParam == null) {
            idParam = context.request().getParam("id");
        }
        if (idParam == null) {
            context.response()
                   .setStatusCode(400)
                   .end("Missing sensor ID.");
            return;
        }

        int sensorId;
        try {
            sensorId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            context.response()
                   .setStatusCode(400)
                   .end("Invalid sensor ID format.");
            return;
        }

        Sensor sensor = db.find(Sensor.class, sensorId);
        if (sensor == null) {
            context.response()
                   .setStatusCode(404)
                   .end("Sensor not found.");
            return;
        }

        JsonObject body = context.body().asJsonObject();

        try {
            db.getTransaction().begin();

            // Champs génériques
            if (body.containsKey("name")) {
                sensor.setName(body.getString("name"));
            }
            if (body.containsKey("description")) {
                sensor.setDescription(body.getString("description"));
            }

            // Mise à jour de la grille si présente
            if (body.containsKey("grid")) {
                Integer gridId = safeGetInt(body, "grid");
                if (gridId != null) {
                    Grid g = db.find(Grid.class, gridId);
                    if (g != null) {
                        sensor.setGrid(g);
                    }
                }
            }

            // Propriétaires ("owners" ou "owned_sensors")
            JsonArray ownerIds = null;
            if (body.containsKey("owners")) {
                ownerIds = body.getJsonArray("owners");
            } else if (body.containsKey("owned_sensors")) {
                ownerIds = body.getJsonArray("owned_sensors");
            }
            if (ownerIds != null) {
                List<Person> owners = ownerIds.stream()
                        .map(Object::toString)
                        .map(Integer::parseInt)
                        .map(id -> db.find(Person.class, id))
                        .filter(p -> p != null)
                        .collect(Collectors.toList());
                sensor.setOwners(owners);
            }

            // Producteur
            if (sensor instanceof Producer) {
                if (body.containsKey("power_source")) {
                    ((Producer) sensor).setPowerSource(body.getString("power_source"));
                }
            }

            // Consommateur
            if (sensor instanceof Consumer) {
                Double maxPower = safeGetDouble(body, "max_power", "maxPower");
                if (maxPower != null) {
                    ((Consumer) sensor).setMaxPower(maxPower);
                }
            }

            // Spécifique : éolienne
            if (sensor instanceof WindTurbine) {
                WindTurbine wt = (WindTurbine) sensor;
                Double height = safeGetDouble(body, "height");
                if (height != null) wt.setHeight(height);
                Double bladeLength = safeGetDouble(body, "blade_length", "bladeLength");
                if (bladeLength != null) wt.setBladeLength(bladeLength);
            }

            // Spécifique : panneau solaire
            if (sensor instanceof SolarPanel) {
                SolarPanel sp = (SolarPanel) sensor;
                Double eff = safeGetDouble(body, "efficiency");
                if (eff != null) sp.setEfficiency(eff);
            }

            // Spécifique : borne EV
            if (sensor instanceof EVCharger) {
                EVCharger ev = (EVCharger) sensor;
                String type = body.getString("type", body.getString("connector_type", null));
                if (type != null) {
                    ev.setType(type);
                }
                Integer voltage = safeGetInt(body, "voltage", "voltage_level");
                if (voltage != null) {
                    ev.setVoltage(voltage);
                }
                Integer maxAmp = safeGetInt(body, "maxAmp", "max_amp");
                if (maxAmp != null) {
                    ev.setMaxAmp(maxAmp);
                }
            }

            db.merge(sensor);
            db.getTransaction().commit();

            context.response()
                   .putHeader("Content-Type", "application/json")
                   .setStatusCode(200)
                   .end(sensor.toJSON().encode());

        } catch (Exception e) {
            e.printStackTrace();
            db.getTransaction().rollback();
            context.response().setStatusCode(500).end("Error while updating sensor.");
        }
    }

    private Double safeGetDouble(JsonObject body, String... keys) {
        for (String key : keys) {
            if (body.containsKey(key)) {
                Object val = body.getValue(key);
                if (val instanceof Number) return ((Number) val).doubleValue();
            }
        }
        return null;
    }

    private Integer safeGetInt(JsonObject body, String... keys) {
        for (String key : keys) {
            if (body.containsKey(key)) {
                Object val = body.getValue(key);
                if (val instanceof Number) return ((Number) val).intValue();
            }
        }
        return null;
    }
}
