/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.mahi.manager.ActionDAO;
import org.mahi.persistence.StonyImages;
import org.mahi.persistence.StonyImgCat;
import org.mhi.imageutilities.ImageResizerAdv;

/**
 *
 * @author MaHi
 */
@WebServlet(urlPatterns = {"/gallery/*"})
public class image extends HttpServlet {

    @EJB
    ActionDAO service;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws org.apache.tika.mime.MimeTypeException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MimeTypeException {
        String CATALINA_HOME = System.getProperty("catalina.base");
        String imgCatByID = request.getParameter("imgCat");
        String imgByID = request.getParameter("img");
        String imgPrevByID = request.getParameter("imgPrev");

        // IMAGE BY ID
        if (imgByID != null) {
            StonyImages img = service.findImageById(Long.valueOf(imgByID));
            if (img != null && img.getFileName() != null) {
                File file = new File(CATALINA_HOME + img.getPath() + "/" + img.getFileName());
                InputStream iStream = new FileInputStream(file);

                // StonyImages as ByteArray
                byte[] imageRaw = IOUtils.toByteArray(iStream);

                response.setContentLength(imageRaw.length);
                response.getOutputStream().write(imageRaw);
            }else{
                Logger.getLogger(image.class.getName()).log(Level.WARNING,"Image ID (" + imgByID + ") ist not valid. Req-URL: " + request.getRequestURI());
                Logger.getLogger(image.class.getName()).log(Level.WARNING,"UserAgent (" + request.getHeader("user-agent") + ")");
            }
        }

        if (imgPrevByID != null) {
            int targetWidth = 120;
            int targetHeight = 120;
            byte[] imageRaw;

            StonyImages img = service.findImageById(Long.valueOf(imgPrevByID));
            
             if (img != null && img.getFileName() != null) {

                File file = new File(CATALINA_HOME + img.getPath() + "/" + img.getFileName());
                InputStream iStream = new FileInputStream(file);

                BufferedImage buff = ImageIO.read(new ByteArrayInputStream(IOUtils.toByteArray(iStream)));

                // Mime-Type
                MimeType mime = MimeTypes.getDefaultMimeTypes().forName(img.getcTyp());
                String extension = mime.getExtension(); // .jpg

                if (buff.getWidth() > targetWidth && buff.getHeight() > targetHeight) {
                    ImageResizerAdv ir2 = new ImageResizerAdv();
                    buff = ir2.progressive_bilinear_scaling(buff, targetWidth, targetHeight);
                }

                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    ImageIO.write(buff, extension.substring(1), bos);
                    bos.flush();
                    imageRaw = bos.toByteArray();
                }
                response.setContentLength(imageRaw.length);
                response.getOutputStream().write(imageRaw);
            }else{
                Logger.getLogger(image.class.getName()).log(Level.WARNING,"ImagePrev ID (" + imgByID + ") ist not valid. Req-URL: " + request.getRequestURL());
            }

        }

        // CAT-IMAGE BY ID
        if (imgCatByID != null) {
            int targetWidth = 120;
            int targetHeight = 120;
            byte[] imageRaw;

            StonyImgCat img = service.findImgCatById(Long.valueOf(imgCatByID));
//            BufferedImage buff = ImageIO.read(new ByteArrayInputStream(img.getFileBlob()));
            if (img != null && img.getFilename() != null) {

                File file = new File(CATALINA_HOME + img.getPath() + "/" + img.getFilename());
                InputStream iStream = new FileInputStream(file);

                BufferedImage buff = ImageIO.read(new ByteArrayInputStream(IOUtils.toByteArray(iStream)));

                // Mime-Type
                MimeType mime = MimeTypes.getDefaultMimeTypes().forName(img.getcTyp());
                String extension = mime.getExtension(); // .jpg

                if (buff.getWidth() > targetWidth && buff.getHeight() > targetHeight) {
                    ImageResizerAdv ir2 = new ImageResizerAdv();
                    buff = ir2.progressive_bilinear_scaling(buff, targetWidth, targetHeight);
                }

                try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    ImageIO.write(buff, extension.substring(1), bos);
                    bos.flush();
                    imageRaw = bos.toByteArray();
                }
                response.setContentLength(imageRaw.length);
                response.getOutputStream().write(imageRaw);
            }else{
                Logger.getLogger(image.class.getName()).log(Level.WARNING,"ImageCat ID (" + imgByID + ") ist not valid. Req-URL: " + request.getRequestURL());
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (MimeTypeException ex) {
            Logger.getLogger(image.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (MimeTypeException ex) {
            Logger.getLogger(image.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
