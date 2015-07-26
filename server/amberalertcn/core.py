#!/usr/bin/env python
# -*- coding: utf-8 -*-

import amberalertcn.utils as utils
import amberalertcn.api.v1.Pusher as pusher
import flask

class Core(object):
    def __init__(self, dbaccess, lbsaccess):
        self.__dbaccess = dbaccess
        self.__lbsaccess = lbsaccess

    def update_location(self, user_id, channel_id, longitude, latitude):
        dbaccess = self.__dbaccess
        amber_device_id = dbaccess.query_device_id_from_baidu(\
                user_id, channel_id)
        if amber_device_id is None:
            # It is a new device ID, but can it be a new user ID
            print("Create new device ID")
            amber_user_id = dbaccess.query_user_id_from_baidu(\
                    user_id, channel_id)
            if amber_user_id is None:
                amber_user_id = dbaccess.create_amber_user_id()
            amber_device_id = dbaccess.create_amber_device_id_to_user(\
                    amber_user_id)
            dbaccess.bind_amber_device_id_to_baidu(\
                    amber_device_id, user_id, channel_id)
        else:
            amber_user_id = dbaccess.query_user_id_from_device(amber_device_id)
            assert amber_user_id is not None

        dbaccess.update_device_location(amber_device_id, longitude, latitude)
        print("User: %d, longitude: %s, latitude: %s -- " %\
                (amber_user_id, longitude, latitude))
        resp = {\
                "status_code": utils.httplib.OK,\
                "amber_device_id": amber_device_id,\
                "amber_user_id": amber_user_id\
                }
        return resp

    def publish_alert(self, user_id, channel_id, \
            child_id, longitude, latitude):
        dbaccess = self.__dbaccess
        amber_from_user_id = dbaccess.query_user_id_from_baidu(\
                user_id, channel_id)
        amber_device_id = dbaccess.query_device_id_from_baidu(\
                user_id, channel_id)
        assert amber_device_id is not None
        assert amber_from_user_id is not None

        distance = 50 # TO BE DETERMINED

        amber_alert_id = dbaccess.create_alert(amber_from_user_id, \
                amber_device_id)
        matched_device_list = dbaccess.query_device_list_in_range(\
                amber_device_id, distance, longitude, latitude)
        if len(matched_device_list) == 0:
            return {\
                    "status_code": utils.httplib.OK,\
                    "alerted_count": 0}

        message = [
                "警告：有孩子走失！", # title
                "三岁，有点胖，大红帽子绿上衣，包包头", # description
                amber_alert_id, # amber_alert_id
                amber_from_user_id # from_user_id
                ]
        print("Send")
        p = pusher.Pusher(flask.current_app.config["AACN_SECRET"])
        result_json = p.pushAlert_to_users(matched_device_list, message)
        print("Done")
        # TBD result_json will be handled later.

        return { "status_code" : utils.httplib.OK,\
                "alerted_count" : len(matched_device_list)\
                }

    def send_message(self, sender_id, amber_alert_id, message):
        return utils.make_json_response({\
                "status_code": utils.httplib.OK })
