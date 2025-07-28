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
    public void shouldGenerateNanoId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN org.neo4j.nanoid.generate() AS id").single();
            String nanoId = record.get("id").asString();
            
            assertThat(nanoId).isNotNull();
            assertThat(nanoId).hasSize(21); // Default NanoID size
            assertThat(nanoId).matches("[A-Za-z0-9_-]+"); // URL-safe characters
        }
    }

    @Test
    public void shouldGenerateCustomSizedNanoId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN org.neo4j.nanoid.sized(10) AS id").single();
            String nanoId = record.get("id").asString();
            
            assertThat(nanoId).isNotNull();
            assertThat(nanoId).hasSize(10);
        }
    }

    @Test
    public void shouldGenerateCustomNanoId() {
        try (Session session = driver.session()) {
            Record record = session.run("RETURN org.neo4j.nanoid.custom('ABCDEF', 8) AS id").single();
            String nanoId = record.get("id").asString();
            
            assertThat(nanoId).isNotNull();
            assertThat(nanoId).hasSize(8);
            assertThat(nanoId).matches("[ABCDEF]+");
        }
    }
}