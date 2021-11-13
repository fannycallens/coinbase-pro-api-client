import com.coinbase.pro.api.client.ClientFactory;
import com.coinbase.pro.api.client.WebSocketClient;
import com.coinbase.pro.api.client.impl.WebSocketClientImpl;

import java.lang.System;

/*
// Request
// Subscribe to ETH-USD and ETH-EUR with the level2, heartbeat and ticker channels,
// plus receive the ticker entries for ETH-BTC and ETH-USD
{
    "type": "subscribe",
    "product_ids": [
        "ETH-USD",
        "ETH-EUR"
    ],
    "channels": [
        "level2",
        "heartbeat",
        {
            "name": "ticker",
            "product_ids": [
                "ETH-BTC",
                "ETH-USD"
            ]
        }
    ]
}
 */
public class Application {


        public static void main(String[] args) {
                System.out.println("Hello World!");
                ClientFactory factory = ClientFactory.newInstance();
                factory.newWebSocketClient();
                //WebSocketClient.onDepthEvent();

                //send subscribe message
                /*
                conn.onopen = function(evt) {
        conn.send(JSON.stringify({ method: "subscribe", topic: "orders", address: "bnb1m4m9etgf3ca5wpgkqe5nr6r33a4ynxfln3yz4v" }));
    }

                    ws.send(
                        json.dumps(
                                {
                                        "type": "subscribe",
                        "product_ids": ['BTC-USD'],
                "channels": ["matches"],
                }
                 */
                 */
                 */
                String json = "{\"method\":\"subscribe\",\"product_ids\":\"ETH-US\",\"channels\":\"level2\"}";


            )
        )
        }
}