package cc.openxiot.product.db.product;

import cc.openxiot.product.db.product.basic.ProductBasicEntity;
import cc.openxiot.product.db.product.instance.ProductInstanceEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.List;

@MongoEntity(collection = "product")
@BsonDiscriminator
public class ProductEntity extends PanacheMongoEntity {

    public ProductBasicEntity basic;

    public List<ProductInstanceEntity> instances;

    public ProductEntity() {
    }
}
