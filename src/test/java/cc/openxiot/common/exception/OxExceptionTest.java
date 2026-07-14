package cc.openxiot.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class OxExceptionTest {

    @Test
    void shouldPreserveMessage() {
        OxException e = new OxException("test error");
        assertEquals("test error", e.getMessage());
    }

    @Test
    void shouldAcceptNullMessage() {
        OxException e = new OxException(null);
        assertNull(e.getMessage());
    }
}
