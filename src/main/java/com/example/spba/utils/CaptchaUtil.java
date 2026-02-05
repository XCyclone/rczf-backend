package com.example.spba.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码生成工具类
 */
public class CaptchaUtil {

    private static final char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final Random random = new Random();

    /**
     * 生成随机验证码字符串
     * @param length 验证码长度
     * @return 验证码字符串
     */
    public static String generateCaptchaText(int length) {
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < length; i++) {
            captcha.append(chars[random.nextInt(chars.length)]);
        }
        return captcha.toString();
    }

    /**
     * 生成验证码图片
     * @param width 图片宽度
     * @param height 图片高度
     * @param captchaText 验证码文本
     * @return 验证码图片BufferedImage对象
     */
    public static BufferedImage generateCaptchaImage(int width, int height, String captchaText) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // 绘制干扰线
        g.setColor(Color.GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码字符
        for (int i = 0; i < captchaText.length(); i++) {
            char c = captchaText.charAt(i);
            g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
            g.drawString(String.valueOf(c), 20 * i + 10, 25);
        }

        g.dispose();
        return image;
    }
}