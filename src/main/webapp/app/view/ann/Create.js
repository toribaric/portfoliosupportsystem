Ext.define('TSP.view.ann.Create', {
    extend: 'Ext.window.Window',
    alias : 'widget.anncreate',

    requires: ['Ext.form.Panel'],

    title : 'Create new network',
    layout: 'fit',
    autoShow: true,
    height: 180,
    width: 290,
    modal: true,

    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                padding: '5 5 0 5',
                border: false,
                style: 'background-color: #fff;',

                items: [
                    {
                        xtype: 'textfield',
                        name : 'name',
                        fieldLabel: 'Name',
                        labelWidth: 110,
                        allowBlank: false
                    },
                    {
                        xtype: 'textfield',
                        name : 'description',
                        fieldLabel: 'Description',
                        labelWidth: 110,
                        allowBlank: false
                    },
                    {
                        xtype: 'textfield',
                        name : 'structure',
                        fieldLabel: 'Network structure',
                        labelWidth: 110,
                        allowBlank: false
                    },
                    {
                        xtype: 'combo',
                        mode: 'local',
                        forceSelection: true,
                        editable: false,
                        fieldLabel: 'Activation function',
                        labelWidth: 110,
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
                        }),
                        allowBlank: false
                    }
                ]
            }
        ];

        this.buttons = [
            {
                text: 'Save',
                action: 'saveNewAnn'
            },
            {
                text: 'Cancel',
                scope: this,
                handler: this.close
            }
        ];

        this.callParent(arguments);
    }
});