package sendverificationcode;

import DataBaseInfo.DataBaseInfo;
import JdbcUtils.JdbcUtils;
import net.sf.json.JSONObject;
import serverutils.ServerUtils;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

@WebServlet(name = "SendVerificationCode")
public class SendVerificationCode extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject params = JSONObject.fromObject(jsonString);

        String account = (String) params.get("account");
        String verificationCode = generateVerificationCode();
        sendVerificationCodeToUserAccount(account, verificationCode);

        long time = System.currentTimeMillis() + ServerUtils.VALID_TIME;
        String sql = "select verification_code from verify where account='" + account + "'";
        List<HashMap<String, Object>> ans = jdbcUtils.find(sql);
        if (ans.isEmpty()) {
            sql = "insert verify(account, verification_code, overtime) values('" + account + "', '" + verificationCode + "', " + time + ")";
        } else {
            sql = "update verify set verification_code='" + verificationCode + "', overtime=" + time + " where account='" + account + "'";

        }
        jdbcUtils.update(sql);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private String generateVerificationCode() {
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

    private void sendVerificationCodeToUserAccount(String account, String verificationCode) {
        String subject = "验证码信息";
        String code = "<font color=\"red\" weight=\"bold\" ><u>" + verificationCode + "</u></font>";
        String content = "尊敬的用户：<br>" +
                "&emsp;您好！<br>" +
                "&emsp;我们收到您关于发送验证码的请求，您的验证码是：" + code + "（一个小时之内有效）<br>" +
                "&emsp;感谢您对城市故事地图的使用！ <br>&emsp;此致<br>敬礼";
        try {
            ServerUtils.sendEmail(subject, content, account);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
