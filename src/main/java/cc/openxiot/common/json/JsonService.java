package cc.openxiot.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class JsonService {

    @Inject  // 注入Quarkus统一配置的ObjectMapper
    ObjectMapper objectMapper;

    public String toJson(PanacheMongoEntity entity) throws JsonProcessingException {
        return objectMapper.writeValueAsString(entity);
    }
}