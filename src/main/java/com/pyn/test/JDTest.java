package com.pyn.test;

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
  public void test(){
//    String str = "commmmmm({'verifycode':false})";
    String str = "({'verifycode':false})";
    System.out.println(CommonUtils.removeTojson(str));
  }
}
