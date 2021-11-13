package com.coinbase.pro.api.client.domain;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom serializer for an OrderBookChangeEntry.
 */
public class OrderBookChangeEntrySerializer extends JsonSerializer<OrderBookChangeEntry> {

    @Override
    public void serialize(OrderBookChangeEntry orderBookChangeEntry, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        gen.writeString(orderBookChangeEntry.getBuyOrSell());
        gen.writeString(orderBookChangeEntry.getPrice());
        gen.writeString(orderBookChangeEntry.getQty());
        gen.writeEndArray();
    }
}
