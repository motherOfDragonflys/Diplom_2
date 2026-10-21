package base;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import config.WireMockStubs;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Rule;

/**
 * ____⚠️⚠️⚠️____НЕ ИСПОЛЬЗОВАТЬ КЛАСС при прогоне НА РЕАЛЬНОМ СЕРВЕРЕ____⚠️⚠️⚠️___________________
 */
public class BaseWireMockTest extends BaseApiTest {

    private static final int WIREMOCK_PORT = 8089;
    private static final String LOCAL_URI = "http://localhost:" + WIREMOCK_PORT;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
            WireMockConfiguration.options()
                    .port(WIREMOCK_PORT)
                    .extensions(new ResponseTemplateTransformer(false)) // false = шаблоны только там, где явно включены
    );

    @Before
    public void setup() {
        WireMockStubs.registerAll(wireMockRule); // правило = сервер
        RestAssured.baseURI = LOCAL_URI;
    }
}
