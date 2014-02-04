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

    Ext.define('springchat.view.Messages', {
        extend: 'Ext.Panel',
        alias: 'widget.jschat-messages-panel',
        requires: [
            'springchat.i18n',
            'Ext.TitleBar',
            'Ext.Toolbar'
        ],
        config: {
            layout: 'fit',
            items: [
                {
                    xtype: 'dataview',
                    store: 'Message',
                    cls: 'ks-basic',
                    itemTpl: new Ext.XTemplate('{[this.print(values)]}', {
                        print: function (data) {
                            var dt = Ext.Date.format(new Date(data.timestamp), 'G:i:s');
                            var tpl = '<div class="content">{0}</div><div class="info"><span>{1}</span><span>{2}</span></div>';
                            return Ext.String.format(tpl, data.content, data.from, dt);
                        }
                    }),
                    items: [
                        {
                            docked: 'top',
                            xtype: 'titlebar',
                            title: springchat.i18n.get('application.name')
                        },
                        {
                            xtype: 'toolbar',
                            docked: 'bottom',
                            layout: 'hbox',
                            items: [
                                {
                                    xtype: 'textfield',
                                    flex: 1
                                },
                                {
                                    xtype: 'button',
                                    action: 'sendMessage',
                                    iconCls: 'reply',
                                    iconMask: true,
                                    width: 50
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    });

}());

