#!/usr/bin/python
#coding=utf-8
#
#  fetch app info from mysql 
#
#  BY yangzt@vivo 20140806
#
import MySQLdb

debug = False

def fetch(cursor,pageIndex,pageSize):
	sql = "select id,appCnName,appEnName, appPackage,appAuthor, appKeyword, downloadCount, official, appStatus, filter_model, minSdkVersion, maxSdkVersion, apkSize, appVersion,appVersionCode,appIcon,appApk,avgComment,commentCount,patchs,auto_update  from t_app_info where appStatus!=12 limit " +str(pageIndex*pageSize) +"," + str(pageSize)	
	count = cursor.execute(sql)
	if debug:
		print ">>>fetch total apps: [%d]" % count

	#apps = cursor.fetchmany(10)
	apps = cursor.fetchall()
	for app in apps:
	    	if debug:
			print app[0:5]
		else:
			pass 

	#cursor.scroll(0,mode='absolute')  
	# XXX:  fetch again 

	return apps


if __name__ == '__main__':
	debug = True
	conn = MySQLdb.connect(host="172.20.124.107",user="appstore",passwd="appstore",db="appstore",charset="utf8")
	cursor = conn.cursor()
	d = fetch(cursor,0,20)
	if debug:
		print 'return : ',len(d)
	conn.close()
# END
