package pe.archety.unit;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;
import pe.archety.*;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;

public class GetIdentityKnowsTest {
    private ArchetypeService service;
    private GraphDatabaseService db;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node identity = db.createNode(Labels.Identity);
            identity.setProperty("hash", validMD5Hash);

            Node identity2 = db.createNode(Labels.Identity);
            identity2.setProperty("hash", validMD5Hash2);
            identity.createRelationshipTo(identity2, RelationshipTypes.KNOWS);

            Node identity3 = db.createNode(Labels.Identity);
            identity3.setProperty("hash", validMD5Hash3);
            identity.createRelationshipTo(identity3, RelationshipTypes.KNOWS);

            tx.success();
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        service = new ArchetypeService();
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        populate(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void shouldGetIdentityKnowsByEmail() throws IOException {
        Response response = service.getIdentityKnows(validEmail, "", db);
        ArrayList actual = objectMapper.readValue((String)response.getEntity(), ArrayList.class);
        assertEquals(validIdentityKnows, actual);
    }

    @Test
    public void shouldGetIdentityKnowsByHash() throws IOException {
        Response response = service.getIdentityKnows("", validMD5Hash, db);
        ArrayList actual = objectMapper.readValue((String)response.getEntity(), ArrayList.class);
        assertEquals(validIdentityKnows, actual);
    }

    @Test
    public void shouldThrowBecauseOfIdentityNotFound() throws IOException {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(pe.archety.Exception.identityNotFound.getMessage());
        service.getIdentityKnows(notFoundEmail, "", db);
    }

}
