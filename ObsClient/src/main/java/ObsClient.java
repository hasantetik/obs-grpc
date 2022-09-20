import com.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ObsClient {
    public static void main(String[] args) {
        System.out.println("Hello I'm a gRPC client");

        ObsClient main = new ObsClient();
        main.run();
    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9091)
                .usePlaintext()
                .build();
        doUnaryCall(channel);
        doServerStreamingCall(channel);
        doBidiStreamingCall(channel);
        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    public void doUnaryCall(ManagedChannel channel) {
        ObsServiceGrpc.ObsServiceBlockingStub stub = ObsServiceGrpc.newBlockingStub(channel);

        StudentRegistration request = StudentRegistration.newBuilder()
                .setFirstName("Hasan")
                .setSecondName("Tetik")
                .build();
        RegisteredStudent response = stub.registration(request);

        System.out.println(request.getFirstName() + request.getSecondName() + " = " + response.getMd5Encrypt());

    }

    private void doServerStreamingCall(ManagedChannel channel) {
        ObsServiceGrpc.ObsServiceBlockingStub stub = ObsServiceGrpc.newBlockingStub(channel);

        stub.division(StudentRegistration.newBuilder()
                        .setFirstName("HASAN")
                        .setSecondName("")
                        .build())
                .forEachRemaining(divisionName -> {
                    System.out.println(divisionName.getLetter());
                });
    }

    private void doBidiStreamingCall(ManagedChannel channel) {
        ObsServiceGrpc.ObsServiceStub asyncClient = ObsServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);


        StreamObserver<FindTheLongestWordRequest> requestObserver = asyncClient.findLongestWord(new StreamObserver<FindTheLongestWordResponse>() {
            @Override
            public void onNext(FindTheLongestWordResponse value) {
                System.out.println("Got new maximum length word: " + value.getWord());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending messages");
            }
        });


        Arrays.asList("a", "ab", "abcde", "abc", "abcd", "aa", "bbb").forEach(
                word -> {
                    System.out.println("Sending word: " + word);
                    requestObserver.onNext(FindTheLongestWordRequest.newBuilder()
                            .setWord(word)
                            .build());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
