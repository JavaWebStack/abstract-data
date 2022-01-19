package org.javawebstack.abstractdata.bson;

import org.bson.*;
import org.bson.internal.Base64;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.javawebstack.abstractdata.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BsonConverter {

    static {
        System.out.println("Hello");
    }

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BsonConverter dateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return this;
    }

    public BsonConverter dateFormat(String format) {
        return dateFormat(new SimpleDateFormat(format));
    }

    public AbstractObject toAbstract(ObjectId value) {
        return new AbstractObject().set("$oid", value.toHexString());
    }

    public AbstractObject toAbstract(Decimal128 value) {
        return new AbstractObject().set("$numberDecimal", value.toString());
    }

    public AbstractElement toAbstract(BsonValue value) {
        switch (value.getBsonType()) {
            case NULL:
                return AbstractNull.INSTANCE;
            case STRING:
                return new AbstractPrimitive(value.asString().getValue());
            case BOOLEAN:
                return new AbstractPrimitive(value.asBoolean().getValue());
            case INT32:
                return new AbstractPrimitive(value.asInt32().getValue());
            case INT64:
                return new AbstractPrimitive(value.asInt64().getValue());
            case DOUBLE:
                return new AbstractPrimitive(value.asDouble().getValue());
            case OBJECT_ID:
                return toAbstract(value.asObjectId().getValue());
            case DATE_TIME:
                return new AbstractObject().set("$date", dateFormat.format(new Date(value.asDateTime().getValue())));
            case UNDEFINED:
                return new AbstractObject().set("$undefined", true);
            case SYMBOL:
                return new AbstractObject().set("$symbol", value.asSymbol().getSymbol());
            case MIN_KEY:
                return new AbstractObject().set("$minKey", 1);
            case MAX_KEY:
                return new AbstractObject().set("$maxKey", 1);
            case JAVASCRIPT:
                return new AbstractObject().set("$code", value.asJavaScript().getCode());
            case JAVASCRIPT_WITH_SCOPE:
                return new AbstractObject()
                        .set("$code", value.asJavaScriptWithScope().getCode())
                        .set("$scope", toAbstract(value.asJavaScriptWithScope().getScope()));
            case DECIMAL128:
                return toAbstract(value.asDecimal128().getValue());
            case REGULAR_EXPRESSION:
                return new AbstractObject().set("$regularExpression", new AbstractObject()
                        .set("pattern", value.asRegularExpression().getPattern())
                        .set("options", value.asRegularExpression().getOptions())
                );
            case TIMESTAMP:
                return new AbstractObject().set("$timestamp", new AbstractObject()
                        .set("t", Integer.toUnsignedLong(value.asTimestamp().getTime()))
                        .set("i", Integer.toUnsignedLong(value.asTimestamp().getInc()))
                );
            case BINARY:
                return new AbstractObject().set("$binary", new AbstractObject()
                        .set("base64", Base64.encode(value.asBinary().getData()))
                        .set("subType", String.format("%02x", value.asBinary().getType()))
                );
            case ARRAY: {
                AbstractArray a = new AbstractArray();
                value.asArray().forEach(v -> a.add(toAbstract(v)));
                return a;
            }
            case DOCUMENT: {
                AbstractObject o = new AbstractObject();
                value.asDocument().forEach((k, v) -> o.set(k, toAbstract(v)));
                return o;
            }
        }
        throw new UnsupportedOperationException("Unsupported Bson Type: " + value.getBsonType().name());
    }

    public BsonValue toBson(AbstractElement element) {
        if(element == null || element.isNull())
            return BsonNull.VALUE;
        if(element.isString())
            return new BsonString(element.string());
        if(element.isBoolean())
            return new BsonBoolean(element.bool());
        if(element.isNumber()) {
            Number n = element.number();
            if(n instanceof Integer || n instanceof Short || n instanceof Byte) {
                return new BsonInt32(n.intValue());
            } else if(n instanceof Long) {
                return new BsonInt64(n.longValue());
            } else if(n instanceof Float || n instanceof Double) {
                return new BsonDouble(n.doubleValue());
            }
        }
        if(element.isArray()) {
            BsonArray a = new BsonArray();
            for(AbstractElement e : element.array())
                a.add(toBson(e));
            return a;
        }
        AbstractObject o = element.object();
        if(o.size() == 1 && o.has("$oid") && o.get("$oid").isString())
            return new BsonObjectId(new ObjectId(o.string("$oid")));
        if(o.size() == 1 && o.has("$undefined") && o.get("$undefined").isBoolean())
            return new BsonUndefined();
        if(o.size() == 1 && o.has("$date") && o.get("$date").isString()) {
            try {
                return new BsonDateTime(dateFormat.parse(o.string("$date")).getTime());
            } catch (ParseException ignored) {}
        }
        if(o.size() == 1 && o.has("$numberDecimal") && o.get("$numberDecimal").isString())
            return new BsonDecimal128(Decimal128.parse(o.string("$numberDecimal")));
        if(o.size() == 1 && o.has("$minKey") && o.get("$minKey").isNumber())
            return new BsonMinKey();
        if(o.size() == 1 && o.has("$maxKey") && o.get("$maxKey").isNumber())
            return new BsonMinKey();
        if(o.size() == 1 && o.has("$symbol") && o.get("$symbol").isString())
            return new BsonSymbol(o.string("$symbol"));
        if(o.size() == 1 && o.has("$code") && o.get("$code").isString())
            return new BsonJavaScript(o.string("$code"));
        if(o.size() == 2 && o.has("$code") && o.has("$scope") && o.get("$code").isString() && o.get("$scope").isObject())
            return new BsonJavaScriptWithScope(o.string("$code"), toBson(o.get("$scope")).asDocument());
        if(o.size() == 1 && o.has("$timestamp") && o.get("$timestamp").isObject()) {
            AbstractObject ts = o.object("$timestamp");
            if(ts.has("t") && ts.has("i") && ts.get("t").isNumber() && ts.get("i").isNumber()) {
                return new BsonTimestamp((int) ts.number("t").longValue(), (int) ts.number("i").longValue());
            }
        }
        if(o.size() == 1 && o.has("$regularExpression") && o.get("$regularExpression").isObject()) {
            AbstractObject re = o.object("$regularExpression");
            if(re.has("pattern") && re.has("options") && re.get("pattern").isString() && (re.get("options").isString() || re.get("options").isNull())) {
                return new BsonRegularExpression(re.string("pattern"), (String) re.toObject());
            }
        }
        if(o.size() == 1 && o.has("$binary") && o.get("$binary").isObject()) {
            AbstractObject bin = o.object("$binary");
            if(bin.has("base64") && bin.has("subType") && bin.get("base64").isString() && bin.get("subType").isString()) {
                byte[] data = Base64.decode(bin.string("base64"));
                byte type = (byte) Integer.parseInt(bin.string("subType"), 16);
                return new BsonBinary(type, data);
            }
        }
        BsonDocument doc = new BsonDocument(o.size());
        for(String k : o.keys())
            doc.put(k, toBson(o.get(k)));
        return doc;
    }

}
