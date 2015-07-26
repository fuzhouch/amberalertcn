#!/usr/bin/env python
# -*- coding: utf-8 -*-

import amberalertcn.utils as utils

class Core(object):
    def __init__(self, dbaccess):
        self.__dbaccess = dbaccess

    def update_location(self, user_id, channel_id, longitude, latitude):
        return utils.make_json_response({\
                "status_code": utils.httplib.OK })

    def publish_alsert(self, user_id, channel_id, \
            child_id, longitude, latitude):
        return utils.make_json_response({\
                "status_code": utils.httplib.OK })

    def send_message(self, sender_id, amber_alert_id, message):
        return utils.make_json_response({\
                "status_code": utils.httplib.OK })
