#!/usr/bin/python
# _*_ coding: UTF-8 _*_
import ConfigParser
import string, os, sys

class Secret(object):
	
	apiKey = "apiKey"
	secretKey = "secretKey"

	def __init__(self):
		cf = ConfigParser.ConfigParser()
		cf.read("secret.ini")
		#read by type
		self.apiKey = cf.get("baidu_secret", "apikey")
		self.secretKey = cf.get("baidu_secret", "secretkey")


		
