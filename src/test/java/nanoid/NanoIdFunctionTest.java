package nanoid;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NanoIdFunctionTest {

    private Driver driver;
    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {
        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withFunction(NanoIdFunction.class)
                .build();

        this.driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
    }

    @AfterAll
    void closeDriver() {
        this.driver.close();
        this.embeddedDatabaseServer.close();
    }

    @Test
    public void shouldGenerateStandardNanoId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN nanoid() AS id").single();
            String nanoId = record.get("id").asString();

            assertThat(nanoId).isNotNull();
            assertThat(nanoId).hasSize(21);
            assertThat(nanoId).matches("[A-Za-z0-9_-]+"); // URL-safe characters
        }
    }

    @Test
    public void shouldGenerateStandardSizedNanoId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN nanoid.generateSized(15) AS id").single();
            String nanoId = record.get("id").asString();

            assertThat(nanoId).isNotNull();
            assertThat(nanoId).hasSize(15);
            assertThat(nanoId).matches("[A-Za-z0-9_-]+"); // URL-safe characters
        }
    }

    @Test
    public void shouldGenerateCustomNanoId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN nanoid.generateCustom('ABCDEF') AS id").single();
            String nanoId = record.get("id").asString();

            assertThat(nanoId).isNotNull();
            assertThat(nanoId).hasSize(21); // Default size
            assertThat(nanoId).matches("[ABCDEF]+");
        }
    }

    @Test
    public void shouldGenerateCustomSizedNanoId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN nanoid.generateCustomSized('ABCDEF', 8) AS id").single();
            String nanoId = record.get("id").asString();

            assertThat(nanoId).isNotNull();
            assertThat(nanoId).hasSize(8);
            assertThat(nanoId).matches("[ABCDEF]+");
        }
    }

    @Test
    public void shouldGenerateQuickId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN quickid() AS id").single();
            String quickId = record.get("id").asString();

            assertThat(quickId).isNotNull();
            assertThat(quickId).hasSize(14); // Default quickid size
            assertThat(quickId).matches("[A-Za-z0-9]+"); // Alphanumeric only
        }
    }

    @Test
    public void shouldGenerateSizedQuickId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN quickid.generateSized(10) AS id").single();
            String quickId = record.get("id").asString();

            assertThat(quickId).isNotNull();
            assertThat(quickId).hasSize(10);
            assertThat(quickId).matches("[A-Za-z0-9]+"); // Alphanumeric only
        }
    }

    @Test
    public void shouldHandleEdgeCases() {
        try (Session session = driver.session()) {
            // Test nanoid with negative size - should fallback to default
            Record record1 = session.run("RETURN nanoid.generateSized(-5) AS id").single();
            String nanoId1 = record1.get("id").asString();
            assertThat(nanoId1).hasSize(21); // Falls back to nanoid default size

            // Test nanoid.custom with empty alphabet - should fallback to default
            Record record2 = session.run("RETURN nanoid.generateCustomSized('', 10) AS id").single();
            String nanoId2 = record2.get("id").asString();
            assertThat(nanoId2).hasSize(21); // Falls back to default behavior
            assertThat(nanoId2).matches("[A-Za-z0-9_-]+"); // Uses URL-safe alphabet

            // Test quickid with negative size - should fallback to default
            Record record3 = session.run("RETURN quickid.generateSized(-3) AS id").single();
            String quickId = record3.get("id").asString();
            assertThat(quickId).hasSize(14); // Falls back to quickid default size
            assertThat(quickId).matches("[A-Za-z0-9]+");

            // Test quickid with null size - should fallback to default
            Record record4 = session.run("RETURN quickid.generateSized(null) AS id").single();
            String quickId2 = record4.get("id").asString();
            assertThat(quickId2).hasSize(14); // Falls back to quickid default size
            assertThat(quickId2).matches("[A-Za-z0-9]+");

            // Test nanoid.custom with null alphabet - should fallback to default
            Record record5 = session.run("RETURN nanoid.generateCustom(null) AS id").single();
            String nanoId3 = record5.get("id").asString();
            assertThat(nanoId3).hasSize(21); // Default size
            assertThat(nanoId3).matches("[A-Za-z0-9_-]+"); // Default alphabet
        }
    }

    @Test
    public void shouldGenerateUniqueIds() {
        try (Session session = driver.session()) {
            // Generate multiple IDs and ensure they're unique
            Record record = session.run(
                "UNWIND range(1, 100) AS i " +
                "RETURN collect(DISTINCT nanoid()) AS ids"
            ).single();
            
            java.util.List<Object> ids = record.get("ids").asList();
            assertThat(ids).hasSize(100); // All IDs should be unique
        }
    }

    @Test
    public void shouldValidateCustomAlphabets() {
        try (Session session = driver.session()) {
            // Test with numbers only
            Record record1 = session.run("RETURN nanoid.generateCustom('0123456789') AS id").single();
            String numericId = record1.get("id").asString();
            assertThat(numericId).hasSize(21);
            assertThat(numericId).matches("[0-9]+");

            // Test with letters only
            Record record2 = session.run("RETURN nanoid.generateCustom('ABCDEFGHIJKLMNOPQRSTUVWXYZ') AS id").single();
            String letterIdid = record2.get("id").asString();
            assertThat(letterIdid).hasSize(21);
            assertThat(letterIdid).matches("[A-Z]+");

            // Test with special characters
            Record record3 = session.run("RETURN nanoid.generateCustomSized('!@#$%^&*()', 8) AS id").single();
            String specialId = record3.get("id").asString();
            assertThat(specialId).hasSize(8);
            assertThat(specialId).matches("[!@#$%^&*()]+");
        }
    }

    @Test
    public void shouldHandleZeroSize() {
        try (Session session = driver.session()) {
            // Test nanoid with zero size - should fallback to default
            Record record1 = session.run("RETURN nanoid.generateSized(0) AS id").single();
            String nanoId = record1.get("id").asString();
            assertThat(nanoId).hasSize(21); // Falls back to default

            // Test quickid with zero size - should fallback to default
            Record record2 = session.run("RETURN quickid.generateSized(0) AS id").single();
            String quickId = record2.get("id").asString();
            assertThat(quickId).hasSize(14); // Falls back to default

            // Test custom nanoid with zero size - should fallback to default
            Record record3 = session.run("RETURN nanoid.generateCustomSized('ABC', 0) AS id").single();
            String customId = record3.get("id").asString();
            assertThat(customId).hasSize(21); // Falls back to default
            assertThat(customId).matches("[ABC]+");
        }
    }
}