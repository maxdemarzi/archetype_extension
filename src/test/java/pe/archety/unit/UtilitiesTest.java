package pe.archety.unit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pe.archety.*;
import pe.archety.Exception;

import static org.junit.Assert.*;
import static pe.archety.TestObjects.*;

public class UtilitiesTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldCalculateHash() {
        assertEquals(validMD5Hash, Utilities.calculateHash(validEmail));
    }

    @Test
    public void shouldValidateMD5Hash() {
        assertTrue(Utilities.isValidMD5(validMD5Hash));
        assertFalse(Utilities.isValidMD5("not a valid hash"));
    }

    @Test
    public void shouldGetValidHash() {
        assertEquals(validMD5Hash, Utilities.getHash(validEmail, ""));
        assertEquals(validMD5Hash, Utilities.getHash(validEmail.toUpperCase(), ""));
        assertEquals(validMD5Hash, Utilities.getHash("", validMD5Hash));
        assertEquals(validMD5Hash, Utilities.getHash("", validMD5Hash.toUpperCase()));
    }

    @Test
    public void shouldThrowBecauseOfMissingQueryParameters() {
        thrown.expect(pe.archety.Exception.class);
        thrown.expectMessage(Exception.missingQueryParameters.getMessage());
        Utilities.getHash("","");
    }

    @Test
    public void shouldThrowBecauseOfInvalidEmailParameter() {
        thrown.expect(Exception.class);
        thrown.expectMessage(Exception.invalidEmailParameter.getMessage());
        Utilities.getHash("invalid email","");
    }

    @Test
    public void shouldThrowBecauseOfInvalidMD5HashParameter() {
        thrown.expect(Exception.class);
        thrown.expectMessage(Exception.invalidMD5HashParameter.getMessage());
        Utilities.getHash("","invalid md5 hash");
    }
}
