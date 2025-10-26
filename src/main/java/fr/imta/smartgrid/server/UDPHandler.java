package fr.imta.smartgrid.server;

import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramPacket;
import io.vertx.core.datagram.DatagramSocket;

public class UDPHandler {

    private final Vertx vertx;

    public UDPHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void start() {
        DatagramSocket socket = vertx.createDatagramSocket();

        socket.listen(12345, "0.0.0.0", asyncResult -> {
            if (asyncResult.succeeded()) {
                System.out.println(" UDP server listening on port 12345");
            } else {
                System.err.println(" Failed to bind UDP port 12345: " + asyncResult.cause());
            }
        });

        socket.handler(packet -> handlePacket(packet));
    }

    private void handlePacket(DatagramPacket packet) {
        String data = packet.data().toString();
        System.out.println(" Received UDP packet: " + data);

        String[] parts = data.split(":");
        if (parts.length != 4) {
            System.err.println("Invalid format, skipping.");
            return;
        }

        try {
            int id = Integer.parseInt(parts[0]);
            double temperature = Double.parseDouble(parts[1]);
            double power = Double.parseDouble(parts[2]);
            long timestamp = Long.parseLong(parts[3]);

            System.out.println("Parsed solar panel data:");
            System.out.println("ID: " + id + ", Temp: " + temperature + ", Power: " + power + ", Time: " + timestamp);


        } catch (NumberFormatException e) {
            System.err.println(" Failed to parse numbers: " + e.getMessage());
        }
    }
}
