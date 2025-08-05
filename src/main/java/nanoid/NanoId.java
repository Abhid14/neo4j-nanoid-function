package nanoid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j user-defined function for generating Nano IDs
 */
public class NanoId {
    private static final int NANOID_DEFAULT_SIZE = 21;

    // Standard nanoid function - default size
    @UserFunction
    @Description("nanoid.generate() - generates a URL-safe unique ID (21 chars)")
    public String generate() {
        return NanoIdUtils.randomNanoId();
    }

    // Standard nanoid function - custom size
    @UserFunction
    @Description("nanoid.generateSized(size) - generates a URL-safe unique ID with custom size")
    public String generateSized(@Name("size") Long size) {
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId();
        }
        return NanoIdUtils.randomNanoId(new java.util.Random(), NanoIdUtils.DEFAULT_ALPHABET, size.intValue());
    }

    // Custom nanoid function - alphabet only (default size)
    @UserFunction
    @Description("nanoid.generateCustom(alphabet) - generates a Nano ID with custom alphabet (21 chars)")
    public String generateCustom(@Name("alphabet") String alphabet) {
        // Validate alphabet: must not be null or empty
        if (alphabet == null || alphabet.trim().isEmpty()) {
            return NanoIdUtils.randomNanoId();
        }

        return NanoIdUtils.randomNanoId(new java.util.Random(), alphabet.toCharArray(), NANOID_DEFAULT_SIZE);
    }

    // Custom nanoid function - alphabet and size
    @UserFunction
    @Description("nanoid.generateCustomSized(alphabet, size) - generates a Nano ID with custom alphabet and size")
    public String generateCustomSized(
            @Name("alphabet") String alphabet,
            @Name("size") Long size) {
        // Validate alphabet: must not be null or empty
        if (alphabet == null || alphabet.trim().isEmpty()) {
            return NanoIdUtils.randomNanoId();
        }

        // Use default size if not provided or invalid
        int finalSize = (size == null || size <= 0) ? NANOID_DEFAULT_SIZE : size.intValue();

        return NanoIdUtils.randomNanoId(new java.util.Random(), alphabet.toCharArray(), finalSize);
    }
}