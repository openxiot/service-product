package cc.openxiot.product.prepared;

import cn.geekcity.xiot.spec.codec.vertx.template.DeviceTemplateCodec;
import cn.geekcity.xiot.spec.definition.*;
import cn.geekcity.xiot.spec.definition.urn.*;
import cn.geekcity.xiot.spec.template.DeviceTemplate;
import cn.geekcity.xiot.spec.template.TemplateSummary;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@ApplicationScoped
public class TemplatePrepared {

    @Inject
    Logger logger;

    public static String TEMPLATE_FILE = "/templates/templates.json";
    public static String HOMEKIT_SPEC = "homekit-spec";
    public static String BLUETOOTH_SPEC = "bluetooth-spec";

    private final Set<String> preloaded = Set.of(HOMEKIT_SPEC, BLUETOOTH_SPEC);
    private final Map<DeviceType, String> templates = new HashMap<>();

    public int count() throws IOException {
        if (templates.isEmpty()) {
            init();
        }

        return templates.size();
    }

    public boolean contains(String namespace) {
        return preloaded.contains(namespace);
    }

    public DeviceTemplate getTemplate(DeviceType type) throws IOException {
        if (templates.isEmpty()) {
            init();
        }

        String path = templates.get(type);
        if (path != null) {
            return getDeviceTemplate(path);
        } else {
            throw new IOException("Template not found: " + type);
        }
    }

    public List<TemplateSummary> getTemplates(String namespace) throws IOException {
        if (templates.isEmpty()) {
            init();
        }

        List<TemplateSummary> result = new ArrayList<>();

        List<DeviceType> list = templates.keySet().stream().filter(x -> x.ns().equals(namespace)).toList();

        for (DeviceType type : list) {
            try {
                DeviceTemplate template = getTemplate(type);

                TemplateSummary summary = new TemplateSummary();
                summary.lifecycle(template.lifecycle());
                summary.type(template.type());
                summary.description(template.description());

                result.add(summary);
            } catch (IOException e) {
                logger.error(e);
            }
        }

        return result;
    }

    public List<TemplateSummary> getAllTemplate() throws IOException {
        if (templates.isEmpty()) {
            init();
        }

        List<TemplateSummary> result = new ArrayList<>();

        for (String path : templates.values()) {
            try {
                DeviceTemplate template = getDeviceTemplate(path);

                TemplateSummary summary = new TemplateSummary();
                summary.lifecycle(template.lifecycle());
                summary.type(template.type());
                summary.description(template.description());

                result.add(summary);
            } catch (IOException e) {
                logger.error(e);
            }
        }

        return result;
    }

    private DeviceTemplate getDeviceTemplate(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/templates/" + path)) {
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                String json = new String(bytes);
                JsonObject o = new JsonObject(json);
                return DeviceTemplateCodec.decode(o);
            } else {
                throw new IOException("path not found: " + path);
            }
        }
    }

    private void init() throws IOException {
        try (InputStream is = getClass().getResourceAsStream(TEMPLATE_FILE)) {
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                String json = new String(bytes);
                JsonObject o = new JsonObject(json);
                for (Map.Entry<String, Object> entry : o) {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    DeviceType type = DeviceType.parse(key);
                    templates.put(type, value.toString());
                }
            } else {
                throw new IOException("path not found: " + TEMPLATE_FILE);
            }
        }
    }
}
