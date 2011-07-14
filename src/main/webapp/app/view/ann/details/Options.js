Ext.define('TSP.view.ann.details.Options', {
    extend: 'Ext.Panel',
    alias: 'widget.annoptions',

    layout: 'accordion',
    title: 'Network options',
    width: 200,

    defaults: {
        bodyStyle: 'padding: 10px'
    },

    layoutConfig: {
        titleCollapse: false,
        animate: true,
        activeOnTop: true
    },

    items: [
        {
            title: 'Actions',
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Structure',
                    defaultType: 'button',
                    defaults: {anchor: '100%'},
                    layout: 'anchor',
                    items: [
                        {
                            xtype: 'button',
                            text: 'Modify structure',
                            action: 'modifyStructure',
                            margin: '0 0 7 0'
                        },
                        {
                            xtype: 'button',
                            text: 'Delete network',
                            action: 'deleteNetwork'
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: 'Training',
                    defaultType: 'button',
                    defaults: {anchor: '100%'},
                    layout: 'anchor',
                    items: [
                        {
                            xtype: 'button',
                            text: 'Train network',
                            action: 'train',
                            margin: '0 0 7 0'
                        },
                        {
                            xtype: 'button',
                            text: 'Test network',
                            action: 'test'
                        }
                    ]
                }
            ]
        },
        {
            title: 'Configuration',
            items: [
                {
                    xtype: 'form',
                    border: false,
                    items: [
                        {
                            xtype:'fieldset',
                            title: 'Change configuration',
                            layout: 'anchor',
                            defaults: {
                                labelAlign: 'left',
                                labelWidth: 70,
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'combo',
                                    itemId: 'activationFunction',
                                    mode: 'local',
                                    forceSelection: true,
                                    editable: false,
                                    fieldLabel: 'Activation f.',
                                    name: 'activationFunction',
                                    displayField: 'name',
                                    valueField: 'value',
                                    queryMode: 'local',
                                    store: Ext.create('Ext.data.Store', {
                                        fields : ['name', 'value'],
                                        data   : [
                                            {name : 'Logistic',   value: 'LOG'},
                                            {name : 'Hyperbolic tangent',   value: 'TANH'}
                                        ]
                                    })
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'id',
                                    itemId: 'annId'
                                }
                            ]
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
                            text: 'Update',
                            action: 'updateAnnConf'
                        }
                    ]
                }
            ]
        }
    ]

});