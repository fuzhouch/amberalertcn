#!/usr/bin/env python
# -*- coding: utf-8 -*_

import amberalertcn
import flask
import functools
import amberalertcn.utils as utils
import json

api = amberalertcn.View('apiv1')

@api.route('/updatelocation', methods=['POST'])
def update_location():
    try:
        user_id = flask.request.args.get('user_id')
        channel_id = flask.request.args.get('channel_id')
        longitude = flask.request.args.get('longitude')
        latitude = flask.request.args.get('latitude')
        core = amberalertcn.Application.current_core()
        resp = core.update_location(user_id, channel_id, longitude, latitude)
        return utils.make_json_response(resp)
    except Exception as e:
        print("!!! ", e)
        return utils.make_json_response(None)

@api.route('/publishalert', methods=['POST'])
def publish_alert():
    try:
        user_id = flask.request.args.get('user_id')
        channel_id = flask.request.args.get('channel_id')
        longitude = float(flask.request.args.get('longitude'))
        latitude = float(flask.request.args.get('latitude'))
        child_id = 0 # no use
        core = amberalertcn.Application.current_core()
        resp = core.publish_alert(user_id, channel_id, child_id, \
                longitude, latitude)
        return utils.make_json_response(resp)
    except Exception as e:
        print(e)
        return utils.make_json_response(None)

@api.route('/sendmessage', methods=['POST'])
def send_message():
    try:
        user_id = int(flask.request.args.get('user_id'))
        channel_id = int(flask.request.args.get('channel_id'))
        receiver = int(flask.request.args.get('amber_alert_id'))
        message_json_str = flask.request.data.encode('utf-8')
        message_json = json.loads(message_json_str)
        message = message_json["message"]
        core = amberalertcn.Application.current_core()
        resp = core.send_message(user_id, channel_id, receiver, message)
        return utils.make_json_response(resp)
    except Exception as e:
        print(e)
        return utils.make_json_response(None)
