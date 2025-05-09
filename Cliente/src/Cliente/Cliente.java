package cliente;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 1234;
        boolean conexionActiva = true;

        try (
            Socket socket = new Socket(host, puerto);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conectado al servidor.");

            // Recibe clave de salida y la envía
            String mensajeInicial = reader.readLine();
            System.out.println(mensajeInicial);
            String claveCliente = scanner.nextLine();
            writer.println(claveCliente);

            // Hilo para recibir mensajes
            Thread hiloRecibirMensajes = new Thread(() -> {
                try {
                    String mensajeRecibido;
                    while ((mensajeRecibido = reader.readLine()) != null) {
                        System.out.println("SERVIDOR: " + mensajeRecibido);
                        if (mensajeRecibido.contains("cerrado") || mensajeRecibido.contains("Adiós")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error al recibir mensaje del servidor.");
                }
            });
            hiloRecibirMensajes.start();

            // Enviar mensajes
            while (conexionActiva) {
                String mensaje = scanner.nextLine();
                writer.println(mensaje);
                if (mensaje.equals(claveCliente)) {
                    conexionActiva = false;
                }
            }

            System.out.println("Desconectado del servidor.");
        } catch (IOException e) {
            System.out.println("No se pudo conectar al servidor.");
        }
    }
}
