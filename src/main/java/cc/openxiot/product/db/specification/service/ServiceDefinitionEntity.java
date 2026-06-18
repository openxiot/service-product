package cc.openxiot.product.db.specification.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;
import java.util.Map;

public class ServiceDefinitionEntity {

    public String lifecycle;

    public String code;

    public int value;

    public Map<String, String> description;

    @JsonProperty("optional-properties")
    @BsonProperty("optional-properties")
    public List<String> optionalProperties;

    @JsonProperty("required-properties")
    @BsonProperty("required-properties")
    public List<String> requiredProperties;

    @JsonProperty("optional-actions")
    @BsonProperty("optional-actions")
    public List<String> optionalActions;

    @JsonProperty("required-actions")
    @BsonProperty("required-actions")
    public List<String> requiredActions;

    @JsonProperty("optional-events")
    @BsonProperty("optional-events")
    public List<String> optionalEvents;

    @JsonProperty("required-events")
    @BsonProperty("required-events")
    public List<String> requiredEvents;
}
