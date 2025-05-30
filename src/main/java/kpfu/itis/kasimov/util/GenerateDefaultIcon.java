package kpfu.itis.kasimov.util;

import java.util.Random;

public class GenerateDefaultIcon {

    public static String generateDefaultIcon(String name) {

        char firstLetter = name.charAt(0);

        String randomColor = generateRandomColor();

        String url = String.format("https://placehold.co/400x400/%s/FFFFFF?text=%c", randomColor, firstLetter);

        return url;
    }

    private static String generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("%02X%02X%02X", r, g, b);
    }

}
