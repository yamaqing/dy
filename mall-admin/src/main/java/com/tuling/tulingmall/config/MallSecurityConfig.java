package com.tuling.tulingmall.config;

import com.tuling.tulingmall.service.UmsAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * mall-security 模块相关配置
 * Created on 2019/11/5.
 *
 * M0-2 迁移说明：原实现 extends 幻影模块 tulingmall-security 的 SecurityConfig（目录不存在，无法迁移）。
 * Spring Security 6 中 WebSecurityConfigurerAdapter 已移除、EnableGlobalMethodSecurity 已废弃，
 * 本类改为最小可编译配置，仅保留 UserDetailsService 装配。
 * TODO(M3)：统一认证中心（无状态 JWT）接管安全配置，本类届时整体重写。
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class MallSecurityConfig {

    @Autowired
    private UmsAdminService adminService;

    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> adminService.loadUserByUsername(username);
    }
}
