package com.pyn.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class JDTest {

  private static String loginUrl = "http://passport.jd.com/uc/login";
  private JD jd = new JD();

  @Test
  public void showAuthCodeTest() {
    // 访问登陆页面
    jd.getText(loginUrl);
    jd.showAuthCode();

  }

  @Test
  public void getAuthImg() {
    jd.getParams();
  }

  @Test
  public void test() {
    // String str = "commmmmm({'verifycode':false})";
    String str = "({'verifycode':false})";
    System.out.println(CommonUtils.removeTojson(str));
  }


  /**
   * unix时间戳转换为dateFormat
   * 
   * @param beginDate
   * @return
   */
  public static String timestampToDate(String beginDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String sd = sdf.format(new Date(Long.parseLong(beginDate)));
    return sd;
  }

  @Test
  public void dateTOtimestampTest() {
    Long timestamp = System.currentTimeMillis() / 1000;
    System.out.println(timestamp);

  }

  @Test
  public void timestampTOdateTest() {
    SimpleDateFormat fm1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    SimpleDateFormat fm2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    long unixLong = 0;
//    Long timestamp = System.currentTimeMillis() / 1000;
    Long timestamp1 =1466235684L;
    Long timestamp2 =1466236482L;
    System.out.println(fm2.format(timestamp1));
    System.out.println(fm2.format(timestamp2));
  }

}
