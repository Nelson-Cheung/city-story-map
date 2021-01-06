package story;

import DataBaseInfo.DataBaseInfo;
import JdbcUtils.JdbcUtils;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "DeleteStory")
public class DeleteStory extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.setCharacterEncoding("utf-8");
//        response.setCharacterEncoding("utf-8");
//
//        String jsonString, sql;
//        JSONObject jsonObject;
//        PrintWriter out = response.getWriter();
//
//        jsonString = request.getParameter("json_string");
//        jsonObject = JSONObject.fromObject(jsonString);
//
//        String account = (String) jsonObject.get("account");
//        int year = (int) jsonObject.get("year");
//        int month = (int) jsonObject.get("month");
//        int day = (int) jsonObject.get("day");
//        int hour = (int) jsonObject.get("hour");
//        int minute = (int) jsonObject.get("minute");
//        int second = (int) jsonObject.get("second");
//        int myLatitude = (int) jsonObject.get("my_latitude");
//        int myLongitude = (int) jsonObject.get("my_longitude");
//
//        sql = "delete from stories where account=? and year=? and " +
//                "month=? and day=? and hour=? and minute=? and second=? and " +
//                "my_latitude=? and my_longitude=?";
//
//        ArrayList<Object> params = new ArrayList<Object>();
//
//        params.add(account);
//        params.add(year);
//        params.add(month);
//        params.add(day);
//        params.add(hour);
//        params.add(minute);
//        params.add(second);
//        params.add(myLatitude);
//        params.add(myLongitude);
//
//        System.out.println(params);
//
//        jdbcUtils.update(sql, params);
//        out.write(StoryResult.DELETE_SUCCESS);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
