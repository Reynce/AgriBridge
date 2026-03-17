package com.reyn.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaUtil {
    private static final char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int LENGTH = 4;
    private static final Random random = new Random();

    /**
     * 生成随机验证码文本
     */
    public static String generateText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            sb.append(chars[random.nextInt(chars.length)]);
        }
        return sb.toString();
    }

    /**
     * 创建验证码图片
     */
    public static BufferedImage createImage(String text) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置背景色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置边框
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        // 绘制干扰线
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        // 绘制验证码字符
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            int x = 20 + i * 20;
            g2d.setColor(randomColor());
            g2d.drawString(c, x, 26);
        }

        g2d.dispose();
        return image;
    }

    /**
     * 生成随机颜色
     */
    private static Color randomColor() {
        return new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
}
