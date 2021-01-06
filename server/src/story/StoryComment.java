package story;

import base.BaseJson;
import myposition.MyPosition;
import net.sf.json.JSONObject;

import java.math.BigInteger;

public class StoryComment implements BaseJson {
    private String account;
    private String time;
    private String content;
    private MyPosition position;

    public StoryComment() {
        position = new MyPosition();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MyPosition getPosition() {
        return position;
    }

    public void setPosition(MyPosition position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "StoryComment{" +
                "account='" + account + '\'' +
                ", time=" + time +
                ", content='" + content + '\'' +
                ", position=" + position +
                '}';
    }

    @Override
    public void fromJSONObject(JSONObject param) {
        account = (String) param.get("account");
        time = (String)param.get("time");
        content = (String)param.get("content");

        position = new MyPosition();
        position.fromJSONObject(param.getJSONObject("position"));
    }
}
