package com.kyawn.googlecse.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kyawn.googlecse.entity.TextMsg;
import com.kyawn.googlecse.main.SearchEngine;
import com.kyawn.googlecse.utils.SHA1;
import com.thoughtworks.xstream.XStream;

/**
 * Servlet implementation class ReceiveMsg
 */
@WebServlet("/ReceiveMsg")
public class ReceiveMsg extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public Map<String, String> ReceivedMsgMap;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReceiveMsg() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 设置编码
		request.setCharacterEncoding("UTF-8");
		response.setContentType("html/text;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		// 获取输出流

		// 设置一个全局的token,开发者自己设置。api这样解释：Token可由开发者可以任意填写，
		// 用作生成签名（该Token会和接口URL中包含的Token进行比对，从而验证安全性）
		String token = "sanese";
		// 根据api说明，获取上述四个参数
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		if (nonce != null & timestamp != null & echostr != null & signature != null) {
			String[] str = { token, timestamp, nonce };
			Arrays.sort(str); // 字典序排序
			String bigStr = str[0] + str[1] + str[2];
			// SHA1加密
			String digest = new SHA1().getDigestOfString(bigStr.getBytes()).toLowerCase();

			// 确认请求来至微信
			if (digest.equals(signature)) {
				response.getWriter().print(echostr);
			} else {
				response.getWriter().print("invalid signature");
			}
		} else {
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("html/text;charset=utf-8");
		response.setCharacterEncoding("UTF-8");

		SearchEngine se = new SearchEngine();
		StringBuilder sendMsg = new StringBuilder();

		try {
			ReceivedMsgMap = parseXml(request);
			JSONArray searchResults = new JSONArray();
			JSONObject item = new JSONObject();
			searchResults = se.search(ReceivedMsgMap.get("Content")).getJSONArray("items");
			for (int i = 0; i < searchResults.length(); i++) {
				item = searchResults.getJSONObject(i);
				sendMsg.append(
						i + 1 + ".<a href='" + item.getString("link") + "'>" + item.getString("title") + "</a>\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		TextMsg textMsg = new TextMsg();
		textMsg.setToUserName(ReceivedMsgMap.get("FromUserName"));// 发送和接收信息“User”刚好相反
		textMsg.setFromUserName(ReceivedMsgMap.get("ToUserName"));
		textMsg.setCreateTime(new Date().getTime());// 消息创建时间 （整型）
		textMsg.setMsgType("text");// 文本类型消息
		textMsg.setContent(sendMsg.toString());

		// // 第二步，将构造的信息转化为微信识别的xml格式
		XStream xStream = new XStream();
		xStream.alias("xml", textMsg.getClass());
		String textMsg2Xml = xStream.toXML(textMsg);
		System.out.println("sendMsg:" + textMsg2Xml);

		// // 第三步，发送xml的格式信息给微信服务器，服务器转发给用户
		PrintWriter printWriter = response.getWriter();
		printWriter.print(textMsg2Xml);
	}

	public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
		Map<String, String> map = new HashMap<String, String>();

		InputStream inputStream = request.getInputStream();

		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		Element root = document.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> elementList = root.elements();

		for (Element e : elementList)
			map.put(e.getName(), e.getText());

		inputStream.close();
		inputStream = null;
		return map;
	}

}
