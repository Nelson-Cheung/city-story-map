package edu.sysu.citystorymap.base;

import net.sf.json.JSONObject;

public interface BaseJson {
    void fromJSONObject(JSONObject param);

    JSONObject toJSONObject();
}
