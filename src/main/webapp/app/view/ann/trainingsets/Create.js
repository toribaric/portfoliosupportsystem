Ext.define('TSP.view.ann.trainingsets.Create', {
    extend: 'Ext.Panel',
    alias: 'widget.tscreate',

    title: 'Create new training set',
    layout: 'accordion',

    layoutConfig: {
        titleCollapse: false,
        animate: true,
        activeOnTop: true
    },

    items: [
        {
            title: 'Create manually',
            bodyPadding: 5,
            items: [
                {
                    xtype:'fieldset',
                    title: 'Set properties',
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        labelAlign: 'left',
                        labelWidth: 110,
                        anchor: '100%'
                    },
                    items: [
                        {
                            fieldLabel: 'Set name',
                            itemId: 'setName'
                        },
                        {
                            fieldLabel: 'Number of inputs',
                            itemId: 'numInputs'
                        },
                        {
                            fieldLabel: 'Number of outputs',
                            itemId: 'numOutputs'
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
                            text: 'Create',
                            action: 'createTrainingSet',
                            margin: '0 5 0 0'
                        },
                        {
                            xtype: 'button',
                            text: 'Reset',
                            action: 'resetTrainingSetFields'
                        }
                    ]
                }
            ]
        },
        {
            title: 'Create from file',
            bodyPadding: 5,
            xtype: 'form',
            itemId: 'uploadForm',
            items: [
                {
                    xtype:'fieldset',
                    title: '',
                    layout: 'anchor',
                    defaults: {
                        labelAlign: 'left',
                        labelWidth: 100,
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Set name',
                            itemId: 'uploadSetName',
                            name: 'uploadSetName',
                            allowBlank: false
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Num. of inputs',
                            itemId: 'numInputs',
                            name: 'numInputs',
                            allowBlank: false
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Num. of outputs',
                            itemId: 'numOutputs',
                            name: 'numOutputs',
                            allowBlank: false
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Fields delimiter',
                            itemId: 'fieldsDelimiter',
                            name: 'fieldsDelimiter',
                            value: '<space>',
                            allowBlank: false
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Rows delimiter',
                            itemId: 'rowsDelimiter',
                            name: 'rowsDelimiter',
                            value: '<newline>',
                            allowBlank: false
                        },
                        {
                            xtype: 'filefield',
                            itemId: 'trainingSetFile',
                            emptyText: 'Select file',
                            fieldLabel: 'File',
                            name: 'trainingSetFile',
                            buttonText: '',
                            buttonConfig: {
                                iconCls: 'icon-upload'
                            },
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
                            text: 'Create',
                            action: 'uploadCreateTrainingSet',
                            margin: '0 5 0 0'
                        },
                        {
                            xtype: 'button',
                            text: 'Reset',
                            action: 'uploadResetTrainingSetFields'
                        }
                    ]
                }
            ]
        }
    ]

});
