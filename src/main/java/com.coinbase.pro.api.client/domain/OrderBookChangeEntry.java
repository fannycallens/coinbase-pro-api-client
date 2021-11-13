package com.coinbase.pro.api.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * An order book change entry consisting of buy/sell flag, price and quantity.
 */

@JsonDeserialize(using = OrderBookChangeEntryDeserializer.class)
@JsonSerialize(using = OrderBookChangeEntrySerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBookChangeEntry {

    private String buyOrSell;
    private String price;
    private String qty;

    public String getBuyOrSell() {
        return buyOrSell;
    }

    public void setBuyOrSell(String buyOrSell) {
        this.buyOrSell = buyOrSell;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
         this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("buyOrSell", buyOrSell)
                .append("price", price)
                .append("qty", qty)
                .toString();
    }
}
