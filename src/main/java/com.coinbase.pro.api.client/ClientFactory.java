package com.coinbase.pro.api.client;

import com.coinbase.pro.api.client.impl.WebSocketClientImpl;
import okhttp3.OkHttpClient;
import okhttp3.Dispatcher;
import retrofit2.Converter;
import retrofit2.converter.jackson.JacksonConverterFactory;
import java.util.concurrent.TimeUnit;
/**
 * Base domain for all URLs (https://api.exchange.coinbase.com)
 * All API endpoints are documented here :
 * https://docs.cloud.coinbase.com/exchange/reference/exchangerestapi_getaccounts
 */

/**
 * A factory for creating client objects.
 */
public class ClientFactory {

    private static final OkHttpClient sharedClient;
    private static final Converter.Factory converterFactory = JacksonConverterFactory.create();
    private static final String WsFeed = "wss://ws-feed.exchange.coinbase.com";
    private static final String WsFeedSandbox = "wss://ws-feed-public.sandbox.exchange.coinbase.com";

    static {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);
        sharedClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .
                pingInterval(20, TimeUnit.SECONDS)
                .build();
    }
    private ClientFactory() {}
    /**
     *
     * Instantiates a new client factory for market data
     * No authenticstion required
     *
     * @return client factory
     */
    public static ClientFactory newInstance() {
        return new ClientFactory();
    }

    /**
     * Creates a new web socket client used for handling data streams.
     */
    public WebSocketClient newWebSocketClient() {
        return new WebSocketClientImpl(sharedClient);
    }

    /**
     * @return websocket url
     */
    public static String getStreamApiBaseUrl() {
        return WsFeedSandbox;
    }

}
