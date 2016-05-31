package com.pyn.test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JD {
  // The configuration items

  private Logger logger = LoggerFactory.getLogger(JD.class);

  // private static String redirectURL = "http://order.jd.com/center/list.action";
  private static String redirectURL = "http://cart.jd.com/cart.action";
  private static String loginUrl = "http://passport.jd.com/uc/login";

  // Don't change the following URL
  private static String renRenLoginURL = "https://passport.jd.com/uc/loginService";

  // The HttpClient is used in one session
  private HttpResponse response;
  private HttpClient httpclient = new DefaultHttpClient();
  private String loginname = "13438389109";
  private String loginpwd = "Pyn19881128";

  // private String loginname = "mapzchen";
  // private String loginpwd = "9Eit8Dh54A1";



  /**
   * 
   * <p>
   * Description: 获取Form表单中的nput数据
   * </p>
   * 
   * @return
   * @author Peng Yanan
   * @date 2016年5月31日
   */
  public Map<String, String> getParams() {
    Map<String, String> map = new HashMap<String, String>();
    String html = getText(loginUrl);
    // System.out.println(html);
    Document doc = Jsoup.parse(html);
    // 获取uuid
    Element uuidElement = doc.getElementById("uuid");
    String uuid = uuidElement.attr("value");
    map.put("uuid", uuid);

    Element content = doc.getElementById("formlogin");
    Elements inputs = content.getElementsByTag("input");
    for (Element element : inputs) {
      String name = element.attr("name");
      String value = element.attr("value");
      if (value != null && !"".equals(value)) {
        map.put(name, value);
      }

    }
    return map;
  }

  /**
   * 
   * <p>
   * Description: 下载验证码图片
   * </p>
   * 
   * @param doc
   * @author Peng Yanan
   * @date 2016年5月31日
   */
  public void authcode(Document doc) {
    Element imgElement = doc.getElementById("JD_Verification1");
    String url = "http:" + imgElement.attr("src2");
    System.out.println(url);
    HttpClient httpclient2 = new DefaultHttpClient();
    HttpPost httpost2 = new HttpPost(url);
    HttpResponse response2;
    try {
      response2 = httpclient2.execute(httpost2);
      HttpEntity imgEntity = response2.getEntity();
      if (imgEntity != null) {
        byte[] bit = EntityUtils.toByteArray(imgEntity);
        BufferedOutputStream out = null;
        if (bit.length > 0) {
          try {
            out = new BufferedOutputStream(new FileOutputStream("/authImg/authcode.jpg"));
            logger.info("authcode.jpg download successfully");
            out.write(bit);
            out.flush();
          } finally {
            if (out != null)
              out.close();
          }
        }
      }
    } catch (ClientProtocolException e) {
      logger.error("error message", e);
      e.printStackTrace();
    } catch (IOException e) {
      logger.error("error message", e);
    }
  }

  /**
   * 
   * <p>Description: 登陆</p>
   * @return
   * @author Peng Yanan
   * @date      2016年5月31日
   */
  private boolean login() {
    Map<String, String> map = getParams();

    HttpPost httpost = new HttpPost(renRenLoginURL);
    // All the parameters post to the web site
    List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
    nvps.add(new BasicNameValuePair("ReturnUrl", redirectURL));
    nvps.add(new BasicNameValuePair("loginname", loginname));
    nvps.add(new BasicNameValuePair("nloginpwd", loginpwd));
    nvps.add(new BasicNameValuePair("loginpwd", loginpwd));
    nvps.add(new BasicNameValuePair("nr", "1"));
    nvps.add(new BasicNameValuePair("version", "2015"));
    String rString = String.valueOf(Math.random());
    nvps.add(new BasicNameValuePair("r", rString));
    Iterator it = map.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next().toString();
      String value = map.get(key).toString();
      nvps.add(new BasicNameValuePair(key, value));

    }

    try {
      httpost.setEntity(new UrlEncodedFormEntity(
          (List<? extends org.apache.http.NameValuePair>) nvps));
      response = httpclient.execute(httpost);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      httpost.abort();
    }
    return true;
  }

  
  /**
   * 
   * <p>Description: 获取location信息</p>
   * @return
   * @author Peng Yanan
   * @date      2016年5月31日
   */
  private String getRedirectLocation() {
    BufferedHeader locationHeader = (BufferedHeader) response.getFirstHeader("Location");
    if (locationHeader == null) {
      return null;
    }
    return locationHeader.getValue();
  }

  /**
   * 
   * <p>Description: 抓取数据</p>
   * @param redirectLocation
   * @return
   * @author Peng Yanan
   * @date      2016年5月31日
   */
  private String getText(String redirectLocation) {
    HttpGet httpget = new HttpGet(redirectLocation);
    ResponseHandler<String> responseHandler = new BasicResponseHandler();
    String responseBody = "";
    try {
      responseBody = httpclient.execute(httpget, responseHandler);
    } catch (Exception e) {
      e.printStackTrace();
      responseBody = null;
    } finally {
      httpget.abort();
    }
    return responseBody;
  }

  /**
   * 
   * <p>Description: 登陆成功后 打印跳转页面的内容</p>
   * @author Peng Yanan
   * @date      2016年5月31日
   */
  public void printText() {
    if (login()) {
      System.out.println(getText(redirectURL));
      String redirectLocation = getRedirectLocation();
      if (redirectLocation != null) {
        System.out.println(getText(redirectLocation));
      }
    }
  }

  public static void main(String[] args) {
    JD renRen = new JD();
    renRen.printText();
  }
}
