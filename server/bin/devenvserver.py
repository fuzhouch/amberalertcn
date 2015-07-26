#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os
current = os.getcwd()
sys.path.append(current)

import amberalertcn
import amberalertcn.api.v1

if __name__ == '__main__':
    app = amberalertcn.Application(config='amberalertcn.config.devenv')
    api_page = amberalertcn.api.v1.api
    app.register_blueprint(api_page, url_prefix='/api/v1')
    app.run(debug=True)
