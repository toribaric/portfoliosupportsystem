Ext.define('TSP.view.ann.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.annlist',

    id: 'annlist',

    title: 'Neural networks',

    store: 'NeuralNetworks',

    initComponent: function() {

        this.columns = [
            {header: 'ID', dataIndex: 'id', width: 40},
            {header: 'Name', dataIndex: 'name', width: 140},
            {header: 'Description', dataIndex: 'description', flex: 1},
            {header: 'Input neurons', dataIndex: 'numInputNeurons', width: 90},
            {header: 'Output neurons', dataIndex: 'numOutputNeurons', width: 90},
            {header: 'Hidden layers', dataIndex: 'numHiddenLayers', width: 90},
            {header: 'Activation function', dataIndex: 'activationFunction', width: 110},
            {
                header: 'Validation error',
                dataIndex: 'validationError',
                width: 90,
                renderer: function(value) {
                    if( value != null ) {
                        return value.toFixed(8);
                    }
                    return value;
                }
            },
            {
                header: 'R^2',
                dataIndex: 'r2',
                width: 80,
                renderer: function(value) {
                    if( value != null ) {
                        return value.toFixed(8);
                    }
                    return value;
                }
            },
            {
                header: 'Error energy',
                dataIndex: 'errorEnergy',
                width: 80,
                renderer: function(value) {
                    if( value != null ) {
                        return value.toFixed(8);
                    }
                    return value;
                }
            },
            {
                header: 'Trained',
                dataIndex: 'trained',
                width: 60,
                renderer: function(value) {
                    if( value ) {
                        return '<img src="images/icons/accept.png" />';
                    }
                    else {
                        return '<img src="images/icons/delete.gif" />';
                    }
                }
            }
        ];

        this.callParent(arguments);

    }
});
