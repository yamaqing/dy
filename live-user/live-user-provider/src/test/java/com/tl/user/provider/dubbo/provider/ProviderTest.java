package com.tl.user.provider.dubbo.provider;

import com.tl.user.inter.IUserRPCService;
import com.tl.user.provider.impl.UserRPCService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.io.IOException;

/**
 * Author：  roy
 * Description：
 **/
public class ProviderTest {

    public static final String REGISTER_ADDR = "nacos://nacos.tllive.com:8848?namespace=tl-live";

    private static RegistryConfig registryConfig;

    private static ApplicationConfig applicationConfig;

    static {
        registryConfig = new RegistryConfig();
        registryConfig.setAddress(REGISTER_ADDR);

        applicationConfig = new ApplicationConfig();
        applicationConfig.setName("dubbo-test-provider");
        applicationConfig.setRegistry(registryConfig);
    }

    public void initProvider(){
        ProtocolConfig dubboProtocolConfig = new ProtocolConfig();
        dubboProtocolConfig.setPort(9090);
        dubboProtocolConfig.setName("com/tl/user/provider/dubbo");
        ServiceConfig<IUserRPCService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(IUserRPCService.class);
        serviceConfig.setProtocol(dubboProtocolConfig);
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setRef(new UserRPCService());
        //暴露服务，并注册到注册中心
        serviceConfig.export();
        System.out.println("服务暴露");
    }

    public static void main(String[] args) {
        ProviderTest provider = new ProviderTest();
        provider.initProvider();
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
