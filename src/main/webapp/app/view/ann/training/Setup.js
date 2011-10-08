Ext.define('TSP.view.ann.training.Setup', {
    extend: 'Ext.window.Window',
    alias : 'widget.trainingsetup',

    title : 'Setup training',
    layout: 'fit',
    autoShow: false,
    height: 384,
    width: 310,
    modal: true,

    constructor: function(config) {
        Ext.apply(this, {
            neuralNetworkId: config.neuralNetworkId,
            trainingSetStore: config.trainingSetStore
        });
        this.callParent([config]);
    },

    initComponent: function() {

        this.items = [
            {
                xtype: 'form',
                bodyPadding: 5,
                border: false,
                items: [
                    {
                        xtype:'fieldset',
                        title: 'Set learning parameters',
                        layout: 'anchor',
                        defaults: {
                            labelAlign: 'left',
                            labelWidth: 110,
                            anchor: '100%'
                        },
                        items: [
                            {
                                xtype: 'hiddenfield',
                                name: 'neuralNetworkId',
                                value: this.neuralNetworkId
                            },
                            {
                                xtype: 'textfield',
                                name: 'learningRate',
                                fieldLabel: 'Learning rate',
                                allowBlank: false,
                                value: 0.5
                            },
                            {
                                xtype: 'textfield',
                                name: 'errorThreshold',
                                fieldLabel: 'Error threshold',
                                allowBlank: false,
                                value: 0.001
                            },
                            {
                                xtype: 'combo',
                                name: 'learningAlgorithm',
                                fieldLabel: 'Learning algorithm',
                                displayField: 'name',
                                valueField: 'value',
                                queryMode: 'local',
                                store: Ext.create('Ext.data.Store', {
                                    fields : ['name', 'value'],
                                    data   : [
                                        {name : 'Backprop',   value: 'BACKPROP'},
                                        {name : 'Backprop with momentum',   value: 'BACKPROPMOMENTUM'},
                                        {name : 'Resilient propagation',   value: 'RPROP'}
                                    ]
                                }),
                                allowBlank: false
                            },
                            {
                                xtype: 'container',
                                layout: 'hbox',
                                items: [
                                    {
                                        xtype: 'checkbox',
                                        name: 'useMaxIterations',
                                        boxLabel: 'Max. iterations:',
                                        hideLabel: false,
                                        checked: false,
                                        margin: '0 8 0 0',
                                        handler: function(me, checked) {
                                            var textField = me.ownerCt.items.get('maxIterations');
                                            textField.setDisabled(!checked);
                                            textField.el.animate({opacity: !checked ? .3 : 1});
                                        }
                                    },
                                    {
                                        xtype: 'textfield',
                                        itemId: 'maxIterations',
                                        name: 'maxIterations',
                                        width: 150,
                                        fieldLabel: '',
                                        allowBlank: false,
                                        disabled: true
                                    }
                                ]
                            },
                            {
                                xtype: 'combo',
                                fieldLabel: 'Training set',
                                name: 'trainingSet',
                                itemId: 'trainingSet',
                                displayField: 'name',
                                valueField: 'id',
                                queryMode: 'remote',
                                store: this.trainingSetStore,
                                listeners: {
                                    beforedestroy: function(combo) {
                                        combo.store.load();
                                    }
                                }
                            },
                            {
                                xtype: 'container',
                                layoutConfig : {
                                    type : 'vbox',
                                    align : 'stretch',
                                    pack : 'start'
                                },
                                items: [
                                    {
                                        xtype: 'checkbox',
                                        name: 'useValidation',
                                        boxLabel: 'Use network validation',
                                        hideLabel: false,
                                        checked: false,
                                        margin: '0 8 0 0',
                                        handler: function(me, checked) {
                                            var fieldSet = me.ownerCt.items.get('validationParams');
                                            fieldSet.setDisabled(!checked);
                                            fieldSet.el.animate({opacity: !checked ? .3 : 1});
                                        }
                                    },
                                    {
                                        xtype:'fieldset',
                                        itemId: 'validationParams',
                                        title: 'Set validation parameters',
                                        layout: 'anchor',
                                        disabled: true,
                                        defaults: {
                                            labelAlign: 'left',
                                            labelWidth: 80,
                                            anchor: '100%'
                                        },
                                        items: [
                                            {
                                                xtype: 'combo',
                                                fieldLabel: 'Validation set',
                                                name: 'validationSet',
                                                itemId: 'validationSet',
                                                displayField: 'name',
                                                valueField: 'id',
                                                queryMode: 'remote',
                                                store: this.trainingSetStore,
                                                listeners: {
                                                    beforedestroy: function(combo) {
                                                        combo.store.load();
                                                    }
                                                }
                                            },
                                            {
                                                xtype: 'textfield',
                                                itemId: 'validationFrequency',
                                                name: 'validationFrequency',
                                                width: 150,
                                                fieldLabel: 'Frequency',
                                                allowBlank: false,
                                                value: 50
                                            }
                                        ]
                                    }
                                ]
                            },
                            {
                                xtype: 'checkboxfield',
                                name: 'showChart',
                                itemId: 'showChart',
                                boxLabel: 'Show learning graph (slower learning)'
                            }
                        ]
                    }
                ]
            }
        ];

        this.buttons = [
            {
                text: 'Start training',
                action: 'startTraining'
            },
            {
                text: 'Close',
                scope: this,
                handler: this.close
            }
        ];

        this.callParent(arguments);

    }

});