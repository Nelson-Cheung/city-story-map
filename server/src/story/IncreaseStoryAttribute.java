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
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "InceaseStoryAttribute")
public class IncreaseStoryAttribute extends HttpServlet {
    private JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject param = JSONObject.fromObject(jsonString);

        JSONObject ans = new JSONObject();

        String attribute = (String) param.get("attribute");
        if (attribute.equals("thumb_up")) {
            String id = (String) param.get("id");
            String reader = (String) param.get("reader");
            int res = giveThumbUp(id, reader);
            ans.put("result", res);
        } else if (attribute.equals("comment")) {
            StoryComment storyComment = new StoryComment();
            storyComment.fromJSONObject(param.getJSONObject("comment"));
            String id = (String) param.get("id");
            int res = updateStoryComment(id, storyComment);
            ans.put("result", res);
        }

        jsonString = ServerUtils.httpBase64Encode(ans.toString());
        PrintWriter out = response.getWriter();
        out.write(jsonString);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private int giveThumbUp(String id, String reader) {
        String sql = "select time from thumb_up where story_id=" + id;
        List<HashMap<String, Object>> datas = jdbcUtils.find(sql);

        if (datas.isEmpty()) {
            sql = "insert into thumb_up(story_id, reader_account, time) values(?,?,?)";
            List<Object> param = new ArrayList<Object>();
            param.add(id);
            param.add(reader);

            Calendar c = Calendar.getInstance();
            param.add(c.getTimeInMillis());

            int res = jdbcUtils.update(sql, param);
            if (res == 0) {
                return IncreaseStoryAttributeResult.FAILURE;
            } else {

                sql = "select thumb_up from stories where id="+id;
                List<HashMap<String, Object>> list = jdbcUtils.find(sql);
                if(!list.isEmpty()) {
                    // 临界资源
                    int increase = Integer.parseInt(list.get(0).get("thumb_up").toString()) + 1;
                    sql = "update stories set thumb_up="+increase+" where id="+id;
                    jdbcUtils.update(sql);
                }
                return IncreaseStoryAttributeResult.SUCCESS;
            }

        } else {
            return IncreaseStoryAttributeResult.FAILURE;
        }
    }

    private int updateStoryComment(String id, StoryComment storyComment) {
        String sql = "select story_id from comments where story_id=" + id +
                " and reader_account='" + storyComment.getAccount() + "'";
        List<HashMap<String, Object>> datas = jdbcUtils.find(sql);

        if (datas.isEmpty()) {
            Calendar c = Calendar.getInstance();
            sql = "insert into comments(story_id, reader_account, comment_time, " +
                    "my_latitude, my_longitude, content) values(?,?,?,?,?,?)";
            List<Object> param = new ArrayList<Object>();
            param.add(id);
            param.add(storyComment.getAccount());
            param.add(c.getTimeInMillis());
            param.add(storyComment.getPosition().getMyLatitude());
            param.add(storyComment.getPosition().getMyLongitude());
            param.add(storyComment.getContent());

            int res = jdbcUtils.update(sql, param);
            if (res == 0) {
                return IncreaseStoryAttributeResult.FAILURE;
            } else {
                return IncreaseStoryAttributeResult.SUCCESS;
            }
        } else {
            Calendar c = Calendar.getInstance();
            sql = "update comments set story_id=?, reader_account=?, comment_time=?, " +
                    "my_latitude=?, my_longitude=?, content=? where story_id=? and reader_account=?";
            List<Object> param = new ArrayList<Object>();
            param.add(id);
            param.add(storyComment.getAccount());
            param.add(c.getTimeInMillis());
            param.add(storyComment.getPosition().getMyLatitude());
            param.add(storyComment.getPosition().getMyLongitude());
            param.add(storyComment.getContent());
            param.add(id);
            param.add(storyComment.getAccount());

            int res = jdbcUtils.update(sql, param);
            if (res == 0) {
                return IncreaseStoryAttributeResult.FAILURE;
            } else {
                return IncreaseStoryAttributeResult.COMMENT_EXISTED;
            }
        }
    }
}

