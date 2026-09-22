package com.tuling.tulingmall.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 图形验证码工具（算术验证码）。
 *
 * M0-2 迁移说明：原实现依赖 com.ramostear:Happy-Captcha 1.0.1（javax 系，2021 年停止维护，
 * 无 jakarta/SB3 兼容版本，编译期类型不匹配无法保留），此处以纯 JDK 实现等价替代：
 * 生成算术题图片，计算结果存 HttpSession，校验时一次性消费。
 * TODO(M3)：统一认证中心接管登录流程时，验证码能力（图形/行为验证）统一选型重做。
 */
public class CaptchaUtil {

    /** 验证码结果在 session 中的属性名 */
    public static final String SESSION_KEY = "MEMBER_CAPTCHA";

    private static final Random RANDOM = new Random();
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    /**
     * 生成算术验证码图片并写入响应，计算结果存入 session。
     */
    public static void require(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int a = RANDOM.nextInt(10);
        int b = RANDOM.nextInt(10);
        // 随机加减法，避免负数结果
        char operator = (a >= b) ? '+' : '-';
        int result = (operator == '+') ? a + b : a - b;
        String text = a + " " + operator + " " + b + " = ?";

        request.getSession().setAttribute(SESSION_KEY, String.valueOf(result));

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            // 干扰线
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 6; i++) {
                g.drawLine(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT),
                        RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
            }
            // 验证码文本
            g.setColor(new Color(30, 30, 120));
            g.setFont(new Font("Arial", Font.BOLD, 22));
            g.drawString(text, 10, 28);
        } finally {
            g.dispose();
        }

        response.setHeader("Cache-Control", "no-store");
        response.setContentType("image/jpeg");
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }

    /**
     * 校验用户输入的验证码（与 session 中保存的结果比对，一次性消费）。
     *
     * @param ignoreCase 兼容原 Happy-Captcha 调用签名保留；算术结果为数字，无大小写之分
     */
    public static boolean verification(HttpServletRequest request, String code, boolean ignoreCase) {
        Object cached = request.getSession().getAttribute(SESSION_KEY);
        // 校验后立即失效，防止重复使用
        request.getSession().removeAttribute(SESSION_KEY);
        if (cached == null || code == null) {
            return false;
        }
        return cached.toString().equals(code.trim());
    }
}
