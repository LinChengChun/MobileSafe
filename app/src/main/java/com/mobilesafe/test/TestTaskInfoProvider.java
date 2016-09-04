package com.mobilesafe.test;

import android.test.AndroidTestCase;

import com.mobilesafe.bean.TaskInfo;
import com.mobilesafe.engine.TaskInfoProvider;
import com.mobilesafe.utils.LogUtil;

import java.util.List;

/**
 * Created by cclin on 2016/9/4.
 */
public class TestTaskInfoProvider extends AndroidTestCase{

    public void testGetTaskInfo() throws Exception{

        List<TaskInfo> taskInfos = TaskInfoProvider.getTaskInfo(getContext());
        for (TaskInfo info: taskInfos){
            LogUtil.d(info.toString());
        }
    }

    public void testGetServiceInfo() throws Exception{
        List<TaskInfo> taskInfos = TaskInfoProvider.getServiceInfo(getContext());
        for (TaskInfo info: taskInfos){
            LogUtil.d(info.toString());
        }
    }
}
