/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.filter.security;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.mahi.manager.ActionDAO;
import org.mahi.persistence.Stats;

@WebFilter(filterName = "MainFilter1")
public class MainFilter implements Filter {

    @EJB
    ActionDAO service;

    private static final Logger logger = Logger.getLogger(MainFilter.class.getName());

    /**
     * Checks if user is logged in. If not it redirects to the login.xhtml page.
     *
     * @param request
     * @param response
     * @param chain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        /*
            Das ist der erste Kontakt der Applikation bei einer Request-Anfrage.
            Definiert in der web.xml als Filter und erster in der Kette (Chain).
         */

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        /*
            Lass uns ein paar Information herausfinden.
            Identität.
         */

        String remoteAddr = request.getRemoteAddr();            // The Last Client IP ( Could be Proxy ) 
        String remoteHost = request.getRemoteHost();            // The Last FQDN (Could be Proxy )

        String remoteScheme = request.getScheme();              // Request use http , https , ftp etc.
        String remoteLocale = request.getLocale().getCountry(); // Client Language Settings

        String userAgent = httpRequest.getHeader("user-agent");
        String servletPath = httpRequest.getServletPath();
        String remoteUser = httpRequest.getRemoteUser();      // Nicht der eingeloggt User ? tested.

        HttpSession session = httpRequest.getSession();

        logger.log(Level.INFO, "Main Servlet - Servlet-Path ({0})", servletPath);

        /*
            Session gibt Hinweise über den Benutzer.
            Der Client weiss noch nichts über eine Session bisher.
         */
        if (session != null && session.isNew()) {
            
            
            Stats stats = new Stats();
            //stats.setIpAddr(httpRequest.getRemoteAddr());
            stats.setFqdn(httpRequest.getRemoteHost());
            stats.setLocaleCountry(httpRequest.getLocale().getCountry());
            stats.setLocaleLanguage(httpRequest.getLocale().getLanguage());
            stats.setPort(httpRequest.getRemotePort());
            stats.setScheme(httpRequest.getScheme());
            stats.setUserAgent(httpRequest.getHeader("user-agent"));
            stats.setTimestmp(new Date());
            
            /*  
                How can we detect if the user request has come from a direct URL in browser
                or from a search engine tab?.

                This can be checked by using the user-agent property in the request.
                http://www.mkyong.com/java/java-check-if-web-request-is-from-google-crawler/
             */
            if (!userAgent.isEmpty() && isABot(userAgent)) {
                // Möglichweise ein Bot FakeBot Googlebot
                stats.setBot(true);
            } else {
                stats.setBot(false);
            }
            service.saveVisitor(stats);
            //logger.log(Level.INFO, "Main Servlet - {("+remoteScheme+")} IP ({"+remoteAddr+"}) FQDN ({"+remoteHost+"})");
            //logger.log(Level.INFO, "Main Servlet - remoteLocale ({0})",remoteLocale);
            //logger.log(Level.INFO, "Main Servlet - UserAgent ({0})", userAgent);

        }

        // Weiter Filtern in der web.xml Hierachie or Resource invoke if this last.
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    @Override
    public void destroy() {
        // Nothing to do here!
    }

    public boolean isABot(String userAgent) {
        boolean bot = false;

        if (userAgent.toLowerCase().contains("http://")) {
            bot = true;
        }

        return bot;
    }

}
