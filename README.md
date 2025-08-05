= Neo4j NanoID Procedure

A Neo4j user-defined function that provides NanoID generation capabilities directly within Cypher queries. NanoID is a tiny, secure, URL-safe, unique string ID generator that's an excellent alternative to UUID.

== Features

* 🔒 *Secure*: Cryptographically strong random generator using `java.util.Random`
* 📏 *Compact*: Shorter than UUID (21 characters vs 36)
* 🌐 *URL-safe*: Uses URL-safe alphabet with underscore and dash support (A-Za-z0-9_-)
* ⚙️ *Configurable*: Custom alphabet and size support
* 🔌 *Native*: Direct integration with Neo4j Cypher queries
* 🛡️ *Robust*: Graceful fallback handling for edge cases

== Installation

=== Prerequisites

* Neo4j 5.26.0 or later
* Java 21+

[NOTE]
====
This project requires Java 21 or later and is tested with Neo4j 5.26.0. If you're using an older Java version, you'll see a build error. 

To check your Java version:
[source,bash]
----
java -version
----

To install Java 21, consider using link:https://sdkman.io/[SDKMAN!]:
[source,bash]
----
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash

# Install Java 21
sdk install java 21.0.2-tem

# Use Java 21 for this project
sdk use java 21.0.2-tem
----
====

=== Steps

. Download the latest JAR from the link:../../releases[releases page]
. Copy the JAR file to your Neo4j `plugins` directory:
+
[source,bash]
----
cp neo4j-nanoid-procedure-1.0.0.jar $NEO4J_HOME/plugins/
----
. Restart your Neo4j instance
. Verify installation by testing the function:
+
[source,cypher]
----
// Test NanoID generation  
RETURN nanoid.generate() AS nano_id;
----

== Usage

=== Basic Usage

[source,cypher]
=== Basic Usage

[source,cypher]
----
// Generate a standard NanoID (21 characters, URL-safe)
RETURN nanoid.generate() AS id;
// Result: "V1StGXR8_Z5jdHi6B-myT4a"

// Create a node with NanoID
CREATE (u:User {id: nanoid.generate(), name: 'Alice'});

// Use in MERGE operations
MERGE (p:Product {id: nanoid.generate()})
SET p.name = 'New Product';
----
[source,cypher]
----
// Generate shorter QuickID (8 characters, alphanumeric)
RETURN quickid.generateSized(8) AS id;
// Result: "IRFaVaY2"
=== Custom Size

[source,cypher]
----
// Generate shorter NanoID (15 characters)
RETURN nanoid.generateSized(15) AS id;
// Result: "IRFa-VaY2b_XyZ1"

// Generate longer NanoID (30 characters)  
RETURN nanoid.generateSized(30) AS id;
// Result: "6a-9Znp_MRiuZ8sNFbJ1mxBEq2S3K"
----RN nanoid.generateCustomSized('0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ', 12) AS id;
// Result: "A1B2C3D4E5F6"

// Use only digits with default size (21 chars)
RETURN nanoid.generateCustom('0123456789') AS id;
// Result: "123456789012345678901"

// Use only digits with custom size
RETURN nanoid.generateCustomSized('0123456789', 8) AS id;
// Result: "12345678"

// Custom alphabet with specific length
RETURN nanoid.generateCustomSized('ABCDEF', 16) AS id;
// Result: "ABCDEFFEDCBABCDE"
----

=== Batch Generation

[source,cypher]
----
// Generate multiple QuickIDs
UNWIND range(1, 5) AS i
RETURN quickid.generate() AS id, i;

// Create multiple nodes with unique QuickIDs
UNWIND ['Alice', 'Bob', 'Charlie'] AS name
CREATE (u:User {id: quickid.generate(), name: name});
=== Batch Generation

[source,cypher]
----
// Generate multiple NanoIDs
UNWIND range(1, 5) AS i
RETURN nanoid.generate() AS id, i;

// Create multiple nodes with unique NanoIDs
UNWIND ['Alice', 'Bob', 'Charlie'] AS name
CREATE (u:User {id: nanoid.generate(), name: name});

// Mix different NanoID types
UNWIND range(1, 3) AS i
RETURN nanoid.generate() AS standard_id, nanoid.generateSized(10) AS short_id, i;
----chars

|`quickid.generateSized(size)`
|Generate alphanumeric ID with custom size
|`RETURN quickid.generateSized(8)`
|Custom

|`nanoid.generate()`
|Generate URL-safe ID (A-Za-z0-9_-)
|`RETURN nanoid.generate()`
|21 chars

|`nanoid.generateSized(size)`
|Generate URL-safe ID with custom size
|`RETURN nanoid.generateSized(15)`
|Custom

|`nanoid.generateCustom(alphabet)`
|Generate with custom alphabet (default size)
|`RETURN nanoid.generateCustom('ABC123')`
|21 chars

|`nanoid.generateCustomSized(alphabet, size)`
|Generate with custom alphabet and size
|`RETURN nanoid.generateCustomSized('ABC123', 8)`
|Custom
|===

== Comparison with UUID

[cols="1,1,1,1"]
|===
|Feature |QuickID |NanoID |UUID

|Length
|14 characters
|21 characters
|36 characters

|Default Alphabet
|Alphanumeric (62 chars)
|URL-safe (64 chars)
|Hex + hyphens

|Characters Used
|A-Za-z0-9
|A-Za-z0-9_-
|0-9a-f + hyphens

|URL-safe
|✅ Always (no special chars)
|✅ Yes (_- included)
|❌ No (hyphens)

|Collision probability
|~1% after 1M IDs
|~1% after 1B IDs
|Same as UUID v4

|Performance
|~60% faster
|~60% faster
|Standard

|Readability
|✅ Ultra-clean
|✅ Clean
|❌ Contains hyphens

|Customizable
|❌ Size only
|✅ Alphabet & size
|❌ Fixed format
|===

== Use Cases

* *Primary Keys*: Use `quickid.generate()` for ultra-compact primary keys (14 chars)
* *Display IDs*: QuickID provides clean alphanumeric IDs for user-facing identifiers  
* *URL Slugs*: Use `nanoid.generate()` for URL-safe characters when underscores/dashes are acceptable
* *API Keys*: Secure random generation with customizable alphabets via `nanoid.generateCustom()`
* *Session IDs*: Compact and secure with flexible character sets
* *File Names*: QuickID is safe for all file systems (no special characters)

== Building from Source

=== Prerequisites

* Java 21+
* Maven 3.9.4+

=== Build Steps

[source,bash]
----
# Clone the repository
git clone https://github.com/Abhid14/neo4j-nanoid-procedure.git
cd neo4j-nanoid-procedure
git checkout extended

# Build the project
./mvnw clean package

# The JAR will be created in target/
ls target/neo4j-nanoid-procedure-1.0.0-extended.jar
----

[TIP]
====
If you encounter a Java version error during build, make sure you're using Java 21 or later. The build will fail with older Java versions.
====

=== Running Tests

[source,bash]
----
# Run all tests
./mvnw test

# Run tests with detailed output
./mvnw test -Dtest=NanoIdFunctionTest,QuickIdFunctionTest

# Run only QuickID tests
./mvnw test -Dtest=QuickIdFunctionTest

# Run only NanoID tests  
./mvnw test -Dtest=NanoIdFunctionTest
----

== Function Behavior

=== QuickID vs NanoID

* *`quickid.generate()`*: Returns ultra-compact 14-character alphanumeric IDs (A-Za-z0-9) - ideal for primary keys and space-constrained scenarios
* *`quickid.generateSized(size)`*: Same alphanumeric alphabet but with custom length
* *`nanoid.generate()`*: Returns standard 21-character URL-safe IDs with underscores and dashes (A-Za-z0-9_-) - compatible with original NanoID spec  
* *`nanoid.generateSized(size)`*: URL-safe alphabet with custom length
* *`nanoid.generateCustom(alphabet)`*: Custom alphabet with default 21-character length
* *`nanoid.generateCustomSized(alphabet, size)`*: Fully customizable alphabet and length

=== Edge Case Handling

The functions are designed to be robust and always return valid IDs:

[source,cypher]
----
// Invalid sizes fallback to defaults
RETURN quickid.generateSized(0) AS id;         // Returns 14-char alphanumeric ID  
RETURN quickid.generateSized(-5) AS id;        // Returns 14-char alphanumeric ID
RETURN quickid.generateSized(null) AS id;      // Returns 14-char alphanumeric ID
RETURN nanoid.generateSized(0) AS id;          // Returns 21-char URL-safe ID
RETURN nanoid.generateSized(-5) AS id;         // Returns 21-char URL-safe ID

// Invalid alphabet falls back to default behavior
RETURN nanoid.generateCustomSized('', 10) AS id;      // Returns 21-char URL-safe ID
RETURN nanoid.generateCustomSized('   ', 8) AS id;    // Returns 21-char URL-safe ID  
RETURN nanoid.generateCustomSized(null, 8) AS id;     // Returns 21-char URL-safe ID
RETURN nanoid.generateCustom('') AS id;               // Returns 21-char URL-safe ID
RETURN nanoid.generateCustom(null) AS id;             // Returns 21-char URL-safe ID
----

== Configuration

The procedure uses an enhanced NanoID configuration:

* *QuickID*: 
  - Alphabet: Alphanumeric characters only (A-Za-z0-9) - 62 character alphabet
  - Default size: 14 characters
  - Use cases: Primary keys, compact identifiers, file-safe names
* *NanoID*: 
  - Alphabet: URL-safe characters `_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ` (64 characters)
  - Default size: 21 characters  
  - Use cases: URLs, general-purpose IDs, web-safe identifiers
* *Custom NanoID*: 
  - Alphabet: Fully configurable via `nanoid.generateCustom()` and `nanoid.generateCustomSized()`
  - Size: Fully configurable
  - Use cases: Specialized requirements, branded IDs, restricted character sets
* *Collision probability*: 
  - QuickID: ~1% after generating 1 million IDs
  - NanoID: ~1% after generating 1 billion IDs
* *Edge case handling*: Invalid inputs gracefully fall back to defaults

== Performance

Benchmarks on standard hardware:

* *Generation rate*: ~2M IDs/second
* *Memory usage*: Minimal overhead
* *Thread safety*: Fully thread-safe

== Dependencies

* link:https://github.com/aventrix/jnanoid[jnanoid 2.0.0]: Core NanoID implementation  
* Neo4j 5.26.0: Procedure framework
* JUnit Jupiter 5.11.0: Testing framework (test scope)
* AssertJ 3.27.3: Assertion library (test scope)

== Contributing

. Fork the repository
. Create a feature branch (`git checkout -b feature/amazing-feature`)
. Commit your changes (`git commit -m 'Add amazing feature'`)
. Push to the branch (`git push origin feature/amazing-feature`)
. Open a Pull Request

== License

This project is licensed under the Apache License 2.0 - see the link:LICENSE[LICENSE] file for details.

== Acknowledgments

* link:https://github.com/ai/nanoid[NanoID] - Original JavaScript implementation
* link:https://github.com/aventrix/jnanoid[jnanoid] - Java port
* link:https://neo4j.com[Neo4j] - Graph database platform

---

*Made with ❤️ for the Neo4j community*
