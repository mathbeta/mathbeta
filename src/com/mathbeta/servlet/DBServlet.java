package com.mathbeta.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

public class DBServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String result = "";
		Map<String, String> env = System.getenv();
		StringBuilder sb = new StringBuilder("jdbc:mysql://");
		sb.append(env.get("MYSQL_PORT_3306_TCP_ADDR")).append(":").append(
				env.get("MYSQL_PORT_3306_TCP_PORT")).append("/").append(
				env.get("MYSQL_INSTANCE_NAME"));
//				"test");
		String url = sb.toString();
		String user = env.get("MYSQL_USERNAME");
//		String user = "root";
		String passwd = env.get("MYSQL_PASSWORD");
//		String passwd = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url, user, passwd);
			PreparedStatement stmt = conn
					.prepareStatement("select * from wp_users");
			ResultSet set = stmt.executeQuery();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			while (set.next()) {
				Map<String, Object> o = new HashMap<String, Object>();
				o.put("name", set.getString("user_nicename"));
				o.put("reg", set.getDate("user_registered"));
				o.put("email", set.getString("user_email"));
				list.add(o);
			}
			stmt.close();
			conn.close();

			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("url", url);
			ret.put("user", user);
			ret.put("passwd", passwd);
			ret.put("list", list);

			String s = JSON.toJSONString(ret);
			System.out.println(s);
			result = s;
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage() + ", exception occured...";
		}
		result += "<br>url: " + url;
		result += "<br>user: " + user;
		result += "<br>passwd: " + passwd;
		resp.setContentType("text/html;charset=utf-8");
		resp.getWriter().write(result);
	}
}
