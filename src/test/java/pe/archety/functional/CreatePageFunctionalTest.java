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

public class CreatePageFunctionalTest {
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
            Node page = db.createNode(Labels.Page);
            page.setProperty("url", validURL2);
            tx.success();
        }
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void shouldCreatePage() throws IOException {
        JaxRsResponse response = request.post("service/page?url=" + validURL, "");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldGetDuplicatePages() throws IOException {
        JaxRsResponse response = request.post("service/page?url=" + validURL2, "");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void shouldNotCreatePageBecauseOfMissingQueryParameters() throws IOException {
        JaxRsResponse response = request.post("service/page", "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                pe.archety.Exception.missingQueryParameters.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotCreatePageBecauseOfInvalidURL() throws IOException {
        JaxRsResponse response = request.post("service/page?url=invalid", "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.invalidURL.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotCreatePageBecauseOfInvalidWikipediaURL() throws IOException {
        JaxRsResponse response = request.post("service/page?url=" + invalidWikipediaURL, "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.invalidWikipediaURL.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotCreatePageBecauseWikipediaURLNotFound() throws IOException {
        JaxRsResponse response = request.post("service/page?url=" + validURL + "invalid", "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.wikipediaURLNotFound.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }
    @Test
    public void shouldNotCreatePageBecauseOfError() throws IOException {
        server.getDatabase().getGraph().shutdown();
        JaxRsResponse response = request.post("service/page?url=" + validURL, "");
        HashMap actual = objectMapper.readValue(response.getEntity(), HashMap.class);
        HashMap expected = objectMapper.readValue(
                Exception.pageNotCreated.getResponse().getEntity().toString(), HashMap.class);
        assertEquals(expected, actual);
    }
}
