#!/usr/bin/env python
# -*- coding: utf-8 -*-

import amberalertcn.utils as utils

class Core(object):
    def __init__(self, dbaccess, lbsaccess):
        self.__dbaccess = dbaccess
        self.__lbsaccess = lbsaccess

    def update_location(self, user_id, channel_id, longitude, latitude):
        dbaccess = self.__dbaccess
        amber_device_id = dbaccess.query_device_id_from_baidu_push(\
                user_id, channel_id)
        if amber_device_id is None:
            # It is a new device ID, but can it be a new user ID
            print("Create new device ID")
            amber_user_id = dbaccess.query_amber_user_id_from_baidu(\
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

    def publish_alsert(self, user_id, channel_id, \
            child_id, longitude, latitude):
        return utils.make_json_response({\
                "status_code": utils.httplib.OK })

    def send_message(self, sender_id, amber_alert_id, message):
        return utils.make_json_response({\
                "status_code": utils.httplib.OK })
