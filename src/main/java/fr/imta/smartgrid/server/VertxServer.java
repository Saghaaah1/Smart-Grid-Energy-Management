package fr.imta.smartgrid.server;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.persistence.config.PersistenceUnitProperties.CONNECTION_POOL_MIN;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_SERVER;
import org.eclipse.persistence.config.TargetServer;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

public class VertxServer {
    private Vertx vertx;
    private EntityManager db; 

    public VertxServer() {
        this.vertx = Vertx.vertx();

        // setup database connexion
        Map<String, String> properties = new HashMap<>();

        properties.put(LOGGING_LEVEL, "FINE");
        properties.put(CONNECTION_POOL_MIN, "1");

        properties.put(TARGET_SERVER, TargetServer.None);

        var emf = Persistence.createEntityManagerFactory("smart-grid", properties);
        db = emf.createEntityManager();
    }

    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());


        router.get("/hello").handler(new ExampleHandler(this.db));
        router.get("/grids").handler(new GetGridIdsHandler(this.db));
        router.get("/persons").handler(new GetPersonIdsHandler(this.db));
        router.get("/person/:id").handler(new GetPersonByIdHandler(this.db));
        router.get("/sensor/:id").handler(new GetSensorByIdHandler(this.db));
        router.get("/grid/:id").handler(new GetGridByIdHandler(this.db));
        router.get("/sensors/:kind").handler(new GetSensorsByKindHandler(this.db));
        router.get("/producers").handler(new GetProducersHandler(this.db));
        router.get("/consumers").handler(new GetConsumersHandler(this.db));
        router.get("/measurement/:id").handler(new GetMeasurementByIdHandler(this.db));
        router.get("/measurement/:id/values").handler(new GetMeasurementValuesHandler(this.db));
        router.get("/grid/:id/production").handler(new GetGridProductionHandler(this.db));
        router.get("/grid/:id/consumption").handler(new GetGridConsumptionHandler(this.db));
        router.post("/ingress/windturbine").handler(new WindTurbineIngressHandler());
        new UDPHandler(vertx).start();

        router.post("/person/:id").handler(new UpdatePersonHandler(this.db));
        router.delete("/person/:id").handler(new DeletePersonHandler(this.db));
        router.put("/person").handler(new CreatePersonHandler(this.db));
        router.post("/sensor/:id").handler(new UpdateSensorHandler(this.db));
        router.post("/sensors/update").handler(new UpdateSensorHandler(this.db));
        
        vertx.createHttpServer().requestHandler(router).listen(8080);





        // Démarrage du serveur
        vertx.createHttpServer().requestHandler(router).listen(8080);
    }


    public static void main(String[] args) {
        new VertxServer().start();
    }
}
