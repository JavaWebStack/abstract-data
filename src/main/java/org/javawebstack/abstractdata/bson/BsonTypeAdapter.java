package org.javawebstack.abstractdata.bson;

import org.bson.*;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.io.BasicOutputBuffer;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.mapper.MapperContext;
import org.javawebstack.abstractdata.mapper.MapperTypeAdapter;
import org.javawebstack.abstractdata.mapper.exception.MapperException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BsonTypeAdapter implements MapperTypeAdapter {

    private final BsonConverter converter;

    public BsonTypeAdapter() {
        this(new BsonConverter());
    }

    public BsonTypeAdapter(BsonConverter converter) {
        this.converter = converter;
    }

    public AbstractElement toAbstract(MapperContext context, Object value) throws MapperException {
        if(value instanceof byte[]) {
            ByteBuffer buffer = ByteBuffer.wrap((byte[]) value);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            return converter.toAbstract(new BsonDocumentCodec().decode(new BsonBinaryReader(buffer), DecoderContext.builder().build()));
        }
        if(value instanceof ObjectId)
            return converter.toAbstract((ObjectId) value);
        if(value instanceof Decimal128)
            return converter.toAbstract((Decimal128) value);
        return converter.toAbstract((BsonValue) value);
    }

    public Object fromAbstract(MapperContext context, AbstractElement element, Class<?> type) throws MapperException {
        BsonValue value = converter.toBson(element);
        if(value instanceof BsonNull && !BsonNull.class.equals(type))
            return null;
        if(byte[].class.equals(type)) {
            BasicOutputBuffer outputBuffer = new BasicOutputBuffer();
            new BsonDocumentCodec().encode(new BsonBinaryWriter(outputBuffer), value.asDocument(), EncoderContext.builder().build());
            return outputBuffer.toByteArray();
        }
        if(ObjectId.class.equals(type))
            return value.asObjectId().getValue();
        if(Decimal128.class.equals(type))
            return value.asDecimal128().getValue();
        return value;
    }

    public Class<?>[] getSupportedTypes() {
        return new Class[] {
                ObjectId.class,
                Decimal128.class,
                BsonValue.class,
                BsonDocument.class,
                BsonArray.class,
                BsonObjectId.class,
                BsonJavaScript.class,
                BsonJavaScriptWithScope.class,
                BsonTimestamp.class,
                BsonDateTime.class,
                BsonInt32.class,
                BsonInt64.class,
                BsonDouble.class,
                BsonString.class,
                BsonBoolean.class,
                BsonBinary.class,
                BsonNull.class,
                BsonDecimal128.class,
                BsonSymbol.class,
                BsonRegularExpression.class,
                BsonMinKey.class,
                BsonMaxKey.class,
                BsonNumber.class,
                BsonUndefined.class
        };
    }

}
