<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- 
__   _(_)_   _____  
\ \ / / \ \ / / _ \ 
 \ V /| |\ V / (_) |
  \_/ |_| \_/ \___/ 
-->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AppSearch</title>
<style type="text/css">
	body{font-family: Arial,'微软雅黑';}
</style>
</head>
<body>

	<div>
		<h1>APPSEARCH</h1>
	</div>

	<div style="line-height: 60px;">
		<span>1. Search API :</span> <a href="search.jsp">search</a>
	</div>
	<br/>
	<div>
		<span>2. Suggest API :</span> <br/>
		<a href="search/hotwords">搜索热词</a> 
			hotwords?imei=111&model=X3&app_version=550&word=QQ
		<br/>
		<a href="search/sug?imei=111&model=X3&app_version=550&word=QQ">搜索联想词</a><br/>
		<a href="rec.jsp">相关推荐 morelikethis </a><br/>
		suggest [TODO] <br/>
		hotword [TODO] <br/>
	</div>
	<br/>
	<br/>
	<div>
		<span>3. Indexing API :</span> <br/>
		get <a href="indexing.jsp">indexing</a> [ TODO ]<br/>
		add<br/>
		delete<br/>
		update<br/>
	</div>
	<br/>
	<br/>
	
	
	<span style="font-style: italic;font-size: 12px;">Last modified at 2014-11-14</span>
</body>
</html>