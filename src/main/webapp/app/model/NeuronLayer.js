Ext.define('TSP.model.NeuronLayer', {
    extend: 'Ext.data.Model',
    fields: ['id', 'numNeurons'],
    hasMany: {
        model: 'TSP.model.Neuron',
        name: 'neurons'
    }
});
