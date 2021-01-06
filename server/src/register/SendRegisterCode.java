package register;

import DataBaseInfo.DataBaseInfo;
import JdbcUtils.JdbcUtils;
import serverutils.ServerUtils;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@WebServlet(name = "SendRegisterCode")
public class SendRegisterCode extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();

        String account = request.getParameter("account");
        String verificationCode = ServerUtils.generateVerificationCode();
        String sql = "update registers set verification_code='"+verificationCode+"' where account='"+account+"'";
        int ans = jdbcUtils.update(sql);

        if( ans == 0 ) {
            sql = "insert into registers(account, verification_code) values('"+account+"', '"+verificationCode+"')";
            jdbcUtils.update(sql);
        }

        sendEMailToUser(account, verificationCode);
    }

    private void sendEMailToUser(String account, String verificationCode) {
        String subject = "用户注册信息";
        String code ="<font color=\"red\" weight=\"bold\" ><u>"+verificationCode+"</u></font>";
        String content = "尊敬的用户：<br>" +
                "&emsp;您好！<br>" +
                "&emsp;感谢您对城市故事地图的关注使用，您的验证码是："+code+"<br>"+
                "&emsp;预祝您在城市故事地图中找到您的乐趣！<br>&emsp;此致<br>敬礼";
        try {
            ServerUtils.sendEmail(subject,content,account);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }


}
