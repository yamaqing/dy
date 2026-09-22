package com.tl.user.provider.service;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.tl.common.redis.SMSCacheKeyBuilder;
import com.tl.user.dto.MsgCheckDTO;
import com.tl.user.provider.config.SMSCCPConfig;
import com.tl.user.provider.config.SmsTemplateIDEnum;
import com.tl.user.provider.config.ThreadPoolManager;
import com.tl.user.provider.entity.SmsDO;
import com.tl.user.provider.mapper.SmsMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Author： roy
 * Description：
 **/
@Service
public class SmsService {

    private Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Resource
    private SMSCacheKeyBuilder smsCacheKeyBuilder;

    @Resource
    private SmsMapper smsMapper;

    @Resource
    private SMSCCPConfig smsccpConfig;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    public MsgCheckDTO checkSmsCode(String mobile, Integer code) {

        //参数校验
        if (!StringUtils.hasText(mobile) || code == null || code < 1000) {
            return new MsgCheckDTO(false, "参数异常");
        }
        //redis校验验证码
        String codeCacheKey = smsCacheKeyBuilder.buildSmsLoginCodeKey(mobile);
        Integer cacheCode = (Integer) redisTemplate.opsForValue().get(codeCacheKey);
        if (cacheCode == null || cacheCode < 1000) {
            return new MsgCheckDTO(false, "验证码已过期");
        }
        if (cacheCode.equals(code)) {
            redisTemplate.delete(codeCacheKey);
            return new MsgCheckDTO(true, "验证码校验成功");
        }
        return new MsgCheckDTO(false, "验证码校验失败");
    }

    public boolean sendLoginCode(String mobile) {
        //参数校验
        if (!StringUtils.hasText(mobile)) {
            return false;
        }

        //找到手机号对应的Redis缓存Key
        String smsCacheKey = smsCacheKeyBuilder.buildSmsLoginCodeKey(mobile);
        //redis中有记录，表示发送过二维码了。
        if (redisTemplate.hasKey(smsCacheKey)) {
            logger.info("手机号 {} 申请短信过于频繁。", mobile);
            return false;
        }
        //redis中没有记录，生成二维码,并缓存到Redis。
        int smsCode = new Random().nextInt(1000, 9999);
        redisTemplate.opsForValue().set(smsCacheKey, smsCode, 60, TimeUnit.SECONDS);
        //发送二维码，保存二维码发送记录
        ThreadPoolManager.commonAsyncPool.execute(() -> {
            //如果发送失败，重新尝试3次。
            for (int i = 0; i < 3; i++) {
                boolean sendStatus = sendSmsToCCP(mobile, smsCode);
                if (sendStatus) {
                    //保存短信推送记录。
                    insertSSMRecord(mobile, smsCode);
                    break;
                }
            }
        });
        //返回成功
        return true;
    }

    private void insertSSMRecord(String mobile, int smsCode) {
        SmsDO smsDO = new SmsDO();
        smsDO.setPhone(mobile);
        smsDO.setCode(smsCode);
        smsMapper.insert(smsDO);

    }

    private boolean sendSmsToCCP(String mobile, Integer code) {
        logger.info("发送短信，当前是否测试环境: {},手机号: {},验证码: {}", smsccpConfig.getTest(), mobile, code);
        //本地测试环境，就不发短信了。
        if (smsccpConfig.getTest()) {
            return true;
        } else {
            try {
                //请求地址
                String serverIp = smsccpConfig.getSmsServerIp();
                //请求端口
                String serverPort = String.valueOf(smsccpConfig.getPort());
                //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
                String accountSId = smsccpConfig.getAccountSId();
                String accountToken = smsccpConfig.getAccountToken();
                //请使用管理控制台中已创建应用的APPID
                String appId = smsccpConfig.getAppId();
                CCPRestSmsSDK sdk = new CCPRestSmsSDK();
                sdk.init(serverIp, serverPort);
                sdk.setAccount(accountSId, accountToken);
                sdk.setAppId(appId);
                sdk.setBodyType(BodyType.Type_JSON);
                //测试账号，所有短信都会往这里发送
                String to = smsccpConfig.getTestPhone();
                //正式环境，发送到实际手机号码
//                String to = String.valueOf(mobile);
                String templateId = SmsTemplateIDEnum.SMS_LOGIN_CODE_TEMPLATE.getTemplateId();
                //测试开发支持的文案如下：【云通讯】您的验证码是{1}，请于{2}分钟内正确输入。其中{1}和{2}为短信模板参数。
                String[] datas = {String.valueOf(code), "1"};
                //可选 扩展码，四位数字 0~9999
                String subAppend = "1234";
                String reqId = UUID.randomUUID().toString();
                //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
                HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas, subAppend, reqId);
                if ("000000".equals(result.get("statusCode"))) {
                    //正常返回输出data包体信息（map）
                    HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
                    Set<String> keySet = data.keySet();
                    for (String key : keySet) {
                        Object object = data.get(key);
                        logger.info("key is {},object is {}", key, object);
                    }
                } else {
                    //异常返回输出错误码和错误信息
                    logger.error("错误码:{},错误信息:{}", result.get("statusCode"), result.get("statusMsg"));
                    return false;
                }
                return true;
            } catch (Exception e) {
                logger.error("[sendSmsToCCP] error is ", e);
                throw new RuntimeException(e);
            } finally {
                return false;
            }
        }
    }
}
