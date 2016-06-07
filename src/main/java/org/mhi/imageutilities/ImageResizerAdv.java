/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mhi.imageutilities;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author techadmin
 */
public class ImageResizerAdv {

    public BufferedImage progressive_bilinear_scaling(BufferedImage img, int targetWidth, int targetHeight) {
        int cnt = 0;
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = img;
        BufferedImage scratchImage = null;
        Graphics2D g2 = null;

        int width = img.getWidth();
        int height = img.getHeight();

        int prevW = width;
        int prevH = height;

        do {
            if (width > targetWidth) {
                width /= 2;
                // Übernimm Ziel-Vorgabe wenn 'width' kleiner als Ziel-Vorgabe.
                width = (width < targetWidth) ? targetWidth : width;
            }

            if (height > targetHeight) {
                height /= 2;
                // Übernimm Ziel-Vorgabe wenn 'height' kleiner als Ziel-Vorgabe.
                height = (height < targetHeight) ? targetHeight : height;
            }

            if (scratchImage == null) {
                scratchImage = new BufferedImage(width, height, type);
                g2 = scratchImage.createGraphics();
            }

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(ret, 0, 0, width, height, 0, 0, prevW, prevH, null);

            prevW = width;
            prevH = height;
            ret = scratchImage;
            cnt++;
            if (cnt > 50) {
                Logger.getLogger(ImageResizerAdv.class.getName())
                        .log(Level.SEVERE, "Billinear Scaling detect a possible inifinty loop and break.");
                break;
            }
        } while (width != targetWidth || height != targetHeight);

        // Flush the graphic und resources.
        if (g2 != null) {
            g2.dispose();
        }

        if (targetWidth != ret.getWidth() || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth, targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }

        return ret;

    }

}
