#!/usr/bin/python
#
# check apk download url's availability 
#
# By yangzhentao @201409 @vivosz
#

import urllib
import urllib2
import cookielib

BAIDU_APK_PREFIX_WEB = 'http://bs.baidu.com/appstore/apk_'
BAIDU_APK_PREFIX_MOBILE = 'HTTP://gdown.baidu.com/data/wisegame/'

class NoRedirectHandler(urllib2.HTTPRedirectHandler):
    def http_error_302(self, req, fp, code, msg, headers):
        infourl = urllib.addinfourl(fp, headers, req.get_full_url())
        infourl.status = code
        infourl.code = code
        return infourl
    http_error_300 = http_error_302
    http_error_301 = http_error_302
    http_error_303 = http_error_302
    http_error_307 = http_error_302

def http_get(url,n):
	real_url = None
	n += 1
	cj = cookielib.CookieJar() 
	cookieprocessor = urllib2.HTTPCookieProcessor(cj) 
	opener = urllib2.build_opener(NoRedirectHandler,cookieprocessor)  
	opener.addheaders = [('User-agent','Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)')]  
	urllib2.install_opener(opener)  
	#req = urllib2.Request(url)  
	#req.add_header("Referer","http://xxoo.com")  
	resp = urllib2.urlopen(url)  
	if (resp.code == 301 or resp.code == 302):
		print 'Got ',resp.code, 'times=',n,' redirect=',resp.info().get('location')
		http_get(resp.info().get('location'),n) 
	elif (resp.code == 200):
		print 'Got 200 : times=',n,' url=',url
		real_url = resp.geturl()
		if (resp.info().get('Content-Length') < 20*1024):
			print 'warn: small body with url ', real_url 
			real_url = None
	else:
		print 'Got ',resp.code,' times=',n, ' error=',resp.msg
	return real_url

def http_get_follow_redirect(url):
	if (url==None or url.trim()==''):
		return None
	if (url.startswith(BAIDU_APK_PREFIX_WEB) or url.startswith(BAIDU_APK_PREFIX_MOBILE)):
		return url
	real_url = None
        req = urllib2.Request(url)  
        req.add_header("User-agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.202 Safari/535.1")
	resp = urllib2.urlopen(req)
        if (resp.code == 200):
		real_url = resp.geturl()
		if (resp.info().get('Content-Length') < 20*1024):
			print 'warn: small body with url ', real_url
			real_url = None
        else:
                print 'Got ',resp.code, ' msg=',resp.msg
	return real_url 

import sys
import unittest
if __name__ == '__main__':
	print '[START]'

	#s = http_get(sys.argv[1],0)
	s = http_get_follow_redirect(sys.argv[1]) 
	print 'input:',sys.argv[1] 
	print 'real :',s

	#print 'Run test case ...'
	unittest.main()
	#print 'Test ... over !'
	print '[END]'


## unit test
test_url_0 = 'http://bs.baidu.com/appstore/apk_F6A9A50947E7DBB4078C7D83BD8E4D75.apk'
test_url_1 = 'http://www.appchina.com/market/d/2146632/cop.baidu_0/com.caynax.a6w.apk'
test_url_2 = ''

class TestMe(unittest.TestCase):
	def test_http_get_follow_redirect():
		print 'Case 1 > '
		http_get(test_url_0)

		print 'Case 2 > '
		http_get(test_url_1)

		print 'Case 3 > '
		s3 = http_get(test_url_2)
		self.assertEqual(s3,None)

		print 'Test over !'
# End!
