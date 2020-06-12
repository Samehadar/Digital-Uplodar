package com.grimeoverhere.uploaderbot.util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class AsciiArt {


    public static String drawString(String text, String artChar, Settings settings) {
        BufferedImage image = getImageIntegerMode(settings.width, settings.height);

        Graphics2D graphics2D = getGraphics2D(image.getGraphics(), settings);
        graphics2D.drawString(text, 6, ((int) (settings.height * 0.67)));

        StringBuilder result = new StringBuilder();
        for (int y = 0; y < settings.height; y++) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int x = 0; x < settings.width; x++) {
                stringBuilder.append(image.getRGB(x, y) == -16777216 ? " " : artChar);
            }

            if (stringBuilder.toString()
                    .trim()
                    .isEmpty()) {
                continue;
            }

            result.append(stringBuilder).append("\n");
        }

        graphics2D.dispose();
        image.flush();

        return result.toString();
    }

    public static Settings generateDefaultSettingsForText(String text) {
        return new Settings(new Font("Sans-serif", Font.BOLD, 18), text.length() * 30, 30); // 30 pixel width per character
    }

    private static BufferedImage getImageIntegerMode(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    private static Graphics2D getGraphics2D(Graphics graphics, Settings settings) {
        graphics.setFont(settings.font);

        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        return graphics2D;
    }

    private static record Settings(Font font, int width, int height) {}

    public static String drawAndGetLogotype() {
        var grime = "GOH Battle";
        return "\n"
                .concat(drawString(grime, "/", generateDefaultSettingsForText(grime)));
    }

    public static String getLogotype() {
        return """
                                                                                                                             \s
                                                                                                     ////                    \s
                    /////////    ///////     ////    ////        ///////                             ////                    \s
                   //////////   /////////    ////    ////        /////////                           ////                    \s
                  ///////////  ///////////   ////    ////        /////////             ////   ////   ////                    \s
                 /////    /// /////   /////  ////    ////        //// /////            ////   ////   ////                    \s
                 ////         ////     ////  ////    ////        //// ///// ////////  ////////////// ////   ///////          \s
                /////        /////     ////  ////    ////        /////////  ////////  ////////////// ////  ////////          \s
                ////         ////      ///// ////////////        /////////  /// ////  ////////////// ////  /////////         \s
                ////    //// ////      ///// ////////////        /////////      ////   ////   ////   //// ////  ////         \s
                /////   /////////      ///// ////////////        ////////// ////////   ////   ////   //// //////////         \s
                /////   //////////     ////  ////    ////        ////  /////////////   ////   ////   //// //////////         \s
                 ////    //// ////     ////  ////    ////        ////  ////////  ///   ////   ////   //// /////              \s
                 //////  //// /////  //////  ////    ////        ////////////// /////  /////  /////  //// ////// ///         \s
                  ///////////  ///////////   ////    ////        ///////////////////// ////// ////// ////  /////////         \s
                   //////////   /////////    ////    ////        /////////  //////////  /////  ///// ////   ////////         \s
                     /////        /////                                       //  ///    ////   ////          ////           \s
                     """;
    }
}

