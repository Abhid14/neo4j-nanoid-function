package nanoid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j user-defined function for generating NanoIDs
 */
public class NanoIdFunction {

    private static final int NANOID_DEFAULT_SIZE = 21;

    // nanoid functions - standard URL-safe behavior
    @UserFunction("nanoid")
    @Description("nanoid() - generates a URL-safe unique ID (21 chars default)")
    public String nanoid() {
        return NanoIdUtils.randomNanoId();
    }

    @UserFunction("nanoid")
    @Description("nanoid(size) - generates a URL-safe unique ID with custom size")
    public String nanoidSized(@Name("size") Long size) {
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId();
        }
        return NanoIdUtils.randomNanoId(new java.util.Random(), NanoIdUtils.DEFAULT_ALPHABET, size.intValue());
    }

    // nanoid.custom functions - fully customizable
    @UserFunction("nanoid.custom")
    @Description("nanoid.custom(alphabet) - generates a NanoID with custom alphabet (21 chars default)")
    public String nanoidCustomAlphabet(@Name("alphabet") String alphabet) {
        // Validate alphabet: must not be null or empty
        if (alphabet == null || alphabet.trim().isEmpty()) {
            return NanoIdUtils.randomNanoId();
        }
        
        return NanoIdUtils.randomNanoId(new java.util.Random(), alphabet.toCharArray(), NANOID_DEFAULT_SIZE);
    }

    @UserFunction("nanoid.custom")
    @Description("nanoid.custom(alphabet, size) - generates a NanoID with custom alphabet and size")
    public String nanoidFullyCustom(
            @Name("alphabet") String alphabet,
            @Name("size") Long size) {
        // Validate alphabet: must not be null or empty
        if (alphabet == null || alphabet.trim().isEmpty()) {
            return NanoIdUtils.randomNanoId();
        }
        
        // Validate size: must be positive
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId(new java.util.Random(), alphabet.toCharArray(), NANOID_DEFAULT_SIZE);
        }
        
        return NanoIdUtils.randomNanoId(new java.util.Random(), alphabet.toCharArray(), size.intValue());
    }
}