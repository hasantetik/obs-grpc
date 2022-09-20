import com.proto.*;
import io.grpc.stub.StreamObserver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ObsServiceImpl extends ObsServiceGrpc.ObsServiceImplBase {
    @Override
    public void division(StudentRegistration request, StreamObserver<DivisionName> responseObserver) {
        String fullname = request.getFirstName().concat(request.getSecondName());
        for(int i =0 ; i < fullname.length() ; i ++){
            responseObserver.onNext(DivisionName.newBuilder()
                    .setLetter(String.valueOf(fullname.charAt(i))
                    ).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Letters> combineLetters(StreamObserver<Combineletter> responseObserver) {
        StreamObserver<Letters> requestObserver = new StreamObserver<Letters>() {
            StringBuilder stringBuilder = new StringBuilder();
            @Override
            public void onNext(Letters letters) {
                stringBuilder.append(letters.getLetter());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        Combineletter.newBuilder()
                                .setFullString(stringBuilder.toString())
                                .build()
                );
            }
        };
        return requestObserver;
    }

    @Override
    public void registration(StudentRegistration request, StreamObserver<RegisteredStudent> responseObserver) {

        MessageDigest messageDigestNesnesi = null;
        try {
            messageDigestNesnesi = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigestNesnesi.update((request.getFirstName().concat(request.getSecondName())).getBytes(StandardCharsets.UTF_8));
        byte messageDigestDizisi[] = messageDigestNesnesi.digest();
        StringBuffer sb16 = new StringBuffer();
        StringBuffer sb32 = new StringBuffer();
        for (int i = 0; i < messageDigestDizisi.length; i++) {
            sb16.append(Integer.toString((messageDigestDizisi[i] & 0xff) + 0x100, 16).substring(1));
            sb32.append(Integer.toString((messageDigestDizisi[i] & 0xff) + 0x100, 32));
        }
        RegisteredStudent registeredStudent = RegisteredStudent.newBuilder()
                .setMd5Encrypt(sb16.toString())
                .build();

        responseObserver.onNext(registeredStudent);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<FindTheLongestWordRequest> findLongestWord(StreamObserver<FindTheLongestWordResponse> responseObserver) {
        return  new StreamObserver<FindTheLongestWordRequest>() {

            String LongestWord = "";

            @Override
            public void onNext(FindTheLongestWordRequest findMaximumRequest) {
                int len = findMaximumRequest.getWord().length();
                if(len > LongestWord.length()){
                    LongestWord = findMaximumRequest.getWord();
                    responseObserver.onNext(
                            FindTheLongestWordResponse.newBuilder()
                                    .setWord(LongestWord)
                                    .build()
                    );
                }


            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        FindTheLongestWordResponse.newBuilder()
                                .setWord(LongestWord)
                                .build()
                );
                responseObserver.onCompleted();
            }
        };
    }
}
