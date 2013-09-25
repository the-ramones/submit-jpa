package sp.service.rest;

import javax.persistence.EntityManager;
import sp.model.Report;
import java.net.URI;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST service facade
 *
 * @author Paul Kulitski
 */
@Path("sp/model/report")
@com.sun.jersey.spi.resource.Singleton
//@com.sun.jersey.api.spring.Autowire
public class ReportRESTFacade {

    @PersistenceContext(unitName = "reportPU")
    protected EntityManager entityManager;

    public ReportRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public Response create(Report entity) {
        entityManager.persist(entity);
        return Response.created(URI.create(entity.getId().toString())).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Transactional
    public void edit(Report entity) {
        entityManager.merge(entity);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void remove(@PathParam("id") Long id) {
        Report entity = entityManager.getReference(Report.class, id);
        entityManager.remove(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public Report find(@PathParam("id") Long id) {
        return entityManager.find(Report.class, id);
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Transactional
    public List<Report> findAll() {
        return find(true, -1, -1);
    }

    @GET
    @Path("{max}/{first}")
    @Produces({"application/xml", "application/json"})
    @Transactional
    public List<Report> findRange(@PathParam("max") Integer max, @PathParam("first") Integer first) {
        return find(false, max, first);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    @Transactional
    public String count() {
        try {
            Query query = entityManager.createQuery("SELECT count(o) FROM Report AS o");
            return query.getSingleResult().toString();
        } finally {
            entityManager.close();
        }
    }

    private List<Report> find(boolean all, int maxResults, int firstResult) {
        try {
            Query query = entityManager.createQuery("SELECT object(o) FROM Report AS o");
            if (!all) {
                query.setMaxResults(maxResults);
                query.setFirstResult(firstResult);
            }
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
}
