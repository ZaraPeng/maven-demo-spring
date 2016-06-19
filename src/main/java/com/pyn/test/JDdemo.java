package com.pyn.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class JDdemo {

  private CloseableHttpClient httpClient = getHttpClient();
  private CloseableHttpResponse httpResponse;
  private String loginname = "13438389109";
  private String loginpwd = "Pyn19881128";

  /**
   * 通过GET方式发起http请求
   */
  public void requestByGetMethod(String url) {
    // 创建默认的httpClient实例
    try {
      // 用get方法发送http请求
      HttpGet get = new HttpGet(url);
      System.out.println("执行get请求:...." + get.getURI());
      // 发送get请求
      httpResponse = httpClient.execute(get);
      try {
        // response实体
        HttpEntity entity = httpResponse.getEntity();
        if (null != entity) {
          System.out.println("响应状态码:" + httpResponse.getStatusLine());
          System.out.println("-------------------------------------------------");
          System.out.println("响应内容:" + EntityUtils.toString(entity));
          System.out.println("-------------------------------------------------");
        }
      } finally {
        httpResponse.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  /**
   * POST方式发起http请求
   */
  public void requestByPostMethod(String URL, List<NameValuePair> list) {
    try {
      HttpPost post = new HttpPost(URL); // 这里用上本机的某个工程做测试
      // // 创建参数列表
      // List<NameValuePair> list = new ArrayList<NameValuePair>();
      // list.add(new BasicNameValuePair("j_username", "admin"));
      // list.add(new BasicNameValuePair("j_password", "admin"));
      // url格式编码
      UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
      post.setEntity(uefEntity);
      System.out.println("POST 请求...." + post.getURI());
      // 执行请求
      httpResponse = httpClient.execute(post);
      try {
        HttpEntity entity = httpResponse.getEntity();
        if (null != entity) {
          System.out.println("-------------------------------------------------------");
          System.out.println(EntityUtils.toString(uefEntity));
          System.out.println("-------------------------------------------------------");
        }
      } finally {
        httpResponse.close();
      }

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private CloseableHttpClient getHttpClient() {
    return HttpClients.createDefault();
  }

  private void closeHttpClient(CloseableHttpClient client) throws IOException {
    if (client != null) {
      client.close();
    }
  }

  public void redirectPage() {
    String URL =
        "http://divide.jd.com/user_routing?skuId=2876449&sn=f195d9e951efc5b7a4f5d5f26818b86e&from=pc";
    requestByGetMethod(URL);
  }

  public void queue() {
    String URL =
        "http://bolt.jd.com/captcha.html?from=pc&skuId=2876449&sn=f195d9e951efc5b7a4f5d5f26818b86e";
    requestByGetMethod(URL);
  }

  public void validateQuickBusy() {
    String URL = "http://bolt.jd.com/validate/repeatGoToOrder";
    // All the parameters post to the web site
    String rid = "f195d9e951efc5b7a4f5d5f26818b86e";
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("skuId", "2876449"));
    nvps.add(new BasicNameValuePair("rid", rid));
    requestByPostMethod(URL, nvps);
  }

  public String getText(String redirectLocation) {
    HttpGet httpget = new HttpGet(redirectLocation);
    ResponseHandler<String> responseHandler = new BasicResponseHandler();
    String responseBody = "";
    try {
      responseBody = httpClient.execute(httpget, responseHandler);
    } catch (Exception e) {
      e.printStackTrace();
      responseBody = null;
    } finally {
      httpget.abort();
    }
    return responseBody;
  }

  public Map<String, String> getParams() {
    Map<String, String> map = new HashMap<String, String>();
    String html = getText("http://passport.jd.com/uc/login");
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

  public void login() {
    Map<String, String> map = getParams();

    String loginUrl ="https://passport.jd.com/uc/loginService";
    // All the parameters post to the web site
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("ReturnUrl", "http://cart.jd.com/cart.action"));
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
    requestByPostMethod(loginUrl,nvps);
  }

  @Test
  public void run() {
    login();// 登陆
    redirectPage();// 跳转页面
    queue();// 排队
    validateQuickBusy();// 验证

  }
}
