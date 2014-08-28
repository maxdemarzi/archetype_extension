package pe.archety;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.helpers.collection.IteratorUtil;
import org.neo4j.tooling.GlobalGraphOperations;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Path("/service")
public class ArchetypeService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Path("/helloworld")
    public String helloWorld() {
        return "Hello World!";
    }

    @GET
    @Path("/warmup")
    public String warmUp(@Context GraphDatabaseService db) {
        try ( Transaction tx = db.beginTx()) {
            for ( Node n : GlobalGraphOperations.at(db).getAllNodes()) {
                n.getPropertyKeys();
                for ( Relationship relationship : n.getRelationships()) {
                    relationship.getPropertyKeys();
                    relationship.getStartNode();
                }
            }
        }
        return "Warmed up and ready to go!";
    }

    @GET
    @Path("/migrate")
    public String migrate(@Context GraphDatabaseService db) {
        boolean migrated;
        try (Transaction tx = db.beginTx()) {
            migrated = db.schema().getConstraints().iterator().hasNext();
        }

        if(migrated){
            return "Already Migrated!";
        } else {
            // Perform Migration
            try (Transaction tx = db.beginTx()) {
                Schema schema = db.schema();
                schema.constraintFor(Labels.Identity)
                        .assertPropertyIsUnique("hash")
                        .create();
                schema.constraintFor(Labels.Page)
                        .assertPropertyIsUnique("url")
                        .create();
                tx.success();
            }

            try (Transaction tx = db.beginTx()) {
                Schema schema = db.schema();
                schema.awaitIndexesOnline(1, TimeUnit.DAYS);
            }
            return "Migrated!";
        }
    }

    @GET
    @Path("/identity")
    public Response getIdentity(@DefaultValue("") @QueryParam("email") String email,
                                @DefaultValue("") @QueryParam("md5hash") String hash,
                                @Context GraphDatabaseService db) throws IOException {
        hash = Utilities.getHash(email, hash);
        try ( Transaction tx = db.beginTx()) {
            final Node identity =
                    IteratorUtil.singleOrNull(db.findNodesByLabelAndProperty(Labels.Identity, "hash", hash));
            if(identity != null) {
                return Response.ok(objectMapper.writeValueAsString(
                        Collections.singletonMap("identity", (String)identity.getProperty("hash"))
                )).build();
            } else {
                throw Exception.identityNotFound;
            }
        }

    }
}
