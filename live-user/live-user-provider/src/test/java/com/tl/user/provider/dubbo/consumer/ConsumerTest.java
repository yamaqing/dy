package com.tl.user.provider.dubbo.consumer;

import com.tl.user.dto.UserDTO;
import com.tl.user.inter.IUserRPCService;
import com.tl.user.provider.dubbo.provider.ProviderTest;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import java.io.IOException;

/**
 * Author： roy
 * Description：
 **/
public class ConsumerTest {

    private static RegistryConfig registryConfig;

    private static ApplicationConfig applicationConfig;

    private IUserRPCService userRPCService;

    static {
        registryConfig = new RegistryConfig();
        registryConfig.setAddress(ProviderTest.REGISTER_ADDR);

        applicationConfig = new ApplicationConfig();
        applicationConfig.setName("dubbo-test-consumer");
        applicationConfig.setRegistry(registryConfig);
    }

    public void initConsumer() {
        ReferenceConfig<IUserRPCService> userRpcReferenceConfig = new ReferenceConfig<>();
        userRpcReferenceConfig.setApplication(applicationConfig);
        userRpcReferenceConfig.setRegistry(registryConfig);
        userRpcReferenceConfig.setLoadbalance("random");
        userRpcReferenceConfig.setInterface(IUserRPCService.class);
        userRPCService = userRpcReferenceConfig.get();
    }


    public static void main(String[] args) throws InterruptedException {
        ConsumerTest consumerTest = new ConsumerTest();
        consumerTest.initConsumer();
        UserDTO user = consumerTest.userRPCService.getUserById(1L);
        System.out.println(">>>>>>>>>>>>>>"+user);
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
