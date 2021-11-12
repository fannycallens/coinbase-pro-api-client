package com.coinbase.pro.api.client;

/**
 * A factory for creating client objects.
 */
public class ClientFactory {

    /**
     * Base domain for URLs.
     */
    private static String BASE_DOMAIN = "coinbase.com";

    /**
     * API Key
     */
    private String apiKey;

    /**
     * Secret.
     */
    private String secret;

    /**
     * Instantiates a new client factory.
     *
     * @param apiKey the API key
     * @param secret the Secret
     */
    private ClientFactory(String apiKey, String secret) {
        this.apiKey = apiKey;
        this.secret = secret;
    }


    /**
     * New instance.
     *
     * @param apiKey the API key
     * @param secret the Secret
     *
     * @return client factory
     */
    public static ClientFactory newInstance(String apiKey, String secret) {
        return new ClientFactory(apiKey, secret);
    }

    /**
     * New instance without authentication.
     *
     * @return tclient factory
     */
    public static ClientFactory newInstance() {
        return new ClientFactory(null, null);
    }


    /**
     * Creates a new web socket client used for handling data streams.
     */
    public WebSocketClient newWebSocketClient() {
        return new WebSocketClientImpl(getSharedClient());
    }


    public static void setBaseDomain(final String baseDomain) {
        BASE_DOMAIN = baseDomain;
    }

    public static String getBaseDomain() {
        return BASE_DOMAIN;
    }

    public static String getStreamApiBaseUrl() {
        return String.format("wss://stream.%s:9443/ws", getBaseDomain());
    }


}
