#!/usr/bin/env python
#coding=utf-8
#
# pyes (python client for ElasticSearch) init index  
# 
# BY yangzt@vivo 201408
#
import time
import random
import pyes
import MySQLdb

import fetch_app


INDEX_NAME = "appsearch_v1" # XXX note here 
INDEX_NAME_ALIAS = "appsearch"
INDEX_TYPE = "apps_vivo"
conn = pyes.es.ES('192.168.2.183:9200',timeout=5) # timeout 5s ?

mysql_conn = MySQLdb.connect(host="172.20.124.107",user="appstore",passwd="appstore",db="appstore",charset="utf8")


## init 
def init():
	conn.indices.create_index_if_missing(INDEX_NAME) # index -> database
	time.sleep(3)
	
	mapping = {u'id':{'index':'not_analyzed','type':u'long'},
		u'cnName':{'index':'analyzed','type':u'string','analyzer':'ik'},
		u'enName':{'index':'analyzed','type':u'string'},
		u'pkgName':{'index':'analyzed','type':u'string'},
		u'developer':{'index':'analyzed','type':u'string','analyzer':'ik'},
		u'keyword':{'index':'analyzed','type':u'string','analyzer':'ik'},
		
		u'downloadCount':{'index':'not_analyzed','type':u'long'},	
		u'official':{'index':'not_analyzed','type':u'string'},
		u'appStatus':{'index':'not_analyzed','type':u'string'},
		u'filterModel':{'index':'not_analyzed','type':u'string'},
		u'minSdkVersion':{'index':'not_analyzed','type':u'integer'},
		u'maxSdkVersion':{'index':'not_analyzed','type':u'integer'},

		u'pkgSize':{'index':'not_analyzed','type':u'integer'},
		u'versionName':{'index':'not_analyzed','type':u'string'},
		u'versionCode':{'index':'not_analyzed','type':u'long'},
		u'iconUrl':{'index':'not_analyzed','type':u'string'},
		u'downloadUrl':{'index':'not_analyzed','type':u'string'},

		u'avgComment':{'index':'not_analyzed','type':u'float'},
		u'commentCount':{'index':'not_analyzed','type':u'long'},
		u'patchs':{'index':'not_analyzed','type':u'string'},
		u'autoUpdate':{'index':'not_analyzed','type':u'integer'},
		}

	conn.indices.put_mapping(INDEX_TYPE,{'properties':mapping},INDEX_NAME) # type -> table
	conn.indices.add_alias(INDEX_NAME_ALIAS,INDEX_NAME)

## delete index type (mapping)
def deleteMapping():
	conn.indices.delete_mapping(INDEX_NAME,'apps_vivo') # XXX big bang ! 

import time
## index
def index():
	cursor = mysql_conn.cursor()
	for i in range(0,5000):
		if (i%20 == 0):
			time.sleep(0.5)
		print '>fetch page ',i
		apps = fetch_app.fetch(cursor,i,20)
		if len(apps)<1:
			print 'fetch no apps, break here !'
			break
		print ' indexing page ',i 
		for app in apps:
			conn.index({'id':app[0],'cnName':app[1],
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
				INDEX_NAME,INDEX_TYPE,id=app[0],bulk=True)
		# XXX: add more
	conn.indices.refresh()

	mysql_conn.close()
## delete index
def delete_index_by_id(id):
	conn.delete(INDEX_NAME,INDEX_TYPE,id)

def delete_index():
	query = pyes.query.QueryStringQuery('keyword') # XXX 
	conn.delete_by_query(INDEX_NAME,INDEX_TYPE,query)
	
	conn.flush() # XXX 

## search 
def search_by_id(id):
	return conn.get(INDEX_NAME,INDEX_TYPE,id)


def search(kw):
	must = pyes.query.QueryStringQuery(kw)
	res = conn.search(pyes.query.BoolQuery(must=must),INDEX_NAME,INDEX_TYPE,start=0,size=20)#,sort="{'_score':{'order':'desc'}}")#,'downloadCount':{'order':'desc'},'autoUpdate':{'order':'desc'}}",)
	for app in res:
		print 'id=',app['id'],' cnName=',app['cnName'].encode('utf-8'),' keyword=',app['keyword'].encode('utf-8'),' dc=',app['downloadCount']
	

def usage(s):
	print 'USAGE:\n\t',s,' -s [init|index|search] -h ','\nor\n\t',s,' --step [init|index|search] --help'
	print ''

import getopt
import sys
if __name__ == '__main__':
	if len(sys.argv)<2:
		usage(sys.argv[0])
		sys.exit()
	
	try:
		print '[START]'
		options,args = getopt.getopt(sys.argv[1:],'s:h',['step=','help'])

		for name,value in options:
			if name in ('-s','--step'):
				if value=='init':
					print 'init...'
					init()
				elif value=='index':
					print 'index...'
					print index()
				elif value=='search':
					kw = '淘宝商城'
					print 'search...[',kw,']'
					search(kw)
				else:
					print 'unknown value : ',value
			elif name in ('-h','--help'):
				usage(sys.argv[0])
			else:
				usage(sys.argv[0])
				
		print '[END]'
	except getopt.GetoptError, err:
		print 'getopt error:',str(err)
		print '[EXIT]'
		sys.exit(2)

	#print 'delete mapping ...'
	#deleteMapping()

	#print 'delete index ...'
	#delete_index()
	
#END 
