package com.pyn.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JD {
  // The configuration items


  private static String redirectURL = "http://order.jd.com/center/list.action";
  private static String loginUrl = "http://passport.jd.com/uc/login";
  // Don't change the following URL
  private static String renRenLoginURL = "https://passport.jd.com/uc/loginService";

  // The HttpClient is used in one session
  private HttpResponse response;
  private HttpClient httpclient = new DefaultHttpClient();
  private String loginname = "13438389109";
  private String loginpwd = "Pyn19881128";


  public Map<String, String> getParams() {
    Map<String, String> map = new HashMap<String, String>();
    String html = getText(loginUrl);
    Document doc = Jsoup.parse(html);
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
    // Elements links = content.getElementsByTag("input");
    return map;
  }

  private boolean login() {
    Map<String, String> map = getParams();

    HttpPost httpost = new HttpPost(renRenLoginURL);
    // All the parameters post to the web site
    List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
    nvps.add(new BasicNameValuePair("ReturnUrl", redirectURL));
    // nvps.add(new BasicNameValuePair("loginname", Constant.userName));
    // nvps.add(new BasicNameValuePair("nloginpwd", Constant.password));
    // nvps.add(new BasicNameValuePair("loginpwd", Constant.password));
    nvps.add(new BasicNameValuePair("loginname", loginname));
    nvps.add(new BasicNameValuePair("nloginpwd", loginpwd));
    nvps.add(new BasicNameValuePair("loginpwd", loginpwd));
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

  private String getRedirectLocation() {
    BufferedHeader locationHeader = (BufferedHeader) response.getFirstHeader("Location");
    if (locationHeader == null) {
      return null;
    }
    return locationHeader.getValue();
  }

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
      // httpclient.getConnectionManager().shutdown();
    }
    return responseBody;
  }

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
    // renRen.getParams();
    renRen.printText();
  }
}
