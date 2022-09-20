import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ObsServer {
    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = ServerBuilder.forPort(9091)
                .addService(new ObsServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));


        server.awaitTermination();

    }
}
