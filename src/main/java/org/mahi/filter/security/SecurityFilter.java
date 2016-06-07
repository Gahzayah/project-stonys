/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.filter.security;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@WebFilter(filterName="SecurityFilter2")
public class SecurityFilter implements Filter {

    private static final Logger logger = Logger.getLogger(SecurityFilter.class.getName());
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
        logger.log(Level.SEVERE, "Second Kontakt");
        System.out.println("Second Kontakt 222");
        
        HttpSession session = ((HttpServletRequest) request).getSession();      // Session holen
        String servletPath = ((HttpServletRequest) request).getServletPath();
        String responsePage = "index.xhtml";                         // Response or Login Page

        // Mit diesem ManagedBean wird überprüft ob der Benutzer den Filter passieren kann.
        LoginBean loginBean = (LoginBean) session.getAttribute("loginBean");

        // Session besitzt kein Login Bean
        if (loginBean == null) {
            loginBean = new LoginBean();
            session.setAttribute("loginBean", loginBean);
        }

        // Überprüfen ob es die Url gibt die der User versucht zu erreichen.
        // Session ist nicht eingeloggt oder Session möchte Login.
        if (!loginBean.isLoggedin()) {
            // Damit wir beim Login wissen, zu welcher URL unser User möchte,
            // müssen wir hier beginnen, das Session Attribut die nötigen Information mitzugeben      
            session.setAttribute("willAccessThisURI", servletPath);
            // Dieses Attribut wird benötigt um den Client zu signalisieren, du
            // bist dabei eine gesicherte Seite betreteten zu wollen.
            // Der Client benötigt ein Attribut welches er auswerten kann
            request.setAttribute("statuscode", HttpServletResponse.SC_UNAUTHORIZED);
            // Der Benutzer wird jetzt zur Login-Seite weitergeleitet.
//            RequestDispatcher rd = request.getRequestDispatcher(responsePage);
//            rd.forward(request, response);
            ((HttpServletResponse) response).sendRedirect("index.xhtml");
        }
        // Weiter or Stop Filtern
        //chain.doFilter(request, response);
        // Veruracht eine Exception die nach dem Response geworfen wird.
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    @Override
    public void destroy() {
        // Nothing to do here!
    }

}
