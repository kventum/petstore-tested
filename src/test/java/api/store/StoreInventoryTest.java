package api.store;

import api.BaseTest;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import petstore.services.StoreService;

import static org.apache.http.HttpStatus.SC_OK;
import static petstore.constants.Others.POSITIVE;

@DisplayName("Тесты на проверку инвентаря магазина")
public class StoreInventoryTest extends BaseTest {

    private final StoreService storeService = new StoreService();

    @Override
    protected void prepare() {}

    @Test
    @Tag(POSITIVE)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Наличие существующих статусов животных в инвентаре магазина")
    public void verifyStatusesInStoreInventory() {
        storeService.getInventory(SC_OK, "jsonSchema/inventorySchema.json");
    }
}
