package com.coinbase.pro.api.client.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Custom deserializer for an OrderBookChangeEntry
 * The API returns an array in the format [buyOrSell, price, qty]
 */
public class OrderBookChangeEntryDeserializer extends JsonDeserializer<OrderBookChangeEntry> {

    @Override
    public OrderBookChangeEntry deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        final String buyOrSell = node.get(0).asText();
        final String price = node.get(1).asText();
        final String qty = node.get(2).asText();

        OrderBookChangeEntry orderBookChangeEntry = new OrderBookChangeEntry();
        orderBookChangeEntry.setBuyOrSell(buyOrSell);
        orderBookChangeEntry.setPrice(price);
        orderBookChangeEntry.setQty(qty);
        return orderBookChangeEntry;
    }
}
