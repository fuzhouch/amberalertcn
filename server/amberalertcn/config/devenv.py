#!/usr/bin/env python
# -*- coding: utf-8 -*-

import amberalertcn.core

# In dev environment I use fake data.
class FakeDBAccess(object):
    def __init__(self):
        self.__baidu_user_id_channel_id_to_amber_identity_id = {}
        self.__amber_user_id_to_baidu_identity_id = {}
        self.__amber_user_id_to_baidu_channel_id = {}

        self.__amber_user_id_longitude = {}
        self.__amber_user_id_lantitude = {}
        self.__amber_alert_id_to_amber_user_id = {}
        self.__amber_alert_id_brief = {}

    def register_or_amber_user_id(baidu_user_id, baidu_channel_id):
        return 1

    def update_location(amber_user_id, longitude, latitude):
        return

AACN_CORE = amberalertcn.core.Core(FakeDBAccess())
