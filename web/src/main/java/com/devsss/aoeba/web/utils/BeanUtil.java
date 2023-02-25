package com.devsss.aoeba.web.utils;

import org.springframework.cglib.beans.BeanMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeanUtil {


    public static <T> List<T> mapListToBeanList(List<Map<String, Object>> mapList, Class<T> beanType) throws Exception {
        ArrayList<T> beanList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            T bean = beanType.getDeclaredConstructor().newInstance();
            BeanMap beanMap = BeanMap.create(bean);
            beanMap.putAll(map);
            beanList.add(bean);
        }
        return beanList;
    }
}
