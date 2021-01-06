package user;

/*
 *用户类
 */

import base.BaseJson;
import net.sf.json.JSONObject;

public class User implements BaseJson {
    private String account;
    private String name;
    private String introduction;
    private String headPortraitPath;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getHeadPortraitPath() {
        return headPortraitPath;
    }

    public void setHeadPortraitPath(String headPortraitPath) {
        this.headPortraitPath = headPortraitPath;
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", introduction='" + introduction + '\'' +
                ", headPortraitPath='" + headPortraitPath + '\'' +
                '}';
    }

    @Override
    public void fromJSONObject(JSONObject param) {
        setAccount(param.get("account").toString());
        setHeadPortraitPath(param.get("headPortraitPath").toString());
        setIntroduction(param.get("introduction").toString());
        setName(param.get("name").toString());
    }
}
