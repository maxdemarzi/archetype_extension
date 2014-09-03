package pe.archety.unit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pe.archety.*;
import pe.archety.Exception;

import static org.junit.Assert.*;
import static pe.archety.TestObjects.*;

public class IdentityTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldCalculateHash() {
        assertEquals(validMD5Hash, Identity.calculateHash(validEmail));
    }

    @Test
    public void shouldValidateMD5Hash() {
        assertTrue(Identity.isValidMD5(validMD5Hash));
        assertFalse(Identity.isValidMD5("not a valid hash"));
    }

    @Test
    public void shouldGetValidHash() {
        assertEquals(validMD5Hash, Identity.getHash(validEmail, ""));
        assertEquals(validMD5Hash, Identity.getHash(validEmail.toUpperCase(), ""));
        assertEquals(validMD5Hash, Identity.getHash("", validMD5Hash));
        assertEquals(validMD5Hash, Identity.getHash("", validMD5Hash.toUpperCase()));
    }

    @Test
    public void shouldThrowBecauseOfMissingQueryParameters() {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(Exception.missingQueryParameters.getMessage());
        Identity.getHash("", "");
    }

    @Test
    public void shouldThrowBecauseOfInvalidEmailParameter() {
        thrown.expect(Exception.class);
        thrown.expectMessage(Exception.invalidEmailParameter.getMessage());
        Identity.getHash("invalid email", "");
    }

    @Test
    public void shouldThrowBecauseOfInvalidMD5HashParameter() {
        thrown.expect(Exception.class);
        thrown.expectMessage(Exception.invalidMD5HashParameter.getMessage());
        Identity.getHash("", "invalid md5 hash");
    }
}
