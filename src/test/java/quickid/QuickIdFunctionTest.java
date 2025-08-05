package quickid;

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
public class QuickIdFunctionTest {

    private Driver driver;
    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {
        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withDisabledServer()
                .withFunction(QuickId.class)
                .build();

        this.driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
    }

    @AfterAll
    void closeDriver() {
        this.driver.close();
        this.embeddedDatabaseServer.close();
    }

    @Test
    public void shouldGenerateQuickId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN quickid.generate() AS id").single();
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
            // Test quickid with negative size - should fallback to default
            Record record1 = session.run("RETURN quickid.generateSized(-3) AS id").single();
            String quickId = record1.get("id").asString();
            assertThat(quickId).hasSize(14); // Falls back to quickid default size
            assertThat(quickId).matches("[A-Za-z0-9]+");

            // Test quickid with null size - should fallback to default
            Record record2 = session.run("RETURN quickid.generateSized(null) AS id").single();
            String quickId2 = record2.get("id").asString();
            assertThat(quickId2).hasSize(14); // Falls back to quickid default size
            assertThat(quickId2).matches("[A-Za-z0-9]+");
        }
    }

    @Test
    public void shouldGenerateUniqueIds() {
        try (Session session = driver.session()) {
            // Generate multiple IDs and ensure they're unique
            Record record = session.run(
                "UNWIND range(1, 100) AS i " +
                "RETURN collect(DISTINCT quickid.generate()) AS ids"
            ).single();
            
            java.util.List<Object> ids = record.get("ids").asList();
            assertThat(ids).hasSize(100); // All IDs should be unique
        }
    }

    @Test
    public void shouldHandleZeroSize() {
        try (Session session = driver.session()) {
            // Test quickid with zero size - should fallback to default
            Record record = session.run("RETURN quickid.generateSized(0) AS id").single();
            String quickId = record.get("id").asString();
            assertThat(quickId).hasSize(14); // Falls back to default
        }
    }
}