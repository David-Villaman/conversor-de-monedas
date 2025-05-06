import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ExchangeRateClient {
    // API Key y URL base para la API de tasas de cambio
    private static final String API_KEY = System.getenv("EXCHANGE_RATE_API_KEY");
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    // Cliente HTTP para realizar solicitudes a la API
    private final HttpClient client;

    // Objeto Gson para convertir JSON a objetos Java
    private final Gson gson;

    // Lista para almacenar el historial de conversiones
    private static final int MAX_HISTORY = 10;
    private final List<String> conversionHistory = new LinkedList<>();

    // Constructor que inicializa el cliente HTTP y el objeto Gson
    public ExchangeRateClient() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // Obtiene la tasa de conversión entre dos monedas ingresadas por el usuario
    public double getExchangeRate(String baseCurrency, String targetCurrency, double amount) {
        String url = BASE_URL + baseCurrency;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            if (jsonResponse.has("conversion_rates")) {
                JsonObject rates = jsonResponse.getAsJsonObject("conversion_rates");

                if (rates.has(targetCurrency)) {
                    double exchangeRate = rates.get(targetCurrency).getAsDouble();
                    double convertedAmount = amount * exchangeRate;

                    addToHistory(baseCurrency, targetCurrency, amount, convertedAmount); // Guardar en el historial

                    return exchangeRate;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener los datos: " + e.getMessage());
        }
        return -1;
    }


    // Obtiene la lista de monedas disponibles para la conversión
    private JsonObject getAvailableCurrencies(String baseCurrency) {
        String url = BASE_URL + baseCurrency; // Usa la moneda ingresada como referencia
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
            return jsonResponse.has("conversion_rates") ? jsonResponse.getAsJsonObject("conversion_rates") : null;
        } catch (Exception e) {
            System.out.println("Error al obtener la lista de monedas: " + e.getMessage());
            return null;
        }
    }

    // Obtiene el nombre de la moneda según su código


    // Metodo para mostrar la lista de monedas y permitir conversiones personalizadas
    // Permite al usuario ingresar la moneda base y la moneda destino
    public void showAvailableCurrencies(Scanner scanner) {
        JsonObject rates = getAvailableCurrencies("USD"); // Usamos USD solo para obtener la lista de códigos de moneda
        if (rates == null) {
            System.out.println("No se pudo obtener la lista de monedas. Intente nuevamente.");
            return;
        }

        System.out.println("\nLista de monedas disponibles:");
        for (String currencyCode : rates.keySet()) {
            String currencyName = CurrencyList.getCurrencyName(currencyCode);
            System.out.printf("%s - %s%n", currencyCode, currencyName);
        }

        // Ahora permitimos conversiones después de mostrar la lista
        while (true) {
            System.out.println("\nIngrese la moneda base (Ejemplo: MXN) o escriba 'salir' para volver a las conversiones base:");
            String baseCurrency = scanner.next().toUpperCase();
            if (baseCurrency.equals("SALIR")) {
                break;
            }

            System.out.println("Ingrese la moneda destino (Ejemplo: USD):");
            String targetCurrency = scanner.next().toUpperCase();
            if (!rates.has(targetCurrency)) {
                System.out.println("Moneda destino no válida. Intente nuevamente.");
                continue;
            }

            System.out.print("Ingrese la cantidad a convertir: ");
            while (!scanner.hasNextDouble()) {
                System.out.println("Entrada inválida. Debe ingresar un número.");
                scanner.next();
            }

            double amount = scanner.nextDouble();
            double exchangeRate = getExchangeRate(baseCurrency, targetCurrency, amount);

            if (exchangeRate != -1) {
                double convertedAmount = amount * exchangeRate;
                System.out.printf("La cantidad %.2f %s equivale a %.2f %s.%n", amount, baseCurrency, convertedAmount, targetCurrency);
            } else {
                System.out.println("No se pudo obtener la tasa de cambio.");
            }
        }
    }

    // Metodo para agregar un registro al historial de conversiones
    // Utilizando java.time.LocalDateTime;
    public void addToHistory(String baseCurrency, String targetCurrency, double amount, double convertedAmount) {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTimestamp = timestamp.format(formatter);

        String record = String.format("[%s] %.2f %s → %.2f %s", formattedTimestamp, amount, baseCurrency, convertedAmount, targetCurrency);

        if (conversionHistory.size() >= MAX_HISTORY) {
            conversionHistory.remove(0); // Eliminar el más antiguo si supera los 10 registros
        }

        conversionHistory.add(record);
    }

    // Metodo para mostrar el historial de conversiones (Ultimas 10 conversiones)
    // Si no hay conversiones, muestra un mensaje indicando que no hay conversiones recientes
    public void showConversionHistory() {
        if (conversionHistory.isEmpty()) {
            System.out.println("No hay conversiones recientes.");
            return;
        }

        System.out.println("\nÚltimas 10 conversiones:");
        for (String record : conversionHistory) {
            System.out.println(record);
        }
    }

}
