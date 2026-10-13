package base;

import io.restassured.RestAssured;
import org.junit.Before;

public class BaseApiTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
    }
}
