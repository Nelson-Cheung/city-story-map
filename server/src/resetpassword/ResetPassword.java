package resetpassword;

import DataBaseInfo.DataBaseInfo;
import JdbcUtils.JdbcUtils;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import serverutils.ServerUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

/**
 * 密码重置
 */
@WebServlet(name = "ResetPassword")
public class ResetPassword extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject params = JSONObject.fromObject(jsonString);

        String action_flag;
        int res;

        action_flag = (String)params.get("action_flag");
        if (action_flag.equals("old_password")) {
            String account, oldPassword, newPassword;

            account = (String)params.get("account");
            oldPassword = (String)params.get("old_password");
            newPassword = (String)params.get("new_password");
            res = resetPasswordWithOldPassword(account, oldPassword, newPassword);
        } else if (action_flag.equals("verification_code")) {
            String account, newPassword, verificationCode;

            account = (String)params.get("account");
            newPassword = (String)params.get("new_password");
            verificationCode = (String)params.get("verification_code");
            res = resetPasswordWithVerificationCode(account, newPassword, verificationCode);
        } else {
            res = ResetPasswordResult.INVALID_ACTION;
        }

        JSONObject result = new JSONObject();
        result.put("answer", res);
        jsonString = ServerUtils.httpBase64Encode(result.toString());
        PrintWriter out = response.getWriter();
        out.write(jsonString);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 用原密码重设账账号密码
     *
     * @param account     账号密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    private int resetPasswordWithOldPassword(String account, String oldPassword, String newPassword) {
        String sql;
        List<HashMap<String, Object>> ansList;

        sql = "select password from users where account='" + account + "'";
        ansList = jdbcUtils.find(sql);

        if (ansList.size() == 0) {
            return ResetPasswordResult.INVALID_ACCOUNT;
        } else {
            String truePassword;

            truePassword = (String) (ansList.get(0).get("password"));
            if (oldPassword.equals(truePassword)) {
                sql = "update users set password='" + newPassword + "' where account='" + account + "'";
                jdbcUtils.update(sql);
                return ResetPasswordResult.SUCCESS;
            } else {
                return ResetPasswordResult.INCORRECT_PASSWORD;
            }
        }
    }

    /**
     * 用验证码修改账号密码，验证码具有有效期
     *
     * @param account          用户账号
     * @param newPassword      新密码
     * @param verificationCode 验证码
     * @return 修改密码结果
     */
    private int resetPasswordWithVerificationCode(String account, String newPassword, String verificationCode) {
        String sql;
        List<HashMap<String, Object>> ansList;

        sql = "select * from verify where account='" + account + "'";
        ansList = jdbcUtils.find(sql);

        if (ansList.size() == 0) {
            return ResetPasswordResult.INVALID_ACCOUNT;
        } else {
            String trueVerificationCode = (String) ansList.get(0).get("verification_code");
            long time = Long.parseLong(ansList.get(0).get("overtime").toString());
            long current = System.currentTimeMillis();

            if (trueVerificationCode.equals(verificationCode) && current <= time) {
                sql = "update users set password='" + newPassword + "' where account='" + account + "'";
                jdbcUtils.update(sql);
                return ResetPasswordResult.SUCCESS;
            } else {
                return ResetPasswordResult.INCORRECT_VERIFICATION_CODE;
            }
        }

    }
}
