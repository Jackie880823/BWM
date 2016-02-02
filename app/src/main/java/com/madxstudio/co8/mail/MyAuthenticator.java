package com.madxstudio.co8.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 如果需要身份认证，则创建一个密码验证器
 *
 */
public class MyAuthenticator extends Authenticator{  
    String userName=null;
    String password=null;
    
    public MyAuthenticator(){  
    }
    
    public MyAuthenticator(String username, String password) {   
        this.userName = username;   
        this.password = password;   
    }  
    
    protected PasswordAuthentication getPasswordAuthentication(){  
        return new PasswordAuthentication(userName, password);  
    }  
}