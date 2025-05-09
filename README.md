# Conversor de Monedas
## Descripción del Proyecto:
Este proyecto es un **conversor de divisas** que utiliza la API [ExchangeRatesAPI](https://exchangeratesapi.io/) 
para obtener tasas de cambio actualizadas. Permite convertir entre múltiples monedas y almacena un historial de conversiones.





## Cómo agregar tu propia Environment Variable
Para configurar tu propia ***API Key***, sigue estos pasos:

### Paso 1: En la esquina superior derecha, haz clic en el ícono de tres puntos > Edit...
![Paso 1](assets/images/1.png)

### Paso 2: En la ventana que se abre haz clic en el ícono de la hoja de papel a la derecha de Environment variables...
![Paso 2](assets/images/3.png)

### Paso 4: En la ventana que se abre haz clic en el signo de más (+) para agregar una nueva variable
![Paso 4](assets/images/4.png)

### Paso 5: Agrega el nombre de la variable y su valor.
#### Por ejemplo: 
#### Name: `EXCHANGE_RATE_API_KEY` 
#### Value: `1234567890` 
#### Y presionamos "OK"
![Paso 5](assets/images/5.png)

### Paso 6: Ahora el Environment Variable debería verse de la siguiente manera. Presiona *"Apply" > "OK"*
![Paso 6](assets/images/6.png)

📌 **Nota:**
## Configuración de API Key

Para acceder a la API y obtener tasas de cambio, usando la ***variable de entorno*** guardada 

Puedes llamarla en tu código de la siguiente manera:

```java
private static final String API_KEY = System.getenv("EXCHANGE_RATE_API_KEY");
private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";
