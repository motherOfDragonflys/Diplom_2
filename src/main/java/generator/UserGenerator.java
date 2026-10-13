package generator;

import model.User;
import java.util.UUID;

public class UserGenerator {

    public static User randomUser() {

        String email =
                "test-" + UUID.randomUUID() + "@yandex.ru";

        return new User(
                email,
                "pAss!321",
                "Тестовый котик"
        );
    }
}
