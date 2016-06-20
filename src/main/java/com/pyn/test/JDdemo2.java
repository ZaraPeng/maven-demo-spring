package com.pyn.test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
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
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDdemo2 {

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
   * Description: 登陆时获取LoginForm表单中的nput数据
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

    // 判断是否需要输入验证码
    // Element imgDivElement = doc.getElementById("o-authcode");
    // String styleString = imgDivElement.attr("style");
    // if (styleString.contains("none")) {
    // logger.info("need input authcode");
    // authcode(doc);
    // } else {
    // logger.info("no need input authcode");
    // }

    // 下载验证码
    authcode(doc);

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
   * Description: 是否显示验证码 https://passport.jd.com/uc/showAuthCode?loginName=13438389109&version=2015
   * </p>
   * 
   * @return
   * @author Peng Yanan
   * @date 2016年6月1日
   */
  public Boolean showAuthCode() {
    String url = "https://passport.jd.com/uc/showAuthCode";
    List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
    nvps.add(new BasicNameValuePair("loginName", loginname));
    nvps.add(new BasicNameValuePair("version", "2105"));
    try {
      String jsonString = CommonUtils.removeTojson(getHttpRequest(url, nvps));
      logger.info("jsonString=" + jsonString);
      JSONObject resultJsonObject = new JSONObject(jsonString);
      return resultJsonObject.getBoolean("verifycode");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * 
   * <p>
   * Description: 获取请求数据
   * </p>
   * 
   * @param url
   * @param nvps
   * @return
   * @author Peng Yanan
   * @date 2016年6月1日
   */
  public String getHttpRequest(String url, List<BasicNameValuePair> nvps) {
    HttpPost httpost = new HttpPost(url);
    BufferedReader bufferedReader = null;
    StringBuilder entityStringBuilder = new StringBuilder();
    try {
      httpost.setEntity(new UrlEncodedFormEntity(
          (List<? extends org.apache.http.NameValuePair>) nvps));
      response = httpclient.execute(httpost);
      // 得到httpResponse的状态响应码
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 200) {
        // 得到httpResponse的实体数据
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
          try {
            bufferedReader =
                new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"),
                    8 * 1024);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
              entityStringBuilder.append(line);
            }
            // 利用从HttpEntity中得到的String生成JsonObject
            // JSONObject resultJsonObject = new JSONObject(entityStringBuilder.toString());
            // System.out.println(resultJsonObject);
            System.out.println(entityStringBuilder.toString());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      logger.error("error message", e);
    } finally {
      httpost.abort();
    }
    return entityStringBuilder.toString();
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
    String url = "http:" + imgElement.attr("src2") + "&yys=" + new Date().getTime();
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
            StringBuilder filename = new StringBuilder(String.valueOf(Math.random()));
            filename.append(".jpg");
            out = new BufferedOutputStream(new FileOutputStream(filename.toString()));
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
   * <p>
   * Description: 登陆 https://passport.jd.com/uc/loginService
   * </p>
   * 
   * @return
   * @author Peng Yanan
   * @date 2016年5月31日
   */
  public boolean login() {
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

    // 判断是否需要输入验证码
    if (showAuthCode()) {
      nvps.add(new BasicNameValuePair("authcode", "wxxy"));
      logger.info("需要输入验证码，登陆失败");
      return false;
    }


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
   * <p>
   * Description: 快速下单，调用：http://easybuy.jd.com/skuDetail/newSubmitEasybuyOrder.action
   * </p>
   * 
   * @param skuId 商品ID
   * @param num 购买数量
   * @author Peng Yanan
   * @date 2016年5月31日
   */
  public String easybuysubmit(String skuId, String num) {
    String easybuysubmitURL = "http://easybuy.jd.com/skuDetail/newSubmitEasybuyOrder.action";
    // All the parameters post to the web site
    List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
    nvps.add(new BasicNameValuePair("callback", "easybuysubmit"));
    nvps.add(new BasicNameValuePair("skuId", skuId));
    nvps.add(new BasicNameValuePair("num", num));

    String jsonString = CommonUtils.removeTojson(getHttpRequest(easybuysubmitURL, nvps));
    JSONObject resultJsonObject;
    String orderUrlString = "";
    try {
      resultJsonObject = new JSONObject(jsonString);
      orderUrlString = resultJsonObject.getString("jumpUrl");
    } catch (JSONException e) {
      logger.error("error:", e);
    }
    return orderUrlString;
  }


  /**
   * 
   * <p>
   * Description: 获取location信息
   * </p>
   * 
   * @return
   * @author Peng Yanan
   * @date 2016年5月31日
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
   * <p>
   * Description: 抓取数据
   * </p>
   * 
   * @param redirectLocation
   * @return
   * @author Peng Yanan
   * @date 2016年5月31日
   */
  public String getText(String redirectLocation) {
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
   * <p>
   * Description: 登陆成功后 打印跳转页面的内容
   * </p>
   * 
   * @author Peng Yanan
   * @date 2016年5月31日
   */
  public void printText() {
    if (login()) {// 登陆
      System.err.println("登陆成功");
      // 快速下单
      // easybuysubmit("2888228", "1");
      // getText("http://item.jd.com/2782638.html");
      // System.err
      // .println("开始跳转页面---> http://divide.jd.com/user_routing?skuId=2782638&sn=d039324512cd07852b2fe4a708db7cd4&from=pc");
      // redirectPage();// 跳转页面
      System.err
          .println("开始排队 -->http://bolt.jd.com/captcha.html?from=pc&skuId=2782638&sn=d039324512cd07852b2fe4a708db7cd4");
      queue();// 排队
      System.err.println("等待前：" + System.currentTimeMillis());
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.err.println("等待后：" + System.currentTimeMillis());
      System.err.println("开始验证 -->http://bolt.jd.com/validate/repeatGoToOrder");
      validateQuickBusy();// 验证
    }
  }

  /**
   * 
   * <p>
   * Description: 抢购跳转页面
   * </p>
   * 
   * @author Peng Yanan
   * @date 2016年6月18日
   */
  public void redirectPage() {
    String URL =
        "http://divide.jd.com/user_routing?skuId=2782638&sn=d039324512cd07852b2fe4a708db7cd4&from=pc";
    getText(URL);
  }

  /**
   * 
   * <p>
   * Description: 排队
   * </p>
   * 
   * @author Peng Yanan
   * @date 2016年6月18日
   */
  public void queue() {
    String URL =
        "http://bolt.jd.com/captcha.html?from=pc&skuId=2782638&sn=d039324512cd07852b2fe4a708db7cd4";
    getText(URL);
  }

  /**
   * 
   * <p>
   * Description: 验证排队是否成功页面
   * </p>
   * 
   * @author Peng Yanan
   * @date 2016年6月18日
   */
  public void validateQuickBusy() {
    String URL = "http://bolt.jd.com/validate/repeatGoToOrder?skuId=2782638&sn=d039324512cd07852b2fe4a708db7cd4";
    // All the parameters post to the web site
    // String rid = "d039324512cd07852b2fe4a708db7cd4";
    // List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
    // nvps.add(new BasicNameValuePair("skuId", "2782638"));
    // nvps.add(new BasicNameValuePair("rid", rid));
    // getHttpRequest(URL, nvps);
    HttpPost httpost = new HttpPost(URL);
//    httpost.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
//    httpost.setHeader("Referer", "http://bolt.jd.com/captcha.html?from=pc&skuId=2782638&sn=d039324512cd07852b2fe4a708db7cd4");
    BufferedReader bufferedReader = null;
    StringBuilder entityStringBuilder = new StringBuilder();
    try {
      // httpost.setEntity(new UrlEncodedFormEntity(
      // (List<? extends org.apache.http.NameValuePair>) nvps));
      response = httpclient.execute(httpost);
      // 得到httpResponse的状态响应码
      int statusCode = response.getStatusLine().getStatusCode();
      if (statusCode == 200) {
        // 得到httpResponse的实体数据
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
          try {
            bufferedReader =
                new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"),
                    8 * 1024);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
              entityStringBuilder.append(line);
            }
            // 利用从HttpEntity中得到的String生成JsonObject
            // JSONObject resultJsonObject = new JSONObject(entityStringBuilder.toString());
            // System.out.println(resultJsonObject);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    } catch (Exception e) {
      logger.error("error message", e);
    } finally {
      httpost.abort();
    }
  }

  /**
   * 
   * <p>
   * Description: 抢购
   * </p>
   * 
   * @author Peng Yanan
   * @date 2016年6月18日
   */
  public void quickBuy() {
    String URL = "http://bolt.jd.com/seckill/seckill.action";
    // All the parameters post to the web site
    Long timestamp = System.currentTimeMillis() / 1000;
    String rid = timestamp.toString() + "d039324512cd07852b2fe4a708db7cd4";
    List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
    nvps.add(new BasicNameValuePair("skuId", "2782638"));
    nvps.add(new BasicNameValuePair("num", "1"));
    nvps.add(new BasicNameValuePair("rid", rid));
    getHttpRequest(URL, nvps);
  }



  public static void main(String[] args) {
    JDdemo2 renRen = new JDdemo2();
    renRen.printText();
  }
}
