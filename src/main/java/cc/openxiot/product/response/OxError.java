package cc.openxiot.product.response;

public class OxError {

    public boolean success = false;
    public String message;

    public OxError(String message) {
        this.success = false;
        this.message = message;
    }
}
