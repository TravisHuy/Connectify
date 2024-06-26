package com.nhathuy.connectify.model;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    //converter value ve gson
    @TypeConverter
    public static List<String> fromString(String value){
        Type listType=new TypeToken<List<String>>(){}.getType();
        return new Gson().fromJson(value,listType);
    }
    @TypeConverter
    public static String fromList(List<String> list){
        Gson gson=new Gson();
        String json=gson.toJson(list);
        return json;
    }
}
