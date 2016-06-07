/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.mahi.persistence.Stats;
import org.mhi.auth.SmtpAuthenticator;
import org.mahi.persistence.StonyArtMain;
import org.mahi.persistence.StonyArticle;
import org.mahi.persistence.StonyImages;
import org.mahi.persistence.StonyImgCat;
import org.mahi.persistence.StonyImgMain;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author techadmin
 */
@ViewScoped
@ManagedBean
public class BeanManager implements Serializable {

    @Inject
    private ActionDAO service;

    // Path 
    private String CATALINA_HOME = System.getProperty("catalina.base");

    // Email
    private String name;
    private String email;
    private String nachricht;
    private boolean sendMailDone;

    // About
    private boolean editArticle;
    private String artMainName;
    private String articleText;
    private String articleTitle;
    private StonyArticle article;
    private StonyArtMain artMain;
    private Long artMainID;
    private List<StonyArtMain> artMainList;

    // Gallerie
    private String imgMainName;
    private String imgCatTitle;
    private String imgCatDesc;
    private UploadedFile imgCatPreview;
    private StonyImgMain imgMain;
    private StonyImgCat imgCat;
    private Long imgMainID;
    private Long imgCatID;
    private List<StonyImgMain> imgMainListAll;
    private List<StonyImgMain> imgMainListNotHidden;
    private List<StonyImgCat> imgCatList;
    

    @PostConstruct
    public void initialize() {
        System.getProperty("java.io.tmpdir");
        System.setProperty("objectdb.temp.no-detach", "true");
        refreshArticle();
        refreshGallery();
        sendMailDone = false;
    }

    public void refreshArticle() {
        // ArtMain List
        this.artMainList = service.getArtMain();

        // Set StartArtikel mit dem flag (only once).
        initStartArticle();
    }

    public void refreshGallery() {
        // ImgMain List
        this.imgMainListNotHidden = service.getImgMainNotHidden();
        this.imgMainListAll = service.getImgMainAll();
        // Set StartGallery mit dem flag (only once). 
        initStartImgMain();
    }

    public void newArtMain() {
        StonyArtMain obj = new StonyArtMain();
        obj.setName(this.artMainName);
        obj.setTimestmp(new Date());
        service.persist(obj);
        this.artMain = obj;

        refreshArticle();
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Neuer Main Artikel erstellt.");
    }

    public void newArticel() {
        artMain = (StonyArtMain) service.findArtMainById(artMainID);
        StonyArticle obj = new StonyArticle();
        // Fill Data
        obj.setOnlyStartPage(false);
        obj.setText(articleText);
        obj.setTitel(articleTitle);
        obj.setStartHexRGB("#F5F5F5");
        obj.setTimestmp(new Date());

        // Initialize Relation
        obj.setArtMain(artMain);

        // Sync
        artMain.getArticleList().add(obj);
        service.update(artMain);

        refreshArticle();
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Neuer Artikel erstellt.");
    }

    public void initStartArticle() {
        if (artMainList != null) {
            for (StonyArtMain t : artMainList) {
                if (t.getArticleList() != null) {
                    for (StonyArticle z : t.getArticleList()) {
                        if (z.isStartArticle()) {
                            this.article = z;
                        }
                    }
                } else {
                    Logger.getLogger(BeanManager.class.getName()).log(Level.WARNING, "Unexpected NullPointer.");
                }
            }
        }
    }

    public void setStartArticle() {
        if (artMainList != null) {
            for (StonyArtMain t : artMainList) {
                for (StonyArticle z : t.getArticleList()) {
                    z.setStartArticle(false);
                    z.setStartHexRGB("#F5F5F5");
                    service.update(z);
                }
            }
        }
        this.article.setStartArticle(true);
        this.article.setStartHexRGB("#08A600");

        service.update(article);
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Start Artikel wurde festgelegt.");
    }

    public void initStartImgMain() {
        for (StonyImgMain m : imgMainListAll) {
            if (m.isStartImgMain()) {
                this.imgMain = m;
            }
        }
        for (StonyImgMain m : imgMainListNotHidden) {
            if (m.isStartImgMain()) {
                this.imgMain = m;
            }
        }
        if (imgMain == null) {
            imgMain = new StonyImgMain();
            System.out.println("kein imgMain gefunden");
        }

    }

    public void setStartGallery() {
        for (StonyImgMain m : imgMainListAll) {
            m.setStartImgMain(false);
            m.setStartHexRGB("#F5F5F5");
            service.update(m);
        }
        this.imgMain.setStartImgMain(true);
        this.imgMain.setStartHexRGB("#08A600");

        service.update(imgMain);
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Start Gallery wurde festgelegt.");
    }

    public void setThisArticle(StonyArticle article) {
        this.article = article;
    }

    public void saveThisArticle(StonyArticle article) {
        service.update(article);
        this.editArticle = false;
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Artikel gespeichert.");
    }

    public void setThisArtMain(StonyArtMain artMain) {
        this.artMain = artMain;
        this.artMainID = artMain.getId();
    }

    public void setHiddenGallery() {
//        for (StonyImgMain m : imgMainListAll) {
//            m.setHiddenGallery(false);
//        }
        if (imgMain.isHiddenGallery()) {
            this.imgMain.setHiddenGallery(false);
        } else {
            this.imgMain.setHiddenGallery(true);
        }

        service.update(imgMain);
        this.imgMain = service.findImgMainById(imgMain.getId());
    }

    public void saveImages(FileUploadEvent event) throws IOException, MimeTypeException {
        StonyImages obj = new StonyImages();
        obj.setImgCat(imgCat);
        obj.setTimestmp(new Date());

        // Make Directory
        String directory = createDirectory(imgCat.getName());  // Create Directory under /images/*imgCatName*/
        obj.setPath("/images/" + this.imgCat.getName());

        // Mime-Type
        MimeType mime = MimeTypes.getDefaultMimeTypes().forName(event.getFile().getContentType());
        String extension = mime.getExtension(); // .jpg
        obj.setcTyp(event.getFile().getContentType());

        // Unique Filename
        String filename = UUID.randomUUID().toString() + extension;
        obj.setFileName(filename);

        writeToDirectory(directory + "/", event.getFile().getInputstream(), filename);

//        image.setFileBlob(IOUtils.toByteArray(upload.getInputstream()));
        imgCat.getImages().add(obj);
        service.update(imgCat);

        refreshGallery();
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Bilder gespeichert.");
    }

    /**
     * Edit StonyArticle for Primefaces Editor to show up.
     */
    public void editArticleBool() {
        this.editArticle = true;
    }

    /**
     * Add new Main Category
     */
    public void newImgMain() {
        StonyImgMain obj = new StonyImgMain();
        obj.setName(this.imgMainName);
        obj.setStartHexRGB("#F5F5F5");
        obj.setTimestmp(new Date());
        service.persist(obj);
        this.imgMain = obj;

        refreshGallery();
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Neue Main Gallery erstellt.");
    }

    public UploadedFile getImgCatPreview() {
        return imgCatPreview;
    }

    public void setImgCatPreview(UploadedFile imgCatPreview) {
        this.imgCatPreview = imgCatPreview;
    }

    public void handleFileUpload(FileUploadEvent event) {
        System.out.println(event.getFile().getFileName());
        this.imgCatPreview = event.getFile();
    }

    /**
     * Add new Image Sub Category
     *
     * @throws java.io.IOException
     * @throws org.apache.tika.mime.MimeTypeException
     */
    public void newImgCat() throws IOException, MimeTypeException {
        StonyImgCat obj = new StonyImgCat();
        // Fill Data
        obj.setName(this.imgCatTitle);
        obj.setDescription(this.imgCatDesc);
        obj.setTimestmp(new Date());

        if (imgCatPreview != null) {
            // Make Directory
            String directory = createDirectory(this.imgCatTitle);  // Create Directory under /images/*imgCatName*/
            obj.setPath("/images/" + this.imgCatTitle.toLowerCase());

            // Mime-Type
            MimeType mime = MimeTypes.getDefaultMimeTypes().forName(imgCatPreview.getContentType());
            String extension = mime.getExtension(); // .jpg
            // Unique Filename
            String filename = UUID.randomUUID().toString() + extension;
            obj.setFilename(filename);
            obj.setcTyp(imgCatPreview.getContentType());

            // Preview Image
            writeToDirectory(directory + "/", imgCatPreview.getInputstream(), filename);
        } else {
            // Dummy Image
            obj.setFilename("dummy_vorschau.png");
            obj.setPath(createDirectory("system"));
            obj.setcTyp("image/png");
        }
        // Initialize Relation
        imgMain.getImgCatList().add(obj);
        obj.setImgMain(imgMain);
        // Sync
        service.update(imgMain);
        // Update ImgMain
        this.imgMain = service.findImgMainById(imgMain.getId());
//        refreshGallery();
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Neue Gallery Kategorie erstellt.");
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Neuer Ordner ("+ obj.getPath() +") erstellt.");
    }

    public String createDirectory(String name) {
        String uri = CATALINA_HOME + "/images/" + name.toLowerCase();
        File file = new File(uri);
        if (!file.exists()) {
            if (file.mkdirs()) {
                Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Directory is created!");
            } else {
                Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, "Failed to create directory!");
            }
        }
        
        return uri;
    }

    public void writeToDirectory(String directory, InputStream file, String filename) {
        OutputStream out;
        try {
            out = new FileOutputStream(directory + filename);
            byte[] buf = new byte[1024];
            int len;
            while ((len = file.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setThisImgMain(StonyImgMain imgMain) {
        this.imgMain = imgMain;
        this.imgCat = null;
    }

    public void setThisImgCat(StonyImgCat imgCat) {
        this.imgCat = imgCat;
    }

    public void removeArtMain(StonyArtMain obj) {
        service.remove(obj);
        refreshArticle();
         Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Main Artikel gelöscht. " + obj.getName());
    }

    public void removeArticle(StonyArticle obj) {
        obj.getArtMain().getArticleList().remove(obj);
        service.update(obj.getArtMain());
        refreshArticle();
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Artikel gelöscht. " + obj.getTitel());
    }

    public void removeImgMain(StonyImgMain obj) {
        for (StonyImgCat cat : obj.getImgCatList()) {
            // Get File Path to ImgCat Directory
            File file = new File(cat.getPath());
            try {
                if (!cat.getPath().endsWith("system")) {
                    // Remove Files and Content from Disk
                    FileUtils.deleteDirectory(file);
                    Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Removed sucessfully directory ({0}) from file system.", file);
                }
            } catch (IOException ex) {
                Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Recursivly delete all ImCat with orphanRemove.
        service.remove(obj);

        refreshGallery();
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Main Gallery und Subfolder gelöscht. " + obj.getName());
    }

    public void removeImgCat(StonyImgCat obj) {
        // Get File Path to ImgCat Directory
        File file = new File(obj.getPath());
        try {
            if (!obj.getPath().endsWith("system")) {
                // Remove Files and Content from Disk
                FileUtils.deleteDirectory(file);
                Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Removed sucessfully directory ({0}) from file system.", file);
                // Remove Obj from Database
                obj.getImgMain().getImgCatList().remove(obj);
                service.update(obj.getImgMain());
                Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Removed sucessfully from database.");
            }
        } catch (IOException ex) {
            Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        refreshGallery();
        Logger.getLogger(BeanManager.class.getName()).log(Level.INFO, "Main Image Kategorie und Subfolder gelöscht. " + obj.getName());
    }

    public void sendMail() {
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", true); // added this line
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.user", "username");
        props.put("mail.smtp.password", "password");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);

        Session session = Session.getInstance(props, null);

        MimeMessage message = new MimeMessage(session);

        // Create the email addresses involved
        try {
            //  Adresses
            Address fromAdress = new InternetAddress("fjubuu@gmail.com");
            Address toAdress = new InternetAddress("stonys-glas@bluewin.ch");
//            Address toAdress = new InternetAddress("m.hinrichs@gmx.com");

            message.setSubject(name + " benutzte stonys-glas.ch - Kontaktformular");
            message.setFrom(fromAdress);
            message.setContent("<h3> Email from: " + email + " ("+ name+") </h3>" + "<hr/>" + "<p>" + nachricht + "</p>", "text/html");
            message.setRecipient(Message.RecipientType.TO, toAdress);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", "fjubuu@gmail.com", "rag1ceup1r#1984#");
            transport.sendMessage(message, message.getAllRecipients());
            sendMailDone = true;

        } catch (AddressException e) {
            Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, e);
        } catch (MessagingException e) {
            Logger.getLogger(BeanManager.class.getName()).log(Level.SEVERE, null, e);
        } finally {

        }

    }

    /**
     * Send an Email to a specifed static Address. Very Simple static custom
     * SendMail Form.
     */
//    public void sendMail() {
//        Properties props = new Properties();
//        props.put("mail.transport.protocol", "smtps");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtps.host", "smtp.gmail.com");
//        props.put("mail.smtps.port", 465);
//        props.put("mail.smtps.quitwait", "false");
//        //To use TLS
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//
//        //To use SSL
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.port", "465");
//
//        try {
//            // 1 - get a Mail Session
//            SmtpAuthenticator authentication = new SmtpAuthenticator();
//            Session session = Session.getDefaultInstance(props, authentication);
//            session.setDebug(true);
//
//            // 2 - create Message
//            Message msg = new MimeMessage(session);
//            msg.setSubject(name + " von stonys-glas.ch - Kontaktformular");
//            msg.setContent("<h3> Email from: " + email + "</h3>" + "<hr/>" + "<p>" + nachricht + "</p>", "text/html");
//
//            // 3 - Adresses
//            Address fromAdress = new InternetAddress("fjubuu@gmail.com");
////            Address toAdress = new InternetAddress("stonys-glas@bluewin.ch");
//            Address toAdress = new InternetAddress("m.hinrichs@gmx.com");
//            msg.setFrom(fromAdress);
//            msg.setRecipient(Message.RecipientType.TO, toAdress);
//
//            Transport transport = session.getTransport("smtp");
//            transport.connect("smtp.gmail.com", 465, "fjubuu@gmail.com", "rag1ceup1r#1984#");
//            Transport.send(msg);
//            sendMailDone = true;
//
//        } catch (MessagingException ex) {
//            Logger.getLogger(BeanManager.class
//                    .getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
    
    public long getAnzahlVisitor(){
        return service.cntVisitor();
    }
    
    public long getAnzahlVisitorAll(){
        return service.cntVisitorAll();
    }
    
    public List<Stats> getLastTenVisitor(){
        return service.lastTenVisitor();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNachricht() {
        return nachricht;
    }

    public void setNachricht(String nachricht) {
        this.nachricht = nachricht;
    }

    public List<StonyArtMain> getArtMainList() {
        return artMainList;
    }

    public void setArtMainList(List<StonyArtMain> artMainList) {
        this.artMainList = artMainList;
    }

    public String getArtMainName() {
        return artMainName;
    }

    public void setArtMainName(String artMainName) {
        this.artMainName = artMainName;
    }

    public String getArticleText() {
        return articleText;
    }

    public void setArticleText(String articleText) {
        this.articleText = articleText;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public Long getArtMainID() {
        return artMainID;
    }

    public void setArtMainID(Long artMainID) {
        this.artMainID = artMainID;
    }

    public StonyArticle getArticle() {
        return article;
    }

    public void setArticle(StonyArticle article) {
        this.article = article;
    }

    public StonyArtMain getArtMain() {
        return artMain;
    }

    public void setArtMain(StonyArtMain artMain) {
        this.artMain = artMain;
    }

    public boolean isEditArticle() {
        return editArticle;
    }

    public void setEditArticle(boolean editArticle) {
        this.editArticle = editArticle;
    }

    public StonyImgMain getImgMain() {
        return imgMain;
    }

    public void setImgMain(StonyImgMain imgMain) {
        this.imgMain = imgMain;
    }

    public StonyImgCat getImgCat() {
        return imgCat;
    }

    public void setImgCat(StonyImgCat imgCat) {
        this.imgCat = imgCat;
    }

    public Long getImgMainID() {
        return imgMainID;
    }

    public void setImgMainID(Long imgMainID) {
        this.imgMainID = imgMainID;
    }

    public Long getImgCatID() {
        return imgCatID;
    }

    public void setImgCatID(Long imgCatID) {
        this.imgCatID = imgCatID;
    }

    public List<StonyImgCat> getImgCatList() {
        return imgCatList;
    }

    public void setImgCatList(List<StonyImgCat> imgCatList) {
        this.imgCatList = imgCatList;
    }

    public String getImgMainName() {
        return imgMainName;
    }

    public void setImgMainName(String imgMainName) {
        this.imgMainName = imgMainName;
    }

    public List<StonyImgMain> getImgMainListNotHidden() {
        return imgMainListNotHidden;
    }

    public void setImgMainListNotHidden(List<StonyImgMain> imgMainListNotHidden) {
        this.imgMainListNotHidden = imgMainListNotHidden;
    }

    public String getImgCatTitle() {
        return imgCatTitle;
    }

    public void setImgCatTitle(String imgCatTitle) {
        this.imgCatTitle = imgCatTitle;
    }

    public String getImgCatDesc() {
        return imgCatDesc;
    }

    public boolean isSendMailDone() {
        return sendMailDone;
    }

    public void setSendMailDone(boolean sendMailDone) {
        this.sendMailDone = sendMailDone;
    }

    public void setImgCatDesc(String imgCatDesc) {
        this.imgCatDesc = imgCatDesc;
    }

    public List<StonyImgMain> getImgMainListAll() {
        return imgMainListAll;
    }

    public void setImgMainListAll(List<StonyImgMain> imgMainListAll) {
        this.imgMainListAll = imgMainListAll;
    }
    
    
}
