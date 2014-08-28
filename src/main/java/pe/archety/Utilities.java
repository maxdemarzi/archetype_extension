package pe.archety;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.EmailValidator;

public class Utilities {

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidMD5(String hash) {
        return hash.matches("[a-f0-9]{32}");
    }

    public static String calculateHash(String input) {
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher()
                .putString(input.toLowerCase(), Charsets.UTF_8)
                .hash();
        return hc.toString();
    }

    public static String getHash(String email, String hash) {
        if(hash.isEmpty()) {
            if(email.isEmpty()) {
                throw Exception.missingQueryParameters;
            } else {
                if(isValidEmail(email)){
                    return calculateHash(email);
                } else {
                    throw Exception.invalidEmailParameter;
                }
            }
        } else {
            hash = hash.toLowerCase();
            if(isValidMD5(hash)) {
                return hash;
            } else {
                throw Exception.invalidMD5HashParameter;
            }
        }
    }
}
