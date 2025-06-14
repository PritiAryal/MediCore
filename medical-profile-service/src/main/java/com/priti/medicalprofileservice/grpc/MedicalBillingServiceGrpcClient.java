package com.priti.medicalprofileservice.grpc;

import billing.MedicalBillingRequest;
import billing.MedicalBillingResponse;
import billing.MedicalBillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class MedicalBillingServiceGrpcClient {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MedicalBillingServiceGrpcClient.class);
    private final MedicalBillingServiceGrpc.MedicalBillingServiceBlockingStub blockingStub;
    // Anytime we make calls to medical billing service using this blocking stub execution is going to wait for a response
    // to come back from medical billing service before it continues.

    //localhost:9001/MedicalBillingService/CreateMedicalProfileAccount
    //aws.grpc:123123/MedicalBillingService/CreateMedicalProfileAccount
    public MedicalBillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serviceAddress,
            @Value("${billing.service.grpc.port:9001}") int servicePort) {

        log.info("Connecting to Medical Billing Service GRPC service at {}:{}", serviceAddress, servicePort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serviceAddress, servicePort)
                .usePlaintext()
                .build();

        blockingStub = MedicalBillingServiceGrpc.newBlockingStub(channel);
    }

    public MedicalBillingResponse createMedicalBillingAccount(String medicalProfileId, String name, String email) {
        MedicalBillingRequest request = MedicalBillingRequest.newBuilder()
                .setMedicalProfileId(medicalProfileId)
                .setName(name)
                .setEmail(email)
                .build();
        MedicalBillingResponse response = blockingStub.createMedicalBillingAccount(request);
        log.info("Received response from Medical Billing Service via GRPC: {}", response);
        return response;
    }
}
