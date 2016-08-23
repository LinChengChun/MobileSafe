package com.mobilesafe.test;

import android.test.AndroidTestCase;

import com.mobilesafe.db.BlackNumberDBOpenHelper;
import com.mobilesafe.db.dao.BlackNumberDao;

/**
 * Created by Administrator on 2016/8/23.
 */
public class TestBlackNumberDB extends AndroidTestCase{

    public void testCreateDB() throws Exception{

        BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
        helper.getWritableDatabase();
    }

    public void testAdd() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.add("110", "1");
    }

    public void testDelete() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.delete("110");
    }

    public void testUpdate() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        dao.update("110", "2");
    }

    public void testExists() throws Exception{
        BlackNumberDao dao = new BlackNumberDao(getContext());
        boolean result = dao.exists("110");
        assertEquals(true, result);
    }

}
