package quickid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j user-defined function for generating Quick IDs using Nano ID
 */
public class QuickId {

    // Alphanumeric alphabet without underscore and dash (for quickid)
    private static final char[] ALPHANUMERIC_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            .toCharArray();
    private static final int QUICKID_DEFAULT_SIZE = 14;

    // quickid functions - alphanumeric only
    @UserFunction
    @Description("quickid.generate() - generates a unique ID using alphanumeric characters only (14 chars)")
    public String generate() {
        return NanoIdUtils.randomNanoId(new java.util.Random(), ALPHANUMERIC_ALPHABET, QUICKID_DEFAULT_SIZE);
    }

    @UserFunction
    @Description("quickid.generateSized(size) - generates a unique ID with custom size using alphanumeric characters only")
    public String generateSized(@Name("size") Long size) {
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId(new java.util.Random(), ALPHANUMERIC_ALPHABET, QUICKID_DEFAULT_SIZE);
        }
        return NanoIdUtils.randomNanoId(new java.util.Random(), ALPHANUMERIC_ALPHABET, size.intValue());
    }
}