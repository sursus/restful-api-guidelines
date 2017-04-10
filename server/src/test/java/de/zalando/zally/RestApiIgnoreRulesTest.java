package de.zalando.zally;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;


@TestPropertySource(properties = "zally.ignoreRules=H999")
public class RestApiIgnoreRulesTest extends RestApiBaseTest {
    @Test
    public void shouldIgnoreSpecifiedRules() throws Exception {
        final JsonNode rootObject = getRootObject();
        final JsonNode violations = rootObject.get("violations");

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).get("title").asText()).isEqualTo("dummy1");
    }

    @Test
    public void shouldAffectCounters() throws Exception {
        final JsonNode rootObject = getRootObject();
        final JsonNode counters = rootObject.get("violations_count");

        assertThat(counters.get("must").asInt()).isEqualTo(1);
        assertThat(counters.get("should").asInt()).isEqualTo(0);
        assertThat(counters.get("could").asInt()).isEqualTo(0);
        assertThat(counters.get("hint").asInt()).isEqualTo(0);
    }

    private JsonNode getRootObject() throws IOException {
        final ResponseEntity<JsonNode> responseEntity = sendRequest(
                new ObjectMapper().readTree(ResourceUtils.getFile("src/test/resources/fixtures/api_spp.json")));

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        return responseEntity.getBody();
    }
}