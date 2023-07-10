package com.tools.payhelper.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Tag {
    private ArrayList mChildren;
    private String mContent;
    private String mName;
    private String mPath;

    Tag(String arg2, String arg3) {
        super();
        this.mChildren = new ArrayList();
        this.mPath = arg2;
        this.mName = arg3;
    }

    void addChild(Tag arg2) {
        this.mChildren.add(arg2);
    }

    Tag getChild(int arg2) {
        Tag v0_1 = null;
        if(arg2 < 0 || arg2 >= this.mChildren.size()) {
            v0_1 = null;
        }
        else {
            Object v0 = this.mChildren.get(arg2);
        }

        return v0_1;
    }

    ArrayList getChildren() {
        return this.mChildren;
    }

    int getChildrenCount() {
        return this.mChildren.size();
    }

    String getContent() {
        return this.mContent;
    }

    HashMap getGroupedElements() {
        HashMap v2 = new HashMap();
        Iterator v4 = this.mChildren.iterator();
        while(v4.hasNext()) {
            Object v0 = v4.next();
            String v3 = ((Tag)v0).getName();
            Object v1 = v2.get(v3);
            if(v1 == null) {
                ArrayList v1_1 = new ArrayList();
                v2.put(v3, v1_1);
            }

            ((ArrayList)v1).add(v0);
        }

        return v2;
    }

    String getName() {
        return this.mName;
    }

    String getPath() {
        return this.mPath;
    }

    boolean hasChildren() {
        boolean v0 = this.mChildren.size() > 0 ? true : false;
        return v0;
    }

    void setContent(String arg5) {
        int v1 = 0;
        if(arg5 != null) {
            int v2;
            for(v2 = 0; v2 < arg5.length(); ++v2) {
                int v0 = arg5.charAt(v2);
                if(v0 != 0x20 && v0 != 0xA) {
                    v1 = 1;
                    break;
                }
            }
        }

        if(v1 != 0) {
            this.mContent = arg5;
        }
    }

    public String toString() {
        return "Tag: " + this.mName + ", " + this.mChildren.size() + " children, Content: " + this.mContent;
    }
}

