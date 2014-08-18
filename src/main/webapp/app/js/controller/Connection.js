/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function () {
    'use strict';

    var pingTimeout = 1000 * 60 * 9; // 9 minutes

    function ping() {
        Ext.Ajax.request({
            url: window.ux.ROOT_URL + 'rest/auth',
            success: function () {
                window.setTimeout(function () {
                    ping();
                }, pingTimeout);
            },
            failure: function () {
                Ext.Msg.alert('error', 'error.connection', function () {
                    window.location.reload();
                });
            }
        });
    }

    Ext.define('springchat.controller.Connection', {
        extend: 'Ext.app.Controller',
        requires: [
            'Ext.MessageBox',
            'Ext.util.DelayedTask'
        ],
        config: {
            views: ['Messages'],
            refs: {
                messages: {
                    selector: 'jschat-messages-panel'
                }
            }
        },
        connectSocket: function () {
            var me = this;
            me.getMessages().mask({
                xtype: 'loadmask',
                message: springchat.i18n.get('connecting')
            });
            var location = window.location;
            var protocol = 'ws';
            if (location.protocol === 'https:' || location.protocol === 'https') {
                protocol = 'wss';
            }
            var wsPath = protocol + '://' + location.hostname + ':' + location.port + window.ux.ROOT_URL + 'ws/connection;jsessionid=' + window.ux.SESSION_ID;
            var connection = new window.WebSocket(wsPath);
            var pingSocketTask = new Ext.util.DelayedTask();
            var pingSocket = function () {
                try {
                    connection.send('ping');
                } catch (e) {
                    window.console.log('WebSocket: Impossible to ping.', e);
                }
                pingSocketTask.delay(50000, pingSocket);
            };
            connection.onopen = function () {
                window.console.log('WebSocket: connection started.');
                me.getMessages().unmask();
                var store = Ext.StoreManager.get('Message');
                store.load();
                pingSocket();
            };
            connection.onclose = function () {
                me.getMessages().mask({
                    xtype: 'loadmask',
                    message: springchat.i18n.get('connection.closed')
                });
                pingSocketTask.cancel();
            };
            connection.onerror = function (error) {
                Ext.Msg.alert('error', 'error.connection', function () {
                    window.location.reload();
                });
            };
            connection.onmessage = function (e) {
                try {
                    var evtData = Ext.JSON.decode(e.data);
                    me.getApplication().fireEvent('websocket-event-' + evtData.type, evtData.data);
                    window.console.log('WebSocket: message -> ' + e.data);
                } catch (ex) {
                    window.console.error('WebSocket: parse -> ' + ex);
                }
            };

        },
        init: function () {
            var me = this;
            me.control({
                messages: {
                    show: function (panel) {
                        me.connectSocket();
                        ping();
                    }
                }
            });

        }
    });

}());