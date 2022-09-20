package org.work.service;

import com.google.protobuf.Descriptors;
import com.proto.ObsServiceGrpc;
import com.proto.RegisteredStudent;
import com.proto.StudentRegistration;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AnyService {

    @GrpcClient("grpc-work-service")
    ObsServiceGrpc.ObsServiceBlockingStub synchronousClient;

    public Map<Descriptors.FieldDescriptor, Object> registration(String firstName, String secondName) {
        StudentRegistration anyRequest = StudentRegistration.newBuilder()
                .setFirstName(firstName)
                .setSecondName(secondName)
                .build();
        RegisteredStudent anyResponse = synchronousClient.registration(anyRequest);
        return anyResponse.getAllFields();
    }
}
