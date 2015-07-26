#!/usr/bin/env python
# -*- coding: utf-8 -*-

import amberalertcn.core

# In dev environment I use fake data.
class FakeDBAccess(object):
    def __init__(self):
        self.__baidu_user_location = {}
        self.__amber_user_to_alert = {}
        self.__amber_device_to_baidu = {}
        self.__baidu_to_amber_device = {}
        self.__amber_device_to_user = {}
        self.__amber_user_to_device = {}
        self.__baidu_to_amber_user = {}
        self.__alert_info = {}

        self.__amber_user_sequence = 1
        self.__amber_device_sequence = 1

    def create_amber_alert_id(self):
        datetime.datetime.now()

    @staticmethod
    def get_baidu_user_channel_key(baidu_user_id, baidu_channel_id):
        return "{0}-{1}".format(baidu_user_id, baidu_channel_id)

    def create_amber_user_id(self):
        new_amber_user_id = self.__amber_user_sequence
        self.__amber_user_sequence += 1

        # Create corresponding user information.
        self.__amber_user_to_device[new_amber_user_id] = []
        self.__amber_user_to_alert[new_amber_user_id] = []

        return new_amber_user_id

    def update_device_location(self, amber_device_id, longitude, latitude):
        amber_user_id = self.__amber_device_to_user[amber_device_id]
        user_id, channel_id = self.__amber_device_to_baidu[amber_device_id]
        key = self.get_baidu_user_channel_key(user_id, channel_id)
        self.__baidu_to_amber_device[key] = amber_device_id

    def query_device_id_from_baidu_push(self, user_id, channel_id):
        key = self.get_baidu_user_channel_key(user_id, channel_id)
        if key in self.__baidu_to_amber_device:
            return self.__baidu_to_amber_device[key]
        return None

    def query_amber_user_id_from_baidu(self, user_id, channel_id):
        key = self.get_baidu_user_channel_key(user_id, channel_id)
        if key in self.__baidu_to_amber_user:
            return self.__baidu_to_amber_user[key]
        return None

    def query_user_id_from_device(self, amber_device_id):
        if amber_device_id in self.__amber_device_to_user:
            return self.__amber_device_to_user[amber_device_id]
        return None

    def create_amber_device_id_to_user(self, amber_user_id):
        new_amber_device_id = self.__amber_device_sequence
        self.__amber_device_sequence += 1
        self.__amber_user_to_device[amber_user_id].append(new_amber_device_id)
        self.__amber_device_to_user[new_amber_device_id] = amber_user_id
        return new_amber_device_id

    def bind_amber_device_id_to_baidu(self, amber_device_id,\
            baidu_user_id, baidu_channel_id):
        self.__amber_device_to_baidu[amber_device_id] =\
                (baidu_user_id, baidu_channel_id)
        amber_user_id = self.__amber_device_to_user[amber_device_id]
        key = self.get_baidu_user_channel_key(baidu_user_id, baidu_channel_id)
        self.__baidu_to_amber_user[key] = amber_user_id

AACN_CORE = amberalertcn.core.Core(FakeDBAccess(), None)
