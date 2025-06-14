package com.priti.medicalbillingservice.grpc;

import billing.MedicalBillingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import billing.MedicalBillingServiceGrpc.MedicalBillingServiceImplBase;
import org.slf4j.Logger;
import org.slf4j.ILoggerFactory;

// Tell spring that this is a grpc service and we want to have it managed by the spring boot lifecycle
@GrpcService
public class MedicalBillingGrpcService  extends MedicalBillingServiceImplBase {

    // This class will implement the gRPC service methods for medical billing
    // It will handle requests related to medical profiles, billing, and other related operations
    // The actual implementation will depend on the generated gRPC stubs and the business logic
    // that needs to be applied to the medical billing operations.
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MedicalBillingGrpcService.class);
    @Override
    public void createMedicalBillingAccount(billing.MedicalBillingRequest medicalBillingRequest, StreamObserver<billing.MedicalBillingResponse> responseObserver){
        log.info("createMedicalBillingAccount request received {}", medicalBillingRequest.toString());

        //Business logic such as save to db, perform calculations, etc.
        //we will not do it now.

        MedicalBillingResponse response = MedicalBillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("ACTIVE")
                .build();

        // Send the response back to the client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
