package com.coinbase.pro.api.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * {"type":"l2update","product_id":"ETH-BTC","changes":[["sell","0.07250","0.00000000"]],
 * "time":"2021-11-13T17:30:34.775264Z"}
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBookEvent {

    @JsonProperty("type")
    private String type;

    @JsonProperty("product_id")
    private String product_id;

    @JsonProperty("changes")
    private List<OrderBookChangeEntry> changes;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("type", type)
                .append("product_id", product_id)
                .append("changes", changes)
                .toString();
    }

    public List<OrderBookChangeEntry> getChanges() {
        return changes;
    }

}

