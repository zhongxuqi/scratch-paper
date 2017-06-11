package com.musketeer.baselibrary.paser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhongxuqi on 15-12-7.
 */
public class JsonPaser<T> implements IJsonPaser<T>{
    @Override
    public T BuildModel(JSONObject jsonObject) throws JSONException {
        return null;
    }

    @Override
    public List<T> BuildModelList(JSONObject jsonObject) throws JSONException {
        return null;
    }
}
