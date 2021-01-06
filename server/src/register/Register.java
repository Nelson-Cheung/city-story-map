package register;

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

@WebServlet(name = "Register")
public class Register extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject params = JSONObject.fromObject(jsonString);


        String sql, account, password, verificationCode;
        List<HashMap<String, Object>> ansList;

        account = (String)params.get("account");
        password = (String)params.get("password");
        verificationCode = (String)params.get("verification_code");

        sql = "select account from users where account='" + account + "'";
        ansList = jdbcUtils.find(sql);
        JSONObject result = new JSONObject();

        if (ansList.size() != 0) {
            result.put("answer",RegisterResult.DUPLICATE_ACCOUNT);
        } else {
            if (validateAccount(account)) {
                if (validatePassword(password)) {
                    if (validateVerificationCode(account, verificationCode)) {
                        List<String> list = ServerUtils.getDataPath();
                        list.add(account);
                        list.add("head_portrait");

                        sql = "insert into users(account, password, head_portrait, name, introduction) values('" + account + "', '" + password + "', '" + ServerUtils.toFilePath(list, "linux") + "','','')";
                        jdbcUtils.update(sql);

                        ServerUtils.createDataDir(account);

                        sql = "delete from verify where account='" + account + "'";
                        jdbcUtils.update(sql);
                        result.put("answer", RegisterResult.SUCCESS);
                    } else {
                        result.put("answer", RegisterResult.INVALID_VERIFICATION_CODE);
                    }
                } else {
                    result.put("answer", RegisterResult.INVALID_PASSWORD);
                }
            } else {
                result.put("answer",RegisterResult.INVALID_ACCOUNT);
            }
        }

        jsonString = ServerUtils.httpBase64Encode(result.toString());
        PrintWriter out = response.getWriter();
        out.write(jsonString);
    }

    private boolean validateVerificationCode(String account, String verificationCode) {
        String sql = "select * from verify where account='" + account + "'";
        List<HashMap<String, Object>> list = jdbcUtils.find(sql);

        if (list.size() > 0) {
            String trueCode = (String) list.get(0).get("verification_code");
            long overtime = Long.parseLong(list.get(0).get("overtime").toString());
            long time = System.currentTimeMillis();
            System.out.println();
            return (trueCode.equals(verificationCode) && time <= overtime);
        } else {
            return false;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private boolean validateAccount(String account) {
        return true;
    }

    private boolean validatePassword(String passoword) {
        return true;
    }
}
