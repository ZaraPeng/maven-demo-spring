package com.pyn.test;

import org.springframework.util.StringUtils;

public class CommonUtils {

  /**
   * 
   * <p>
   * Description: 去除'()前后'
   * </p>
   * 
   * @return
   * @author Peng Yanan
   * @date 2016年6月1日
   */
  public static String removeTojson(String str) {
    // str = StringUtils.delete(str, "(");
    // str = StringUtils.delete(str, ")");
    int i = str.indexOf("(");
    String beforeStr = str.substring(0, i);
    String result = StringUtils.delete(str, beforeStr);
    result = StringUtils.deleteAny(result, "()");
    return result;
  }
}
