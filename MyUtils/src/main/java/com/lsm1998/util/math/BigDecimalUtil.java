package com.lsm1998.util.math;

import com.lsm1998.util.math.annotation.NotKeepDecimal;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * @作者：刘时明
 * @时间：2019/6/10-10:17
 * @作用：BigDecimal工具类
 */
public class BigDecimalUtil
{
    /**
     * 所有BigDecimal类型字段保留n位小数
     *
     * @param ele 目标对象
     * @param n   保留位数
     * @return
     */
    public static <E> void keepDecimal(E ele, int n)
    {
        Class<? extends Object> c = ele.getClass();
        if (isCollection(ele))
        {
            Collection collection = (Collection) ele;
            for (Object o : collection)
            {
                keepDecimal(o, 4);
            }
        } else if (isMap(ele))
        {
            Map map = (Map) ele;
            for (Object o : map.entrySet())
            {
                Map.Entry e = (Map.Entry) o;
                keepDecimal(e.getValue(), 4);
            }
        }else
        {
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields)
            {
                if(!f.isAnnotationPresent(NotKeepDecimal.class))
                {
                    scale(f, ele, n);
                }
            }
        }
    }

    /**
     * 保留小数scale方法
     *
     * @param f    字段
     * @param o    目标对象
     * @param n    保留位数
     * @param type 保留策略
     */
    private static void scale(Field f, Object o, int n, int type)
    {
        try
        {
            if (!java.lang.reflect.Modifier.isFinal(f.getModifiers()))
            {
                if (f.getType() == BigDecimal.class && n > 0)
                {
                    f.setAccessible(true);
                    BigDecimal temp = (BigDecimal) f.get(o);
                    f.set(o, temp.setScale(n, type));
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void scale(Field f, Object o, int n)
    {
        // 采用默认的舍弃策略
        scale(f, o, n, BigDecimal.ROUND_DOWN);
    }

    private static boolean isCollection(Object o)
    {
        return o instanceof Collection;
    }

    private static boolean isMap(Object o)
    {
        return o instanceof Map;
    }


    private static boolean isBigDecimal(Object o)
    {
        return o instanceof BigDecimal;
    }
}
