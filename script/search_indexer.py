#!/usr/bin/env python
#coding=utf-8
#
# Copyright (C) 2014 yangzt@vivo
# Released under vivo
# BY yangzt@vivo 201409
# 
# DESC: search index listener   
#

## config
#redis
redis_master_host = '192.168.2.99'
redis_master_port = 6381
#es
elastic_search_node = '192.168.2.99:9200' 
#mysql
mysql_host ='172.20.124.107'
mysql_database = 'appstore'
mysql_user = 'appstore'
mysql_password = 'appstore'

#debug
debug=True

##log
import sys
raw_out = sys.stdout
flog=open('indexer.log','aw')
sys.stdout=flog
import time
def log(*s):
	print time.strftime('%Y-%m-%d %H:%M:%S'),
	for ss in s:
		print ss,
	print 
	flog.flush()

## run 
log('[START]')

## connect redis 
log('connect redis : ',redis_master_host,redis_master_port)
import redis
CHANNEL_NAME = "chan_appstore_indexing"

redis_conn_pool = redis.ConnectionPool(host=redis_master_host,port=redis_master_port,db=0)
redis_node = redis.Redis(connection_pool=redis_conn_pool)
log('... ok')

## connect elastic search 
log('connect elastic search : ',elastic_search_node )
import pyes
from pyes.exceptions import (NotFoundException, IndexAlreadyExistsException)
INDEX_NAME = 'appsearch'
INDEX_TYPE = 'apps_vivo'

es_node = pyes.es.ES(elastic_search_node,timeout=5)

def getIndexById(app_id):
	return es_node.get(INDEX_NAME,INDEX_TYPE,app_id)
log('... ok')

## connect appstore database
log('connect appstore mysql : ',mysql_host,mysql_database)
import MySQLdb
sql = "select id,appCnName,appEnName, appPackage,appAuthor, appKeyword, downloadCount, official, appStatus, filter_model, minSdkVersion, maxSdkVersion, apkSize, appVersion,appVersionCode,appIcon,appApk,avgComment,commentCount,patchs,auto_update  from t_app_info where id="
log('... ok')

## pubsub handlers 
def indexing_handler(msg):
	d =eval(msg['data'])
	if debug:
		log('[DEBUG] recv msg: ', d) 
	app_ids =  d['app_id'].split(',')
	if debug:
		log('[DEBUG] split ids: ',app_ids)
	for app_id in app_ids:
		if app_id=='' or app_id.strip()=='' or app_id==None:
			continue
		if (d['action']=='onsale'):
			onsale_handler(app_id)
		elif (d['action']=='offsale'):
			offsale_handler(app_id)
		elif (d['action']=='update_info'):
			update_info_handler(app_id) 
		else:
			log('[WARN] unknown action: ',d)
	#end

def get_app(app_id):
	appstore_conn = MySQLdb.connect(host=mysql_host,user=mysql_user,passwd=mysql_password,db=mysql_database,charset="utf8")
	appstore_cursor = appstore_conn.cursor()
	count = appstore_cursor.execute(sql+str(app_id)+";")
	app = appstore_cursor.fetchone()
	if not app:
		log('[ERROR] not found onsale app id=',app_id)
		return None
	if debug:
		log('[DEBUG] fetch app: ',app)
	
	#appstore_cursor.commit() # use when insert/update
	appstore_cursor.close()
	appstore_conn.close()
	return app

def onsale(app):
	es_node.index({'id':app[0],'cnName':app[1],
				'enName':app[2],
				'pkgName':app[3],
				'developer':app[4],
				'keyword':app[5],
				'downloadCount':app[6],
				'official':app[7],
				'appStatus':app[8],
				'filterModel':app[9],
				'minSdkVersion':app[10] if app[10] else 0,
				'maxSdkVersion':app[11] if app[11] else 10000,
				'pkgSize':app[12],
                                'versionName':app[13],
                                'versionCode':app[14],
                                'iconUrl':app[15],
                                'downloadUrl':app[16],
                                'avgComment':app[17],
                                'commentCount':app[18],
                                'patchs':app[19],
                                'autoUpdate':app[20]
				},
				INDEX_NAME,INDEX_TYPE,id=app[0]) 
	#end

def delete(app_id):
	try:
                es_node.delete(INDEX_NAME,INDEX_TYPE,app_id)
        except NotFoundException,e:
                log('[WARN] requet to delete not found index, app_id=',app_id )
	#end

def onsale_handler(app_id):
	log('[MSG] onsale:', app_id)
	app = get_app(app_id)
	if app:
		onsale(app)
		log('[MSG] onsale OK')
	else:
		log('[WARN] onsale not found id')

def offsale_handler(app_id):
	log('[MSG] offsale:', app_id)
	delete(app_id)
	log('[MSG] offsale OK')

def update_info_handler(app_id):
	log('[MSG] update_info:', app_id)
	delete(app_id)
	log('[MSG] delete,then onsale ... ')
	app = get_app(app_id)
	if not app:
		return None
	if app[8]==0 or app[8]==13:
		onsale(app) 
		log('[MSG] update_info OK')
	else:
		log('[MSG] update offsale app , ignore it.')

## sub channel 
log('[SUB] Sub redis channel : ',CHANNEL_NAME )
ps = redis_node.pubsub() 
ps.subscribe(**{CHANNEL_NAME:indexing_handler})
ps_thread = ps.run_in_thread(sleep_time=0.002) # second 

log('[RUN] Wait message here ...' )
import time,traceback
try:
	while True:
		if not ps_thread.isAlive():
			break
		time.sleep(1)

except KeyboardInterrupt, e: 
	traceback.print_exc()
	ps_thread.stop()
	ps.close()
	log('[SUB] Stop sub by keyboard(ctrl+c)')


ps_thread.stop()
ps.close()
log('[SUB] Stop sub redis channel (close thread).')
log('[QUIT]')

sys.stdout=raw_out
flog.close()

redis_conn_pool.disconnect()
#END 
