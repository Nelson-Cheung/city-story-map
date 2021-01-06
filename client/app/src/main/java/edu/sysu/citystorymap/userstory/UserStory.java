package edu.sysu.citystorymap.userstory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.sysu.citystorymap.base.BaseJson;
import edu.sysu.citystorymap.myposition.MyPosition;


public class UserStory implements BaseJson {
    public static final int UPDATE = 0;
    public static final int INSERT = 1;
    private String id;
    private String account;
    private String name;
    private String lastEditTime;
    private String newEditTime;
    private String summary;
    private String wholeStory;
    private int thumbUps;
    private String tags;
    private MyPosition position;
    private List<StoryComment> comments;
    private int type;

    public UserStory() {
        position = new MyPosition();
        comments = new ArrayList<StoryComment>();
        id = "";
        account = "";
        name = "";
        lastEditTime = "";
        newEditTime = "";
        summary = "";
        wholeStory = "";
        thumbUps = 0;
        tags = "";

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return account;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(String lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public String getNewEditTime() {
        return newEditTime;
    }

    public void setNewEditTime(String newEditTime) {
        this.newEditTime = newEditTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getWholeStory() {
        return wholeStory;
    }

    public void setWholeStory(String wholeStory) {
        this.wholeStory = wholeStory;
    }

    public int getThumbUps() {
        return thumbUps;
    }

    public void setThumbUps(int thumbUps) {
        this.thumbUps = thumbUps;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public MyPosition getPosition() {
        return position;
    }

    public void setPosition(MyPosition position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<StoryComment> getComments() {
        return comments;
    }

    public void setComments(List<StoryComment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "UserStory{" +
                "id=" + id +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", lastEditTime=" + lastEditTime +
                ", newEditTime=" + newEditTime +
                ", summary='" + summary + '\'' +
                ", wholeStory='" + wholeStory + '\'' +
                ", thumbUps=" + thumbUps +
                ", tags='" + tags + '\'' +
                ", position=" + position +
                ", comments=" + comments +
                ", type=" + type +
                '}';
    }

    @Override
    public void fromJSONObject(JSONObject param) {
        id = (String) param.get("id");
        account = (String) param.get("account");
        name = (String) param.get("name");
        lastEditTime = (String) param.get("lastEditTime");
        newEditTime = (String) param.get("newEditTime");
        summary = (String) param.get("summary");
        wholeStory = (String) param.get("wholeStory");
        thumbUps = Integer.parseInt(param.get("thumbUps").toString());
        tags = (String) param.get("tags");

        position = new MyPosition();
        position.fromJSONObject(param.getJSONObject("position"));

        List<StoryComment> comments = new ArrayList<StoryComment>();
        JSONArray array = param.getJSONArray("comments");
        for (int i = 0; i < array.size(); ++i) {
            StoryComment comment = new StoryComment();
            comment.fromJSONObject(array.getJSONObject(i));
            comments.add(comment);
        }
        this.comments = comments;

        type = Integer.parseInt(param.get("type").toString());
    }

    public JSONObject toJSONObject() {
        JSONObject ans = new JSONObject();
        ans.put("id", id);
        ans.put("account", account);
        ans.put("name", name);
        ans.put("lastEditTime", lastEditTime);
        ans.put("newEditTime", newEditTime);
        ans.put("summary", summary);
        ans.put("wholeStory", wholeStory);
        ans.put("thumbUps", thumbUps);
        ans.put("tags", tags);
        ans.put("position", position.toJSONObject());

        JSONArray array = new JSONArray();
        for (int index = 0; index < comments.size(); ++index) {
            array.add(comments.get(index).toJSONObject());
        }
        ans.put("comments", array);
        ans.put("type", type);
        return ans;
    }
}
