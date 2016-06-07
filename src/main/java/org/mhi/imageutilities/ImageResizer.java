/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mhi.imageutilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author MaHi
 */
public class ImageResizer {

    /**
     * Resizes an image to a absolute width and height (the image may not be
     * proportional)
     *
     * @param inc
     * @param scaleWidth
     * @param scaleHeight
     * @return
     * @throws IOException
     */
    public byte[] resize(byte[] inc, int scaleWidth, int scaleHeight) throws IOException {

        // reads input image
        InputStream in = new ByteArrayInputStream(inc);
        BufferedImage image = ImageIO.read(in);
        // creates output image
        BufferedImage outputImage = new BufferedImage(scaleWidth, scaleHeight, image.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(image, 0, 0, scaleWidth, scaleHeight, null);
        g2d.dispose();

        // Convert image to byteArray
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, "jpg", baos);
        baos.flush();
        byte[] result = baos.toByteArray();
        baos.close();

        return result;
    }
}
