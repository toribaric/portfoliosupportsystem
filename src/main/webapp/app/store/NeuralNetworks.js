Ext.define('TSP.store.NeuralNetworks', {
    extend: 'Ext.data.Store',
    model: 'TSP.model.NeuralNetwork',
    autoLoad: true,
    autoSync: false,

    sorters: [
        {
            property: 'id',
            direction: 'ASC'
        }
    ],

    proxy: {
        type: 'ajax',
        api: {
            read: 'ann/list',
            destroy: 'ann/deleteNetwork'
        },
        writer: {
            type: 'json',
            writeAllFields: false
        },
        reader: {
            type: 'json',
            root: 'neuralNetworks',
            successProperty: 'success'
        },
        listeners: {
            exception: function(proxy, response, operation){
                Ext.MessageBox.show({
                    title: 'Failed',
                    msg: response,
                    icon: Ext.MessageBox.ERROR,
                    buttons: Ext.Msg.OK
                });
            }
        }
    }

});