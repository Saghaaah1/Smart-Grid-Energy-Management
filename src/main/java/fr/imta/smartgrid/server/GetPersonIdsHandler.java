package fr.imta.smartgrid.server;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonArray;

import jakarta.persistence.EntityManager;
import fr.imta.smartgrid.model.Person;

import java.util.List;

public class GetPersonIdsHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetPersonIdsHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        List<Person> persons = em.createQuery("SELECT p FROM Person p", Person.class).getResultList();

        JsonArray result = new JsonArray();
        for (Person p : persons) {
            result.add(p.getId());
        }

        context.response()
               .putHeader("content-type", "application/json")
               .end(result.encode());
    }
}
