package user;

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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "UpdateUser")
public class UpdateUser extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String jsonString, sql;
        JSONObject jsonObject;
        boolean flag = false;
        PrintWriter out = response.getWriter();

        jsonString = request.getParameter("json_string");
        System.out.println(jsonString);
        jsonObject = JSONObject.fromObject(jsonString);

        sql = "update users set name=?, introduction=?, head_portrait=? where account=?";
        ArrayList<Object> params = new ArrayList<Object>();

        String value;

        value = (String) jsonObject.get("name");
        params.add(value);
        value = (String) jsonObject.get("introduction");
        params.add(value);
        // 应该在register中设置
        List<String> list = ServerUtils.getDataPath();
        list.add((String)jsonObject.get("account"));
        list.add("head_portrait");

        String headPortraitPath = ServerUtils.toFilePath(list, ServerUtils.OS);

        params.add(headPortraitPath);
        value = (String) jsonObject.get("account");
        params.add(value);


        int ans = jdbcUtils.update(sql, params);
        //应该在前面设置成功后再进行设置，不安全
        if (ans == 0) {
            out.write(UserResult.INVALID);
        } else {
            byte[] headPortrait = (byte[]) jsonObject.get("head_portrait");

            list = ServerUtils.getDataPath();
            list.add((String) jsonObject.get("account"));

            ServerUtils.save(headPortrait, ServerUtils.toFilePath(list, ServerUtils.OS), "head_portrait");
            out.write(UserResult.SUCCESS);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
