/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mahi.filter.security;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author mahi
 */
@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

    // Test-Database
    private static final String[] users = {"techadmin:_techadmin!", "barbara:stonysglas"};

    private String username;
    private String password;
    private boolean loggedin;
    private String willAccessThisURI;

    public String doLogin() {
        // Get every user from our sample database :)
        for (String user : users) {
            String dbUsername = user.split(":")[0];
            String dbPassword = user.split(":")[1];

            // Successful login
            if (dbUsername.equals(username) && dbPassword.equals(password)) {
                loggedin = true;

                return "index.xhtml";
            }
        }
        return "login.xhtml";
    }

    public String doLogout() {
        loggedin = false;
        return "login.xhtml";
    }

    public boolean isLoggedin() {
        return loggedin;
    }

    public void setLoggedin(boolean loggedin) {
        this.loggedin = loggedin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWillAccessThisURI() {
        return willAccessThisURI;
    }

    public void setWillAccessThisURI(String willAccessThisURI) {
        this.willAccessThisURI = willAccessThisURI;
    }

}
