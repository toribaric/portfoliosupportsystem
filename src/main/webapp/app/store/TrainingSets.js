Ext.define('TSP.store.TrainingSets', {
    extend: 'Ext.data.Store',
    model: 'TSP.model.TrainingSet',
    autoLoad: true,
    autoSync: true,

    sorters: [
        {
            property: 'id',
            direction: 'ASC'
        }
    ],

    proxy: {
        type: 'ajax',
        api: {
            read: 'ts/list',
            create: 'ts/create',
            destroy: 'ts/delete'
        },
        writer: {
            type: 'json',
            writeAllFields: false
        },
        reader: {
            type: 'json',
            root: 'trainingSets',
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