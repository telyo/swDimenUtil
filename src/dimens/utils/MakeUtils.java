package dimens.utils;

import dimens.constants.DimenTypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

public class MakeUtils {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
    private static final String XML_RESOURCE_START = "<resources>\r\n";
    private static final String XML_RESOURCE_END = "</resources>\r\n";
    private static final String XML_DIMEN_TEMPLETE = "<dimen name=\"qb_%1$spx_%2$d\">%3$.2fdp</dimen>\r\n";


    private static final String XML_BASE_DPI = "<dimen name=\"base_dpi\">%ddp</dimen>\r\n";
    private static final int MAX_SIZE = 720;

    /**
     * 生成的文件名
     */
    private static final String XML_NAME = "dimens.xml";


    /**
     * 方法名是px2dip 但实际是算出其他尺寸基于设计稿的宽度的dp尺寸（改为adapterBaseDip比较好）
     *
     * @param pxValue     想要的dp 写的是pxValue 但是表达的意思是dp（改为normalDip 比较好）
     * @param sw          当前要适配的屏幕可用高度和宽度的最小尺寸的dp值(屏幕宽度)
     * @param designWidth 设计图的屏幕可用高度和宽度的最小尺寸的dp值(屏幕宽度)
     * @return 在当前屏幕里的实际dp(localDip
     *
     * 总的来说 就是正常的dp(设计稿上的dp长度)长度转换为在当前设备的dp长度
     */
    public static float px2dip(float pxValue, int sw, int designWidth) {
        float dpValue = (pxValue / (float) designWidth) * sw;
        BigDecimal bigDecimal = new BigDecimal(dpValue);
        float finDp = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        return finDp;
    }


    /**
     * 生成所有的尺寸数据
     *
     * @param type
     * @return
     */
    private static String makeAllDimens(DimenTypes type, int designWidth) {
        float dpValue;
        String temp;
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(XML_HEADER);
            sb.append(XML_RESOURCE_START);
            //备份生成的相关信息
            temp = String.format(XML_BASE_DPI, type.getSwWidthDp());
            sb.append(temp);
            for (int i = 0; i <= MAX_SIZE; i++) {

                dpValue = px2dip((float) i, type.getSwWidthDp(), designWidth);
                temp = String.format(XML_DIMEN_TEMPLETE, "", i, dpValue);
                sb.append(temp);
            }


            sb.append(XML_RESOURCE_END);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * 生成的目标文件夹
     * 只需传宽进来就行
     *
     * @param type     枚举类型
     * @param buildDir 生成的目标文件夹
     */
    public static void makeAll(int designWidth, dimens.constants.DimenTypes type, String buildDir) {
        try {
            //生成规则
            final String folderName;
            if (type.getSwWidthDp() > 0) {
                //适配Android 3.2+
                folderName = "values-sw" + type.getSwWidthDp() + "dp";
            } else {
                return;
            }

            //生成目标目录
            File file = new File(buildDir + File.separator + folderName);
            if (!file.exists()) {
                file.mkdirs();
            }

            //生成values文件
            FileOutputStream fos = new FileOutputStream(file.getAbsolutePath() + File.separator + XML_NAME);
            fos.write(makeAllDimens(type, designWidth).getBytes());
            fos.flush();
            fos.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
