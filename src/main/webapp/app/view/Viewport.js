Ext.define('TSP.view.Viewport', {
    extend: 'Ext.container.Viewport',
    alias: 'widget.viewport',

    requires: [
        'TSP.view.Header',
        'TSP.view.MainMenu',
        'TSP.view.ann.Manager',
        'TSP.view.ann.trainingsets.TrainingSets'
    ],

    autoshow: true,
    layout: 'border',

    items: [
        {
            xtype: 'viewheader',
            region: 'north'
        },
        {
            xtype: 'mainmenu',
            region: 'west',
            split: true,
            collapsible: true,
            width: 180
        },
        {
            region: 'center',
            layout: 'fit',
            margin: '2 2 2 0', // top right bottom left
            items: [
                {
                    xtype: 'annmanager',
                    id: 'annmanager'
                }
            ]

        }
    ]

});