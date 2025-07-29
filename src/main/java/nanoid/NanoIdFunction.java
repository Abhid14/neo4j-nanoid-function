package nanoid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j user-defined function for generating NanoIDs
 */
public class NanoIdFunction {

    // Standard alphabet without underscore and dash
    private static final char[] STANDARD_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final int DEFAULT_SIZE = 21;

    @UserFunction("nanoid")
    @Description("nanoid() - generates a standard unique ID using alphanumeric characters only")
    public String nanoid() {
        return NanoIdUtils.randomNanoId(new java.util.Random(), STANDARD_ALPHABET, DEFAULT_SIZE);
    }

    @UserFunction("nanoid")
    @Description("nanoid(size) - generates a standard unique ID with custom size using alphanumeric characters")
    public String nanoidSized(@Name("size") Long size) {
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId(new java.util.Random(), STANDARD_ALPHABET, DEFAULT_SIZE);
        }
        return NanoIdUtils.randomNanoId(new java.util.Random(), STANDARD_ALPHABET, size.intValue());
    }

    @UserFunction("nanoid.standard")
    @Description("nanoid.standard() - generates a URL-safe unique ID including underscore and dash")
    public String standardNanoid() {
        return NanoIdUtils.randomNanoId();
    }

    @UserFunction("nanoid.standard")
    @Description("nanoid.standard(size) - generates a URL-safe unique ID with custom size including underscore and dash")
    public String standardNanoidSized(@Name("size") Long size) {
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId();
        }
        return NanoIdUtils.randomNanoId(new java.util.Random(), NanoIdUtils.DEFAULT_ALPHABET, size.intValue());
    }

    @UserFunction("nanoid.custom")
    @Description("nanoid.custom(alphabet, size) - generates a NanoID with custom alphabet and size")
    public String customNanoid(
            @Name("alphabet") String alphabet,
            @Name("size") Long size) {
        // Validate alphabet: must not be null or empty
        if (alphabet == null || alphabet.trim().isEmpty()) {
            return NanoIdUtils.randomNanoId(new java.util.Random(), STANDARD_ALPHABET, DEFAULT_SIZE);
        }
        
        // Validate size: must be positive
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId(new java.util.Random(), STANDARD_ALPHABET, DEFAULT_SIZE);
        }
        
        return NanoIdUtils.randomNanoId(new java.util.Random(), alphabet.toCharArray(), size.intValue());
    }
}