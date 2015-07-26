#!/usr/bin/env python
# -*- coding: utf-8 -*_

import amberalertcn
import flask
import functools

def make_json_response(data):
    """def make_json_response(data) -> flask.Response(json_blob)

    This is a helper function when returning objects.
    """
    if data is None: # Data not found.
        resp = flask.jsonify(status_code=ruuxee.httplib.BAD_REQUEST)
        resp.status_code = ruuxee.httplib.BAD_REQUEST
        resp.content_encoding = ACCEPTED_CONTENT_ENCODING
    elif "status_code" in data:
        resp = flask.jsonify(**data)
        resp.status_code = data["status_code"]
        resp.content_encoding = ACCEPTED_CONTENT_ENCODING
    else:
        resp = flask.jsonify(status_code=ruuxee.httplib.OK, **data)
        resp.content_encoding = ACCEPTED_CONTENT_ENCODING
    return resp

api = amberalertcn.View('apiv1')

@api.route('/updatelocation', methods=['POST'])
def update_location():
    try:
        user_id = flask.request.args.get('user_id')
        channel_id = flask.request.args.get('channel_id')
        longitude = flask.request.args.get('longitude')
        latitude = flask.request.args.get('latitude')
        core = amberalertcn.current_core()
        resp = core.update_loc(user_id, channel_id, longitude, latitude)
        return make_json_response(resp)
    except Exception as e:
        return make_json_response(None)

@api.route('/publishalert', methods=['POST'])
def publish_alert():
    try:
        user_id = flask.request.args.get('user_id')
        channel_id = flask.request.args.get('channel_id')
        child_id = flask.request.args.get('child_id')
        longitude = flask.request.args.get('longitude')
        latitude = flask.request.args.get('latitude')
        core = amberalertcn.current_core()
        resp = core.publish_alert(user_id, channel_id, child_id, \
                longitude, latitude)
        return make_json_response(resp)
    except Exception as e:
        return make_json_response(None)

@api.route('sendmessage', methods=['POST'])
def send_message():
    try:
        sender_id = flask.request.args.get('sender_id')
        receiver_id = flask.request_args.get('alert_id')
        message_json_str = flask.request.data
        message = json.loads(message_json_str)
        core = amberalertcn.current_core()
        resp = core.send_message(sender_id, receiver_id, message)
        return make_json_response(resp)
    except Exception as e:
        return make_json_response(None)
