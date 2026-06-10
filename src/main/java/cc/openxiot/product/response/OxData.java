package cc.openxiot.product.response;

public class OxData {

    public boolean success;
    public Object data;

    public OxData(Object data) {
        this.success = true;
        this.data = data;
    }
}
