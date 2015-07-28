#!/usr/bin/env python
# -*- coding: utf-8 -*-

import amberalertcn.core
import amberalertcn.api.v1.Secret as secret
import amberalertcn.api.v1.Pusher as pusher
import amberalertcn.utils as utils

# In dev environment I use fake data.
class FakeDBAccess(object):
    def __init__(self):
        self.__baidu_user_location = {}
        self.__amber_user_to_alert = {}
        self.__amber_device_to_alert = {}
        self.__amber_device_to_baidu = {}
        self.__baidu_to_amber_device = {}
        self.__amber_device_to_user = {}
        self.__amber_user_to_device = {}
        self.__baidu_to_amber_user = {}
        self.__amber_device_to_location = {}
        self.__amber_alert_chatroom = {}
        self.__amber_alert_follow_list = {}

        self.__amber_user_sequence = 1
        self.__amber_device_sequence = 1
        self.__amber_alert_sequence = 1

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
        self.__amber_device_to_location[amber_device_id] =\
                (float(longitude), float(latitude))

    def query_device_id_from_baidu(self, user_id, channel_id):
        key = self.get_baidu_user_channel_key(user_id, channel_id)
        if key in self.__baidu_to_amber_device:
            return self.__baidu_to_amber_device[key]
        return None

    def query_user_id_from_baidu(self, user_id, channel_id):
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

    def create_alert(self, amber_user_id, amber_device_id):
        new_amber_alert_id = self.__amber_alert_sequence
        self.__amber_alert_sequence += 1

        if amber_user_id not in self.__amber_user_to_alert:
            self.__amber_user_to_alert[amber_user_id] = [new_amber_alert_id]
        else:
            self.__amber_user_to_alert[amber_user_id].append(new_amber_alert_id)

        if amber_device_id not in self.__amber_device_to_alert:
            self.__amber_device_to_alert[amber_device_id] = \
                    [new_amber_alert_id]
        else:
            self.__amber_user_to_alert[amber_device_id].append(\
                    new_amber_alert_id)

        self.__amber_alert_chatroom[new_amber_alert_id] = []
        self.__amber_alert_follow_list[new_amber_alert_id] = []
        return new_amber_alert_id

    def query_device_list_in_range(self, amber_device_id, distance,\
            longitude, latitude):
        longitude_low_bound = longitude - distance
        longitude_high_bound = longitude + distance
        latitude_low_bound = latitude - distance
        latitude_high_bound = latitude + distance

        matched_list = []
        for each_device in self.__amber_device_to_baidu:
            each_longitude, each_latitude = \
                    self.__amber_device_to_location[each_device]
            if each_longitude >= longitude_low_bound and\
                each_longitude <= longitude_high_bound and\
                each_latitude >= latitude_low_bound and\
                each_latitude <= latitude_high_bound:
                    # This device is in range.
                    print("Found one: ", each_longitude, each_latitude)
                    baidu_info = self.__amber_device_to_baidu[each_device]
                    matched_list.append(baidu_info)
        return matched_list

    def query_amber_user_in_alert_follow_list(self, amber_alert_id,\
            amber_from_user_id):
        follow_list = self.__amber_alert_follow_list[amber_alert_id]
        if amber_from_user_id in follow_list:
            return True
        return False

    def insert_amber_user_to_alert_follow_list(self,\
            amber_alert_id, amber_user_id):
        self.__amber_alert_follow_list[amber_alert_id].append(amber_user_id)

    def query_device_list_following_alert(self, amber_alert_id):
        found_amber_user_ids = self.__amber_alert_follow_list[amber_alert_id]
        matched_device_list = []
        for each_user in found_amber_user_ids:
            device_list = self.__amber_user_to_device[each_user]
            for each_device_id in device_list:
                baidu_id = self.__amber_device_to_baidu[each_device_id]
                matched_device_list.append(baidu_id)
        return matched_device_list

    def add_message_to_chatroom(self, amber_alert_id, message_info):
        ts = utils.get_millisecond_unix_epoch()
        message_info.append(ts)
        self.__amber_alert_chatroom[amber_alert_id].append(message_info)

    def query_alert_by_id(self, amber_alert_id):
        return self.__amber_alert_chatroom[amber_alert_id]

    def query_alerts_by_ids(self, amber_alert_ids):
        amber_alerts = {}
        for alert_id in amber_alert_ids:
            amber_alerts[alert_id] = self.query_alert_by_id(amber_alert_id)
        return amber_alerts

    def query_all_alerts(self):
        return self.__amber_alert_chatroom

    def query_following_alert_ids(self, amber_user_id):
        amber_user_following_alert_ids = []
        print(self.__amber_alert_follow_list)
        for (key, item) in self.__amber_alert_follow_list.items():
            if amber_user_id in item:
                amber_user_following_alert_ids.append(key)
        return amber_user_following_alert_ids

    def query_following_alerts(self, amber_user_id):
        amber_user_following_alert_ids = self.query_following_alert_ids(amber_user_id)
        print(amber_user_following_alert_ids)
        return self.query_alerts_by_ids(amber_user_following_alert_ids)

    def query_user_alerts(self, amber_user_id):
        amber_alert_ids = []
        for (key, item) in self.__amber_alert_chatroom.items():
            if item[0][3] == amber_user_id:
                amber_alert_ids.append(key)
        return self.query_alerts_by_ids(amber_alert_ids)




AACN_SECRET = secret.Secret() # Find secret.ini from current folder.
AACN_CORE = amberalertcn.core.Core(FakeDBAccess(), None)
