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
        user_name = flask.request.args.get('user_name')
        user_face = flask.request.args.get('user_face')
        location = flask.request.args.get('location')
        longitude = float(flask.request.args.get('longitude'))
        latitude = float(flask.request.args.get('latitude'))
        child_id = 0 # no use
        core = amberalertcn.Application.current_core()
        resp = core.publish_alert(user_id, channel_id, child_id, \
                user_name, user_face, location, longitude, latitude)
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

@api.route('/alerts', methods=['GET'])
def get_all_alerts():
    try:
        core = amberalertcn.Application.current_core()
        resp = core.get_all_alerts()
        return utils.make_json_alerts_response(resp)
    except Exception as e:
        print(e)
        return utils.make_json_response(None)

@api.route('/alert/<int:alert_id>', methods=['GET'])
def get_alert(alert_id):
    try:
        core = amberalertcn.Application.current_core()
        resp = core.get_alert_details(alert_id)
        print(resp)
        return utils.make_json_alert_response(resp)
    except Exception as e:
        print(e)
        return utils.make_json_response(None)

@api.route('/myfollowingalerts', methods=['GET'])
def get_my_following_alerts():
    try:
        core = amberalertcn.Application.current_core()
        user_id = int(flask.request.args.get('user_id'))
        resp = core.get_my_following_alerts(user_id)
        return utils.make_json_alerts_response(resp)
    except Exception as e:
        print(e)
        return utils.make_json_response(None)

@api.route('/myalerts', methods=['GET'])
def get_my_amber_alerts():
    try:
        core = amberalertcn.Application.current_core()
        user_id = int(flask.request.args.get('user_id'))
        channel_id = int(flask.request.args.get('channel_id'))
        resp = core.get_my_alerts(user_id, channel_id)
        return utils.make_json_alerts_response(resp)
    except Exception as e:
        print(e)
        return utils.make_json_response(None)
