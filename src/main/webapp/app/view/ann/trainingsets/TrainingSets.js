Ext.define('TSP.view.ann.trainingsets.TrainingSets', {
    extend: 'Ext.Panel',
    alias: 'widget.anntrainingsets',

    requires: [
        'TSP.view.ann.trainingsets.Create',
        'TSP.view.ann.trainingsets.List',
        'TSP.view.ann.trainingsets.TimeSeries'
    ],

    border: false,
    bodyPadding: 5,
    title: 'Training sets',
    layout: 'border',

    items: [
        {
            region: 'north',
            xtype: 'panel',
            layout: 'border',
            border: false,
            height: 300,
            bodyPadding: 0,
            split: true,
            header: false,
            collapsible: true,
            collapseMode: 'mini',
            items: [
                {
                    region: 'west',
                    xtype: 'tscreate',
                    width: 250,
                    split: true,
                    collapsible: true
                },
                {
                    region: 'center',
                    xtype: 'panel',
                    html: '<div id="trainingsets-initial-msg">Select training set to edit from "Training sets" grid</div>'
                },
                {
                    region: 'east',
                    xtype: 'tslist',
                    width: 300,
                    split: true,
                    collapsible: true
                }
            ]
        },
        {
            region: 'center',
            xtype: 'tstimeseries'
        }

    ]

});
