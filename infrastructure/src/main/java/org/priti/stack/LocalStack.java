package org.priti.stack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

public class LocalStack extends Stack {

    private final Vpc vpc;

    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {

        super(scope,id,props);

        this.vpc = createVpc();

        DatabaseInstance authServiceDb = createDatabase("AuthServiceDB", "auth-service-db");

        DatabaseInstance medicalProfileServiceDb = createDatabase("MedicalProfileServiceDB", "medical-profile-service-db");

        CfnHealthCheck authDbHealthCheck = createDbHealthCheck(authServiceDb, "AuthServiceDBHealthCheck");

        CfnHealthCheck medicalProfileDbHealthCheck = createDbHealthCheck(medicalProfileServiceDb, "MedicalProfileServiceDBHealthCheck");

        CfnCluster mskCluster = createMskCluster();

        this.ecsCluster = createEcsCluster();

        FargateService authService =
                createFargateService("AuthService",
                        "auth-service",
                        List.of(8085),
                        authServiceDb,
                        Map.of("JWT_SECRET", "RL4rsSP95xTRF8lfY+fryPyZU5wxgQtCgHsKlfHdCWw="));

        authService.getNode().addDependency(authDbHealthCheck);
        authService.getNode().addDependency(authServiceDb);

        FargateService medicalBillingService =
                createFargateService("MedicalBillingService",
                        "medical-billing-service",
                        List.of(8082,9001),
                        null,
                        null);

        FargateService medicalAnalyticsService =
                createFargateService("MedicalAnalyticsService",
                        "medical-analytics-service",
                        List.of(8083),
                        null,
                        null);

        medicalAnalyticsService.getNode().addDependency(mskCluster);

        FargateService medicalProfileService =
                createFargateService("MedicalProfileService",
                        "medical-profile-service",
                        List.of(8081),
                        medicalProfileServiceDb,
                        Map.of(
                            "BILLING_SERVICE_ADDRESS", "host.docker.internal",
                            "BILLING_SERVICE_GRPC_PORT", "9001"
                        ));

        medicalProfileService.getNode().addDependency(medicalProfileDbHealthCheck);
        medicalProfileService.getNode().addDependency(medicalProfileServiceDb);
        medicalProfileService.getNode().addDependency(medicalBillingService);
        medicalProfileService.getNode().addDependency(mskCluster);

        createApiGatewayService();
    }

    //we have used aws cdk to create a VPC. VPC creates the routing and the networks that are needed for our internal services to work and communicate with each other.
    private Vpc createVpc() {
        return Vpc.Builder
                .create(this, "MedicalProfileManagementVPC")
                .vpcName("MedicalProfileManagementVPC") // This is the name of our VPC. It is a logical grouping of resources in AWS that allows us to isolate our resources from other resources in AWS.
                .maxAzs(2) // This is the maximum number of availability zones that we want to use for our VPC. We are using 2 availability zones for high availability and fault tolerance.
                .build();
    }

    // This method creates a database instance using the AWS CDK. It uses the Postgres engine version 17.2 and creates a database instance with the specified ID and name.
    private DatabaseInstance createDatabase(String id, String dbName) {
        return DatabaseInstance.Builder
                .create(this, id)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps.builder()
                            .version(PostgresEngineVersion.VER_17_2)
                            .build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(
                        InstanceClass.BURSTABLE2,
                        InstanceSize.MICRO))
                .allocatedStorage(20) //  are using 20 GB of storage for our database.
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName) // This is the name of our database. We are using the name that we passed as a parameter to this method.
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }


    // health check for each of our databases. Tt's a way for other services to know when the database is running and ready to accept connections and also if the databases are in a BAD state.
    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                    .type("TCP")
                    .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                    .ipAddress(db.getDbInstanceEndpointAddress())
                    .requestInterval(30)
                    .failureThreshold(3)
                    .build())
                .build();
    }

    private CfnCluster createMskCluster() {
        return CfnCluster.Builder.create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(2)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(vpc.getPrivateSubnets().stream()
                                .map(ISubnet::getSubnetId)
                                .collect(Collectors.toList()))
                        .brokerAzDistribution("DEFAULT")
                        .build())
                .build();
    }

    private Cluster createEcsCluster() {
        return Cluster.Builder.create(this, "MedicalProfileManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("medical-profile-management.local")
                        .build()) // eg: auth-service.medical-profile-management.local . It allows us to use a custom domain name for our services in the cluster. This namespace will be used to register our services in the cluster, allowing them to be discovered by other services in the same namespace.
                .build();
    }

    private FargateService createFargateService(String id,
                                                String imageName,
                                                List<Integer> ports,
                                                DatabaseInstance db,
                                                Map<String, String> additionalEnvVars) {

        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
                .cpu(256) // This is the CPU limit for our task definition. We are using 256 CPU units for our task.
                .memoryLimitMiB(512) // This is the memory limit for our task definition. We are using 512 MB of memory for our task.
                .build();

        ContainerDefinitionOptions.Builder containerOptions =
                ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromRegistry(imageName)) // This is the image that we want to use for our container. We are using the image that we passed as a parameter to this method.
                        .portMappings(ports.stream()
                                .map(port -> PortMapping.builder()
                                        .containerPort(port)  // Inside the container, this is the port that our application will listen on.
                                        .hostPort(port) // Port on the host that will be mapped to the container port. This is the port that we will use to access our application from outside the container.
                                        .protocol(Protocol.TCP)
                                        .build())
                                .toList()) // This is the list of ports that we want to expose for our container. We are using the ports that we passed as a parameter to this method.
                        .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                    .logGroupName("/ecs/" + imageName)
                                    .removalPolicy(RemovalPolicy.DESTROY)
                                    .retention(RetentionDays.ONE_DAY)
                                    .build())
                                .streamPrefix(imageName)
                                .build()));

        Map<String, String> envVars  = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");
        if(additionalEnvVars != null) {
            envVars.putAll(additionalEnvVars); // If we have any additional environment variables that we want to pass to our container, we can add them here.
        }

        if(db != null) {
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db" .formatted(
                db.getDbInstanceEndpointAddress(),
                db.getDbInstanceEndpointPort(),
                imageName
            ));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000"); // This is the time that we want to wait for the database to be ready before failing the application. We are using 60 seconds for this.
        }

        containerOptions.environment(envVars);
        taskDefinition.addContainer(imageName + "Container", containerOptions.build());

        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .build();
    }

    private void createApiGatewayService(){
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, "APIGatewayTaskDefinition")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions containerOptions =
                ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromRegistry("api-gateway"))
                        .environment(Map.of(
                                "SPRING_PROFILES_ACTIVE", "prod",
                                "AUTH_SERVICE_URL", "http://host.docker.internal:8085" // This is the URL of the Auth Service that we want to use for authentication. We are using the host.docker.internal address to access the service running on the host machine from inside the container.
                        ))
                        .portMappings(List.of(8084).stream()
                                .map(port -> PortMapping.builder()
                                        .containerPort(port)  // Inside the container, this is the port that our application will listen on.
                                        .hostPort(port) // Port on the host that will be mapped to the container port. This is the port that we will use to access our application from outside the container.
                                        .protocol(Protocol.TCP)
                                        .build())
                                .toList()) // This is the list of ports that we want to expose for our container. We are using the ports that we passed as a parameter to this method.
                        .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
                                        .logGroupName("/ecs/api-gateway")
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix("api-gateway")
                                .build()))
                        .build();

        taskDefinition.addContainer("APIGatewayContainer", containerOptions);

        //It will automatically create an Application Load Balancer and configure it to route traffic to the Fargate service.
        ApplicationLoadBalancedFargateService apiGateway
                = ApplicationLoadBalancedFargateService.Builder.create(this, "APIGatewayService")
                .cluster(ecsCluster)
                .serviceName("api-gateway")
                .taskDefinition(taskDefinition)
                .desiredCount( 1)
                .healthCheckGracePeriod(Duration.seconds(60))
                .build();
    }

    //Entry point for the code that is going to create our cloud formation template for our infrastructure.
    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());// Here, we are creating a new aws CDK application and we are defining where we want the output to be stored. When ever our stack is created, its going to generate a cloud formation template and store it in the ./cdk.out directory. This is the directory that we are going to use to deploy our stack to AWS using the AWS CLI or any other tool that supports cloud formation templates.
        StackProps props = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer()) //synthesizer is aws term that is used to convert our java code that defines our infrastructure into a cloud formation template. The synthesizer is responsible for generating the cloud formation template from our CDK code. In this case, we are using a BootstraplessSynthesizer which is a type of synthesizer that does not require bootstrapping the CDK environment. This is useful for local development and testing purposes, as it allows us to create stacks without needing to set up the CDK bootstrap resources in our AWS account.
                .build();

        // Link the LocalStack class to the app so that cdk knows to build our stack anytime we run this java app.
        new LocalStack(app, "localstack", props);

        app.synth(); // This method is called to synthesize the app, which means it will generate the cloud formation template based on the defined stacks and resources.

        System.out.println("App synthesizing in progress...");
    }

}
