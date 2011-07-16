Ext.define('TSP.view.ann.training.SimpleData', {
    extend: 'Ext.window.Window',
    alias : 'widget.simpletrainingdata',

    title : 'Training data',
    layout: 'fit',
    autoShow: false,
    height: 140,
    width: 280,
    modal: false,
    maximizable: true,

    closeMode: 'hide',

    constructor: function(config) {
        Ext.apply(this, {
            id: config.id,
            neuralNetworkId: config.neuralNetworkId
        });
        this.callParent([config]);
    },

    initComponent: function() {

        this.items = [
            {
                xtype: 'panel',
                border: false,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                bodyPadding: 5,
                items: [
                    {
                        xtype:'fieldset',
                        title: '',
                        itemId: 'displayFields',
                        bodyPadding: 5,
                        items: [
                            {
                                xtype: 'displayfield',
                                itemId: 'iteration',
                                fieldLabel: 'Iteration',
                                labelWidth: 60,
                                margin: '0 0 5 0',
                                value: 1
                            },
                            {
                                xtype: 'displayfield',
                                itemId: 'totalError',
                                labelWidth: 60,
                                fieldLabel: 'Total error',
                                value: 1.5
                            }
                        ]
                    }
                ]
            }
        ];

        this.buttons = [
            {
                text: 'Stop',
                action: 'stopTraining'
            },
            {
                text: 'Close',
                scope: this,
                handler: function() {
                    if( this.closeMode == 'close' ) {
                        this.close();
                    }
                    else {
                        this.hide();
                    }
                }
            }
        ];

        this.callParent(arguments);

    },

    listeners: {
        beforeclose: function(panel, options) {
            if( this.closeMode == 'hide' ) {
                this.hide();
                return false;
            }
        }
    },

    setCloseMode: function(closeMode) {
        this.closeMode = closeMode;
    },

    getNeuralNetworkId: function() {
        return this.neuralNetworkId;
    }

});
