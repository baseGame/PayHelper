package com.tools.payhelper.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tools.payhelper.bean.QrCodeBean;
import com.tools.payhelper.bean.UnionOrderBean;
import com.tools.payhelper.bean.XposBean;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private SQLiteDatabase db;

    public DBManager(Context arg2) {
        super();
        DBHelper helper = new DBHelper(arg2);
        this.db = helper.getWritableDatabase();
    }

    private Cursor ExecSQLForCursor(String arg4) {
        return this.db.rawQuery(arg4, null);
    }

    public ArrayList<QrCodeBean> FindQrCodes(String arg7) {
        String v3 = "SELECT * FROM qrcode WHERE mark=\'" + arg7 + "\'";
        ArrayList<QrCodeBean> v2 = new ArrayList<>();
        Cursor v0 = this.ExecSQLForCursor(v3);
        while (v0.moveToNext()) {
            QrCodeBean v1 = new QrCodeBean();
            v1.setMoney(v0.getString(v0.getColumnIndex("money")));
            v1.setMark(v0.getString(v0.getColumnIndex("mark")));
            v1.setType(v0.getString(v0.getColumnIndex("type")));
            v1.setPayurl(v0.getString(v0.getColumnIndex("payurl")));
            v1.setDt(v0.getString(v0.getColumnIndex("dt")));
            v2.add(v1);
        }
        v0.close();
        return v2;
    }

    public void addQrCode(QrCodeBean arg7) {
        this.db.beginTransaction();
        try {
            this.db.execSQL("INSERT INTO qrcode VALUES(null,?,?,?,?,?)", new Object[]{arg7.getMoney(), arg7.getMark(), arg7.getType(), arg7.getPayurl(), new StringBuilder(String.valueOf(System.currentTimeMillis() / 0x3E8)).toString()});
            this.db.setTransactionSuccessful();
        } catch (Throwable v1) {
            this.db.endTransaction();
            throw v1;
        }
        this.db.endTransaction();
    }

    public void addUnionPayOrder(UnionOrderBean unionOrderBean) {
        this.db.beginTransaction();
        try {
            this.db.execSQL("INSERT INTO unionpayorder VALUES(null,?,?,?,null,null,null,null)", new Object[]{unionOrderBean.getBillno(), unionOrderBean.getAmount(), unionOrderBean.getPaytime()});
            this.db.setTransactionSuccessful();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            this.db.endTransaction();
        }
        this.db.endTransaction();
    }

    public void updateUnionPayOrderPayInfo(String remark, String payaccountno, String payaccountname, String billno) {
        this.db.beginTransaction();
        try {
            this.db.execSQL("UPDATE unionpayorder SET mark=?,payaccountno=?,payaccountname=? WHERE tradeno=?",
                    new Object[]{remark, payaccountno, payaccountname, billno});
            this.db.setTransactionSuccessful();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            db.endTransaction();
        }
        db.endTransaction();
    }

    public void updateUnionPayOrderStatus(String result, String tradeno) {
        this.db.beginTransaction();
        try {
            this.db.execSQL("UPDATE unionpayorder SET result=? WHERE tradeno=?", new Object[]{result, tradeno});
            this.db.setTransactionSuccessful();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            this.db.endTransaction();
        }
        this.db.endTransaction();
    }

    public boolean isUnionPayOrderExist(String orderNo) {
        boolean isExist = false;
        Cursor cursor = this.ExecSQLForCursor("SELECT * FROM unionpayorder WHERE tradeno=\'" + orderNo + "\'");
        if (cursor.getCount() > 0) {
            isExist = true;
        }
        cursor.close();
        return isExist;
    }

    public UnionOrderBean findUnionPayOrderById(String orderNo) {
        String queryText = "SELECT * FROM unionpayorder WHERE tradeno=\'" + orderNo + "\'";
        Cursor cursor = this.ExecSQLForCursor(queryText);
        UnionOrderBean unionOrderBean = new UnionOrderBean();
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("tradeno")).equals(orderNo)) {
                unionOrderBean.setBillno(cursor.getString(cursor.getColumnIndex("tradeno")));
                unionOrderBean.setPaytime(cursor.getString(cursor.getColumnIndex("ordertime")));
                unionOrderBean.setAmount(cursor.getString(cursor.getColumnIndex("money")));
                unionOrderBean.setPayaccountname(cursor.getString(cursor.getColumnIndex("payaccountname")));
                unionOrderBean.setPayaccountno(cursor.getString(cursor.getColumnIndex("payaccountno")));
                unionOrderBean.setRemark(cursor.getString(cursor.getColumnIndex("mark")));
            }
        }
        cursor.close();
        return unionOrderBean;
    }

    public List<UnionOrderBean> findAllUnionOrder() {
        List<UnionOrderBean> unionOrderBeans = new ArrayList<>();
        String selection = "SELECT * FROM unionpayorder";
        Cursor cursor = ExecSQLForCursor(selection);
        while (cursor.moveToNext()) {
            UnionOrderBean unionOrderBean = new UnionOrderBean();
            unionOrderBean.setBillno(cursor.getString(cursor.getColumnIndex("tradeno")));
            unionOrderBean.setPaytime(cursor.getString(cursor.getColumnIndex("ordertime")));
            unionOrderBean.setAmount(cursor.getString(cursor.getColumnIndex("money")));
            unionOrderBean.setPayaccountname(cursor.getString(cursor.getColumnIndex("payaccountname")));
            unionOrderBean.setPayaccountno(cursor.getString(cursor.getColumnIndex("payaccountno")));
            unionOrderBean.setRemark(cursor.getString(cursor.getColumnIndex("mark")));
            unionOrderBean.setResult(cursor.getString(cursor.getColumnIndex("result")));
            unionOrderBeans.add(unionOrderBean);
        }
        return unionOrderBeans;
    }

    //type 1 = zfb ,2 = wx
    public boolean isXPosOrderExist(String money, String type, String dt, String business) {
        boolean exist = false;
        String queryText = "SELECT * FROM xposorder WHERE money=\'" + money + "\' AND type =\'" + type + "\' AND dt =\'" + dt + "\' AND business =\'" + business + "\'";
        Cursor cursor = this.ExecSQLForCursor(queryText);
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("money")).equals(money)
                    && cursor.getString(cursor.getColumnIndex("type")).equals(type)
                    && cursor.getString(cursor.getColumnIndex("dt")).equals(dt)
                    && cursor.getString(cursor.getColumnIndex("business")).equals(business)) {
                exist = true;
                break;
            }
        }
        cursor.close();
        return exist;
    }

    public void addXposOrder(String money, String type, String dt, String business) {
        this.db.beginTransaction();
        try {
            this.db.execSQL("INSERT INTO xposorder VALUES(null,?,?,?,?,null)", new Object[]{money, type, dt, business});
            this.db.setTransactionSuccessful();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            this.db.endTransaction();
        }
        this.db.endTransaction();
    }

    public void updateXposOrder(String money, String type, String dt, String business) {
        this.db.beginTransaction();
        try {
            this.db.execSQL("UPDATE xposorder SET result=? WHERE money=? AND type=? AND dt = ? AND business = ? ", new Object[]{"0", money, type, dt, business});
            this.db.setTransactionSuccessful();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            this.db.endTransaction();
        }
        this.db.endTransaction();
    }

    public List<XposBean> findAllXPosOrder() {
        List<XposBean> xposBeans = new ArrayList<>();
        String selection = "SELECT * FROM xposorder";
        Cursor cursor = ExecSQLForCursor(selection);
        while (cursor.moveToNext()) {
            XposBean xposBean = new XposBean();
            xposBean.setTxn_amt(cursor.getString(cursor.getColumnIndex("money")));
            xposBean.setPaychannel(cursor.getString(cursor.getColumnIndex("type")));
            xposBean.setTxn_tm(cursor.getString(cursor.getColumnIndex("dt")));
            xposBean.setMerc_id(cursor.getString(cursor.getColumnIndex("business")));
            xposBean.setResult(cursor.getString(cursor.getColumnIndex("result")));
            xposBeans.add(xposBean);
        }
        return xposBeans;
    }
}

