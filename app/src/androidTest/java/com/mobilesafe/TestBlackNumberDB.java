package com.mobilesafe;

import android.test.AndroidTestCase;

import com.mobilesafe.db.BlackNumberDBOpenHelper;

/**
 * Created by Administrator on 2016/8/23.
 */
public class TestBlackNumberDB extends AndroidTestCase{

    public void testCreateDB() throws Exception{

        BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(getContext());
        helper.getWritableDatabase();
    }

    public void testAdd() throws Exception{
//        BlackNumberDao dao = new BlackNumberDao(getContext());
//        long basenumber = 13500000000l;
//        Random random = new Random();
//        for (int i=0; i<100; i++){
//            dao.add(String.valueOf(basenumber+i), String.valueOf(random.nextInt(3)+1));
//        }
    }

    public void testDelete() throws Exception{
//        BlackNumberDao dao = new BlackNumberDao(getContext());
//        dao.delete("110");
    }

    public void testDeleteAll() throws Exception{
//        BlackNumberDao dao = new BlackNumberDao(getContext());
//        dao.delete(null);
    }

    public void testUpdate() throws Exception{
//        BlackNumberDao dao = new BlackNumberDao(getContext());
//        dao.update("110", "2");
    }

    public void testFind() throws Exception{
//        BlackNumberDao dao = new BlackNumberDao(getContext());
//        boolean result = dao.find("110");
//        assertEquals(true, result);
    }

    public void testfindAll() throws Exception{
//        BlackNumberDao dao = new BlackNumberDao(getContext());
//        dao.findAll();
    }

    public void test() throws Exception{

        assertEquals(true, true);
    }
}
