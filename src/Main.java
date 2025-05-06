import java.util.Scanner;

public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    ExchangeRateClient apiClient = new ExchangeRateClient();
    boolean continuar = true;

    while (continuar) {
        System.out.println("\n****************************************");
        System.out.println("\nBienvenido al conversor de divisas.");
        System.out.println("Seleccione una opción:");
        System.out.printf("1. [USD] %s → [MXN] %s%n", CurrencyList.getCurrencyName("USD"), CurrencyList.getCurrencyName("MXN"));
        System.out.printf("2. [COP] %s → [MXN] %s%n", CurrencyList.getCurrencyName("COP"), CurrencyList.getCurrencyName("MXN"));
        System.out.printf("3. [USD] %s → [COP] %s%n", CurrencyList.getCurrencyName("USD"), CurrencyList.getCurrencyName("COP"));
        System.out.printf("4. [EUR] %s → [MXN] %s%n", CurrencyList.getCurrencyName("EUR"), CurrencyList.getCurrencyName("MXN"));
        System.out.printf("5. [GBP] %s → [MXN] %s%n", CurrencyList.getCurrencyName("GBP"), CurrencyList.getCurrencyName("MXN"));
        System.out.println("6. Más monedas");
        System.out.println("7. Historial de conversiones");
        System.out.println("8. Salir");

        int option;
        while (true) {
            System.out.print("Ingrese el número de la opción: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida. Debe ingresar un número.");
                scanner.next(); // Limpiar la entrada incorrecta
                continue; // Volver a pedir la entrada
            }

            option = scanner.nextInt();


            if (option < 1 || option > 8) {
                System.out.println("Entrada inválida. Debe ingresar un número de la lista de opciones.");
                continue; // Volver a pedir la entrada
            }

            break;
        }

        if (option == 8) {
            System.out.println("Programa finalizado. ¡Hasta pronto!");
            break;
        }

        if (option == 7) {
            apiClient.showConversionHistory();
            continue;
        }

        if (option == 6) {
            apiClient.showAvailableCurrencies(scanner);
            continue;
        }

        String baseCurrency = "";
        String targetCurrency = "";

        switch (option) {
            case 1 -> {
                baseCurrency = "USD";
                targetCurrency = "MXN";
            }
            case 2 -> {
                baseCurrency = "COP";
                targetCurrency = "MXN";
            }
            case 3 -> {
                baseCurrency = "USD";
                targetCurrency = "COP";
            }
            case 4 -> {
                baseCurrency = "EUR";
                targetCurrency = "MXN";
            }
            case 5 -> {
                baseCurrency = "GBP";
                targetCurrency = "MXN";
            }
            default -> {
                System.out.println("Opción no válida. Intente nuevamente.");
                continue;
            }
        }

        System.out.print("Ingrese la cantidad a convertir: ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Entrada inválida. Debe ingresar un número.");
            scanner.next();
        }

        double amount = scanner.nextDouble();
        double exchangeRate = apiClient.getExchangeRate(baseCurrency, targetCurrency, amount);

        if (exchangeRate != -1) {
            double convertedAmount = amount * exchangeRate;
            System.out.printf("La cantidad %.2f %s (%s) equivale a %.2f %s (%s).%n",
                    amount, baseCurrency, CurrencyList.getCurrencyName(baseCurrency),
                    convertedAmount, targetCurrency, CurrencyList.getCurrencyName(targetCurrency));
        } else {
            System.out.println("No se pudo obtener la tasa de cambio.");
        }
    }

    scanner.close();
}
