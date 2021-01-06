package login;

import DataBaseInfo.DataBaseInfo;
import JdbcUtils.JdbcUtils;
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
 * 登录验证
 */
@WebServlet(name = "Login")
public class Login extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String jsonString;
        jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject params = JSONObject.fromObject(jsonString);

        String account = (String) params.get("account");
        String password = (String) params.get("password");

        String sql = "select password from users where account='" + account + "'";
        List<HashMap<String, Object>> dataSet = jdbcUtils.find(sql);

        JSONObject ans = new JSONObject();

        if (dataSet.isEmpty()) {
            ans.put("answer", LoginResult.INVAILD_USER_ACCOUNT);
        } else {
            String realPswd = dataSet.get(0).get("password").toString();
            if (realPswd.equals(password)) {
                ans.put("answer", LoginResult.SUCCESS);
            } else {
                ans.put("answer", LoginResult.INCORRECT_PASSWORD);
            }
        }

        jsonString = ServerUtils.httpBase64Encode(ans.toString());
        PrintWriter out = response.getWriter();
        out.write(jsonString);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
