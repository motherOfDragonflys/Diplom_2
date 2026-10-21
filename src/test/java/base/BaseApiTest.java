package base;

import io.restassured.RestAssured;
import org.junit.Before;
import static config.Config.BASE_URI;

public class BaseApiTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URI;
    }
}
