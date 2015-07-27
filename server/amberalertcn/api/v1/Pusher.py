#!/usr/bin/python
# -*- coding: utf-8 -*-

import sys
import time
import json
import ConfigParser
import string, os

sys.path.append("..")
from Channel import *
from Secret import *

message_key = "AmberAlertCn.Key1"

class Pusher(object):
    def __init__(self, secret):
        self.__secret = secret
	
	#user: tuple of user (user_id, channel_id)
	#message: ["title", "description", "amber_alert_id", "from_user_id"]
    def pushAlert_to_users(self, user_list, message):
        for user in user_list:
            self.push_to_user(user, message)

    def pushUpdate_to_users(self, user_list, message):
        for user in user_list:
            self.push_to_user(user, message)
		
    def push_to_user(self, user, message):
        channel = Channel(self.__secret.apiKey, self.__secret.secretKey)
        push_type = 1
        optional = dict()
        optional[Channel.USER_ID] = user[0]
        optional[Channel.CHANNEL_ID] = user[1]
        optional[Channel.MESSAGE_TYPE] = 0
        alertMessage = "{'title':'%s', 'description':'%s', 'custom_content': { 'amber_alert_id':'%s', 'from_user_id':'%s' }}" % (message[0], message[1], message[2], message[3])
        #jsonMessage = json.dumps(updateMessage)
        ret = channel.pushMessage(push_type, alertMessage, message_key, optional)
        print (ret)
        return ret

###
# Test
###

def test_pushAlert_to_users():
	userlist = [(user_id, channel_id)]
	message = ['AlertTitle', 'AlertDesc', 'amber_alert_id', 'from_user_id']
	pushAlert_to_users(userlist, message)

def test_pushUpdate_to_users():
	userlist = [(user_id, channel_id)]
	message = ['UpdateTitle', 'UpdateDesc', 'amber_alert_id', 'from_user_id']
	pushAlert_to_users(userlist, message)

if __name__ == '__main__':
	user_id = "985986247753796219"
	channel_id = 4232420857743892347
	test_pushAlert_to_users()
	time.sleep(1)
	test_pushUpdate_to_users()