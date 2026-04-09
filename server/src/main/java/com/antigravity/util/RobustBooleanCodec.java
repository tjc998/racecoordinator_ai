package com.antigravity.util;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * A robust boolean codec that can handle legacy INT32, INT64, and DOUBLE values as well as the
 * standard BOOLEAN BSON type.
 */
public class RobustBooleanCodec implements Codec<Boolean> {

  @Override
  public Boolean decode(BsonReader reader, DecoderContext decoderContext) {
    BsonType type = reader.getCurrentBsonType();
    if (type == BsonType.BOOLEAN) {
      return reader.readBoolean();
    } else if (type == BsonType.INT32) {
      return reader.readInt32() != 0;
    } else if (type == BsonType.INT64) {
      return reader.readInt64() != 0;
    } else if (type == BsonType.DOUBLE) {
      return reader.readDouble() != 0.0;
    } else if (type == BsonType.NULL) {
      reader.readNull();
      return null;
    } else {
      // Log warning or skip
      reader.skipValue();
      return false;
    }
  }

  @Override
  public void encode(BsonWriter writer, Boolean value, EncoderContext encoderContext) {
    if (value == null) {
      writer.writeNull();
    } else {
      writer.writeBoolean(value);
    }
  }

  @Override
  public Class<Boolean> getEncoderClass() {
    return Boolean.class;
  }
}
