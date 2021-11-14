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
        private static final String BID = "BID";
        private static final String ASKS = "ASKS";
        private static final String ASK = "ASK";
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
                // Create web socket and subscribe to depth events
                final List<DepthEvent> pendingDeltas = startDepthEventStreaming();

                // Initialise order book with snapshot
                // Update order book with l2update
                // Populate depthCache
                // We assume that we will always receive a snapshot event before l2update events
                applyPendingDeltas(pendingDeltas);
        }


        // Begins streaming of depth events.
        private List<DepthEvent> startDepthEventStreaming() {
                final List<DepthEvent> pendingDeltas = new CopyOnWriteArrayList<>();

                // Cache first updates
                wsDepthCallback.setHandler(pendingDeltas::add);

                // create web socket
                this.webSocket = wsClient.onDepthEvent(ticker, wsDepthCallback);

                return pendingDeltas;
        }

        // Deal with any cached updates and switch to normal running.
        private void applyPendingDeltas(final List<DepthEvent> pendingDeltas) {
                final Consumer<DepthEvent> updateOrderBook = newEvent -> {
                                updateOrderBook(getAsks(), newEvent.getAsks(), ASKS, newEvent.getType());
                                updateOrderBook(getBids(), newEvent.getBids(), BIDS, newEvent.getType());
                                printDepthCache();
                };

                final Consumer<DepthEvent> drainPending = newEvent -> {
                        pendingDeltas.add(newEvent);

                        // Apply latest updates received on the web socket
                        pendingDeltas.stream()
                                .forEach(updateOrderBook);

                        // Start applying any newly received depth events to the depth cache.
                        wsDepthCallback.setHandler(updateOrderBook);
                };

                wsDepthCallback.setHandler(drainPending);
        }

        // Update an order book (bids or asks) with depth events
        // if orderBoolDelta is null or if we receive a new snapshot, initialise depth cache with sorted bids and asks
        private void updateOrderBook(NavigableMap<BigDecimal, BigDecimal> lastOrderBookEntries,
                                     List<OrderBookEntry> orderBookDeltas, String askOrBid, String type) {
                // initialise depth cache with sorted maps
                if ((lastOrderBookEntries == null) || (type== "snapshot")){
                        // Sort the asks from smallest to biggest, then we will print the first entries
                        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.naturalOrder());
                        depthCache.put(ASKS, asks);
                        // Sort the bids from biggest to smaller, then we will print the first entries
                        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
                        depthCache.put(BIDS, bids);
                }
                if ((orderBookDeltas != null)) {
                        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
                                BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
                                BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
                                if (qty.compareTo(BigDecimal.ZERO) == 0) {
                                        // Remove price from order book if qty = 0
                                        lastOrderBookEntries.remove(price);
                                } else {
                                        // Insert new price level in order book
                                        lastOrderBookEntries.put(price, qty);
                                }
                                // Update depth cache
                                depthCache.put(askOrBid, lastOrderBookEntries);
                        }
                }
        }

        public NavigableMap<BigDecimal, BigDecimal> getAsks() {
                return depthCache.get(ASKS);
        }

        public NavigableMap<BigDecimal, BigDecimal> getBids() {
                return depthCache.get(BIDS);
        }

        public void close() throws IOException {
                webSocket.close();
        }

         //Print the 10 best bids and 10 best asks
        private void printDepthCache() {
                //System.out.println(depthCache);
                if (getAsks() != null) {
                        int i =0;
                        for (Map.Entry<BigDecimal, BigDecimal> ask : getAsks().entrySet()){
                                if (i < 10) {
                                        System.out.println(toDepthCacheEntryString(ask, ASK, i));
                                }
                                i++;
                        }
                }
                if (getBids() != null){
                        int i =0;
                        for (Map.Entry<BigDecimal, BigDecimal> bid : getBids().entrySet()){
                                if (i < 10) {
                                        System.out.println(toDepthCacheEntryString(bid, BID, i));
                                }
                                i++;
                        }
                }
        }

        // Print an order book entry in the format "bidOrAsk / price / quantity".
        private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry,
                                                      String bidOrAsk, int index) {
                int level = index + 1;
                return bidOrAsk + " " + level + " / " + depthCacheEntry.getKey().toPlainString() + " / "
                        + depthCacheEntry.getValue();
        }

        public static void main(String[] args) {
                String ticker = args[0];
                System.out.println(String.format("Launching app for ticker %s", ticker));
                Application myApp = new Application(ticker);
                //TODO : catch ctrl + C event and close the web socket
//                try {
//                        myApp.close();
//                }
//                catch(IOException e) {
//                        e.printStackTrace();
//                }


        }

        // Implement a specific callback for depth events with handler
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
                String ticker = args[0];
                System.out.println(String.format("Launching app for ticker %s", ticker));
                ClientFactory factory = ClientFactory.newInstance();
                final WebSocketClient wsClient;
                wsClient = factory.newWebSocketClient();
                wsClient.onDepthEvent(ticker, response -> System.out.println(response));
        }

}