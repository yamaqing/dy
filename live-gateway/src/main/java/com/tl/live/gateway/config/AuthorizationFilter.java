package com.tl.live.gateway.config;

import com.tl.live.enums.GatewayHeaderEnum;
import com.tl.user.inter.IUserRPCService;
import jakarta.annotation.Resource;
import org.apache.dubbo.common.constants.ClusterRules;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Author： roy
 * Description：
 **/
@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {
    private Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    @DubboReference(check = false,cluster = ClusterRules.BROADCAST)
    private IUserRPCService userRPCService;
    @Resource
    private GatewayAppProperties gatewayAppProperties;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求URL
        ServerHttpRequest request = exchange.getRequest();
        String reqUrl = request.getURI().getPath();
        logger.info("请求URL：{}",reqUrl);
        //判断URL是不是在白名单。白名单无需登录，直接通过
        for (String whiteUrl : gatewayAppProperties.getWhiteUrlList()) {
            if(reqUrl.startsWith(whiteUrl)){
                logger.info("请求白名单，直接通过");
                return chain.filter(exchange);
            }
        }
        //不是白名单，则通过获取token,判断是否有用户登录
        List<HttpCookie> httpCookies = request.getCookies().get("tltk");
        if(CollectionUtils.isEmpty(httpCookies)){
            logger.error("未携带名为tltk的Cookie,拒绝请求");
            return Mono.empty();
        }
        //获取token
        String token = httpCookies.get(0).getValue();
        if(!StringUtils.hasText(token)){
            logger.error("请求的cookie中tltk是空，拒绝请求");
            return Mono.empty();
        }
        //没有登录，则拒绝请求
        String userId = userRPCService.checkToken(token);
        if(null == userId){
            logger.error("请求的token已经失效，拒绝请求");
            return Mono.empty();
        }
        //登录完成，则将UserId通过header传给下游服务。
        logger.info("当前登录用户 {},请求通过",userId);
        ServerHttpRequest.Builder builder = request.mutate();
        builder.header(GatewayHeaderEnum.USER_LOGIN_ID.getName(),userId);

        return chain.filter(exchange.mutate().request(builder.build()).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
