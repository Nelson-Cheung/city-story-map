package user;

import DataBaseInfo.DataBaseInfo;
import JdbcUtils.JdbcUtils;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import serverutils.ServerUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "GetUser")
public class GetUser extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        PrintWriter out = response.getWriter();

        String jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject param = JSONObject.fromObject(jsonString);

        String account = (String)param.get("account");
        String sql = "select * from users where account='" + account + "'";
        List<HashMap<String, Object>> ansList = jdbcUtils.find(sql);
        JSONObject ans = new JSONObject();

        if (ansList.size() == 0) {
            ans.put("result", UserResult.INVALID);
        } else {
            HashMap<String, Object> map = ansList.get(0);
            String headPortraitPath = (String) map.get("head_portrait");
            headPortraitPath = ServerUtils.SERVER_PATH + "/" + headPortraitPath;
            User user = new User();
            user.setAccount((String)map.get("account"));
            user.setName((String)map.get("name"));
            user.setIntroduction((String)map.get("introduction"));
            user.setHeadPortraitPath(headPortraitPath);

            ans.put("result", UserResult.SUCCESS);
            ans.put("user", user);
        }

        jsonString = ServerUtils.httpBase64Encode(ans.toString());
        out.write(jsonString);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private byte[] getHeadPortrait(String path) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        File file = new File(path);

        try {
            InputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer, 0, 1024)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}
