package by.clevertec.data;

import java.util.UUID;

public class TestCreateData {
    public static UUID createSuccessUUID(){

        return UUID.fromString("1e1a3208-dfc5-4eb7-8d8e-1f50f31dc70a");
    }

    public static UUID createBadUUID() {
        return UUID.fromString("2e1a3208-dfc5-4eb7-8d8e-1f50f31dc70a");
    }
}
