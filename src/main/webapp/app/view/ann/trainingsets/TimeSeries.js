Ext.define('TSP.view.ann.trainingsets.TimeSeries', {
    extend: 'Ext.Panel',
    alias: 'widget.tstimeseries',

    bodyPadding: 5,
    title: 'Add training set records from time series chart',
    layout: 'border',

    items: [
        {
            region: 'west',
            itemId: 'timeSeriesForms',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            width: 245,
            header: false,
            split: true,
            collapsible: true,
            collapseMode: 'mini',
            items: [
                {
                    itemId: 'createSetForm',
                    xtype: 'form',
                    bodyPadding: 5,
                    border: false,
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
                    itemId: 'generateSetRecordsForm',
                    xtype: 'form',
                    hidden: true,
                    border: false,
                    bodyPadding: 5,
                    items: [
                        {
                            xtype:'fieldset',
                            title: 'Generate training set records',
                            layout: 'anchor',
                            defaults: {
                                labelAlign: 'left',
                                labelWidth: 120,
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'numTsRecords',
                                    fieldLabel: 'Num. of records',
                                    value: '1000',
                                    allowBlank: false,
                                    regex: /^[1-9]+[0-9]*$/
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'samplingFrequency',
                                    fieldLabel: 'Sampling frequency',
                                    value: '1',
                                    allowBlank: false,
                                    regex: /^[1-9]+[0-9]*$/
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
                                    text: 'Generate',
                                    action: 'generateTsRecords'
                                }
                            ]
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
