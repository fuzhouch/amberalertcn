#!/usr/bin/env python
# -*- coding: utf-8 -*-
import flask
from amberalertcn import httplib
import hashlib

ACCEPTED_CONTENT_ENCODING = "utf-8"

def generate_baidu_auth_uri(base_uri, params, baidu_ak, baidu_sk):
    """A helper function to generate URI with Baidu's SN"""
    safe = "/:=&?#+!$,;'@()*[]"
    sorted_by_key = sorted(params.items())
    encoded_params = []
    for each in sorted_by_key:
        key = each[0]
        value = each[1]
        encoded_key = urllib.quote(key, safe=safe)
        encoded_value = urllib.quote(value, safe=safe)
        encoded_params.append(encoded_key + "=" + encoded_value)
    encoded_params_uri = "&".join(encoded_params)
    encoded_uri_with_sk = base_uri + encoded_params_uri + baidu_sk
    baidu_sn = hashlib.md5(urllib.quote_plus(encoded_uri_with_sk)).hexdigest()
    encoded_uri = base_uri + encoded_params_uri
    encoded_uri_with_auth = encoded_uri + "&sn=" + baidu_sn
    return encoded_uri_with_auth

def make_json_response(data):
    """def make_json_response(data) -> flask.Response(json_blob)

    This is a helper function when returning objects.
    """
    if data is None: # Data not found.
        resp = flask.jsonify(status_code=httplib.BAD_REQUEST)
        resp.status_code = httplib.BAD_REQUEST
        resp.content_encoding = ACCEPTED_CONTENT_ENCODING
    elif "status_code" in data:
        resp = flask.jsonify(**data)
        resp.status_code = data["status_code"]
        resp.content_encoding = ACCEPTED_CONTENT_ENCODING
    else:
        resp = flask.jsonify(status_code=httplib.OK, **data)
        resp.content_encoding = ACCEPTED_CONTENT_ENCODING
    return resp

