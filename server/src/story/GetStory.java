package story;

import DataBaseInfo.DataBaseInfo;
import JdbcUtils.JdbcUtils;
import myposition.MyPosition;
import net.sf.json.JSONObject;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import serverutils.ServerUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 获取故事
 */
@WebServlet(name = "GetStory")
public class GetStory extends HttpServlet {
    JdbcUtils jdbcUtils = new JdbcUtils(DataBaseInfo.USERNAME, DataBaseInfo.PASSWORD, DataBaseInfo.DRIVER, DataBaseInfo.LOCATION);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String jsonString = ServerUtils.inputStreamToString(request.getInputStream());
        jsonString = ServerUtils.httpBase64Decode(jsonString);
        JSONObject param = JSONObject.fromObject(jsonString);

        List<HashMap<String, Object>> storyList = new ArrayList<HashMap<String, Object>>();
        List<HashMap<String, Object>> commentList = new ArrayList<HashMap<String, Object>>();
        List<MyPosition> positionList = new ArrayList<MyPosition>();

        String action_flag = (String) param.get("action_flag");

        if (action_flag.equals("tags")) {
            String tags = (String) param.get("tags");
            storyList = getStoryWithTags(tags);
        } else if (action_flag.equals("text")) {
            String text = (String) param.get("text");
            storyList = getStoryWithText(text);
        } else if (action_flag.equals("location")) {
            MyPosition position = new MyPosition();
            position.fromJSONObject(param.getJSONObject("location"));
            storyList = getStoryWithLocation(position);
        } else if (action_flag.equals("account")) {
            String account = (String) param.get("account");
            storyList = getStoriesWithAccount(account);
        } else if (action_flag.equals("bound")) {
            MyPosition p1 = new MyPosition();
            p1.fromJSONObject(param.getJSONObject("p1"));
            MyPosition p2 = new MyPosition();
            p2.fromJSONObject(param.getJSONObject("p2"));
            positionList = getStoryWithBound(p1, p2);
        } else if(action_flag.equals("story")) {
            String id = (String)param.get("id");
            storyList = getStoryWithId(id);
        }

        JSONObject ans = new JSONObject();
        if (action_flag.equals("tags") || action_flag.equals("text") || action_flag.equals("location") || action_flag.equals("account")) {
            List<UserStory> stories = new ArrayList<UserStory>();
            for (int i = 0; i < storyList.size(); ++i) {
                UserStory story = toUserStory(storyList.get(i));
                commentList = getStoryComment(story.getId());
                List<StoryComment> list = new ArrayList<StoryComment>();
                for (int j = 0; j < commentList.size(); ++j) {
                    list.add(toStoryComment(commentList.get(j)));
                }
                story.setComments(list);
                stories.add(story);
            }
            ans.put("stories", stories);
        } else if (action_flag.equals("bound")) {
            ans.put("positions", positionList);
        } else if(action_flag.equals("story")) {
            UserStory story = toUserStory(storyList.get(0));
            commentList = getStoryComment(story.getId());
            List<StoryComment> list = new ArrayList<StoryComment>();
            for (int j = 0; j < commentList.size(); ++j) {
                list.add(toStoryComment(commentList.get(j)));
            }
            story.setComments(list);
            ans.put("story", story);
        }

        jsonString = ServerUtils.httpBase64Encode(ans.toString());
        PrintWriter out = response.getWriter();
        out.write(jsonString);

    }

    /**
     * 按用户账号查找故事集合
     *
     * @param account 用户账号
     * @return 故事对象集合
     */
    private List<HashMap<String, Object>> getStoriesWithAccount(String account) {
        String sql = "select * from stories where author_account='" + account + "'";
        List<HashMap<String, Object>> ansList = jdbcUtils.find(sql);
        return ansList;
    }

    /**
     * @return
     */
    private List<HashMap<String, Object>> getStoryWithLocation(MyPosition position) {
        String sql = "select * from stories where my_latitude=" + position.getMyLatitude() +
                " and my_longitude=" + position.getMyLongitude();
        List<HashMap<String, Object>> ansList = jdbcUtils.find(sql);
        return ansList;
    }

    /**
     * 按文本查找故事
     *
     * @param text 查找文本
     * @return 故事对象集合
     */
    private List<HashMap<String, Object>> getStoryWithText(String text) {
        // 分词匹配
        /*String sql = "select * from stories where my_latitude="+myPosition.myLatitude + " and my_longitude="+myPosition.myLongitude;
        List<HashMap<String, Object>> ansList = jdbcUtils.find(sql);
        return convertToStoryList(ansList);*/
        return null;
    }

    /**
     * 按关键词标签查找故事集
     *
     * @param tags 关键词标签
     * @return 故事对象的集合
     */
    private List<HashMap<String, Object>> getStoryWithTags(String tags) {
        String sql = "select * from stories where ";
        String[] tagSet = tags.split(" ");
        for (int i = 0; i < tagSet.length; ++i) {
            sql += "tags like '%" + tagSet[i] + "%'";
            if (i < tagSet.length - 1) {
                sql += " and ";
            }
        }
        List<HashMap<String, Object>> ansList = jdbcUtils.find(sql);
        return ansList;
    }

    private List<MyPosition> getStoryWithBound(MyPosition p1, MyPosition p2) {
        String sql = "select * from stories where my_latitude between " + p2.getMyLatitude() + " and " + p1.getMyLatitude() +
                " and my_longitude between " + p2.getMyLongitude() + " and " + p1.getMyLongitude();
        List<HashMap<String, Object>> ansList = jdbcUtils.find(sql);

        boolean[][] flag = new boolean[p1.getMyLongitude() - p2.getMyLongitude() + 1][p1.getMyLatitude() - p2.getMyLatitude() + 1];
        for (int i = 0; i < flag.length; ++i) {
            for (int j = 0; j < flag[i].length; ++j) {
                flag[i][j] = false;
            }
        }

        for (int i = 0; i < ansList.size(); ++i) {

            int x = (int) ansList.get(i).get("my_longitude");
            int y = (int) ansList.get(i).get("my_latitude");

            x = x - p2.getMyLongitude();
            y = y - p2.getMyLatitude();

            //   System.out.println(x+" "+y);
            flag[x][y] = true;
        }

        ArrayList<MyPosition> list = new ArrayList<MyPosition>();

        for (int i = 0; i < flag.length; ++i) {
            for (int j = 0; j < flag[i].length; ++j) {
                if (flag[i][j]) {
                    MyPosition position = new MyPosition();
                    position.setLongitude((i + p2.getMyLongitude()) / position.getSCALE_NUMBER());
                    position.setLatitude((j + p2.getMyLatitude()) / position.getSCALE_NUMBER());
                    list.add(position);
                }
            }
        }

        return list;
    }

    private List<HashMap<String, Object>> getStoryComment(String id) {
        String sql = "select * from comments where story_id=" + id.toString();
        List<HashMap<String, Object>> list = jdbcUtils.find(sql);
        return list;
    }

    private List<HashMap<String, Object>> getStoryWithId(String id) {
        String sql = "select * from stories where id=" + id;
        return jdbcUtils.find(sql);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * 将数据库中找出的结果转换为故事对象的集合
     *
     * @param map 数据库中返回的结果
     * @return 故事对象的集合
     */
    private UserStory toUserStory(HashMap<String, Object> map) {
        UserStory story = new UserStory();

        story.setAccount((String) map.get("author_account"));
        story.setId(map.get("id").toString());
        MyPosition position = new MyPosition();
        position.setMyLatitude(Integer.parseInt(map.get("my_latitude").toString()));
        position.setMyLongitude(Integer.parseInt(map.get("my_longitude").toString()));
        story.setPosition(position);
        story.setSummary((String) map.get("summary"));
        story.setWholeStory((String) map.get("story"));
        story.setThumbUps(Integer.parseInt(map.get("thumb_up").toString()));
        story.setTags((String) map.get("tags"));
        story.setLastEditTime(map.get("time").toString());

        return story;
    }

    private StoryComment toStoryComment(HashMap<String, Object> map) {
        StoryComment comment = new StoryComment();
        comment.setAccount((String) map.get("reader_account"));
        comment.setTime(map.get("comment_time").toString());
        comment.setContent((String) map.get("content"));

        MyPosition position = new MyPosition();
        position.setMyLongitude(Integer.parseInt(map.get("my_longitude").toString()));
        position.setMyLatitude(Integer.parseInt(map.get("my_latitude").toString()));
        comment.setPosition(position);

        return comment;
    }
}
