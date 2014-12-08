<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>App Search</title>
<style type="text/css">
	body{font-family: Arial,'微软雅黑';}
</style>
</head>
<body>
	<div>
		<h1>APPSEARCH</h1>
		<form action="port/packages" method="get">
			key: <input name="key" value="qq"/> <br/>
			apps_per_page:<input name="apps_per_page" value="20"/><br/>
			page_index:<input name="page_index" value="1"/><br/>
			id:<input name="id" value="all"/><br/>
			cs:<input name="cs" value="0"/> 0 | 1<br/>
			elapsedtime:<input name="elapsedtime" value="13899997"/><br/>
			dpi:<input name="dpi" value="480x960"/><br/>
			model: <input name="model" value="vivo X3L"/><br/>
			app_version: <input name="app_version" value="531"/><br/>
			osversion:<input name="osversion" value="4.4"/><br/>
			cfrom:<input name="cfrom" value="local"/> local | from<br/>
			target: <input name="target" value="local"/> local | baidu <br/>
			format:<input name="format" value="html"/> JSON | XML | HTML<br/>
			<br/>
			
			<input type="submit" name="search_btn" value="Search"/>
		</form>
	</div>
</body>
</html>