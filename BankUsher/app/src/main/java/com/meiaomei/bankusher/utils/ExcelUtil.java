package com.meiaomei.bankusher.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.dialog.AlertDialogCommon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    //sd卡地址
    public static String root = Environment.getExternalStorageDirectory().getPath();

    public static void writeExcel(Context context, LinkedHashMap<Integer,
            HashMap<String, String>> linkedHashMap,
                                       String fileName, String[] title,String hint) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage() > 1000000) {
            Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
            return;
        }

        File file;
        File dir = new File(FileUtils.SDPATH + "/xls");
        file = new File(dir, fileName + ".xls");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建Excel工作表
        WritableWorkbook wwb;
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            wwb = Workbook.createWorkbook(os);
            // 添加第一个工作表并设置第一个Sheet的名字
            WritableSheet sheet = wwb.createSheet("vip来访记录表", 0);
            Label label;

            for (int i = 0; i < title.length; i++) {
                // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
                // 在Label对象的子对象中指明单元格的位置和内容
                label = new Label(i, 0, title[i], getHeader());
                // 将定义好的单元格添加到工作表中
                sheet.addCell(label);
            }

            //这种map的遍历效率高
            Iterator iter = linkedHashMap.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()){
                Map.Entry entry=(Map.Entry)iter.next();
                Integer key= (Integer) entry.getKey();
                HashMap hashMap= (HashMap) entry.getValue();
                Label visitTime = new Label(0, i + 1, (String) hashMap.get("visitTime"));
                Label visitAddress = new Label(1, i + 1, (String) hashMap.get("visitAddress"));
                Label name = new Label(2, i + 1, (String) hashMap.get("name"));
                Label age = new Label(3, i + 1, (String) hashMap.get("age"));
                Label sex = new Label(4, i + 1, (String) hashMap.get("sex"));
                Label vipOrder = new Label(5, i + 1, (String) hashMap.get("vipOrder"));
                Label idNumber = new Label(6, i + 1, (String) hashMap.get("idNumber"));

                sheet.addCell(visitTime);
                sheet.addCell(visitAddress);
                sheet.addCell(name);
                sheet.addCell(age);
                sheet.addCell(sex);
                sheet.addCell(vipOrder);
                sheet.addCell(idNumber);
                i++;

            }

            //这种map遍历效率低
//              int i = 0;
//            Iterator<Integer> iterator = linkedHashMap.keySet().iterator();
//            while (iterator.hasNext()) {
//                Integer key = iterator.next();
//                HashMap hashMap = linkedHashMap.get(key);
//
//                Label visitTime = new Label(0, i + 1, (String) hashMap.get("visitTime"));
//                Label visitAddress = new Label(1, i + 1, (String) hashMap.get("visitAddress"));
//                Label name = new Label(2, i + 1, (String) hashMap.get("name"));
//                Label age = new Label(3, i + 1, (String) hashMap.get("age"));
//                Label sex = new Label(4, i + 1, (String) hashMap.get("sex"));
//                Label vipOrder = new Label(5, i + 1, (String) hashMap.get("vipOrder"));
//                Label idNumber = new Label(6, i + 1, (String) hashMap.get("idNumber"));
//
//                sheet.addCell(visitTime);
//                sheet.addCell(visitAddress);
//                sheet.addCell(name);
//                sheet.addCell(age);
//                sheet.addCell(sex);
//                sheet.addCell(vipOrder);
//                sheet.addCell(idNumber);
//                i++;
//            }
//
            new AlertDialogCommon
                    .Builder(context)
                    .setTitleColor(R.color.j_theme_color)
                    .setContents(new String[]{hint})
                    .setIsShowCancelBtn(false)//是否显示取消按钮 不显示
                    .setSubmitBtnText("确定")
                    .setContentColor(R.color.alertTextContent)
                    .setCenterHorizentle(true)
                    .build()
                    .createAlertDialog();

            // 写入数据
            wwb.write();
            // 关闭文件
            wwb.close();
        } catch (Exception e) {
            Log.e("Exception", "writeExcel: " + e.toString());
            e.printStackTrace();
        }

    }

    public static void writeFaceExcel(Context context, LinkedHashMap<Integer,
            HashMap<String, String>> linkedHashMap,
                                  String fileName, String[] title,String hint) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage() > 1000000) {
            Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
            return;
        }

        File file;
        File dir = new File(FileUtils.SDPATH + "/xls");
        file = new File(dir, fileName + ".xls");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建Excel工作表
        WritableWorkbook wwb;
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            wwb = Workbook.createWorkbook(os);
            // 添加第一个工作表并设置第一个Sheet的名字
            WritableSheet sheet = wwb.createSheet("陌生人来访记录表", 0);
            Label label;

            for (int i = 0; i < title.length; i++) {
                // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
                // 在Label对象的子对象中指明单元格的位置和内容
                label = new Label(i, 0, title[i], getHeader());
                // 将定义好的单元格添加到工作表中
                sheet.addCell(label);
            }

            //这种map的遍历效率高
            Iterator iter = linkedHashMap.entrySet().iterator();
            int i = 0;
            while (iter.hasNext()){
                Map.Entry entry=(Map.Entry)iter.next();
                Integer key= (Integer) entry.getKey();
                HashMap hashMap= (HashMap) entry.getValue();
                Label visitTime = new Label(0, i + 1, (String) hashMap.get("visitTime"));
                Label visitAddress = new Label(1, i + 1, (String) hashMap.get("visitAddress"));

                sheet.addCell(visitTime);
                sheet.addCell(visitAddress);
                i++;

            }

            new AlertDialogCommon
                    .Builder(context)
                    .setTitleColor(R.color.j_theme_color)
                    .setContents(new String[]{hint})
                    .setIsShowCancelBtn(false)//是否显示取消按钮 不显示
                    .setSubmitBtnText("确定")
                    .setContentColor(R.color.alertTextContent)
                    .setCenterHorizentle(true)
                    .build()
                    .createAlertDialog();

            // 写入数据
            wwb.write();
            // 关闭文件
            wwb.close();
        } catch (Exception e) {
            Log.e("Exception", "writeExcel: " + e.toString());
            e.printStackTrace();
        }

    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD);// 定义字体
        try {
            font.setColour(Colour.BLUE);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            // format.setBorder(Border.ALL, BorderLineStyle.THIN,
            // Colour.BLACK);// 黑色边框
            // format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    /**
     * 获取SD可用容量
     */
    private static long getAvailableStorage() {
        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }
}
