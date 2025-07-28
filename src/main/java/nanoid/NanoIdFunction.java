package nanoid;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

/**
 * Neo4j user-defined function for generating NanoIDs
 */
public class NanoIdFunction {

    @UserFunction("org.neo4j.nanoid.generate")
    @Description("org.neo4j.nanoid.generate() - generates a URL-safe, unique ID using NanoID")
    public String nanoid() {
        return NanoIdUtils.randomNanoId();
    }

    @UserFunction("org.neo4j.nanoid.custom")
    @Description("org.neo4j.nanoid.custom(alphabet, size) - generates a NanoID with custom alphabet and size")
    public String customNanoid(
            @Name("alphabet") String alphabet,
            @Name("size") Long size) {
        if (alphabet == null || size == null || size <= 0) {
            return NanoIdUtils.randomNanoId();
        }
        // Using the correct method signature: randomNanoId(Random random, char[] alphabet, int size)
        return NanoIdUtils.randomNanoId(new java.util.Random(), alphabet.toCharArray(), size.intValue());
    }

    @UserFunction("org.neo4j.nanoid.sized")
    @Description("org.neo4j.nanoid.sized(size) - generates a NanoID with custom size using default alphabet")
    public String sizedNanoid(@Name("size") Long size) {
        if (size == null || size <= 0) {
            return NanoIdUtils.randomNanoId();
        }
        // Using the correct method signature: randomNanoId(Random random, char[] alphabet, int size)
        return NanoIdUtils.randomNanoId(new java.util.Random(), NanoIdUtils.DEFAULT_ALPHABET, size.intValue());
    }
}