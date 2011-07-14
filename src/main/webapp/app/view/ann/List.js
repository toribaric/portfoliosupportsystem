Ext.define('TSP.view.ann.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.annlist',

    id: 'annlist',

    title: 'Neural networks',

    store: 'NeuralNetworks',

    initComponent: function() {

        this.columns = [
            {header: 'ID', dataIndex: 'id', width: 50},
            {header: 'Name', dataIndex: 'name', flex: 1},
            {header: 'Description', dataIndex: 'description', width: 300},
            {header: 'Num. of input neurons', dataIndex: 'numInputNeurons', flex: 1},
            {header: 'Num. of output neurons', dataIndex: 'numOutputNeurons', flex: 1},
            {header: 'Num. of hidden layers', dataIndex: 'numHiddenLayers', flex: 1},
            {header: 'Activation function', dataIndex: 'activationFunction', flex: 1},
            {
                header: 'Trained',
                dataIndex: 'trained',
                flex: 1,
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
