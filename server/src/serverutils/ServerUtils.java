package serverutils;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.lang.Math.abs;

public class ServerUtils {
    // 发件人的 邮箱 和 密码（替换为自己的邮箱和密码）
    // PS: 某些邮箱服务器为了增加邮箱本身密码的安全性，给 SMTP 客户端设置了独立密码（有的邮箱称为“授权码”）,
    //     对于开启了独立密码的邮箱, 这里的邮箱密码必需使用这个独立密码（授权码）。
    public static String myEmailAccount = "city_story_map@163.com";
    public static String myEmailPassword = "csmcode0901";
    // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般(只是一般, 绝非绝对)格式为: smtp.xxx.com
    // 网易163邮箱的 SMTP 服务器地址为: smtp.163.com
    public static String myEmailSMTPHost = "smtp.163.com";

    // linux windows
    public static String OS = "linux";
    public static long VALID_TIME = 600000;

    public static String SERVER_PATH = "http://www.nelson-cheung.cn:8080";

    //保存图片
    public static void save(byte[] content, String path, String name) throws IOException {
        if (content == null || content.length == 0) {
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        List<String> list = new ArrayList<String>();
        list.add(path);
        list.add(name);

        OutputStream out = new FileOutputStream(toFilePath(list, OS));
        out.write(content);
        out.close();
    }

    //
    public static void createDataDir(String account) {
        List<String> list = getDataPath();
        list.add(account);

        String path = toFilePath(list, OS);

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        list.add("head_portrait");
        path = toFilePath(list, OS);

        OutputStream out = null;

        try {
            out = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        list = getDataPath();
        list.add("head_portrait");

        byte[] image = new byte[0];

        try {
            image = getImage(toFilePath(list, OS));
            out.write(image);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    public static byte[] getImage(String path) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = new FileInputStream(path);

        while ((len = in.read(buffer, 0, 1024)) != -1) {
            out.write(buffer, 0, len);
        }

        return out.toByteArray();
    }

    public static void sendEmail(String subject, String content, String receiveEMailAccount) throws MessagingException, UnsupportedEncodingException {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();
        // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");
        // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);
        // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");
        // 需要请求认证

        // PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
        //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
        //     打开下面 /* ... */ 之间的注释代码, 开启 SSL 安全连接。

        // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
        //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
        //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
        /*final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);
*/

        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);
        // 设置为debug模式, 可以查看详细的发送 log

        // 3. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(new InternetAddress(myEmailAccount, "城市故事地图", "UTF-8"));

        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveEMailAccount, "尊敬的用户", "UTF-8"));

        // 4. Subject: 邮件主题
        message.setSubject(subject, "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(content, "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(new Date());

        // 7. 保存设置
        message.saveChanges();
        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        //
        //    PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
        //           仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
        //           类型到对应邮件服务器的帮助网站上查看具体失败原因。
        //
        //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
        //           (1) 邮箱没有开启 SMTP 服务;
        //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
        //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
        //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
        //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
        //
        //    PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
        transport.connect(myEmailAccount, myEmailPassword);

        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 7. 关闭连接
        transport.close();
    }

    //生成4位验证码
    public static String generateVerificationCode() {
        long t = System.currentTimeMillis();
        Random random = new Random(t);
        int num = abs(random.nextInt());

        num %= 10000;

        if (num == 0) {
            return "0000";
        } else if (num < 10) {
            return "000" + num;
        } else if (num < 100) {
            return "00" + num;
        } else if (num < 1000) {
            return "0" + num;
        } else {
            return String.valueOf(num);
        }
    }

    public static String toFilePath(List<String> list, String type) {
        StringBuffer buffer = new StringBuffer(list.get(0));
        String str = "";

        if (type.equals("windows")) {
            str = "\\";
        } else if (type.equals("linux")) {
            str = "/";
        } else if (type.equals("mysql")) {
            if (OS.equals("windows")) {
                str = "\\\\";
            } else if (OS.equals("linux")) {
                str = "/";
            }
        }

        for (int i = 1; i < list.size(); ++i) {
            buffer.append(str).append(list.get(i));
        }

        return buffer.toString();
    }

    public static List<String> getDataPath() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("/");
        list.add("usr");
        list.add("local");
        list.add("tomcat");
        list.add("webapps");
        list.add("csm");
        return list;
    }

    public static List<String> getRelativeDataPath() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("csm");
        return list;
    }
    public static String md5Encode(byte[] content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(content);
            content = md.digest();
            return bytesToHexString((content));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String bytesToHexString(byte[] array) {
        StringBuffer buffer = new StringBuffer();
        String temp;

        for (int i = 0; i < array.length; ++i) {
            temp = Integer.toHexString(array[i] & 0xff);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            buffer.append(temp);
        }
        return buffer.toString();
    }

    //base64解码
    public static String httpBase64Decode(String content) {
        try {
            content = content.replace(' ', '+');
            byte[] temp = content.getBytes("utf-8");
            temp = Base64.getDecoder().decode(temp);
            return new String(temp, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    //base64编码
    public static String httpBase64Encode(String content) {
        try {
            byte[] temp = content.getBytes("utf-8");
            temp = Base64.getEncoder().encode(temp);
            return new String(temp, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String inputStreamToString(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        try {
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(out.toByteArray());
    }
}
