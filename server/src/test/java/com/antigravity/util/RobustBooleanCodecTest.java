package com.antigravity.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.codecs.DecoderContext;
import org.junit.Test;

public class RobustBooleanCodecTest {

  @Test
  public void testDecode() {
    RobustBooleanCodec codec = new RobustBooleanCodec();
    BsonReader reader = mock(BsonReader.class);
    DecoderContext context = DecoderContext.builder().build();

    // BOOLEAN true
    when(reader.getCurrentBsonType()).thenReturn(BsonType.BOOLEAN);
    when(reader.readBoolean()).thenReturn(true);
    assertTrue(codec.decode(reader, context));

    // BOOLEAN false
    when(reader.getCurrentBsonType()).thenReturn(BsonType.BOOLEAN);
    when(reader.readBoolean()).thenReturn(false);
    assertFalse(codec.decode(reader, context));

    // INT32 1
    when(reader.getCurrentBsonType()).thenReturn(BsonType.INT32);
    when(reader.readInt32()).thenReturn(1);
    assertTrue(codec.decode(reader, context));

    // INT32 0
    when(reader.getCurrentBsonType()).thenReturn(BsonType.INT32);
    when(reader.readInt32()).thenReturn(0);
    assertFalse(codec.decode(reader, context));

    // INT64 1
    when(reader.getCurrentBsonType()).thenReturn(BsonType.INT64);
    when(reader.readInt64()).thenReturn(1L);
    assertTrue(codec.decode(reader, context));

    // DOUBLE 1.0
    when(reader.getCurrentBsonType()).thenReturn(BsonType.DOUBLE);
    when(reader.readDouble()).thenReturn(1.0);
    assertTrue(codec.decode(reader, context));
  }
}
