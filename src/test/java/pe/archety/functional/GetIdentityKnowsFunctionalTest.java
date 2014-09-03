package pe.archety.functional;

import com.sun.jersey.api.client.Client;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.server.NeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;
import org.neo4j.server.rest.JaxRsResponse;
import org.neo4j.server.rest.RestRequest;
import pe.archety.*;
import pe.archety.Exception;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;

public class GetIdentityKnowsFunctionalTest {
    private static final Client CLIENT = Client.create();
    private static NeoServer server;
    private static RestRequest request;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws IOException {
        server = CommunityServerBuilder.server()
                .withThirdPartyJaxRsPackage("pe.archety", "/v1")
                .build();
        server.start();
        request = new RestRequest(server.baseUri().resolve("/v1"), CLIENT);
        populate(server.getDatabase().getGraph());
    }

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

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void shouldGetIdentityKnowsByEmail() throws IOException {
        JaxRsResponse response = request.get("service/identity/knows?email=" + validEmail);
        ArrayList actual = objectMapper.readValue(response.getEntity(), ArrayList.class);
        assertEquals(validIdentityKnows, actual);
    }

    @Test
    public void shouldGetIdentityKnowsByHash() throws IOException {
        JaxRsResponse response = request.get("service/identity/knows?md5hash=" + validMD5Hash);
        ArrayList actual = objectMapper.readValue(response.getEntity(), ArrayList.class);
        assertEquals(validIdentityKnows, actual);
    }

    @Test
    public void shouldNotGetIdentityKnowsBecauseOfMissingQueryParameters() throws IOException {
        JaxRsResponse response = request.get("service/identity/knows");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                pe.archety.Exception.missingQueryParameters.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGetIdentityKnowsBecauseOfInvalidEmailParameter() throws IOException {
        JaxRsResponse response = request.get("service/identity/knows?email=invalid");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                pe.archety.Exception.invalidEmailParameter.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGetIdentityKnowsBecauseOfInvalidMD5HashParameter() throws IOException {
        JaxRsResponse response = request.get("service/identity/knows?md5hash=invalid");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.invalidMD5HashParameter.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotGetIdentityKnowsBecauseIdentityNotFound() throws IOException {
        JaxRsResponse response = request.get("service/identity/knows?email=" + notFoundEmail);
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.identityNotFound.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }
}
