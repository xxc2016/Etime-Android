package com.student.xxc.etime.helper;

import com.student.xxc.etime.adapter.MyItemTouchHelperCallback;

public class TraceItemTouchHelper extends WItemTouchHelperPlus{
    private MyItemTouchHelperCallback callback;

    public TraceItemTouchHelper(MyItemTouchHelperCallback callback) {
        super(callback);
        this.callback = callback;
    }

    public void setEnableDrag(boolean enableDrag) {
        callback.setEnableDrag(enableDrag);
    }

    public void setEnableSwipe(boolean enableSwipe) {
        callback.setEnableSwipe(enableSwipe);
    }
}

