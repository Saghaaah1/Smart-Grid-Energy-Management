package fr.imta.smartgrid.server;

import java.util.List;

import fr.imta.smartgrid.model.Grid;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import jakarta.persistence.EntityManager;

public class GetGridIdsHandler implements Handler<RoutingContext> {

    private EntityManager em;

    public GetGridIdsHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public void handle(RoutingContext context) {
        // Requête JPQL pour récupérer toutes les grilles
        List<Grid> grids = em.createQuery("SELECT g FROM Grid g", Grid.class).getResultList();

        // On crée une liste JSON vide
        JsonArray result = new JsonArray();

        // On ajoute chaque ID dans la liste
        for (Grid g : grids) {
            result.add(g.getId());  // on ajoute juste l'ID, pas l'objet complet
        }

        // On renvoie la réponse JSON au client
        context.response()
               .putHeader("content-type", "application/json")
               .end(result.encode());
    }
}
