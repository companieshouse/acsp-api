package uk.gov.companieshouse.acsp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiLoggerTest {

    private static final String CONTEXT = "CONTEXT";
    private static final String TEST_MESSAGE = "TEST";
    private static final String LOG_MAP_KEY = "COMPANY_NUMBER";
    private static final String LOG_MAP_VALUE = "00006400";

    private Map<String, Object> logMap;

    @BeforeEach
    void setup() {
        logMap = new HashMap<>();
        logMap.put(LOG_MAP_KEY, LOG_MAP_VALUE);
    }

    @Test
    void testDebugContextLoggingDoesNotModifyLogMap() {
        ApiLogger.debugContext(CONTEXT, TEST_MESSAGE, logMap);

        assertEquals(1, logMap.size());
        assertEquals(LOG_MAP_VALUE, logMap.get(LOG_MAP_KEY));
    }

    @Test
    void testInfoLoggingDoesNotModifyLogMap() {
        ApiLogger.info(TEST_MESSAGE, logMap);

        assertEquals(1, logMap.size());
        assertEquals(LOG_MAP_VALUE, logMap.get(LOG_MAP_KEY));
    }

    @Test
    void testInfoContextLoggingDoesNotModifyLogMap() {
        ApiLogger.infoContext(CONTEXT, TEST_MESSAGE, logMap);

        assertEquals(1, logMap.size());
        assertEquals(LOG_MAP_VALUE, logMap.get(LOG_MAP_KEY));
    }

    @Test
    void testErrorContextLoggingDoesNotModifyLogMap() {
        ApiLogger.errorContext(CONTEXT, TEST_MESSAGE, new Exception("TEST"), logMap);

        assertEquals(1, logMap.size());
        assertEquals(LOG_MAP_VALUE, logMap.get(LOG_MAP_KEY));
    }

    @Test
    void testErrorLoggingDoesNotModifyLogMap() {
        ApiLogger.error(TEST_MESSAGE, new Exception(TEST_MESSAGE), logMap);

        assertEquals(1, logMap.size());
        assertEquals(LOG_MAP_VALUE, logMap.get(LOG_MAP_KEY));
    }

    @Test
    void debugClonesLogMapWithoutModifyingOriginal() {
        Map<String, Object> originalMap = Map.of("key1", "value1");
        ApiLogger.debug("Debug message", originalMap);

        assertEquals(1, originalMap.size());
        assertEquals("value1", originalMap.get("key1"));
    }

    @Test
    void debugContextClonesLogMapWithoutModifyingOriginal() {
        Map<String, Object> originalMap = Map.of("key2", "value2");
        ApiLogger.debugContext("CONTEXT", "Debug context message", originalMap);

        assertEquals(1, originalMap.size());
        assertEquals("value2", originalMap.get("key2"));
    }

    @Test
    void infoClonesLogMapWithoutModifyingOriginal() {
        Map<String, Object> originalMap = Map.of("key3", "value3");
        ApiLogger.info("Info message", originalMap);

        assertEquals(1, originalMap.size());
        assertEquals("value3", originalMap.get("key3"));
    }

    @Test
    void infoContextClonesLogMapWithoutModifyingOriginal() {
        Map<String, Object> originalMap = Map.of("key4", "value4");
        ApiLogger.infoContext("CONTEXT", "Info context message", originalMap);

        assertEquals(1, originalMap.size());
        assertEquals("value4", originalMap.get("key4"));
    }

    @Test
    void errorClonesLogMapWithoutModifyingOriginal() {
        Map<String, Object> originalMap = Map.of("key5", "value5");
        ApiLogger.error("Error message", new Exception("Error"), originalMap);

        assertEquals(1, originalMap.size());
        assertEquals("value5", originalMap.get("key5"));
    }

    @Test
    void errorContextClonesLogMapWithoutModifyingOriginal() {
        Map<String, Object> originalMap = Map.of("key6", "value6");
        ApiLogger.errorContext("CONTEXT", "Error context message", new Exception("Error"), originalMap);

        assertEquals(1, originalMap.size());
        assertEquals("value6", originalMap.get("key6"));
    }

    @Test
    void cloneMapDataReturnsEmptyMapForNullInput() {
        Map<String, Object> clonedMap = ApiLogger.cloneMapData(null);

        assertNotNull(clonedMap);
        assertTrue(clonedMap.isEmpty());
    }
}
