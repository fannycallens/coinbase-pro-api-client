package com.coinbase.pro.api.client.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.List;

/*

{"type":"snapshot","product_id":"ETH-BTC",
"asks":[["0.07262","0.37000000"],["0.07269","0.65000000"],["0.07270","0.05000000"],
["0.07276","0.95000000"],["0.07277","0.07000000"],["0.07299","0.40000000"],["0.07311","1.03000000"],
["0.07312","1.08000000"],["0.08117","0.81000000"],["0.10499","13.92298637"],["0.10955","0.73000000"],
["0.11000","1.16568641"],["0.11199","80000.82000000"],["0.11200","0.99108750"],["0.15000","0.10000000"],
["1.00000","1.00000000"],["2.00000","2.00000000"],["5.00000","31.00000000"],["22.00000","88.00000000"],
["27.23885","99998.29485981"],["41.00000","0.01000000"],["100.00000","10000.00000000"],["130.00000","1.99386400"],
["2917.22000","50.00000000"],["5000.00000","5.00000000"],["33500.00000","7612500.00000000"],
["40182.18000","2787959.94792623"],["56500.70000","42.78707280"],["100000.00000","0.01000000"]],
"bids":[["0.07212","0.73972269"],["0.07205","0.60000000"],["0.07204","0.05000000"],["0.07198","0.56000000"],
["0.07197","0.07000000"],["0.07190","0.03000000"],["0.07175","0.60000000"],["0.07162","0.38000000"],
["0.06830","0.41000000"],["0.05842","0.60000000"],["0.05000","40.98000000"],["0.04500","1000.63000000"],
["0.04176","2393796.54959325"],["0.03499","91456635.45587707"],["0.03400","10.00000000"],["0.02000","1.96000000"],
["0.01700","0.95000000"],["0.01500","0.01000000"],["0.01489","0.02000000"],["0.01020","0.10000000"],
["0.01000","46.13601990"],["0.00500","6.00000000"],["0.00200","0.10000000"],["0.00100","5093.94367505"]]}

 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class DepthEvent {

    @JsonProperty("type")
    private String type;

    @JsonProperty("product_id")
    private String product_id;

    @JsonProperty("asks")
    private List<OrderBookEntry> asks;

    @JsonProperty("bids")
    private List<OrderBookEntry> bids;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("type", type)
                .append("product_id", product_id)
                .append("asks", asks)
                .append("bids", bids)
                .toString();
    }

    public String getType() { return type; }

    public List<OrderBookEntry> getAsks() {
        return asks;
    }

    public List<OrderBookEntry> getBids() {
        return bids;
    }

}
