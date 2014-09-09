package pe.archety.unit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pe.archety.*;
import pe.archety.Exception;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static pe.archety.TestObjects.*;

public class PageTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldValidateURL() {
        assertTrue(Page.isValidURL(validURL));
        assertFalse(Page.isValidURL("not a valid url"));
    }

    @Test
    public void shouldGetValidURL() {
        assertTrue(Page.getValidURL(validURL) != null);
    }

    @Test
    public void shouldThrowBecauseOfInvalidURL()  {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(Exception.invalidURL.getMessage());
        Page.getValidURL("not a valid url");
    }

    @Test
    public void shouldValidateWikipediaURL() {
        assertTrue(Page.isValidWikipediaURL(Page.getValidURL(validURL)));
        assertFalse(Page.isValidWikipediaURL(Page.getValidURL(invalidWikipediaURL)));
    }

    @Test
    public void shouldGetPageURL() throws IOException {
        assertTrue(Page.getPageURL(validURL).equals(validURL));
    }

    @Test
    public void shouldThrowBecauseOfInvalidWikipediaURL() throws IOException {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(Exception.invalidWikipediaURL.getMessage());
        Page.getPageURL(invalidWikipediaURL);
    }

    @Test
    public void shouldThrowBecauseOfwikipediaURLNotFound() throws IOException {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(Exception.wikipediaURLNotFound.getMessage());
        Page.getPageURL(validURL + "invalid");
    }
}
