package story;

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
import java.util.Calendar;
import java.util.List;

@WebServlet(name = "UpdateStory")
public class UpdateStory extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject param = JSONObject.fromObject(jsonString);
        UserStory story = new UserStory();
        story.fromJSONObject(param.getJSONObject("story"));

        String sql;
        JSONObject ans = new JSONObject();
        //更新故事
        if (story.getType() == UserStory.UPDATE) {
            sql = "update stories set time=?, summary=?, story=?, tags=? where id=?";
            List<Object> params = new ArrayList<Object>();
            params.add(Calendar.getInstance().getTimeInMillis());
            params.add(story.getSummary());
            params.add(story.getWholeStory());
            params.add(story.getTags());
            params.add(story.getId());
            int res = jdbcUtils.update(sql, params);
            if (res == 0) {
                ans.put("result", StoryResult.FAILURE);
            } else {
                ans.put("result", StoryResult.SUCCESS);
            }
        }
        //插入故事
        else if (story.getType() == UserStory.INSERT) {
            sql = "insert into stories(author_account, time, my_latitude, my_longitude, summary, story, tags, thumb_up) " +
                    "values(?,?,?,?,?,?,?,?)";

            List<Object> params = new ArrayList<Object>();
            params.add(story.getAccount());
            params.add(Calendar.getInstance().getTimeInMillis());
            params.add(story.getPosition().getMyLatitude());
            params.add(story.getPosition().getMyLongitude());
            params.add(story.getSummary());
            params.add(story.getWholeStory());
            params.add(story.getTags());
            params.add(0);
            int res = jdbcUtils.update(sql, params);
            if (res == 0) {
                ans.put("result", StoryResult.FAILURE);
            } else {
                ans.put("result", StoryResult.SUCCESS);
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
