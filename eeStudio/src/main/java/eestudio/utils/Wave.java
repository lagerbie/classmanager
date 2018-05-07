package eestudio.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;

/**
 * Création des images représentant les données audio.
 *
 * @author Fabrice Alleau
 * @version 0.96
 * @since version 0.95
 */
public class Wave {
    /**
     * Largeur maximale de l'image pour flash
     */
    private static final int imageWidthMax = 4096;
    /**
     * Hauteur de l'image
     */
    private static final int imageHeight = 128;
    /**
     * Extension des fichiers image
     */
    public static final String imageExtension = "png";

    /**
     * Créer les images représentant les données audio.
     *
     * @param leftChannelFile le nom pour le canal gauche.
     * @param rigthChannelFile le nom pour le canal droit.
     * @param format le format des données audio.
     * @param buffer les données audio.
     * @param begin le temps de départ dans les données.
     * @param end le temps de fin dans les données.
     *
     * @since version 0.94 - version 0.95.12
     */
    public static void createWaveImages(File leftChannelFile, File rigthChannelFile, AudioFormat format,
            ByteBuffer buffer, long begin, long end) {

        int channels = format.getChannels();
        int frameSize = format.getFrameSize();
        int sampleSize = format.getSampleSizeInBits() / 8;
        float sampleRate = format.getSampleRate();

        //nombres de bytes à lire
        int nbSamples = (int) ((end - begin) / 1000.0f * sampleRate);
        int nbBytes = nbSamples * frameSize;
        if (nbSamples <= 0) {
            int[] xPoints = {0, 1023};
            int[] yPoints = {imageHeight / 2, imageHeight / 2};
            draw(leftChannelFile, 1024, imageHeight, xPoints, yPoints, 2);
            draw(rigthChannelFile, 1024, imageHeight, xPoints, yPoints, 2);
            return;
        }

        double width = imageWidthMax;
        int pas = (int) Math.ceil(nbSamples / width);
        int nPoints = nbSamples / pas;
        int bufferSize = pas * frameSize;

        StringBuilder info = new StringBuilder(1024);
        info.append("Création de la représentation audio:\n");
        info.append(String.format("pour nbSamples=%1$d\n", nbSamples));
        info.append(String.format("pour nbBytes=%1$d\n", nbBytes));
        info.append(String.format("pour pas=%1$d\n", pas));
        info.append(String.format("pour bufferSize=%1$d\n", bufferSize));
        info.append(String.format("pour nPoints(witdh)=%1$d\n", nPoints));
        Edu4Logger.debug(info.toString());

        int[] xPoints = new int[nPoints];
        int[] yRPoints = new int[nPoints];
        int[] yLPoints = new int[nPoints];
        byte data[] = new byte[bufferSize];

        //repositionnement de la tête
        int samplePosition = (int) (begin / 1000.0f * sampleRate);
        int offset = samplePosition * format.getFrameSize();
        buffer.position(offset);

        int n = 0;
        double valueMax = 0;
        while (nbBytes >= format.getFrameSize()) {
            //nombres de bytes à lire
            int read = Math.min(nbBytes, data.length);
            //copie les données dans la ligne
            buffer.get(data, 0, read);

            xPoints[n] = n;
//            int firstChannel = bytesToInt(data, 0, sampleSize, format.isBigEndian());
            yLPoints[n] = bytesToInt(data, 0, sampleSize, format.isBigEndian());

            if (yLPoints[n] > valueMax) {
                valueMax = yLPoints[n];
            } else if (yLPoints[n] < -valueMax) {
                valueMax = -yLPoints[n];
            }

            if (channels > 1) {
                yRPoints[n] = bytesToInt(data, 0, sampleSize, format.isBigEndian());
//                int secondChannel = bytesToInt(data, sampleSize, sampleSize, format.isBigEndian());

                if (yRPoints[n] > valueMax) {
                    valueMax = yRPoints[n];
                } else if (yRPoints[n] < -valueMax) {
                    valueMax = -yRPoints[n];
                }
            }

            n++;
            nbBytes -= read;
            if (n == nPoints) {
                break;
            }
        }

//        valueMax = Math.pow(2, sampleSize * 8 -1);
        if (valueMax == 0) {
            valueMax = 1;
        }

        double ratio = imageHeight / 2 / valueMax;
        int zeroOffset = imageHeight / 2;
        for (n = 0; n < nPoints; n++) {
            yLPoints[n] = rescale(yLPoints[n], ratio, zeroOffset);
            yRPoints[n] = rescale(yRPoints[n], ratio, zeroOffset);
        }

        draw(leftChannelFile, nPoints, imageHeight, xPoints, yLPoints, nPoints);
        if (channels > 1) {
            draw(rigthChannelFile, nPoints, imageHeight, xPoints, yRPoints, nPoints);
        } else {
            draw(rigthChannelFile, nPoints, imageHeight, xPoints, yLPoints, nPoints);
        }
    }

    /**
     * Quantification pour que les données prennent toute la hauteur de l'image.
     *
     * @param value la valeur initiale.
     * @param ratio le ratio réprésentant la valeur maximale (=1).
     * @param zeroOffset l'offset du zéro dans l'image.
     *
     * @return la valeur quantifiée.
     *
     * @since version 0.95
     */
    private static int rescale(int value, double ratio, int zeroOffset) {
        return (int) (zeroOffset + value * ratio);
    }

    /**
     * Dessine une liste de points dans un fichier.
     *
     * @param file le fichier image.
     * @param width la largeur de l'image.
     * @param height la hauteur de l'image.
     * @param xPoints les coordonnées horizontales des points.
     * @param yPoints les coordonnées verticales des points.
     * @param nPoints le nombre de points.
     *
     * @since version 0.95 - version 0.96
     */
    private static void draw(File file, int width, int height,
            int[] xPoints, int[] yPoints, int nPoints) {
        file.delete();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        graphics.setBackground(new Color(0, 0, 0, 0));
        if (nPoints > 0) {
            graphics.setColor(new Color(255, 255, 255));
            graphics.drawPolyline(xPoints, yPoints, nPoints);
        }

        try {
            ImageIO.write(image, imageExtension, file);
        } catch (IOException e) {
            Edu4Logger.error(e);
        }

        graphics.dispose();
        image.flush();
    }

    /**
     * Convertie un tableau de Byte signé en un entier.
     *
     * @param bytes le tableau de Byte à convertir.
     * @param offset l'indice à partir du quel on commence.
     * @param length le nombre de Byte à convertir.
     * @param bigEndian l'ordre des bytes.
     *
     * @return l'entier correspondant au tableau de Byte.
     *
     * @since version 0.95 - version 0.95.10
     */
    private static int bytesToInt(byte[] bytes, int offset, int length, boolean bigEndian) {
        int value;
        if (bigEndian) {//big-endian
            value = bytes[offset] << (8 * (length - 1));//byte signé
            for (int i = 1; i < length; i++) {
                value |= ((bytes[offset + i] & 0xFF) << (8 * (length - 1 - i)));//byte non signé
            }
        } else {//little-endian
            value = bytes[offset + length - 1] << (8 * (length - 1));//byte signé
            for (int i = 0; i < length - 1; i++) {
                value |= ((bytes[offset + i] & 0xFF) << (8 * i));//byte non signé
            }
        }//end if

        return value;
    }

//    /**
//     * Convertie un entier en un tableau de Byte.
//     *
//     * @param integer l'entier à convertir.
//     * @param length la dimension du tableau à retourner.
//     * @param bigEndian l'ordre des bytes.
//     * @return le tableau de Byte correspondant à l'entier.
//     * @since version 0.95
//     */
//    private byte[] intToBytes(int integer, int length, boolean bigEndian) {
//        byte bytes[] = new byte[length];
//
//        if(bigEndian) {//big-endian
//            bytes[0] = (byte) (integer >> (8*(length-1)));//signé
//            for(int i=1; i<length; i++) {
//                bytes[i] = (byte) ((integer >> (8*(length-1-i))) & 0xFF);
//            }
//        }
//        else {//little-endian
//            for(int i=0; i<length-1; i++) {
//                bytes[i] = (byte) ((integer >> (8*i)) & 0xFF);
//            }
//            bytes[length-1] = (byte) (integer >> (8*(length-1)));//signé
//        }//end if
//
//        return bytes;
//    }

}
