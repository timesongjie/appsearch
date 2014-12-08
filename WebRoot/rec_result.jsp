<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AppSearch</title>
</head>
<body>
是否有结果：<s:property value="recResult.result"/> <s:property value="recResult.resultMsg"/> 
<br/>
推荐列表：<br/>

<table>
	<tr style="background-color: silver;">
		<th></th><th>ID</th><th>Icon</th><th>名称</th><th>包名</th><th>开发者</th><th>下载数</th><th></th>
	</tr>
	
	<s:iterator id="recResult" value="recResult.apps" var="app" status="st">
		<tr>
			<td><s:property value="#st.index+1"/></td>
			<td><s:property value="#app.id"/> </td>
			<td><img width="32" src="<s:property value="#app.iconUrl"/>"/></td>
			<td><s:property value="#app.cnName"/> </td>
			<td><s:property value="#app.pkgName"/> </td>
			<td><s:property value="#app.developer"/> </td>
			<td><s:property value="#app.downloadCount"/> </td>
			<td><a href="<s:property value="#app.downloadUrl"/>" target="_blank">下载</a></td>
		</tr>
	</s:iterator>
	</table>
</body>
</html>