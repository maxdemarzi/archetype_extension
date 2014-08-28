package pe.archety;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Exception extends WebApplicationException {

    public Exception(int code, String error) {
        super(new Throwable(error), Response.status(code)
        .entity("{\"error\":\""+ error + "\"}")
        .type(MediaType.APPLICATION_JSON)
        .build());
    }

    public static Exception missingQueryParameters = new Exception(400, "Missing Query Parameters.");
    public static Exception invalidEmailParameter = new Exception(400, "Invalid E-mail Parameter.");
    public static Exception invalidMD5HashParameter = new Exception(400, "Invalid MD5Hash Parameter.");
    public static Exception identityNotFound = new Exception(404, "Identity not found.");
    public static Exception pageNotFound = new Exception(404, "Page not found.");
}
