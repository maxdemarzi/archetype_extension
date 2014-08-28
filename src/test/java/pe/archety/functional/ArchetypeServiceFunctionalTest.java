package pe.archety.functional;

import com.sun.jersey.api.client.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.server.NeoServer;
import org.neo4j.server.helpers.CommunityServerBuilder;
import org.neo4j.server.rest.JaxRsResponse;
import org.neo4j.server.rest.RestRequest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ArchetypeServiceFunctionalTest {
    private static final Client CLIENT = Client.create();
    private static NeoServer server;
    private static RestRequest request;

    @Before
    public void setUp() throws IOException {
        server = CommunityServerBuilder.server()
                .withThirdPartyJaxRsPackage("pe.archety", "/v1")
                .build();
        server.start();
        request = new RestRequest(server.baseUri().resolve("/v1"), CLIENT);
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void shouldRespondToHelloWorld() {
        JaxRsResponse response = request.get("service/helloworld");
        assertEquals("Hello World!", response.getEntity());
    }

    @Test
    public void shouldWarmUp() {
        JaxRsResponse response = request.get("service/warmup");
        assertEquals("Warmed up and ready to go!", response.getEntity());
    }

    @Test
    public void shouldMigrate() {
        JaxRsResponse response = request.get("service/migrate");
        assertEquals("Migrated!", response.getEntity());
        response = request.get("service/migrate");
        assertEquals("Already Migrated!", response.getEntity());
    }

}
