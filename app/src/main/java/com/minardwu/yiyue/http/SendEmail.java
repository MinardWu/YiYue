package com.minardwu.yiyue.http;

import com.minardwu.yiyue.http.result.FailResult;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * Created by MinardWu on 2018/1/29.
 */

public class SendEmail {

    public static String myEmailAccount = "minardwu100@sina.com";
    public static String myEmailPassword = "3430530";
    public static String myEmailSMTPHost = "smtp.sina.com";
    public static String receiveMailAccount = "1192293859@qq.com";

    public static void sendMail(final String content, final HttpCallback callback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    send(content);
                    callback.onSuccess("success");
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFail(new FailResult(0,e.toString()));
                }
            }
        }).start();
    }

    public static void send(String content) throws Exception {
        //创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        //根据配置创建会话对象, 用于和邮件服务器交互
        //Session session = Session.getInstance(props);
        //session.setDebug(true);
        MyAuthenticator myAuthenticator = new MyAuthenticator(myEmailAccount, myEmailPassword);
        Session session = Session.getDefaultInstance(props, myAuthenticator);

        //创建消息
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myEmailAccount,"一乐用户", "UTF-8")); // 来源邮箱
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMailAccount));// 目的邮箱
        message.setSubject("一乐使用反馈");//设置邮件的标题
        message.setContent(content,"text/html;charset=UTF-8");
        message.setSentDate(new Date());// 设置发件时间
        message.saveChanges();// 保存设置

        //连接、发送、关闭
        Transport transport = session.getTransport();
        transport.connect(myEmailAccount, myEmailPassword);
        transport.sendMessage(message, message.getAllRecipients());//发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.close();//关闭连接
    }

    static class MyAuthenticator extends javax.mail.Authenticator {
        private String strUser;
        private String strPwd;

        public MyAuthenticator(String user, String password) {
            this.strUser = user;
            this.strPwd = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(strUser, strPwd);
        }
    }
}
