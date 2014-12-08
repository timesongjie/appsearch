<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>App Search :: rec</title>
<style type="text/css">
	body{font-family: Arial,'微软雅黑';}
</style>
</head>
<body>
	<div>
		<h1>APPSEARCH REC</h1>
		<form action="app/rec" method="get">
			package_name: <input name="package_name" value="com.tencent.qq"/> <br/>
			cs:<input name="cs" value="0"/><br/>
			elapsedtime:<input name="elapsedtime" value="13899997"/><br/>
			dpi:<input name="dpi" value="480x960"/><br/>
			model: <input name="model" value="X3"/><br/>
			app_version: <input name="app_version" value="510"/><br/>
			osversion:<input name="osversion" value="4.4"/><br/>
			cfrom:<input name="cfrom" value="local"/><br/>
			target: <input name="target" value="local"/><br/>
			format:<input name="format" value="html"/><br/> json | xml | html
			<br/>
			
			<input type="submit" name="search_btn" value="Search"/>
		</form>
	</div>
</body>
</html>