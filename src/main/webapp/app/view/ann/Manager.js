Ext.define('TSP.view.ann.Manager', {
    extend: 'Ext.Panel',
    alias: 'widget.annmanager',

    requires: [
        'TSP.view.ann.List',
        'TSP.view.ann.ListDetails',
        'Ext.toolbar.Toolbar',
        'Ext.Panel'
    ],

    border: false,
    title: 'Manage Neural Networks',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    initComponent: function() {
        this.bodyStyle = {
            background: '#ffffff'
        }
        this.items = [
            {
                xtype: 'toolbar',
                items: [
                    {
                        xtype: 'button',
                        iconCls: 'icon-add',
                        text: 'Create new network',
                        action: "createNew"
                    }
                ]
            },
            {
                xtype: 'panel',
                layout: 'border',
                flex: 1, // automatically adjust w and h of this panel to it's parent's w/h
                bodyPadding: 5,
                border: false,
                items: [
                    {
                        xtype: 'annlist',
                        itemId: 'annList',
                        height: 180,
                        region: 'north',
                        split: true,
                        collapsible: true
                    },
                    {
                        xtype: 'annlistdetails',
                        itemId: 'annListDetails',
                        region: 'center'
                    }
                ]
            }
        ];

        this.callParent(arguments);

    }

});
