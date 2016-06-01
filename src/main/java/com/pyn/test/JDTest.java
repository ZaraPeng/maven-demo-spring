package com.pyn.test;

import org.junit.Test;

public class JDTest {

  private static String loginUrl = "http://passport.jd.com/uc/login";

  @Test
  public void showAuthCodeTest() {
    JD jd = new JD();
    // 登陆
    jd.getText(loginUrl);
    jd.showAuthCode();
  }
}
