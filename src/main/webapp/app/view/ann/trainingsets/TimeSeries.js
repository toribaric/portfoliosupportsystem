Ext.define('TSP.view.ann.trainingsets.TimeSeries', {
    extend: 'Ext.Panel',
    alias: 'widget.tstimeseries',

    bodyPadding: 5,
    title: 'Add training set records from time series chart',
    layout: 'border',

    items: [
        {
            region: 'west',
            itemId: 'createSetForm',
            xtype: 'form',
            width: 245,
            bodyPadding: 5,
            header: false,
            split: true,
            collapsible: true,
            collapseMode: 'mini',
            items: [
                {
                    xtype:'fieldset',
                    title: 'Upload time series file',
                    layout: 'anchor',
                    defaults: {
                        labelAlign: 'left',
                        labelWidth: 80,
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'filefield',
                            itemId: 'timeSeriesFile',
                            emptyText: 'Select file',
                            fieldLabel: 'File',
                            name: 'timeSeriesFile',
                            buttonText: '',
                            buttonConfig: {
                                iconCls: 'icon-upload'
                            },
                            allowBlank: false
                        },
                        {
                            xtype: 'textfield',
                            name: 'dataDelimiter',
                            fieldLabel: 'Data delimiter',
                            value: ';',
                            allowBlank: false
                        },
                        {
                            xtype: 'datefield',
                            name: 'fromDate',
                            fieldLabel: 'From date',
                            format: 'd.m.Y',
                            allowBlank: false
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    layout: 'hbox',
                    border: false,
                    items: [
                        {
                            xtype: 'button',
                            text: 'Upload',
                            action: 'uploadTimeSeriesData',
                            margin: '0 5 0 0'
                        },
                        {
                            xtype: 'button',
                            text: 'Reset',
                            action: 'resetTimeSeriesForm'
                        }
                    ]
                }
            ]
        },
        {
            region: 'center',
            itemId: 'chartContainer',
            xtype: 'panel',
            layout: 'fit',
            header: false
        }

    ]

});
