package nanoid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j user-defined function for generating NanoIDs
 */
public class NanoIdFunction {

    // Alphanumeric alphabet without underscore and dash (for quickid)
    private static final char[] ALPHANUMERIC_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            .toCharArray();
    private static final int QUICKID_DEFAULT_SIZE = 14;
    private static final int NANOID_DEFAULT_SIZE = 21;

    // quickid functions - alphanumeric only
    @UserFunction("quickid")
    @Description("quickid() - generates a unique ID using alphanumeric characters only (14 chars)")
    public String quickidGenerate() {
        return NanoIdUtils.randomNanoId(new java.util.Random(), ALPHANUMERIC_ALPHABET, QUICKID_DEFAULT_SIZE);
    }

    @UserFunction("quickid.generateSized")
    @Description("quickid.generateSized(size) - generates a unique ID with custom size using alphanumeric characters only")
    public String quickidGenerateSized(@Name("size") Long size) {
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId(new java.util.Random(), ALPHANUMERIC_ALPHABET, QUICKID_DEFAULT_SIZE);
        }
        return NanoIdUtils.randomNanoId(new java.util.Random(), ALPHANUMERIC_ALPHABET, size.intValue());
    }

    // Standard nanoid function - default size
    @UserFunction("nanoid")
    @Description("nanoid() - generates a URL-safe unique ID (21 chars)")
    public String nanoidGenerate() {
        return NanoIdUtils.randomNanoId();
    }

    // Standard nanoid function - custom size
    @UserFunction("nanoid.generateSized")
    @Description("nanoid.generateSized(size) - generates a URL-safe unique ID with custom size")
    public String nanoidGenerateSized(@Name("size") Long size) {
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId();
        }
        return NanoIdUtils.randomNanoId(new java.util.Random(), NanoIdUtils.DEFAULT_ALPHABET, size.intValue());
    }

    // Custom nanoid function - alphabet only (default size)
    @UserFunction("nanoid.generateCustom")
    @Description("nanoid.generateCustom(alphabet) - generates a NanoID with custom alphabet (21 chars)")
    public String nanoidGenerateCustom(@Name("alphabet") String alphabet) {
        // Validate alphabet: must not be null or empty
        if (alphabet == null || alphabet.trim().isEmpty()) {
            return NanoIdUtils.randomNanoId();
        }

        return NanoIdUtils.randomNanoId(new java.util.Random(), alphabet.toCharArray(), NANOID_DEFAULT_SIZE);
    }

    // Custom nanoid function - alphabet and size
    @UserFunction("nanoid.generateCustomSized")
    @Description("nanoid.generateCustomSized(alphabet, size) - generates a NanoID with custom alphabet and size")
    public String nanoidGenerateCustomSized(
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