Ext.define('TSP.view.ann.trainingsets.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.tslist',

    title: 'Training sets',

    store: 'TrainingSets',

    initComponent: function() {

        this.dockedItems = [
            {
                xtype: 'toolbar',
                items: [
                    {
                        iconCls: 'icon-delete',
                        text: 'Delete set',
                        action: 'deleteTrainingSet'
                    }
                ]
            }
        ];

        this.columns = [
            {header: 'ID', dataIndex: 'id', width: 50},
            {header: 'Name', dataIndex: 'name', flex: 1},
            {header: 'Inputs', dataIndex: 'numInputs', width: 70},
            {header: 'Outputs', dataIndex: 'numOutputs', width: 70}
        ];

        this.callParent(arguments);

    }
});
