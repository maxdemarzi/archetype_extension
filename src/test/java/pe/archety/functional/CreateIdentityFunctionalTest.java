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
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static pe.archety.TestObjects.*;

public class CreateIdentityFunctionalTest {
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
        request.get("service/migrate");
        populate(server.getDatabase().getGraph());
    }

    public void populate(GraphDatabaseService db) {
        try (Transaction tx = db.beginTx()) {
            Node identity = db.createNode(Labels.Identity);
            identity.setProperty("hash", validMD5Hash2);
            tx.success();
        }
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void shouldCreateIdentityByEmail() throws IOException {
        JaxRsResponse response = request.post("service/identity?email=" + validEmail, "");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldCreateIdentityByHash() throws IOException {
        JaxRsResponse response = request.post("service/identity?md5hash=" + validMD5Hash, "");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldGetDuplicateIdentities() throws IOException {
        JaxRsResponse response = request.post("service/identity?md5hash=" + validMD5Hash2, "");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldNotCreateIdentityBecauseOfMissingQueryParameters() throws IOException {
        JaxRsResponse response = request.post("service/identity", "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                pe.archety.Exception.missingQueryParameters.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotCreateIdentityBecauseOfInvalidEmailParameter() throws IOException {
        JaxRsResponse response = request.post("service/identity?email=invalid", "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.invalidEmailParameter.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotCreateIdentityBecauseOfInvalidMD5HashParameter() throws IOException {
        JaxRsResponse response = request.post("service/identity?md5hash=invalid", "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.invalidMD5HashParameter.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotCreateIdentityBecauseOfError() throws IOException {
        server.getDatabase().getGraph().shutdown();
        JaxRsResponse response = request.post("service/identity?md5hash=" + validMD5Hash2, "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.identityNotCreated.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }
}
