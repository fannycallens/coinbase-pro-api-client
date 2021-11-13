import com.coinbase.pro.api.client.Callback;
import com.coinbase.pro.api.client.ClientFactory;
import com.coinbase.pro.api.client.WebSocketClient;
import com.coinbase.pro.api.client.domain.DepthEvent;
import com.coinbase.pro.api.client.domain.OrderBookEntry;
import java.io.Closeable;
import java.io.IOException;
import java.lang.System;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


public class Application {

        private static final String BIDS = "BIDS";
        private static final String ASKS = "ASKS";
        private final String ticker;
        private final WebSocketClient wsClient;
        private final WsDepthCallback wsDepthCallback = new WsDepthCallback();
        private final Map<String, NavigableMap<BigDecimal, BigDecimal>> depthCache = new HashMap<>();
        private volatile Closeable webSocket;

        public Application(String ticker) {
                this.ticker = ticker;
                ClientFactory factory = ClientFactory.newInstance();
                this.wsClient = factory.newWebSocketClient();
                initialize();
        }

        private void initialize() {
                // Subscribe to depth events and cache any events that are received.
                final List<DepthEvent> pendingDeltas = startDepthEventStreaming();

                //Populate order book with snapshot and l2update
                applyPendingDeltas(pendingDeltas);
        }


        // Begins streaming of depth events.
        private List<DepthEvent> startDepthEventStreaming() {
                final List<DepthEvent> pendingDeltas = new CopyOnWriteArrayList<>();
                wsDepthCallback.setHandler(pendingDeltas::add);

                this.webSocket = wsClient.onDepthEvent(ticker, wsDepthCallback);

                return pendingDeltas;
        }

        // Deal with any cached updates and switch to normal running.
        private void applyPendingDeltas(final List<DepthEvent> pendingDeltas) {
                final Consumer<DepthEvent> updateOrderBook = newEvent -> {
                                updateOrderBook(getAsks(), newEvent.getAsks(), ASKS);
                                updateOrderBook(getBids(), newEvent.getBids(), BIDS);
                                printDepthCache();
                };

                final Consumer<DepthEvent> drainPending = newEvent -> {
                        pendingDeltas.add(newEvent);

                        // Apply any deltas received on the web socket
                        pendingDeltas.stream()
                                .forEach(updateOrderBook);

                        // Start applying any newly received depth events to the depth cache.
                        wsDepthCallback.setHandler(updateOrderBook);
                };

                wsDepthCallback.setHandler(drainPending);
        }

        // Updates an order book (bids or asks) with a delta received from the server.
        // Whenever the qty is ZERO, it means the price should be removed from the order book.
        // if orderBoolDelta is null, nothing to do
        private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries,
                                     List<OrderBookEntry> orderBookDeltas, String askOrBid) {
                if (lastOrderBookEntries == null){
                        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.naturalOrder());
                        depthCache.put(ASKS, asks);

                        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
                        depthCache.put(BIDS, bids);
                }
                if (orderBookDeltas != null) {
                        int i =0;
                        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
                                i +=1;
                                BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
                                BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
                                if (qty.compareTo(BigDecimal.ZERO) == 0) {
                                        i-= 1;
                                        // qty=0 means remove this level
                                        lastOrderBookEntries.remove(price);
                                } else if (i < 11) {
                                        lastOrderBookEntries.put(price, qty);
                                }
                        }
                        depthCache.put(askOrBid,lastOrderBookEntries);
                }
        }

        public NavigableMap<BigDecimal, BigDecimal> getAsks() {
                return depthCache.get(ASKS);
        }

        public NavigableMap<BigDecimal, BigDecimal> getBids() {
                return depthCache.get(BIDS);
        }

         // @return the best ask in the order book
        private Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
                return getAsks().lastEntry();
        }

        // @return the best bid in the order book
        private Map.Entry<BigDecimal, BigDecimal> getBestBid() {
                return getBids().firstEntry();
        }

        //@return a depth cache, containing two keys (ASKs and BIDs), and for each, an ordered list of book entries.
        public Map<String, NavigableMap<BigDecimal, BigDecimal>> getDepthCache() {
                return depthCache;
        }

        public void close() throws IOException {
                webSocket.close();
        }

         //Prints the cached depth of a ticker
        private void printDepthCache() {
                System.out.println(depthCache);
                if (getAsks() != null) {
                        System.out.println("ASKS:(" + getAsks().size() + ")");
                        getAsks().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
                }
                if (getBids() != null){
                        System.out.println("BIDS:(" + getBids().size() + ")");
                        getBids().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
                }
        }
        // Pretty prints an order book entry in the format "price / quantity".
        private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
                return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
        }

        public static void main(String[] args) {

                System.out.println(String.format("Launching app for ticker", System.getProperty("ticker")));
                /*for (String str : args) {
                        System.out.println(String.format("Launching app for ticker", str));
                }*/
                new Application("ETH-BTC");

        }

        private final class WsDepthCallback implements Callback<DepthEvent> {

                private final AtomicReference<Consumer<DepthEvent>> handler = new AtomicReference<>();

                @Override
                public void onResponse(DepthEvent depthEvent) {
                        try {
                                handler.get().accept(depthEvent);
                        } catch (final Exception e) {
                                System.err.println("Exception caught processing depth event");
                                e.printStackTrace(System.err);
                        }
                }

                @Override
                public void onFailure(Throwable cause) {
                        System.out.println("WS connection failed. Reconnecting. cause:" + cause.getMessage());

                        initialize();
                }

                private void setHandler(final Consumer<DepthEvent> handler) {
                        this.handler.set(handler);
                }
        }

        public static void xmain(String[] args) throws InterruptedException, IOException {
                //System.out.println(String.format("Launching app for ticker", System.getProperty("ticker")));
                for (String str : args) {
                        System.out.println(String.format("Launching app for ticker", str));
                }

                ClientFactory factory = ClientFactory.newInstance();
                final WebSocketClient wsClient;
                wsClient = factory.newWebSocketClient();
                wsClient.onDepthEvent("ETH-BTC", response -> System.out.println(response));
        }

}