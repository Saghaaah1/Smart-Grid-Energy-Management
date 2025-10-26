package fr.imta.smartgrid.server;

import fr.imta.smartgrid.model.Person;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class DeletePersonHandler implements Handler<RoutingContext> {

    private final EntityManager db;

    public DeletePersonHandler(EntityManager db) {
        this.db = db;
    }

    @Override
    public void handle(RoutingContext context) {
        String idParam = context.pathParam("id");
        int personId;

        try {
            personId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            System.err.println(" Invalid ID format: " + idParam);
            context.response().setStatusCode(400).end("Invalid ID format.");
            return;
        }

        Person person = db.find(Person.class, personId);

        if (person == null) {
            System.out.println(" Person with ID " + personId + " not found.");
            context.response().setStatusCode(404).end("Person not found.");
            return;
        }

        try {
            db.getTransaction().begin();
            db.remove(person);
            db.getTransaction().commit();

            System.out.println(" Person ID " + personId + " successfully deleted.");
            context.response()
                   .putHeader("Content-Type", "text/plain")
                   .setStatusCode(200)
                   .end("Person deleted.");
        } catch (Exception e) {
            e.printStackTrace();
            db.getTransaction().rollback();
            context.response().setStatusCode(500).end("Error while deleting person.");
        }
    }
}
