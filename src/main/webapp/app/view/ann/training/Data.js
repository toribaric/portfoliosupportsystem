Ext.define('TSP.view.ann.training.Data', {
    extend: 'Ext.window.Window',
    alias : 'widget.trainingdata',

    title : 'Training data',
    layout: 'fit',
    autoShow: false,
    height: 500,
    width: 600,
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
                        xtype: 'panel',
                        border: false,
                        bodyPadding: 2,
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
                    },
                    {
                        xtype:'fieldset',
                        title: 'Total network error',
                        flex: 1,
                        layout: 'fit',
                        itemId: 'chartContainer',
                        bodyPadding: 5,
                        items: [
                            {
                                xtype: 'chart',
                                animate: false,
                                store: Ext.create('Ext.data.Store', {
                                    fields: ['iteration', 'error'],
                                    data: []
                                }),
                                axes: [
                                    {
                                        type: 'Numeric',
                                        position: 'bottom',
                                        fields: ['iteration'],
                                        label: {
                                            font: '10px Arial'
                                        },
                                        title: 'Iteration',
                                        grid: true
                                    },
                                    {
                                        type: 'Numeric',
                                        position: 'left',
                                        fields: ['error'],
                                        title: 'Total error',
                                        grid: true,
                                        label: {
                                            font: '10px Arial'
                                        }
                                    }
                                ],
                                series: [
                                    {
                                        type: 'line',
                                        highlight: {
                                            size: 3,
                                            radius: 1
                                        },
                                        axis: 'left',
                                        fill: true,
                                        xField: 'iteration',
                                        yField: 'error',
                                        tips: {
                                            trackMouse: false,
                                            renderer: function(storeItem, item) {
                                                this.update('<div class="chart-tip"><span>' + storeItem.get('iteration') + ':</span> ' + storeItem.get('error').toFixed(8) + '</div>');
                                            }
                                        },
                                        markerCfg: {
                                            type: 'circle',
                                            size: 0,
                                            radius: 0,
                                            'stroke-width': 0
                                        }
                                    }
                                ]
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
