package com.coinbase.pro.api.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.List;

/*
https://api.exchange.coinbase.com/products/product_id/book?level=1
{
  "sequence": 13051505638,
  "bids": [
    [
      "6247.58",
      "6.3578146",
      2
    ]
  ],
  "asks": [
    [
      "6251.52",
      "2",
      1
    ]
  ]
}
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepthEvent {

    @JsonProperty("sequence")
    private long sequence;

    @JsonProperty("bids")
    private List <String> bids;

    @JsonProperty("asks")
    private List<String> asks;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("sequence", sequence)
                .append("bids", bids)
                .append("asks", asks)
                .toString();
    }
}
