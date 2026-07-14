package cc.openxiot.common.response;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

class OxResponseTest {

    @Test
    void errorWithException() {
        Response response = OxResponse.error(new Exception("boom"));
        assertEquals(200, response.getStatus());
        Object entity = response.getEntity();
        assertInstanceOf(OxError.class, entity);
        assertFalse(((OxError) entity).success);
        assertEquals("boom", ((OxError) entity).message);
    }

    @Test
    void errorWithString() {
        Response response = OxResponse.error("something went wrong");
        assertEquals(200, response.getStatus());
        Object entity = response.getEntity();
        assertInstanceOf(OxError.class, entity);
        assertFalse(((OxError) entity).success);
        assertEquals("something went wrong", ((OxError) entity).message);
    }

    @Test
    void ok() {
        Response response = OxResponse.ok();
        assertEquals(200, response.getStatus());
        assertInstanceOf(OxOk.class, response.getEntity());
        assertTrue(((OxOk) response.getEntity()).success);
    }

    @Test
    void created() {
        Response response = OxResponse.created();
        assertEquals(201, response.getStatus());
        assertInstanceOf(OxOk.class, response.getEntity());
        assertTrue(((OxOk) response.getEntity()).success);
    }

    @Test
    void okWithEntity() {
        String data = "hello";
        Response response = OxResponse.ok(data);
        assertEquals(200, response.getStatus());
        Object entity = response.getEntity();
        assertInstanceOf(OxData.class, entity);
        assertTrue(((OxData) entity).success);
        assertEquals(data, ((OxData) entity).data);
    }
}
