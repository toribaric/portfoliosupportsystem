Ext.define('TSP.view.ann.training.Test', {
    extend: 'Ext.window.Window',
    alias : 'widget.testnetwork',

    title : 'Test network',
    layout: 'fit',
    autoShow: false,
    height: 150,
    width: 600,
    modal: true,

    constructor: function(config) {
        Ext.apply(this, {
            neuralNetworkId: config.neuralNetworkId
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
                        title: 'Enter network input(s)',
                        layout: 'anchor',
                        defaults: {
                            labelAlign: 'left',
                            labelWidth: 100,
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
                                name: 'networkInput',
                                fieldLabel: 'Network input(s)',
                                allowBlank: false
                            }
                        ]
                    }
                ]
            }
        ];

        this.buttons = [
            {
                text: 'Test',
                action: 'testNetwork'
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