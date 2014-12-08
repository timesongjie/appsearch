<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AppSearch</title>
<style type="text/css">
	.tab > tr:nth-child(even) > td {background-color: #ccc;}   
	.tab > tr:nth-child(odd) > td {background-color: #eee;}  
</style>
</head>
<body>
	关键词：<s:property value="sresult.keyword"/><br/>
	搜索到<s:property value="sresult.totalCount"/>条记录  
	<s:property value="sresult.result"/> 
	<s:property value="sresult.resultMsg"/><br/>
	当前第<s:property value="sresult.pageIndex"/>页，
	每页<s:property value="sresult.pageSize"/>条记录,实际 <s:property value="sresult.pageSize"/>条，来自
	<s:property value="sresult.from"/>，
	<br/>下一页是第 
	<s:property value="sresult.pageNext + 1"/>页 
	<table class="tab">
	<tr style="background-color: silver;">
		<th></th><th>ID</th><th>Icon</th><th>名称</th><th>关键字</th><th>包名</th><th>开发者</th><th>下载数</th><th></th><th>自更新</th><th>From</th>
	</tr>
	
	<s:iterator id="sresult" value="sresult.apps" var="app" status="st">
		<tr>
			<td><s:property value="#st.index+1"/></td>
			<td><s:property value="#app.id"/> </td>
			<td><img width="32" src="<s:property value="#app.iconUrl"/>"/></td>
			<td><s:property value="#app.cnName"/> </td>
			<td><s:property value="#app.keyword"/> </td>
			<td><s:property value="#app.pkgName"/> </td>
			<td><s:property value="#app.developer"/> </td>
			<td><s:property value="#app.downloadCount"/> </td>
			<td><a href="<s:property value="#app.downloadUrl"/>" target="_blank">下载</a></td>
			<td><s:property value="#app.autoUpdate"/> </td>
			<td><s:property value="#app.from"/> </td>
		</tr>
	</s:iterator>
	</table>
	<br/>
	<span>注意：翻页请返回查询表单，修改page_index参数 和/或 target参数</span>
	<br/>
	<br/>
</body>
</html>