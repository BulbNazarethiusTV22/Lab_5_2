import java.util.concurrent.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        CompletableFuture<Map<String, Object>> trainOption = checkTransportAsync("Train");
        CompletableFuture<Map<String, Object>> busOption = checkTransportAsync("Bus");
        CompletableFuture<Map<String, Object>> flightOption = checkTransportAsync("Plane");

        CompletableFuture<Void> allOptions = CompletableFuture.allOf(trainOption, busOption, flightOption)
            .thenRun(() -> {
                try {
                    Map<String, Object> train = trainOption.get();
                    Map<String, Object> bus = busOption.get();
                    Map<String, Object> flight = flightOption.get();

                    System.out.println("Available routes:");
                    printRoute(train);
                    printRoute(bus);
                    printRoute(flight);

                    Map<String, Object> bestOption = findBestRoute(Arrays.asList(train, bus, flight));
                    System.out.println("\nOptimal Route: " + bestOption);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

        allOptions.join();
    }

    private static CompletableFuture<Map<String, Object>> checkTransportAsync(String transportType) {
        return CompletableFuture.supplyAsync(() -> fetchRouteDetails(transportType));
    }

    private static Map<String, Object> fetchRouteDetails(String transportType) {
        try {
            Thread.sleep((long) (Math.random() * 3000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double price = Math.random() * 200 + 50; 
        double time = Math.random() * 10 + 1;  
        Map<String, Object> route = new HashMap<>();
        route.put("Transport", transportType); 
        route.put("Price", price);
        route.put("Time", time);
        return route;
    }

    private static Map<String, Object> findBestRoute(List<Map<String, Object>> routes) {
        return routes.stream()
                     .min(Comparator.comparingDouble(route -> 
                         (double) route.get("Price") / (double) route.get("Time")))
                     .orElseThrow(() -> new RuntimeException("No routes available"));
    }

    private static void printRoute(Map<String, Object> route) {
        System.out.println("Transport: " + route.get("Transport") + ", Price: " + route.get("Price") + ", Time: " + route.get("Time"));
    }
}
