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

    function postMessage(txt) {
        Ext.Ajax.request({
            method: 'POST',
            url: window.ux.ROOT_URL + 'rest/messages',
            params: {
                message: txt
            },
            success: function () {
                window.console.log('message sent', txt);
            }
        });
    }

    function setNameAndPost(txt) {
        Ext.Msg.prompt(springchat.i18n.get('name'), springchat.i18n.get('enter.name'), function (buttonText, rawName) {
            var name = Ext.util.Format.trim(rawName);
            if (Ext.isEmpty(name)) {
                setNameAndPost(txt);
            } else {
                Ext.Ajax.request({
                    method: 'POST',
                    url: window.ux.ROOT_URL + 'rest/auth',
                    params: {
                        user: name
                    },
                    success: function (response) {
                        var json = Ext.decode(response.responseText);
                        if (json.success) {
                            window.ux.authenticated = true;
                            postMessage(txt);
                        } else {
                            Ext.Msg.alert('login.exception', json.info, function () {
                                setNameAndPost(txt);
                            });
                        }
                    },
                    failure: function () {
                        Ext.Msg.alert('error', 'error.connection', Ext.emptyFn);
                    }
                });
            }
        });
    }

    Ext.define('springchat.controller.Message', {
        extend: 'Ext.app.Controller',
        requires: [
            'Ext.util.Format',
            'Ext.MessageBox'
        ],
        config: {
            refs: {
                mainPanel: 'jschat-messages-panel',
                list: 'jschat-messages-panel dataview',
                sendMessageBtn: 'jschat-messages-panel button[action=sendMessage]',
                messageTxt: 'jschat-messages-panel textfield'
            },

            control: {
                mainPanel: {
                    show: function () {
                        var me = this;
                        var store = Ext.StoreManager.get('Message');
                        store.on('load', function () {
                            me.scroll();
                        });
                        store.on('addrecords', function () {
                            me.scroll();
                        });
                    }
                },
                sendMessageBtn: {
                    tap: 'onSendMessage'
                }
            }
        },

        scroll: function () {
            var me = this;
            var scroller = me.getList().getScrollable().getScroller();
            scroller.scrollTo(0, Number.MAX_VALUE);
        },

        init: function () {
            var me = this;
            me.getApplication().on('websocket-event-text', function (data) {
                var store = Ext.StoreManager.get('Message');
                store.add(data);
            });
        },

        getTextToSend: function () {
            var me = this;
            var txtField = me.getMessageTxt();
            var message = txtField.getValue();
            txtField.reset();
            return Ext.util.Format.trim(message);
        },

        onSendMessage: function () {
            var me = this;
            var txt = me.getTextToSend();
            if (txt === '') {
                return; // nothing to do.
            }
            if (window.ux.authenticated) {
                postMessage(txt);
            } else {
                setNameAndPost(txt);
            }
        }
    });

}());