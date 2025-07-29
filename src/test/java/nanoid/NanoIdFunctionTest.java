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
            Record record = session.run("RETURN nanoid.generate() AS id").single();
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
    public void shouldHandleEdgeCases() {
        try (Session session = driver.session()) {
            // Test nanoid with negative size - should fallback to default
            Record record2 = session.run("RETURN nanoid.generateSized(-5) AS id").single();
            String nanoId2 = record2.get("id").asString();
            assertThat(nanoId2).hasSize(21); // Falls back to nanoid default size
            
            // Test nanoid.custom with empty alphabet - should fallback to default
            Record record3 = session.run("RETURN nanoid.generateCustomSized('', 10) AS id").single();
            String nanoId3 = record3.get("id").asString();
            assertThat(nanoId3).hasSize(21); // Falls back to default behavior
            assertThat(nanoId3).matches("[A-Za-z0-9_-]+"); // Uses URL-safe alphabet
        }
    }
}